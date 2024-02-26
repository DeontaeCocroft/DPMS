//Created by Deontae Cocroft
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DentalPracticeManagementSystemGUI extends JFrame implements ActionListener {
    private JButton PWButton;
    private JButton AWButton;
    private JButton LogoutButton;
    private JTextField UsernameField;
    private JPasswordField PasswordField;
    private JButton LoginButton;

    public DentalPracticeManagementSystemGUI() {
        setLayout(new FlowLayout());
        LoginScreen();
        setTitle("Patient Management System");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //Code for login
    private void LoginScreen() {
        JPanel MainPanelLS = new JPanel(new GridBagLayout());
        MainPanelLS.setBackground(Color.WHITE);

        GridBagConstraints GBC = new GridBagConstraints();
        GBC.gridx = 0;
        GBC.gridy = 0;
        GBC.gridwidth = 2;
        GBC.insets = new Insets(10, 0, 20, 0);

        ImageIcon LogoIcon = new ImageIcon("C:\\Users\\dcocr\\Desktop\\DPMS\\Images\\dental-clinic-logo-template-dental-care-logo-designs-tooth-teeth-smile-dentist-logo-vector.png");
        JLabel LogoLabel = new JLabel(LogoIcon);
        MainPanelLS.add(LogoLabel, GBC);

        GBC.gridy++;
        GBC.gridwidth = 2;
        GBC.anchor = GridBagConstraints.CENTER;

        JLabel TitleLabelLS = new JLabel("Login");
        TitleLabelLS.setFont(new Font("Arial", Font.BOLD, 24));
        MainPanelLS.add(TitleLabelLS, GBC);

        GBC.anchor = GridBagConstraints.WEST;
        GBC.gridwidth = 1;
        GBC.gridy++;
        GBC.insets = new Insets(5, 10, 5, 10);

        JLabel UsernameLabel = new JLabel("Username:");
        MainPanelLS.add(UsernameLabel, GBC);

        GBC.gridx++;
        UsernameField = new JTextField(20);
        MainPanelLS.add(UsernameField, GBC);

        GBC.gridx = 0;
        GBC.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        MainPanelLS.add(passwordLabel, GBC);

        GBC.gridx++;
        PasswordField = new JPasswordField(20);
        MainPanelLS.add(PasswordField, GBC);

        GBC.gridx = 0;
        GBC.gridy++;
        GBC.gridwidth = 2;
        GBC.anchor = GridBagConstraints.CENTER;

        LoginButton = new JButton("Login");
        LoginButton.addActionListener(this);
        MainPanelLS.add(LoginButton, GBC);

        setContentPane(MainPanelLS);
    }

    private void MainMenu() {

        //Main nenu setup
        JPanel MainPanelMM = new JPanel(new GridBagLayout());
        MainPanelMM.setBackground(Color.BLACK);

        GridBagConstraints GBC = new GridBagConstraints();
        GBC.gridx = 0;
        GBC.gridy = 0;
        GBC.gridwidth = 2;
        GBC.anchor = GridBagConstraints.CENTER;

        JLabel TitleLabelMM = new JLabel("Main Menu");
        JLabel TitleLabelMM2 = new JLabel("Welcome: "+ UsernameField.getText());
        TitleLabelMM.setFont(new Font("Arial", Font.BOLD, 24));
        TitleLabelMM.setForeground(Color.WHITE);
        TitleLabelMM2.setForeground(Color.WHITE);
        TitleLabelMM.setHorizontalTextPosition(SwingConstants.CENTER);
        TitleLabelMM.setHorizontalAlignment(SwingConstants.CENTER);
        MainPanelMM.add(TitleLabelMM, GBC);

        GBC.gridy++;
        GBC.gridwidth = 1;
        MainPanelMM.add(TitleLabelMM2, GBC);
        GBC.gridy++;
        GBC.gridwidth = 1;
        GBC.insets = new Insets(10, 0, 10, 0);

        //Button to open patient window
        String ImagePathPW = "C:\\Users\\dcocr\\Desktop\\DPMS\\Images\\dentalpatient.png";
        ImageIcon PatientIconPW = new ImageIcon(ImagePathPW);
        JPanel PatientPanelMM = new JPanel(new BorderLayout());
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
        String ImagePathAW = "C:\\Users\\dcocr\\Desktop\\DPMS\\Images\\appintment.jpg";
        ImageIcon AppointmentIconAW = new ImageIcon(ImagePathAW);
        JPanel AppointmentPanelMM = new JPanel(new BorderLayout());
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

        //logout button
        GBC.gridy++;
        LogoutButton = new JButton("Logout");
        LogoutButton.addActionListener(this);
        MainPanelMM.add(LogoutButton, GBC);

        setContentPane(MainPanelMM);
        pack();
    }

    @Override
    //Code that triggers login and menu buttons
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == LoginButton) {
            if (DentalPracticeManagementSystem.ValidateLogin(UsernameField.getText(), PasswordField.getPassword())) {
                MainMenu();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == PWButton) {
            DentalPracticeManagementSystem.PatientWindow();
        } else if (e.getSource() == AWButton) {
            DentalPracticeManagementSystem.AppointmentWindow();
        } else if (e.getSource() == LogoutButton) {
            // Close all existing windows
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
        SwingUtilities.invokeLater(() -> new DentalPracticeManagementSystemGUI());
    }
}

