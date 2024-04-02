package src;
//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//GUI for patient window
public class Patient_Window_GUI {

    public static void PatientWindow() {
        JFrame PatientWindow = new JFrame("Patients");

        //Make sure the Patient window doesn't appear behind main menu due to loading screen
        PatientWindow.setAlwaysOnTop(!PatientWindow.isAlwaysOnTop());
        PatientWindow.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e){
                Timer timer = new Timer(250, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        PatientWindow.setAlwaysOnTop(false);
                     }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        //Patient Window Icon
        ImageIcon LogoIcon = new ImageIcon("Images\\dentalpatient.png"); 
        PatientWindow.setIconImage(LogoIcon.getImage());

        // Size of window
        PatientWindow.setSize(1650, 600);
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

        JTable PatientTable = new JTable(TableModelPI);

        // Populate table with data from database
        Patient_Window.PopulatePatientTable(TableModelPI, PatientWindow);

        JScrollPane ScrollPane = new JScrollPane(PatientTable);
        MainPanelPW.add(ScrollPane, BorderLayout.CENTER);

        // Fields to all patient info to database
        JPanel PatientFieldsPanel = new JPanel(new BorderLayout());
        JPanel PatientFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 10);

        String[] Labels = {"*First Name (For Search or Save):", "*Last Name (For Search or Save):", "*DOB (YYYY-MM-DD):", "*Gender:", "*Address:", "*City:", "*State:",
                             "*Zip Code:", "*Insurance:", "*Insurance Number:", "Patient ID (For Search or Delete):"};
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

        //Patient window buttons
        JPanel ButtonPanelPW = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        //Button to save patient info into database
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

        //Button to clear patient info fields
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

        //Button to delete patients
        JButton DeleteButtonPW = new JButton("Delete");
        DeleteButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Patient_Window.DeletePatient(Fields[10].getText(), TableModelPI, Fields, PatientWindow);
            }
        });
        ButtonPanelPW.add(DeleteButtonPW);
        DeleteButtonPW.setBackground(Color.BLUE);
        DeleteButtonPW.setForeground(Color.WHITE);

        //Button that searches patients
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

        //Button that displays patient window help
        JButton HelpButtonPW = new JButton("Help");
        HelpButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                Patient_Window.HelpPatient(PatientWindow, PatientWindow);
            }
        });
        ButtonPanelPW.add(HelpButtonPW);
        HelpButtonPW.setBackground(Color.BLUE);
        HelpButtonPW.setForeground(Color.WHITE);


        MainPanelPW.add(ButtonPanelPW, BorderLayout.SOUTH);
        PatientWindow.add(MainPanelPW);
        PatientWindow.setVisible(true);
        PatientTable.setDefaultEditor(Object.class, null);

    }
    
}
