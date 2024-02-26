import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DentalPracticeManagementSystemGUI extends JFrame implements ActionListener {
    private JButton patientButton;
    private JButton appointmentButton;
    private JButton logoutButton;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public DentalPracticeManagementSystemGUI() {
        setLayout(new FlowLayout());
        createLoginScreen();
        setTitle("Patient Management System");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //Code for login
    private void createLoginScreen() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 20, 0);

        ImageIcon logoIcon = new ImageIcon("C:\\Users\\dcocr\\Desktop\\Test\\Images\\dental-clinic-logo-template-dental-care-logo-designs-tooth-teeth-smile-dentist-logo-vector.png");
        JLabel logoLabel = new JLabel(logoIcon);
        loginPanel.add(logoLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        loginPanel.add(titleLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel usernameLabel = new JLabel("Username:");
        loginPanel.add(usernameLabel, gbc);

        gbc.gridx++;
        usernameField = new JTextField(20);
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx++;
        passwordField = new JPasswordField(20);
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        loginPanel.add(loginButton, gbc);

        setContentPane(loginPanel);
    }

    private void createMainMenu() {

        //Main nenu setup
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Main Menu");
        JLabel titleLabel2 = new JLabel("Welcome: "+usernameField.getText());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel2.setForeground(Color.WHITE);
        titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(titleLabel2, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 0, 10, 0);

        //Button to open patient add/delete
        String imagePathpb = "C:\\Users\\dcocr\\Desktop\\Test\\Images\\dentalpatient.png";
        ImageIcon patientIconpb = new ImageIcon(imagePathpb);
        JPanel patientPanelpb = new JPanel(new BorderLayout());
        patientPanelpb.add(new JLabel(patientIconpb, SwingConstants.CENTER), BorderLayout.CENTER);
        patientPanelpb.add(new JLabel("Patients", SwingConstants.CENTER), BorderLayout.SOUTH);
        patientButton = new JButton();
        patientButton.setLayout(new BorderLayout());
        patientButton.add(patientPanelpb, BorderLayout.CENTER);
        patientButton.addActionListener(this);
        patientButton.setBackground(Color.WHITE);
        patientButton.setForeground(Color.BLACK);
        patientButton.setOpaque(true);
        mainPanel.add(patientButton, gbc);

        //Button to open appointment scheduling
        gbc.gridy++;
        String imagePathab = "C:\\Users\\dcocr\\Desktop\\Test\\Images\\appintment.jpg";
        ImageIcon patientIconab = new ImageIcon(imagePathab);
        JPanel patientPanelab = new JPanel(new BorderLayout());
        patientPanelab.add(new JLabel(patientIconab, SwingConstants.CENTER), BorderLayout.CENTER);
        patientPanelab.add(new JLabel("Appointment Scheduling", SwingConstants.CENTER), BorderLayout.SOUTH);
        appointmentButton = new JButton();
        appointmentButton.setLayout(new BorderLayout());
        appointmentButton.add(patientPanelab, BorderLayout.CENTER);
        appointmentButton.addActionListener(this);
        appointmentButton.setBackground(Color.WHITE);
        appointmentButton.setForeground(Color.BLACK);
        appointmentButton.setOpaque(true);
        mainPanel.add(appointmentButton, gbc);

        //logout button
        gbc.gridy++;
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        mainPanel.add(logoutButton, gbc);

        setContentPane(mainPanel);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            if (DentalPracticeManagementSystem.validateLogin(usernameField.getText(), passwordField.getPassword())) {
                createMainMenu();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == patientButton) {
            DentalPracticeManagementSystem.createPatientDialog();
        } else if (e.getSource() == appointmentButton) {
            DentalPracticeManagementSystem.createAppointmentDialog();
        } else if (e.getSource() == logoutButton) {
            // Close all existing windows
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window instanceof JFrame) {
                    ((JFrame) window).dispose();
                }
            }
            // Set up the login screen in the new JFrame
            createLoginScreen();
            pack();
            setVisible(true);
            setSize(400, 450);
            
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DentalPracticeManagementSystemGUI());
    }
}

