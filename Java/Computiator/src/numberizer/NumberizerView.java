/*
 * NumberizerView.java
 */

package numberizer;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.Timer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * The application's main frame.
 */
public class NumberizerView extends FrameView {

    private final int PRIME = 0;
    private final int MULT = 1;
    
    private final String errorMsg = "You have entered a number that is either "
            + "too large for this program to handle\naccurately, or that is " +
            "invalid. Please choose a smaller, valid number.";
    
    private String coprime_nums;
    private int curr_num = 0;
    private int num_factors = 0;
    private int curr_factors[][];
    
    private boolean first_run = true;
    
    private Timer txtTimer;
    
    public NumberizerView(SingleFrameApplication app) {
        super(app);

        initComponents();
        
        numberBox.getDocument().addDocumentListener(new NDocumentListener());
        numberBox.addMouseWheelListener(new WheelIncrementListener());
        gcd_a.getDocument().addDocumentListener(new MDocumentListener());
        mod.getDocument().addDocumentListener(new ModListener());
        coprime_nums = "";
        
        txtTimer = new Timer(100, new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                numberBox.setText("");
                txtTimer.stop();
            }
        });        
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = NumberizerApp.getApplication().getMainFrame();
            aboutBox = new NumberizerAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        NumberizerApp.getApplication().show(aboutBox);
    }

    private int[][] primefactor(int n)
    {        
        int factors[][] = new int[7][2];        
        int curr = 0;
        int m = n;
        
        boolean first_coprime = true;
        
        if (n < 2)
        {
            int f[][] = {{0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}};
            return f;
        }
        
        for (int i = 2; i <= n; i++)
        {
            if (m % i == 0)
            {
                m /= i;
                factors[curr][PRIME] = i;
                if (m % i == 0)
                {
                    for (int j = 1; j <= n; j++)
                    {
                        if (m % i == 0 && m > 1)
                            m /= i;
                        else
                        {
                            factors[curr++][MULT] = j;
                            break;
                        }
                    }
                }
                else
                {
                    factors[curr++][MULT] = 1;
                }
            }
        }
        
        num_factors = curr;
        
        for (int i = 2; i < n; i++)
        {
            if (gcd(i, n) == 1)
            {
                if (first_coprime)
                {
                    first_coprime = false;
                    coprime_nums = "1, " + String.valueOf(i);
                }
                else
                    coprime_nums += ", " + i;
            }
            
            if (i > 20)
            {
                coprime_nums += "...";
                break;
            }
        }        
        
        return factors;
    }
    
    private void clearData(boolean emptyNumbox)
    {
        numberClass.setText("-");
        prime1.setText("-");
        prime2.setText("-");
        prime3.setText("-");
        prime4.setText("-");
        prime5.setText("-");
        prime6.setText("-");
        prime7.setText("-");
        mult1.setText("-");
        mult2.setText("-");
        mult3.setText("-");
        mult4.setText("-");
        mult5.setText("-");
        mult6.setText("-");
        mult7.setText("-");
        coprimes.setText("");
        sigma.setText("-");
        phi.setText("-");
        tau.setText("-");
        gcd_a.setText("");
        leastRes.setText("-");
        congruence.setText("");
        if (emptyNumbox)
        {
            if (first_run)
            {
                first_run = false;
                txtTimer.start();
            }
            else
                txtTimer.restart();
        }
    }
    
    private void analyze(int n)
    {
        int ph_val = 0;
        int s_val = 0;
        int t_val = 0;
        
        if (n < 2)
        {
            clearData(false);
            
            if (n == 0)
            {
                numberClass.setText("Zero");
            }
            if (n == 1)
            {
                numberClass.setText("One");
            }
            
            return;
        }
        
        curr_num = n;
        curr_factors = primefactor(curr_num);
        if (num_factors > 7 || numberBox.getText().length() > 6)
        {
            JOptionPane.showMessageDialog(null,
                    "You have entered a number that is too large or has " +
                    "too many prime factors\nfor this program to handle " +
                    "accurately. Please choose a smaller number.",
                    "Data Overflow",
                    JOptionPane.ERROR_MESSAGE);
            clearData(true);
            return;
        }
        
        clearData(false);
        
        ph_val = func_phi(curr_factors[0][PRIME], curr_factors[0][MULT]);
        s_val = func_sigma(curr_factors[0][PRIME], curr_factors[0][MULT]);
        t_val = func_tau(curr_factors[0][MULT]);
        
        for (int i = 1; i < num_factors; i++)
        {
            ph_val *= func_phi(curr_factors[i][PRIME], curr_factors[i][MULT]);
            s_val *= func_sigma(curr_factors[i][PRIME], curr_factors[i][MULT]);
            t_val *= func_tau(curr_factors[i][MULT]);
        }
        
        for (int i = num_factors; i < 7; i++)
        {
            curr_factors[i][PRIME] = 0;
            curr_factors[i][MULT] = 0;
        }
        
        if (s_val > 2 * curr_num)
        {
            numberClass.setText("Abundant");
        }
        if (s_val < 2 * curr_num)
        {
            numberClass.setText("Deficient");
        }
        if (s_val == 2 * curr_num)
        {
            numberClass.setText("Perfect");
        }
        
        if (t_val == 2)
        {
            numberClass.setText("Prime");
            coprimes.setText("All Lesser Positive Integers");
        }
        
        phi.setText(String.valueOf(ph_val));
        sigma.setText(String.valueOf(s_val));
        tau.setText(String.valueOf(t_val));
        
        int lower = (int)Math.floor(Math.log(curr_num)/Math.log(2));
        int upper = (int)Math.floor(Math.log(curr_num + 2)/Math.log(2));
        
        for (int k = lower; k <= upper; k++)
        {
            if (Math.pow(2, k) - 1 == curr_num)
            {
                numberClass.setText("Mersenne Prime");
                break;
            }
        }
        
        if (t_val == 2)
            return;
        
        
        for (int i = 0; i < 7; i++)
        {
            if (curr_factors[i][PRIME] > 1)
            {
                switch (i)
                {
                    case 0:
                        prime1.setText(String.valueOf(curr_factors[i][PRIME]));
                        mult1.setText(String.valueOf(curr_factors[i][MULT]));
                        break;
                    case 1:
                        prime2.setText(String.valueOf(curr_factors[i][PRIME]));
                        mult2.setText(String.valueOf(curr_factors[i][MULT]));
                        break;
                    case 2:
                        prime3.setText(String.valueOf(curr_factors[i][PRIME]));
                        mult3.setText(String.valueOf(curr_factors[i][MULT]));
                        break;
                    case 3:
                        prime4.setText(String.valueOf(curr_factors[i][PRIME]));
                        mult4.setText(String.valueOf(curr_factors[i][MULT]));
                        break;
                    case 4:
                        prime5.setText(String.valueOf(curr_factors[i][PRIME]));
                        mult5.setText(String.valueOf(curr_factors[i][MULT]));
                        break;
                    case 5:
                        prime6.setText(String.valueOf(curr_factors[i][PRIME]));
                        mult6.setText(String.valueOf(curr_factors[i][MULT]));
                        break;
                    case 6:
                        prime7.setText(String.valueOf(curr_factors[i][PRIME]));
                        mult7.setText(String.valueOf(curr_factors[i][MULT]));
                }
            }
            else
            {
                switch (i)
                {
                    case 1:
                        prime2.setText("-");
                        mult2.setText("-");
                        break;
                    case 2:
                        prime3.setText("-");
                        mult3.setText("-");
                        break;
                    case 3:
                        prime4.setText("-");
                        mult4.setText("-");
                        break;
                    case 4:
                        prime5.setText("-");
                        mult5.setText("-");
                        break;
                    case 5:
                        prime6.setText("-");
                        mult6.setText("-");
                        break;
                    case 6:
                        prime7.setText("-");
                        mult7.setText("-");
                }
            }
        }
        
        coprimes.setText(coprime_nums);
    }
    
    private int func_phi(int p, int m)
    {
        return ((int)Math.pow(p, m) - (int)Math.pow(p, m - 1));
    }
    
    private int func_sigma(int p, int m)
    {
        return ((int)Math.pow(p, m + 1) - 1)/(p - 1);
    }
    
    private int func_tau(int m)
    {
        return (m + 1);
    }
    
    private int gcd(int a, int b)
    {
        if (b == 0)
            return a;
        else
            return gcd(b, a % b);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated  by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        numberClass = new javax.swing.JTextField();
        tau = new javax.swing.JTextField();
        phi = new javax.swing.JTextField();
        sigma = new javax.swing.JTextField();
        prime1 = new javax.swing.JTextField();
        prime2 = new javax.swing.JTextField();
        prime3 = new javax.swing.JTextField();
        prime4 = new javax.swing.JTextField();
        prime5 = new javax.swing.JTextField();
        prime6 = new javax.swing.JTextField();
        prime7 = new javax.swing.JTextField();
        mult1 = new javax.swing.JTextField();
        mult2 = new javax.swing.JTextField();
        mult3 = new javax.swing.JTextField();
        mult4 = new javax.swing.JTextField();
        mult5 = new javax.swing.JTextField();
        mult6 = new javax.swing.JTextField();
        mult7 = new javax.swing.JTextField();
        coprimes = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        gcd_a = new javax.swing.JTextField();
        gcd = new javax.swing.JTextField();
        numberBox = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        mod = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        leastRes = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        congruence = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(numberizer.NumberizerApp.class).getContext().getResourceMap(NumberizerView.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setAlignmentX(0.5F);
        jLabel4.setFocusable(false);
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        numberClass.setBackground(resourceMap.getColor("numberClass.background")); // NOI18N
        numberClass.setEditable(false);
        numberClass.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numberClass.setText(resourceMap.getString("numberClass.text")); // NOI18N
        numberClass.setFocusable(false);
        numberClass.setName("numberClass"); // NOI18N

        tau.setBackground(resourceMap.getColor("tau.background")); // NOI18N
        tau.setEditable(false);
        tau.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tau.setText(resourceMap.getString("tau.text")); // NOI18N
        tau.setFocusable(false);
        tau.setMaximumSize(new java.awt.Dimension(28, 28));
        tau.setName("tau"); // NOI18N

        phi.setBackground(resourceMap.getColor("phi.background")); // NOI18N
        phi.setEditable(false);
        phi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        phi.setText(resourceMap.getString("phi.text")); // NOI18N
        phi.setFocusable(false);
        phi.setName("phi"); // NOI18N

        sigma.setBackground(resourceMap.getColor("sigma.background")); // NOI18N
        sigma.setEditable(false);
        sigma.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sigma.setText(resourceMap.getString("sigma.text")); // NOI18N
        sigma.setFocusable(false);
        sigma.setName("sigma"); // NOI18N

        prime1.setBackground(resourceMap.getColor("prime4.background")); // NOI18N
        prime1.setEditable(false);
        prime1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prime1.setText(resourceMap.getString("prime4.text")); // NOI18N
        prime1.setFocusable(false);
        prime1.setMinimumSize(new java.awt.Dimension(18, 20));
        prime1.setName("prime1"); // NOI18N
        prime1.setPreferredSize(new java.awt.Dimension(18, 20));

        prime2.setBackground(resourceMap.getColor("prime4.background")); // NOI18N
        prime2.setEditable(false);
        prime2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prime2.setText(resourceMap.getString("prime4.text")); // NOI18N
        prime2.setFocusable(false);
        prime2.setMinimumSize(new java.awt.Dimension(18, 20));
        prime2.setName("prime2"); // NOI18N
        prime2.setPreferredSize(new java.awt.Dimension(18, 20));

        prime3.setBackground(resourceMap.getColor("prime4.background")); // NOI18N
        prime3.setEditable(false);
        prime3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prime3.setText(resourceMap.getString("prime4.text")); // NOI18N
        prime3.setFocusable(false);
        prime3.setMinimumSize(new java.awt.Dimension(18, 20));
        prime3.setName("prime3"); // NOI18N
        prime3.setPreferredSize(new java.awt.Dimension(18, 20));

        prime4.setBackground(resourceMap.getColor("prime4.background")); // NOI18N
        prime4.setEditable(false);
        prime4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prime4.setText(resourceMap.getString("prime4.text")); // NOI18N
        prime4.setFocusable(false);
        prime4.setMinimumSize(new java.awt.Dimension(18, 20));
        prime4.setName("prime4"); // NOI18N
        prime4.setPreferredSize(new java.awt.Dimension(18, 20));

        prime5.setBackground(resourceMap.getColor("prime4.background")); // NOI18N
        prime5.setEditable(false);
        prime5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prime5.setText(resourceMap.getString("prime4.text")); // NOI18N
        prime5.setFocusable(false);
        prime5.setMinimumSize(new java.awt.Dimension(18, 20));
        prime5.setName("prime5"); // NOI18N
        prime5.setPreferredSize(new java.awt.Dimension(18, 20));

        prime6.setBackground(resourceMap.getColor("prime4.background")); // NOI18N
        prime6.setEditable(false);
        prime6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prime6.setText(resourceMap.getString("prime4.text")); // NOI18N
        prime6.setFocusable(false);
        prime6.setMinimumSize(new java.awt.Dimension(18, 20));
        prime6.setName("prime6"); // NOI18N
        prime6.setPreferredSize(new java.awt.Dimension(18, 20));

        prime7.setBackground(resourceMap.getColor("prime4.background")); // NOI18N
        prime7.setEditable(false);
        prime7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        prime7.setText(resourceMap.getString("prime4.text")); // NOI18N
        prime7.setFocusable(false);
        prime7.setMinimumSize(new java.awt.Dimension(18, 20));
        prime7.setName("prime7"); // NOI18N
        prime7.setPreferredSize(new java.awt.Dimension(18, 20));

        mult1.setBackground(resourceMap.getColor("mult1.background")); // NOI18N
        mult1.setEditable(false);
        mult1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mult1.setText(resourceMap.getString("mult1.text")); // NOI18N
        mult1.setFocusable(false);
        mult1.setName("mult1"); // NOI18N

        mult2.setBackground(resourceMap.getColor("mult2.background")); // NOI18N
        mult2.setEditable(false);
        mult2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mult2.setText(resourceMap.getString("mult2.text")); // NOI18N
        mult2.setFocusable(false);
        mult2.setName("mult2"); // NOI18N

        mult3.setBackground(resourceMap.getColor("mult6.background")); // NOI18N
        mult3.setEditable(false);
        mult3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mult3.setText(resourceMap.getString("mult3.text")); // NOI18N
        mult3.setFocusable(false);
        mult3.setName("mult3"); // NOI18N

        mult4.setBackground(resourceMap.getColor("mult6.background")); // NOI18N
        mult4.setEditable(false);
        mult4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mult4.setText(resourceMap.getString("mult4.text")); // NOI18N
        mult4.setFocusable(false);
        mult4.setName("mult4"); // NOI18N

        mult5.setBackground(resourceMap.getColor("mult6.background")); // NOI18N
        mult5.setEditable(false);
        mult5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mult5.setText(resourceMap.getString("mult5.text")); // NOI18N
        mult5.setFocusable(false);
        mult5.setName("mult5"); // NOI18N

        mult6.setBackground(resourceMap.getColor("mult6.background")); // NOI18N
        mult6.setEditable(false);
        mult6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mult6.setText(resourceMap.getString("mult6.text")); // NOI18N
        mult6.setFocusable(false);
        mult6.setName("mult6"); // NOI18N

        mult7.setBackground(resourceMap.getColor("mult7.background")); // NOI18N
        mult7.setEditable(false);
        mult7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mult7.setText(resourceMap.getString("mult7.text")); // NOI18N
        mult7.setFocusable(false);
        mult7.setName("mult7"); // NOI18N

        coprimes.setBackground(resourceMap.getColor("coprimes.background")); // NOI18N
        coprimes.setEditable(false);
        coprimes.setText(resourceMap.getString("coprimes.text")); // NOI18N
        coprimes.setName("coprimes"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        gcd_a.setText(resourceMap.getString("gcd_a.text")); // NOI18N
        gcd_a.setToolTipText(resourceMap.getString("gcd_a.toolTipText")); // NOI18N
        gcd_a.setEnabled(false);
        gcd_a.setName("gcd_a"); // NOI18N

        gcd.setBackground(resourceMap.getColor("gcd.background")); // NOI18N
        gcd.setEditable(false);
        gcd.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gcd.setText(resourceMap.getString("gcd.text")); // NOI18N
        gcd.setFocusable(false);
        gcd.setName("gcd"); // NOI18N

        numberBox.setText(resourceMap.getString("numberBox.text")); // NOI18N
        numberBox.setName("numberBox"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        mod.setText(resourceMap.getString("mod.text")); // NOI18N
        mod.setToolTipText(resourceMap.getString("mod.toolTipText")); // NOI18N
        mod.setEnabled(false);
        mod.setName("mod"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        leastRes.setBackground(resourceMap.getColor("leastRes.background")); // NOI18N
        leastRes.setEditable(false);
        leastRes.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        leastRes.setText(resourceMap.getString("leastRes.text")); // NOI18N
        leastRes.setName("leastRes"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        congruence.setBackground(resourceMap.getColor("congruence.background")); // NOI18N
        congruence.setEditable(false);
        congruence.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        congruence.setText(resourceMap.getString("congruence.text")); // NOI18N
        congruence.setName("congruence"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(congruence, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tau, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sigma, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(phi, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(numberBox, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(numberClass, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(coprimes, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(prime1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(mult1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(prime2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(mult2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                            .addComponent(prime3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(prime4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                            .addComponent(mult3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(mult4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(6, 6, 6)
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                            .addComponent(prime5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(prime6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(prime7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                            .addComponent(mult5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(mult6, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(mult7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addComponent(gcd_a, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                                .addComponent(gcd, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(leastRes, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mod, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5))
                .addGap(406, 406, 406))
        );

        mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {mult1, mult2, mult3, mult4, mult5, mult6, mult7, prime1, prime2, prime3, prime4, prime5, prime6, prime7});

        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(numberClass, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numberBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(sigma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(prime1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prime2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prime3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prime4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prime7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prime5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prime6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(mult2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mult3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mult1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mult4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mult5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mult6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mult7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(coprimes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(gcd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gcd_a, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel11)
                        .addComponent(leastRes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(congruence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        mainPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {mult1, mult2, mult3, mult4, mult5, mult6, mult7, prime1, prime2, prime3, prime4, prime5, prime6, prime7});

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(numberizer.NumberizerApp.class).getContext().getActionMap(NumberizerView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 167, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField congruence;
    private javax.swing.JTextField coprimes;
    private javax.swing.JTextField gcd;
    private javax.swing.JTextField gcd_a;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField leastRes;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTextField mod;
    private javax.swing.JTextField mult1;
    private javax.swing.JTextField mult2;
    private javax.swing.JTextField mult3;
    private javax.swing.JTextField mult4;
    private javax.swing.JTextField mult5;
    private javax.swing.JTextField mult6;
    private javax.swing.JTextField mult7;
    private javax.swing.JTextField numberBox;
    private javax.swing.JTextField numberClass;
    private javax.swing.JTextField phi;
    private javax.swing.JTextField prime1;
    private javax.swing.JTextField prime2;
    private javax.swing.JTextField prime3;
    private javax.swing.JTextField prime4;
    private javax.swing.JTextField prime5;
    private javax.swing.JTextField prime6;
    private javax.swing.JTextField prime7;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField sigma;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextField tau;
    // End of variables declaration//GEN-END:variables

    private JDialog aboutBox;
    
    class NDocumentListener implements DocumentListener
    {
        public void changedUpdate(DocumentEvent de) { }
        
        public void insertUpdate(DocumentEvent de)
        {
            try
            {
                analyze(Integer.valueOf(numberBox.getText()));
                
                if (!gcd_a.isEnabled())
                {
                    gcd_a.setEnabled(true);
                    gcd_a.setText("");
                    gcd_a.setToolTipText(null);
                }
                
                if (!mod.isEnabled())
                {
                    mod.setEnabled(true);
                    mod.setText("");
                    mod.setToolTipText(null);
                }
                
                if (!gcd_a.getText().isEmpty())
                {
                    gcd.setText(String.valueOf(
                        gcd(Integer.valueOf(numberBox.getText()),
                            Integer.valueOf(gcd_a.getText()))
                        ));
                }
                
                if (!mod.getText().isEmpty())
                {
                    int d = Integer.valueOf(mod.getText());
                    String e = "...";
                    String c = ", ";
                    if (d < 1)
                        return;
                    leastRes.setText(String.valueOf(curr_num % d));
                    congruence.setText(e + (-3 * d + curr_num) +
                                       c + (-2 * d + curr_num) +
                                       c + (-1 * d + curr_num) +
                                       c + curr_num +
                                       c + (d + curr_num) +
                                       c + (2 * d + curr_num) +
                                       c + (3 * d + curr_num) + e);
                }
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, errorMsg, "Invalid Number",
                    JOptionPane.ERROR_MESSAGE);
                clearData(false);
            }
        }
        
        public void removeUpdate(DocumentEvent de)
        {
            try
            {
                if (numberBox.getText().length() > 0)
                {
                    analyze(Integer.valueOf(numberBox.getText()));
                    
                    if (!gcd_a.getText().isEmpty())
                    {
                        gcd.setText(String.valueOf(
                            gcd(Integer.valueOf(numberBox.getText()),
                                Integer.valueOf(gcd_a.getText()))
                            ));
                    }
                    
                    if (!mod.getText().isEmpty())
                    {
                        int d = Integer.valueOf(mod.getText());
                        String e = "...";
                        String c = ", ";
                        if (d < 1)
                            return;
                        leastRes.setText(String.valueOf(curr_num % d));
                        congruence.setText(e + (-3 * d + curr_num) +
                                           c + (-2 * d + curr_num) +
                                           c + (-1 * d + curr_num) +
                                           c + curr_num +
                                           c + (d + curr_num) +
                                           c + (2 * d + curr_num) +
                                           c + (3 * d + curr_num) + e);
                    }
                }
                else
                {
                    gcd_a.setText("");
                    gcd_a.setEnabled(false);
                    gcd_a.setToolTipText("You must enter a number above " +
                            "before you can calculate GCD's.");
                    
                    mod.setText("");
                    leastRes.setText("-");
                    congruence.setText("");
                    mod.setEnabled(false);
                    mod.setToolTipText("You must enter a number above " +
                            "before you can calculate congruencies.");
                }
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, errorMsg, "Invalid Number",
                    JOptionPane.ERROR_MESSAGE);
                clearData(false);
            }
        }
    }
    
    class MDocumentListener implements DocumentListener
    {
        public void changedUpdate(DocumentEvent de) { }
        
        public void insertUpdate(DocumentEvent de)
        {
            try
            {
                gcd.setText(String.valueOf(
                        gcd(Integer.valueOf(gcd_a.getText()),
                            Integer.valueOf(numberBox.getText()))
                            ));
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, errorMsg, "Invalid Number",
                    JOptionPane.ERROR_MESSAGE);
                clearData(false);
            }
        }
        
        public void removeUpdate(DocumentEvent de)
        {
            if (gcd_a.getText().isEmpty())
            {
                gcd.setText("-");
                return;
            }
            try
            {
                gcd.setText(String.valueOf(
                        gcd(Integer.valueOf(numberBox.getText()),
                            Integer.valueOf(gcd_a.getText()))
                        ));
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, errorMsg, "Invalid Number",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    class ModListener implements DocumentListener
    {
        public void changedUpdate(DocumentEvent de) { }
        
        public void insertUpdate(DocumentEvent de)
        {
            int d = 0;
            try
            {
                d = Integer.valueOf(mod.getText());
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, errorMsg, "Invalid Number",
                    JOptionPane.ERROR_MESSAGE);
                clearData(false);
                return;
            }
            String e = "...";
            String c = ", ";
            if (d < 1)
                return;
            leastRes.setText(String.valueOf(curr_num % d));
            congruence.setText(e + (-3 * d + curr_num) +
                               c + (-2 * d + curr_num) +
                               c + (-1 * d + curr_num) +
                               c + curr_num +
                               c + (d + curr_num) +
                               c + (2 * d + curr_num) +
                               c + (3 * d + curr_num) + e);
        }
        
        public void removeUpdate(DocumentEvent de)
        {
            if (!mod.getText().isEmpty())
            {
                int d = 0;
                try
                {
                    d = Integer.valueOf(mod.getText());
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(null, errorMsg, "Invalid Number",
                    JOptionPane.ERROR_MESSAGE);
                    clearData(false);
                    return;
                }
                String e = "...";
                String c = ", ";
                if (d < 1)
                    return;
                leastRes.setText(String.valueOf(
                        curr_num % Integer.valueOf(mod.getText())
                        ));
                congruence.setText(e + (-4 * d + curr_num) +
                               c + (-3 * d + curr_num) +
                               c + (-2 * d + curr_num) +
                               c + (-1 * d + curr_num) +
                               c + curr_num +
                               c + (d + curr_num) +
                               c + (2 * d + curr_num) +
                               c + (3 * d + curr_num) +
                               c + (4 * d + curr_num) + e);
            }
            else
            {
                leastRes.setText("-");
                congruence.setText("");
            }
        }
    }
    
    class WheelIncrementListener implements MouseWheelListener
    {
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            if (!numberBox.getText().isEmpty())
            {
                numberBox.setText(String.valueOf(
                    Integer.valueOf(numberBox.getText()) - 
                    e.getWheelRotation())
                    );
            }
        }
    }
}
