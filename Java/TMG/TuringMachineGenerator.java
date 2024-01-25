package glsoft.tmg;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TuringMachineGenerator {
    Random r;
    Map<Integer,Set<Integer>> verifiers;
    Set<Integer> easyVerifiers;
    Set<Integer> medVerifiers;
    Set<Integer> hardVerifiers;
    Set<Integer> advVerifiers;
    Set<Integer> expVerifiers;

    int chosen = 0;
    Verifier[] vCards;

    Verifier[] challenge = null;

    public static final int CD_BT = 0; // blue triangle, first digit in secret code
    public static final int CD_YS = 1; // yellow square, second digit
    public static final int CD_PC = 2; // purple circle, third digit

    public static final int DF_EASY = 0;
    public static final int DF_MED = 1;
    public static final int DF_HARD = 2;
    public static final int DF_ADV = 3;
    public static final int DF_EXP = 4;

    public static final int FI_EASY = 0;
    public static final int FI_MED = 1;
    public static final int FI_HARD = 2;
    public static final int FI_ADV = 3;
    public static final int FI_EXP = 4;

    public static final int CLS_EASY1 = 0;
    public static final int CLS_EASY2 = 1;
    public static final int CLS_EASY3 = 2;
    public static final int CLS_EASY4 = 3;
    public static final int CLS_MED1 = 4;
    public static final int CLS_MED2 = 5;
    public static final int CLS_MED3 = 6;
    public static final int CLS_MED4 = 7;
    public static final int CLS_MED5 = 8;
    public static final int CLS_HARD1 = 9;
    public static final int CLS_HARD2 = 10;

    public static int CLS_MAX = 10;

    public static void main(String[] args) {
        TuringMachineGenerator tmg = new TuringMachineGenerator();
        tmg.runInterface(args);
    }

    public TuringMachineGenerator() {
        r = new Random();
        verifiers = new HashMap<>();
        Verifier.setVerifiers(verifiers);
        for (int s = 0; s < CLS_MAX; s++) {
            verifiers.put(s, new HashSet<>());
        }
        vCards = new Verifier[48];
    }

    public void runInterface(String[] args) {
        // instantiate verifiers and linked criteria with their associated names
        vCards[0] = new Verifier(
            1,
            new Criterion[] {
                new Criterion(530, 491, 527, 495, (code -> code.charAt(CD_BT) == '1')),
                new Criterion(359, 650, 357, 652, (code -> code.charAt(CD_BT) > '1'))
            },
            CLS_EASY1);
        vCards[1] = new Verifier(
            2,
            new Criterion[] {
                new Criterion(224, 778, 221, 780, (code -> code.charAt(CD_BT) < '3')),
                new Criterion(599, 413, 597, 416, (code -> code.charAt(CD_BT) == '3')),
                new Criterion(587, 434, 585, 437, (code -> code.charAt(CD_BT) > '3'))
            },
            CLS_EASY1);
        vCards[2] = new Verifier(
            3,
            new Criterion[] {
                new Criterion(631, 387, 629, 390, (code -> code.charAt(CD_YS) < '3')),
                new Criterion(564, 462, 562, 464, (code -> code.charAt(CD_YS) == '3')),
                new Criterion(664, 346, 662, 348, (code -> code.charAt(CD_YS) > '3'))
            },
            CLS_EASY1);
        vCards[3] = new Verifier(
            4,
            new Criterion[] {
                new Criterion(338, 670, 335, 677, (code -> code.charAt(CD_YS) < '4')),
                new Criterion(577, 447, 573, 453, (code -> code.charAt(CD_YS) == '4')),
                new Criterion(327, 688, 324, 691, (code -> code.charAt(CD_YS) > '4'))
            },
            CLS_EASY1);
        vCards[4] = new Verifier(
            5,
            new Criterion[] {
                new Criterion(252, 758, 247, 763, (code -> code.charAt(CD_BT) == '2' || code.charAt(CD_BT) == '4')),
                new Criterion(445, 578, 441, 580, (code -> code.charAt(CD_BT) == '1' || code.charAt(CD_BT) == '3' || code.charAt(CD_BT) == '5')),
            },
            CLS_EASY2);
        vCards[5] = new Verifier(
            6,
            new Criterion[] {
                new Criterion(543, 481, 540, 483, (code -> code.charAt(CD_YS) == '2' || code.charAt(CD_YS) == '4')),
                new Criterion(490, 532, 486, 534, (code -> code.charAt(CD_YS) == '1' || code.charAt(CD_YS) == '3' || code.charAt(CD_YS) == '5')),
            },
            CLS_EASY2);
        vCards[6] = new Verifier(
            7,
            new Criterion[] {
                //             y    g    p    b
                new Criterion(613, 403, 610, 405, (code -> code.charAt(CD_PC) == '2' || code.charAt(CD_PC) == '4')),
                new Criterion(355, 654, 352, 657, (code -> code.charAt(CD_PC) == '1' || code.charAt(CD_PC) == '3' || code.charAt(CD_PC) == '5')),
            },
            CLS_EASY2);
        vCards[7] = new Verifier(
            8,
            new Criterion[] {
                //             y    g    p    b
                new Criterion(793, 206, 790, 212, (code -> (code.length() - code.replace("1","").length()) == 0)),
                new Criterion(653, 356, 651, 358, (code -> (code.length() - code.replace("1","").length()) == 1)),
                new Criterion(571, 455, 567, 459, (code -> (code.length() - code.replace("1","").length()) == 2)),
                new Criterion(523, 497, 518, 499, (code -> (code.length() - code.replace("1","").length()) == 3))
            },
            CLS_EASY3);
            //------------------------------------------------------
        vCards[8] = new Verifier(
            9,
            new Criterion[] {
                //             y    g    p    b
                new Criterion(xzy, xzy, xzy, xzy, (code -> (code.length() - code.replace("3","").length()) == 0)),
                new Criterion(xzy, xzy, xzy, xzy, (code -> (code.length() - code.replace("3","").length()) == 1)),
                new Criterion(xzy, xzy, xzy, xzy, (code -> (code.length() - code.replace("3","").length()) == 2)),
                new Criterion(xzy, xzy, xzy, xzy, (code -> (code.length() - code.replace("3","").length()) == 3))
            },
            CLS_EASY3);
        vCards[9] = new Verifier(
            10,
            new Criterion[] {
                //             y    g    p    b
                new Criterion(xzy, xzy, xzy, xzy, (code -> (code.length() - code.replace("4","").length()) == 0)),
                new Criterion(xzy, xzy, xzy, xzy, (code -> (code.length() - code.replace("4","").length()) == 1)),
                new Criterion(xzy, xzy, xzy, xzy, (code -> (code.length() - code.replace("4","").length()) == 2)),
                new Criterion(xzy, xzy, xzy, xzy, (code -> (code.length() - code.replace("4","").length()) == 3))
            },
            CLS_EASY3);

        boolean checking = false;
        boolean isValid = true;
        boolean isCorrect = true;
        boolean isLoaded = false;
        int bytesRead = 0;
        byte[] codeData = new byte[3];
        String codeFromFile = null;
        String guess = null;
        int difficulty = 0;
        int verifiers = 4;
        boolean verifiersCountSet = false;

        for (String s: args) {
            if (!checking) {
                if (s.equalsIgnoreCase("easy")) {
                    difficulty = DF_EASY;
                } else if (s.equalsIgnoreCase("medium")) {
                    difficulty = DF_MED;
                } else if (s.equalsIgnoreCase("hard")) {
                    difficulty = DF_HARD;
                } else if (s.equalsIgnoreCase("advanced")) {
                    difficulty = DF_ADV;
                } else if (s.equalsIgnoreCase("expert")) {
                    difficulty = DF_EXP;
                }

                if (s.equalsIgnoreCase("v5")) {
                    verifiersCountSet = true;
                    verifiers = 5;
                } else if (s.equalsIgnoreCase("v6")) {
                    verifiersCountSet = true;
                    verifiers = 6;
                }
            }

            if (checking) {
                if (s.length() == 3) {
                    for (int cx = 0; cx < s.length(); cx++) {
                        if (s.charAt(cx) < '1' || s.charAt(cx) > '5') {
                            System.out.println("Guess must contain only digits 1 thru 5!");
                            isValid = false;
                        }
                    }
                    guess = s;
                } else {
                    System.out.println("Guess length invalid for guess: " + s + ". Codes are 3 digit.");
                }

                if (s.length() == 8) {
                    // load code from file
                    InputStream is = null;
                    try {
                        is = new FileInputStream(s + ".tm");
                        bytesRead = is.read(codeData);
                        if (bytesRead == 3) {
                            codeFromFile = Arrays.toString(codeData);
                            isLoaded = true;
                        }
                        is.close();
                    } catch (Exception e) {
                        System.out.println("File error: " + e);
                    }
                }
            }

            if (checking && isValid && isLoaded) {
                for (int i = 0; i < 3; i++) {
                    if (codeFromFile.charAt(i) != guess.charAt(i)) {
                        isCorrect = false;
                        break;
                    }
                }
                if (isCorrect) {
                    System.out.println("Congratulations, your guess is correct!");
                } else {
                    System.out.println("Your guess is incorrect.");
                }
            }

            if (s.equalsIgnoreCase("check")) {
                checking = true;
            }
        }

        if (!checking) {
            System.out.println("Generating new Turing Machine challenge...");
            System.out.println();

            String code = generateCode();

            challenge = new Verifier[6];
            switch (difficulty) {
                case DF_EASY:
                    // 1 medium, the rest easy
                    generateChallenges(medVerifiers, easyVerifiers, 1, verifiers-1);
                    break;
                case DF_MED:
                    // 2 medium, rest easy
                    generateChallenges(medVerifiers, easyVerifiers, 2, verifiers-2);
                    break;
                case DF_HARD:
                    // 1 hard, rest medium
                    if (!verifiersCountSet) {
                        verifiers = 5;
                    }
                    generateChallenges(hardVerifiers, medVerifiers, 1, verifiers-1);
                    break;
                case DF_ADV:
                    // 2 hard, rest medium
                    if (!verifiersCountSet) {
                        verifiers = 5;
                    }
                    generateChallenges(hardVerifiers, medVerifiers, 2, verifiers-2);
                    break;
                case DF_EXP:
                    // 3 hard, rest medium
                    if (!verifiersCountSet) {
                        verifiers = 6;
                    }
                    generateChallenges(hardVerifiers, medVerifiers, 3, verifiers-3);
                    break;
            }

            String filename = generateFilename();
            System.out.println("Challenge ID: " + filename);
            System.out.println("You will need this ID to verify your result.");
            System.out.println("Run the program again with arguments: 'check' ");
            System.out.println("followed by your guess and the above ID, ");
            System.out.println("all separated with spaces.");

            int n = r.nextInt(4); // choose name type
            for (int v = 0; v < verifiers; v++) {
                System.out.println(challenge[v].getDescription(code, n));
            }

            // save code to file
            OutputStream os;
            try {
                os = new FileOutputStream(filename + ".tm");
                os.write(code.getBytes());
                os.flush();
                os.close();
            } catch (Exception e) {
                System.out.println("File error: " + e);
            }

            // save challenge parameters to file
            OutputStream os2;
            try {
                os2 = new FileOutputStream(filename + ".txt");
                for (int v = 0; v < verifiers; v++) {
                    os2.write((challenge[v].getDescription(code, n) + "\n").getBytes());
                }
                os2.flush();
                os2.close();
            } catch (Exception e) {
                System.out.println("File error: " + e);
            }
        }
    }

    private void generateChallenges(Set<Integer> v1, Set<Integer> v2, int n1, int n2) {
        chosen = r.nextInt(v2.size());
        for (int e = 0; e < n1; e++) {
            challenge[e] = vCards[chosen + firstIndexOfSet]; // first index of easy class (easy1, easy2, etc.)
            chosen = r.nextInt(v2.size());
        }
        chosen = r.nextInt(v1.size());
        for (int d = n1; d < (n1 + n2); d++) {
            challenge[d] = vCards[chosen + firstIndexOfSet];
            chosen = r.nextInt(v1.size());
        }
    }

    private String generateFilename() {
        StringBuilder sb = new StringBuilder(String.valueOf(generateFilenameChar()));
        for (int i = 0; i < 7; i++) {
            sb.append(generateAlphanumeric());
        }
        return sb.toString();
    }

    private char generateFilenameChar() {
        return (char)(r.nextInt(26)+65);
    }

    private char generateAlphanumeric() {
        return (r.nextBoolean() ? generateFilenameChar() : (char)r.nextInt(10));
    }

    private String generateCode() {
        return Stream.of(generateDigit(), generateDigit(), generateDigit()).map(String::valueOf).collect(Collectors.joining());
    }

    private int generateDigit() {
        return r.nextInt(5)+1;
    }
}
