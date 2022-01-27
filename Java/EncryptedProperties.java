/** EncryptedProperties.java
 * Encrypts a properties file.
 * Authors: OWASP (anonymous), Tim Baker
 * Date Authored: 1-14-2008
 * Date Accessed: 2-16-2016
 * Obtained on https://www.owasp.org/index.php/How_to_encrypt_a_properties_file
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;
import java.util.Properties;

import net.iharder.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.PBEKeySpec;

public class EncryptedProperties extends Properties {
    private static InputStreamReader inputStream = null;
    private static final int iterationCount = 20;
    private Cipher encryptor, decryptor;
    private static byte[] salt = { (byte) 0x25, (byte) 0xF1, (byte) 0xEE, (byte) 0xA3, (byte) 0x07,
        (byte) 0xC3, (byte) 0x4D };
    public EncryptedProperties(String args) throws NoSuchAlgorithmException, InvalidKeySpecException,
        NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
            PBEParameterSpec ps = new PBEParameterSpec(salt, iterationCount);
            SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey k = kf.generateSecret(new PBEKeySpec(args.toCharArray()));
            encryptor = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
            decryptor = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
            encryptor.init(Cipher.ENCRYPT_MODE, k, ps);
            decryptor.init(Cipher.DECRYPT_MODE, k, ps);
    }

    @Override
    public String getProperty(String key) {
        try {
            return decrypt(super.getProperty(key));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't decrypt properly: " + e);
        }
    }

    public synchronized Object setProperty(String key, String value) {
        try {
            return super.setProperty(key, encrypt(value));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't encrypt properly: " + e);
        }
    }

    private synchronized String decrypt(String str) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] dec = Base64.decode(str);
        byte[] utf8 = decryptor.doFinal(dec);
        return new String(utf8, "UTF-8");
    }

    private synchronized String encrypt(String str) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] utf8 = str.getBytes("UTF-8");
        byte[] enc = encryptor.doFinal(utf8);
        return Base64.encodeBytes(enc);
    }

    // usage: java EncryptedProperties test.properties password
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException,
        NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
            if (args.length < 2) {
                System.out.println("Incorrect number of arguments provided.");
                System.out.println("Usage: java EncryptedProperties.jar file.properties password [view]");
            } else {
                boolean viewOnly = false;
                if (args.length > 2) {
                    viewOnly = "view".equalsIgnoreCase(args[2]);
                }
                createProperties(new File(args[0]), new EncryptedProperties(args[1]), viewOnly);
            }
    }

    public static void setInputStream(InputStreamReader isr) {
        inputStream = isr;
    }

    public static void createProperties(File props, EncryptedProperties obj, boolean viewOnly) throws IOException {
        try {
            if (props.exists()) {
                FileInputStream in = new FileInputStream(props);
                obj.load(in);

                if (viewOnly) {
                    System.out.println("Viewing File:");
                    Iterator<Object> i = obj.keySet().iterator();

                    while (i.hasNext()) {
                        String k = (String) i.next();
                        String v = obj.getProperty(k);
                        System.out.println("   " + k + "=" + v);
                    }
                }
            }

            if (!viewOnly) {
                BufferedReader br;
                InputStreamReader isr;
                if (inputStream == null) {
                    isr = new InputStreamReader(System.in);
                } else {
                    isr = inputStream;
                }
                br = new BufferedReader(isr);
                String key = null;
                do {
                    System.out.println("Enter key: ");
                    key = br.readLine();
                    System.out.println("Enter value: ");
                    String value = br.readLine();
                    if (key != null & key.length() > 0 && value != null && value.length() > 0) {
                        obj.setProperty(key, value);
                    }
                } while (key != null && key.length() > 0);

                FileOutputStream out = new FileOutputStream(props);
                obj.store(out, "Encrypted Properties File");
                out.close();

                System.out.println("File Contents");
                Iterator<Object> i = obj.keySet().iterator();

                while (i.hasNext()) {
                    String k = (String) i.next();
                    System.out.println("   " + k + "=" + obj.get(k));
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Couldn't access encrypted properties file: " + props.getAbsolutePath() + " " + e);
        }
    }
}
