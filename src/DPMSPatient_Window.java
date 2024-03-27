package src;
//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

//Code for patient window
public class DPMSPatient_Window {

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
        PopulatePatientTable(TableModelPI, PatientWindow);

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
                SavePatientInfo(Fields, TableModelPI, PatientWindow);
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
                ClearFields(Fields);
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
                DeletePatient(Fields[10].getText(), TableModelPI, Fields, PatientWindow);
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
                SearchPatients(TableModelPI, Fields, PatientWindow);
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
                HelpPatient(PatientWindow, PatientWindow);
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
   
    //Code that saves patient info when save button is used in PatientWindow.
    public static void SavePatientInfo(JTextField[] Fields, DefaultTableModel TableModelPI, JFrame ParentFrame) {
        
        // Check for empty fields, non-numeric inputs, and incorrect date format.
        for (int i = 0; i < Fields.length; i++) {
            if (i != 10 && Fields[i].getText().isEmpty()) {
                JOptionPane.showMessageDialog(ParentFrame, "Make sure to fill out all fields correctly and enter numbers" +
                                                            " for zip code and insurance number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ((i == 2 && !ValidDate(Fields[i].getText())) || 
                    (i == 7 || i == 9) && !Numeric(Fields[i].getText())) {
                JOptionPane.showMessageDialog(ParentFrame, "Make sure to fill out all fields correctly and" + 
                                            " enter numbers for zip code and insurance number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        //Inserts info typed into PatientWindow into the database.
        try {
            Connection connection = DPMSConnectDB.getConnection();
    
            String sql = "INSERT INTO patient (first_name, last_name, date_of_birth, gender, address, city, state, zip_code,"+
                            "insurance_company, insurance_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            //convert inputs to match database column data types and excludes some fields needed
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < Fields.length - 1; i++) { 
                if (i == 2) {
                    java.sql.Date dobDate = java.sql.Date.valueOf(Fields[i].getText());
                    statement.setDate(i + 1, dobDate); 

                } else if (i == 7 || i == 9) {
                    statement.setLong(i + 1, Long.parseLong(Fields[i].getText()));
                } else {
                    statement.setString(i + 1, Fields[i].getText());
                }
            }
    
            statement.executeUpdate();
            connection.close();
            JOptionPane.showMessageDialog(ParentFrame, "Patient information saved successfully.");
    
            PopulatePatientTable(TableModelPI, ParentFrame);
            ClearFields(Fields);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while saving patient information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that works with delete button to delete patients from PatientWindow
    public static void DeletePatient(String PatientID, DefaultTableModel TableModelCDP, JTextField[] Fields, JFrame ParentFrame) {

        if (!Numeric(PatientID)) {
            JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(ParentFrame, "Are you sure you want to delete patient " + PatientID + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
    
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection connection = DPMSConnectDB.getConnection();
                String sql = "DELETE FROM patient WHERE patient_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(PatientID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(ParentFrame, "Patient deleted successfully.");
                PopulatePatientTable(TableModelCDP, ParentFrame);
                ClearFields(Fields);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(ParentFrame, "Error occurred while deleting patient: Patient has appointment or bills and cannot be deleted.");
            }
        }
    }

    //Code that works to search patients using certain information
    public static void SearchPatients(DefaultTableModel TableModelSP, JTextField[] Fields, JFrame ParentFrame) {
    
        String FirstName = Fields[0].getText();
        String LastName = Fields[1].getText();
        String PatientIDText = Fields[10].getText();
    
        // Validate patientID using the Numeric function
        int patientID = -1;
        if (!PatientIDText.isEmpty() && Numeric(PatientIDText)) {
            patientID = Integer.parseInt(PatientIDText);
        } else if (!PatientIDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        TableModelSP.setRowCount(0);
    
        try {
            Connection connection = DPMSConnectDB.getConnection();
            String sql = "SELECT * FROM patient WHERE UPPER(first_name) LIKE UPPER(?) AND UPPER(last_name) LIKE UPPER(?) AND (patient_id = ? OR ? = -1) ORDER BY last_name ASC";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + FirstName + "%");
            statement.setString(2, "%" + LastName + "%");
            statement.setInt(3, patientID);
            statement.setInt(4, patientID);
    
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                Object[] RowData = new Object[11];
                for (int i = 1; i <= 11; i++) {
                    RowData[i - 1] = resultSet.getObject(i);
                }
                TableModelSP.addRow(RowData);
            }
    
            connection.close();
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while searching patients: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
     //Code that populates patient table in PatientWindow
     public static void PopulatePatientTable(DefaultTableModel TableModelPPT, JFrame ParentFrame) {
        TableModelPPT.setRowCount(0);
        try {
            Connection connection = DPMSConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT patient_id, first_name, last_name, date_of_birth, gender, address,"+
                                             "city, state, zip_code, insurance_company, insurance_number FROM patient ORDER BY last_name ASC");

            while (resultSet.next()) {
                String[] RowData = {
                        String.valueOf(resultSet.getInt(1)),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDate(4).toString(),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8),
                        String.valueOf(resultSet.getLong(9)),
                        resultSet.getString(10),
                        String.valueOf(resultSet.getLong(11))
                };
                TableModelPPT.addRow(RowData);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while fetching patient information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that displays help message for patient window
    public static void HelpPatient(JFrame frame, JFrame ParentFrame){
        JOptionPane.showMessageDialog(ParentFrame, "To search Patients only use First Name, Last Name, or Patient ID Fields."+ 
        "\nTo delete Patients only use Patient ID field."+ 
        "\nEnsure all fields marked with * are filled out. Ensure correct date format for patient DOB and numerical value for Zip Code, Insurance Number, and Patient ID."+ 
        "\nEnsure state field is an abbreviation for the state. EX: Illinois = IL.", "Patient Help Window", JOptionPane.INFORMATION_MESSAGE);
    }


    //Used to make sure date fields are correct. Used in AppointmentWindow and PatientWindow.
    public static boolean ValidDate(String DateStr) {
        try {
            java.sql.Date.valueOf(DateStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //Used to make sure time field is correct. Used in AppointmentWindow.
    public static boolean ValidTime(String TimeStr) {
        try {
            String[] parts = TimeStr.split(":");
            if (parts.length != 2) {
                return false; 
            }
            int Hours = Integer.parseInt(parts[0]);
            int Minutes = Integer.parseInt(parts[1]);
    
            return Hours >= 0 && Hours <= 23 && Minutes >= 0 && Minutes <= 59;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
    
    //Used to make sure fields are numeric vale. This is used in PatientWindow, AppointmentWindow, searchPatients, confirmDeletePatient.
    public static boolean Numeric(String Str) {
        if (Str == null) {
            return false;
        }
        try {
            Long.parseLong(Str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    //Code that clears fields
    public static void ClearFields(JTextField[] fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    //Code that verifies username and password for login
    public static boolean ValidateLogin(String EnteredUsername, char [] EnteredPassword) {
        String Password = new String(EnteredPassword); 

        try {
            Connection connection = DPMSConnectDB.getConnection();
            String sql = "SELECT * FROM login WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, EnteredUsername);
                statement.setString(2, Password);
                
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next(); 
                    
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 