package src;
//Created by Deontae Cocroft
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//This file has all of the code for the login screen and main menu

public class DPMSGui extends JFrame implements ActionListener {
    private JButton PWButton;
    private JButton AWButton;
    private JButton LogoutButton;
    private JTextField UsernameField;
    private JPasswordField PasswordField;
    private JButton LoginButton;
    private JButton BillButton;
    private JButton PrescriptionButton;
    private static JDialog LoadingScreen;
    public static int count = 0;
    
    public DPMSGui() {
        setLayout(new FlowLayout());
        LoginScreen();
        setTitle("Dental Practice Management System");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        ImageIcon LogoIcon = new ImageIcon("Images\\dental-clinic-logo-template-dental-care-logo-designs-tooth-teeth-smile-dentist-logo-vector.png"); 
        setIconImage(LogoIcon.getImage());
    }

    //Loading screen
    public static void SetLoadingScreen(ActionListener actionListener) {
        
            SwingUtilities.invokeLater(() -> {
                    LoadingScreen = new JDialog();
                    LoadingScreen.setAlwaysOnTop(true);
                    ImageIcon LogoIcon = new ImageIcon("Images\\dental-clinic-logo-template-dental-care-logo-designs-tooth-teeth-smile-dentist-logo-vector.png");
                    JLabel loadingLabel = new JLabel("Please wait...", SwingConstants.CENTER);
                    LoadingScreen.add(loadingLabel);
                    LoadingScreen.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    LoadingScreen.setSize(200, 100);
                    LoadingScreen.setLocationRelativeTo(null);
                    LoadingScreen.setVisible(true);
                    count = 0;
                    LoadingScreen.setIconImage(LogoIcon.getImage());

                    
            });
    }

    //Close the loading screen
    public static void closeLoadingScreen() {
        if (LoadingScreen != null) {
            LoadingScreen.dispose();
            LoadingScreen = null;
            count = 1;
        }
    }

    //login screen
    private void LoginScreen() {
        
        JPanel MainPanelLS = new JPanel(new GridBagLayout());
        MainPanelLS.setBackground(Color.LIGHT_GRAY);

        GridBagConstraints GBC = new GridBagConstraints();
        GBC.gridx = 0;
        GBC.gridy = 0;
        GBC.gridwidth = 2;
        GBC.insets = new Insets(10, 0, 20, 0);

        ImageIcon LogoIcon = new ImageIcon("Images\\dental-clinic-logo-template-dental-care-logo-designs-tooth-teeth-smile-dentist-logo-vector.png"); 
        JLabel LogoLabel = new JLabel(LogoIcon);
        MainPanelLS.add(LogoLabel, GBC);

        GBC.gridy++;
        GBC.gridwidth = 2;
        GBC.anchor = GridBagConstraints.CENTER;

        JLabel TitleLabelLS = new JLabel("Login");
        TitleLabelLS.setFont(new Font("Arial", Font.BOLD, 24));
        TitleLabelLS.setForeground(Color.BLACK);
        MainPanelLS.add(TitleLabelLS, GBC);

        GBC.anchor = GridBagConstraints.WEST;
        GBC.gridwidth = 1;
        GBC.gridy++;
        GBC.insets = new Insets(5, 10, 5, 10);

        JLabel UsernameLabel = new JLabel("Username:");
        MainPanelLS.add(UsernameLabel, GBC);
        UsernameLabel.setForeground(Color.BLACK);

        GBC.gridx++;
        UsernameField = new JTextField(20);
        UsernameLabel.setForeground(Color.BLACK);
        MainPanelLS.add(UsernameField, GBC);

        GBC.gridx = 0;
        GBC.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        MainPanelLS.add(passwordLabel, GBC);
        passwordLabel.setForeground(Color.BLACK);
        passwordLabel.setForeground(Color.BLACK);

        GBC.gridx++;
        PasswordField = new JPasswordField(20);
        MainPanelLS.add(PasswordField, GBC);

        GBC.gridx = 0;
        GBC.gridy++;
        GBC.gridwidth = 2;
        GBC.anchor = GridBagConstraints.CENTER;

        LoginButton = new JButton("Login");
        LoginButton.addActionListener(this);
        LoginButton.setBackground(Color.BLUE);
        LoginButton.setForeground(Color.WHITE);
        MainPanelLS.add(LoginButton, GBC);

        setContentPane(MainPanelLS);
    }

    //Main menu setup
    private void MainMenu() {

        //Main menu title and setup
        JPanel MainPanelMM = new JPanel(new GridBagLayout());
        MainPanelMM.setBackground(Color.LIGHT_GRAY);

        GridBagConstraints GBC = new GridBagConstraints();
        GBC.gridx = 0;
        GBC.gridy = 0;
        GBC.gridwidth = 2;
        GBC.anchor = GridBagConstraints.CENTER;

        Dimension buttonPanelSize = new Dimension(210, 200);
        JLabel TitleLabelMM = new JLabel("Main Menu");
        TitleLabelMM.setForeground(Color.BLACK);
        JLabel TitleLabelMM2 = new JLabel("Welcome: "+ UsernameField.getText());
        TitleLabelMM2.setForeground(Color.BLACK);
        TitleLabelMM.setFont(new Font("Arial", Font.BOLD, 24));
        TitleLabelMM.setForeground(Color.BLACK);
        TitleLabelMM2.setForeground(Color.BLACK);
        TitleLabelMM.setHorizontalAlignment(SwingConstants.CENTER);
        TitleLabelMM2.setHorizontalAlignment(SwingConstants.CENTER);

        MainPanelMM.add(TitleLabelMM, GBC);
        GBC.gridy++;
        MainPanelMM.add(TitleLabelMM2, GBC);
        GBC.gridwidth = 1;
        GBC.gridy++;
        GBC.insets = new Insets(10, 10, 10, 0);

        //Button to open patient window
        String ImagePathPW = "Images\\dentalpatient.png";
        ImageIcon PatientIconPW = new ImageIcon(ImagePathPW);
        JPanel PatientPanelMM = new JPanel(new BorderLayout());
        PatientPanelMM.setPreferredSize(buttonPanelSize);
        PatientPanelMM.add(new JLabel(PatientIconPW, SwingConstants.CENTER), BorderLayout.CENTER);
        PatientPanelMM.add(new JLabel("Patients", SwingConstants.CENTER), BorderLayout.SOUTH);
        PWButton = new JButton();
        PWButton.setLayout(new BorderLayout());
        PWButton.add(PatientPanelMM, BorderLayout.CENTER);
        PWButton.addActionListener(this);
        PWButton.setBackground(Color.WHITE);
        PWButton.setForeground(Color.BLACK);
        PWButton.setOpaque(true);
        MainPanelMM.add(PWButton, GBC);

        //Button to open appointment window
        GBC.gridy++;
        String ImagePathAW = "Images\\appointment.jpg";
        ImageIcon AppointmentIconAW = new ImageIcon(ImagePathAW);
        JPanel AppointmentPanelMM = new JPanel(new BorderLayout());
        AppointmentPanelMM.setPreferredSize(buttonPanelSize);
        AppointmentPanelMM.add(new JLabel(AppointmentIconAW, SwingConstants.CENTER), BorderLayout.CENTER);
        AppointmentPanelMM.add(new JLabel("Appointment Scheduling", SwingConstants.CENTER), BorderLayout.SOUTH);
        AWButton = new JButton();
        AWButton.setLayout(new BorderLayout());
        AWButton.add(AppointmentPanelMM, BorderLayout.CENTER);
        AWButton.addActionListener(this);
        AWButton.setBackground(Color.WHITE);
        AWButton.setForeground(Color.BLACK);
        AWButton.setOpaque(true);
        MainPanelMM.add(AWButton, GBC);

        //Button to open billing
        GBC.gridy++;
        GBC.gridx = 1;
        GBC.gridy = 2;
        String ImagePathBW = "Images\\billing.png";
        ImageIcon BillIconBW = new ImageIcon(ImagePathBW);
        JPanel BillPanelMM = new JPanel(new BorderLayout());
        BillPanelMM.setPreferredSize(buttonPanelSize);
        BillPanelMM.add(new JLabel(BillIconBW, SwingConstants.CENTER), BorderLayout.CENTER);
        BillPanelMM.add(new JLabel("Billing", SwingConstants.CENTER), BorderLayout.SOUTH);
        BillButton = new JButton();
        BillButton.setLayout(new BorderLayout());
        BillButton.add(BillPanelMM, BorderLayout.CENTER);
        BillButton.addActionListener(this);
        BillButton.setBackground(Color.WHITE);
        BillButton.setForeground(Color.BLACK);
        BillButton.setOpaque(true);
        MainPanelMM.add(BillButton, GBC);

        //Butt to open prescription and prescription billing
        GBC.gridy++;
        GBC.gridx = 1;
        GBC.gridy = 3;
        String ImagePathPreW = "Images\\prescription.png";
        ImageIcon PrescriptionIconBW = new ImageIcon(ImagePathPreW);
        JPanel PrescriptionPanelMM = new JPanel(new BorderLayout());
        PrescriptionPanelMM.setPreferredSize(buttonPanelSize);
        PrescriptionPanelMM.add(new JLabel(PrescriptionIconBW, SwingConstants.CENTER), BorderLayout.CENTER);
        PrescriptionPanelMM.add(new JLabel("Prescription and Prescription Billing", SwingConstants.CENTER), BorderLayout.SOUTH);
        PrescriptionButton = new JButton();
        PrescriptionButton.setLayout(new BorderLayout());
        PrescriptionButton.add(PrescriptionPanelMM, BorderLayout.CENTER);
        PrescriptionButton.addActionListener(this);
        PrescriptionButton.setBackground(Color.WHITE);
        PrescriptionButton.setForeground(Color.BLACK);
        PrescriptionButton.setOpaque(true);
        MainPanelMM.add(PrescriptionButton, GBC);

        //logout button
        GBC.gridy++;
        GBC.weighty = 1;  
        GBC.gridx = 0;      
        GBC.gridwidth = 2;  

        GBC.gridy++;
        GBC.weighty = 0;

        LogoutButton = new JButton("Logout");
        LogoutButton.addActionListener(this);
        MainPanelMM.add(LogoutButton, GBC);
        LogoutButton.setBackground(Color.BLUE);
        LogoutButton.setForeground(Color.WHITE);

        setContentPane(MainPanelMM);
        pack();
    }

    //Checks for correct username and password for login.
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == LoginButton) {
            if (DPMSPatient_Window.ValidateLogin(UsernameField.getText(), PasswordField.getPassword())) {
                MainMenu();
                setSize(550, 600);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }

    //Show loading screen and open patient window
    } else if (e.getSource() == PWButton) {
        if (LoadingScreen == null){
            SetLoadingScreen(this);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    DPMSPatient_Window.PatientWindow();
                    return null;
                }

                @Override
                protected void done() {
                    closeLoadingScreen();
                }
            };
            worker.execute();
        }
    
    //Show loading screen and open appointment window
    } else if (e.getSource() == AWButton) {
        if (LoadingScreen == null){
            SetLoadingScreen(this);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    DPMSAppointment_Window.AppointmentWindow();
                    return null;
                }
                @Override
                protected void done() {
                    closeLoadingScreen();
                }
            };
            worker.execute();
        }
        
    //Show loading screen and open bill window
    } else if (e.getSource() == BillButton) {
        if (LoadingScreen == null){
            SetLoadingScreen(this);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    DPMSBilling_Window.BillWindow();
                    return null;
                }

                @Override
                protected void done() {
                    closeLoadingScreen();
                }
            };
            worker.execute();
         }

        //Show loading screen and open bill window
        } else if (e.getSource() == PrescriptionButton) {
            if (LoadingScreen == null){
                SetLoadingScreen(this);
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        DPMSPrescription_Window.PrescriptionWindow();
                        return null;
                    }
    
                    @Override
                    protected void done() {
                        closeLoadingScreen();
                    }
                };
                worker.execute();
             }

    // Close all existing windows
    } else if (e.getSource() == LogoutButton) {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JFrame) {
                ((JFrame) window).dispose();
            }
        }
        // Set up the login screen in the new JFrame
        LoginScreen();
        pack();
        setVisible(true);
        setSize(400, 450);
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DPMSGui());
    }
}