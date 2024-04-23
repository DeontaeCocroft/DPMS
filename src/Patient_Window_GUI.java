package src;
//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

// GUI for patient window
public class Patient_Window_GUI {

    public static void PatientWindow() {
        JFrame PatientWindow = new JFrame("Patients");

        // Make sure the Patient window doesn't appear behind main menu due to loading screen
        PatientWindow.setAlwaysOnTop(!PatientWindow.isAlwaysOnTop());
        PatientWindow.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                Timer timer = new Timer(250, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        PatientWindow.setAlwaysOnTop(false);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        // Patient Window Icon
        ImageIcon LogoIcon = new ImageIcon("Images/dentalpatient.png");
        PatientWindow.setIconImage(LogoIcon.getImage());

        // Size of window
        PatientWindow.setSize(1700, 600);
        PatientWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        PatientWindow.setLocationRelativeTo(null);

        JPanel MainPanelPW = new JPanel(new BorderLayout());

        // Table to display patient information
        DefaultTableModel TableModelPI = new DefaultTableModel();
        TableModelPI.addColumn("Patient ID");
        TableModelPI.addColumn("First Name");
        TableModelPI.addColumn("Last Name");
        TableModelPI.addColumn("DOB");
        TableModelPI.addColumn("Gender");
        TableModelPI.addColumn("Address");
        TableModelPI.addColumn("City");
        TableModelPI.addColumn("State");
        TableModelPI.addColumn("Zip Code");
        TableModelPI.addColumn("Insurance");
        TableModelPI.addColumn("Insurance Number");
        TableModelPI.addColumn("Phone Number");
        TableModelPI.addColumn("Xray Images");

        // Populate table with data from database
   
        Patient_Window.PopulatePatientTable(TableModelPI, PatientWindow);
        
        //Allows Xray Image column to be a clickable link

        JTable PatientTable = new JTable(TableModelPI) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 12 ? URI.class : super.getColumnClass(columnIndex);
            }
        };

        PatientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = PatientTable.rowAtPoint(e.getPoint());
                int col = PatientTable.columnAtPoint(e.getPoint());
                if (col == 12 && e.getClickCount() == 1) { 
                    try {
                        URI uri = new URI((String) PatientTable.getValueAt(row, col));
                        Desktop.getDesktop().browse(uri);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(PatientWindow, "Error opening Xray Images link.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JScrollPane ScrollPane = new JScrollPane(PatientTable);
        MainPanelPW.add(ScrollPane, BorderLayout.CENTER);

        // Fields to enter patient info to database
        JPanel PatientFieldsPanel = new JPanel(new BorderLayout());
        JPanel PatientFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 10);

        String[] Labels = {"*First Name (For Search or Save):", "*Last Name (For Search or Save):", "*DOB (YYYY-MM-DD):", "*Gender:", "*Address:", "*City:", "*State:",
                "*Zip Code:", "*Insurance:", "*Insurance Number:", "*Phone Number: ", "Xray Images Link: ", "Patient ID (For Search, Update, or Delete):"};
        JTextField[] Fields = new JTextField[Labels.length];

        for (int i = 0; i < Labels.length; i++) {
            JLabel label = new JLabel(Labels[i]);
            JTextField field = new JTextField();
            field.setPreferredSize(new Dimension(150, 30));
            gbc.gridx = 0;
            gbc.gridy = i;
            PatientFields.add(label, gbc);
            gbc.gridx = 1;
            PatientFields.add(field, gbc);
            Fields[i] = field;
        }

        PatientFieldsPanel.add(PatientFields, BorderLayout.NORTH);
        MainPanelPW.add(PatientFieldsPanel, BorderLayout.WEST);

        // Patient window buttons
        JPanel ButtonPanelPW = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Button to save patient info into database
        JButton SaveButtonPW = new JButton("Save");
        SaveButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Patient_Window.SavePatientInfo(Fields, TableModelPI, PatientWindow);
            }
        });
        ButtonPanelPW.add(SaveButtonPW);
        SaveButtonPW.setBackground(Color.BLUE);
        SaveButtonPW.setForeground(Color.WHITE);
        SaveButtonPW.setBorderPainted(false);
        SaveButtonPW.setOpaque(true);

        // Button to clear patient info fields
        JButton ClearButtonPW = new JButton("Clear Fields");
        ClearButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Patient_Window.ClearFields(Fields);
            }
        });
        ButtonPanelPW.add(ClearButtonPW);
        ClearButtonPW.setBackground(Color.BLUE);
        ClearButtonPW.setForeground(Color.WHITE);
        ClearButtonPW.setBorderPainted(false);
        ClearButtonPW.setOpaque(true);

        // Button to update patient information
        JButton UpdateButtonPW = new JButton("Update");
        UpdateButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // We get the Patient ID from the Fields array, index 10, assuming this holds the Patient ID
                String patientID = Fields[12].getText();
                if (patientID != null && !patientID.isEmpty()) {
                    Patient_Window.UpdatePatientInfo(Fields, TableModelPI, PatientWindow, patientID);
                } else {
                    JOptionPane.showMessageDialog(PatientWindow, "Please enter a valid Patient ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        ButtonPanelPW.add(UpdateButtonPW);
        UpdateButtonPW.setBackground(Color.BLUE);
        UpdateButtonPW.setForeground(Color.WHITE);
        UpdateButtonPW.setBorderPainted(false);
        UpdateButtonPW.setOpaque(true);

        // Button to delete patients
        JButton DeleteButtonPW = new JButton("Delete");
        DeleteButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Patient_Window.DeletePatient(Fields[12].getText(), TableModelPI, Fields, PatientWindow);
            }
        });
        ButtonPanelPW.add(DeleteButtonPW);
        DeleteButtonPW.setBackground(Color.BLUE);
        DeleteButtonPW.setForeground(Color.WHITE);
        DeleteButtonPW.setBorderPainted(false);
        DeleteButtonPW.setOpaque(true);

        // Button that searches patients
        JButton SearchButtonPW = new JButton("Search");
        SearchButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Patient_Window.SearchPatients(TableModelPI, Fields, PatientWindow);
            }
        });
        ButtonPanelPW.add(SearchButtonPW);
        SearchButtonPW.setBackground(Color.BLUE);
        SearchButtonPW.setForeground(Color.WHITE);
        SearchButtonPW.setBorderPainted(false);
        SearchButtonPW.setOpaque(true);

        // Button that displays patient window help
        JButton HelpButtonPW = new JButton("Help");
        HelpButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Patient_Window.HelpPatient(PatientWindow, PatientWindow);
            }
        });
        ButtonPanelPW.add(HelpButtonPW);
        HelpButtonPW.setBackground(Color.BLUE);
        HelpButtonPW.setForeground(Color.WHITE);
        HelpButtonPW.setBorderPainted(false);
        HelpButtonPW.setOpaque(true);

        MainPanelPW.add(ButtonPanelPW, BorderLayout.SOUTH);
        PatientWindow.add(MainPanelPW);
        PatientWindow.setVisible(true);
        PatientTable.setDefaultEditor(Object.class, null);

    }
}