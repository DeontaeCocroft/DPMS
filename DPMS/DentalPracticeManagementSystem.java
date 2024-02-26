//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class DentalPracticeManagementSystem {
    
    //Main code the works to add, view, and delete customer information. Uses methods PopulatePatientTable, SavePatientInfo, and DeletePatient.
    public static void PatientWindow() {
        JFrame PatientWindow = new JFrame("Patients");

        // Size of window
        PatientWindow.setSize(1550, 600);
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
        PopulatePatientTable(TableModelPI);

        JScrollPane ScrollPane = new JScrollPane(PatientTable);
        MainPanelPW.add(ScrollPane, BorderLayout.CENTER);

        // Fields to all patient info to database
        JPanel PatientFields = new JPanel(new GridLayout(12, 2, 10, 10));

        String[] Labels = {"First Name: (For Search or Save)", "Last Name: (For Search or Save)", "DOB (YYYY-MM-DD):", "Gender:", "Address:", "City:", "State:",
                             "Zip Code:", "Insurance:", "Insurance Number:", "Patient ID (For Search or Delete):"};
        JTextField[] Fields = new JTextField[Labels.length];

        for (int i = 0; i < Labels.length; i++) {
            PatientFields.add(new JLabel(Labels[i]));
            Fields[i] = new JTextField();
            PatientFields.add(Fields[i]);
        }

        MainPanelPW.add(PatientFields, BorderLayout.NORTH);

        //Patient add/delete buttons
        JPanel ButtonPanelPW = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        //Button to save patient info into database
        JButton SaveButtonPW = new JButton("Save");
        SaveButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SavePatientInfo(Fields, TableModelPI);
            }
        });
        ButtonPanelPW.add(SaveButtonPW);

        //Button to clear patient info
        JButton ClearButtonPW = new JButton("Clear Fields");
        ClearButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClearFields(Fields);
            }
        });
        ButtonPanelPW.add(ClearButtonPW);

        //Button to delete patients
        JButton DeleteButtonPW = new JButton("Delete");
        DeleteButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeletePatient(Fields[10].getText(), TableModelPI, Fields, PatientWindow);
            }
        });
        ButtonPanelPW.add(DeleteButtonPW);

        MainPanelPW.add(ButtonPanelPW, BorderLayout.SOUTH);

        PatientWindow.add(MainPanelPW);
        PatientWindow.setVisible(true);
        PatientTable.setDefaultEditor(Object.class, null);

        //Button that searches patients by first and last name
        JButton SearchButtonPW = new JButton("Search");
        SearchButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchPatients(TableModelPI, Fields);
            }
        });
        ButtonPanelPW.add(SearchButtonPW);
    }
   
    //Code that saves patient info when save button is used in PatientWindow.
    public static void SavePatientInfo(JTextField[] Fields, DefaultTableModel TableModelPI) {
        
        // Check for empty fields, non-numeric inputs, and incorrect date format.
        for (int i = 0; i < Fields.length; i++) {
            if (i != 10 && Fields[i].getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Make sure to fill out all fields correctly and enter numbers" +
                                                            " for zip code and insurance number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ((i == 2 && !ValidDate(Fields[i].getText())) || 
                    (i == 7 || i == 9) && !Numeric(Fields[i].getText())) {
                JOptionPane.showMessageDialog(null, "Make sure to fill out all fields correctly and" + 
                                            " Aenter numbers for zip code and insurance number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        //Inserts info typed into PatientWindow into the database.
        try {
            Connection connection = ConnectDPDB.getConnection();
    
            String sql = "INSERT INTO Patient (FirstName, LastName, DOB, Gender, Address,City, State, ZipCode,"+
                            "Insurance, InsuranceNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            //convert inputs to match database column data types and exculdes some fields needed
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
            JOptionPane.showMessageDialog(null, "Patient information saved successfully.");
    
            PopulatePatientTable(TableModelPI);
            ClearFields(Fields);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while saving patient information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that works with delete button to delete patients from PatientWindow
    public static void DeletePatient(String PatientID, DefaultTableModel TableModelCDP, JTextField[] Fields, JFrame ParentFrame) {

        if (!Numeric(PatientID)) {
            JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(ParentFrame, "Are you sure you want to delete this patient?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
    
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection connection = ConnectDPDB.getConnection();
                String sql = "DELETE FROM Patient WHERE PatientID = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(PatientID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(ParentFrame, "Patient deleted successfully.");
                PopulatePatientTable(TableModelCDP);
                ClearFields(Fields);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(ParentFrame, "Error occurred while deleting patient: Patient has appointment or bills and cannot be deleted.");
            }
        }
    }

    public static void SearchPatients(DefaultTableModel TableModelSP, JTextField[] Fields) {
        // Retrieve search criteria from text fields
        String FirstName = Fields[0].getText();
        String LastName = Fields[1].getText();
        String PatientIDText = Fields[10].getText();
    
        // Validate patientID using the Numeric function
        int patientID = -1;
        if (!PatientIDText.isEmpty() && Numeric(PatientIDText)) {
            patientID = Integer.parseInt(PatientIDText);
        } else if (!PatientIDText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Patient ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Clear the table before populating with search results
        TableModelSP.setRowCount(0);
    
        try {
            Connection connection = ConnectDPDB.getConnection();
    
            // SQL query to handle searching by FirstName, LastName, and PatientID
            String sql = "SELECT * FROM Patient WHERE UPPER(FirstName) LIKE UPPER(?) AND UPPER(LastName) LIKE UPPER(?) AND (PatientID = ? OR ? = -1)";
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
            JOptionPane.showMessageDialog(null, "Error occurred while searching patients: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
     //Code that populates patient table in PatientWindow
     public static void PopulatePatientTable(DefaultTableModel TableModelPPT) {
        TableModelPPT.setRowCount(0);
        try {
            Connection connection = ConnectDPDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT PatientID, FirstName, LastName, DOB, Gender,Address ,"+
                                                                "City, State, ZipCode, Insurance, InsuranceNumber FROM Patient ORDER BY LastName ASC");

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
            JOptionPane.showMessageDialog(null, "Error occurred while fetching patient information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that works to add, delete, and view appointment information. Uses methods SaveAppointmentInfo, DeleteAppointment, and PopulateAppointmentInfo
    public static void AppointmentWindow() {
        JFrame AppointmentWindow = new JFrame("Appointment Scheduling");
    
        // Size of window
        AppointmentWindow.setSize(1900, 1000);
        AppointmentWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        AppointmentWindow.setLocationRelativeTo(null);
    
        JPanel MainPanelAW = new JPanel(new BorderLayout());
    
        // Table to display Appointment information
        DefaultTableModel TableModelAI = new DefaultTableModel();
        TableModelAI.addColumn("Appointment ID");
        TableModelAI.addColumn("Patient ID");
        TableModelAI.addColumn("Procedure ID");
        TableModelAI.addColumn("Procedure Occurrences");
        TableModelAI.addColumn("Dentist ID");
        TableModelAI.addColumn("Dental Hygienist ID");
        TableModelAI.addColumn("Dental Assistant ID");
        TableModelAI.addColumn("Dental Surgeon ID");
        TableModelAI.addColumn("Appointment Date");
        TableModelAI.addColumn("Appointment Time");
        TableModelAI.addColumn("Notes");
        TableModelAI.addColumn("Canceled");
    
        JTable AppointmentTable = new JTable(TableModelAI);
        AppointmentTable.getColumnModel().getColumn(10).setPreferredWidth(300);
        PopulateAppointmentTable(TableModelAI);
        JScrollPane ScrollPane = new JScrollPane(AppointmentTable);
        MainPanelAW.add(ScrollPane, BorderLayout.CENTER);

        //Table to display Dental Assistant information
        DefaultTableModel TableModelDA = new DefaultTableModel();
        TableModelDA.addColumn("DentalAssistant ID");
        TableModelDA.addColumn("First Name");
        TableModelDA.addColumn("Last Name");
        TableModelDA.addColumn("Phone Number");

        JTable dentalassistanttable = new JTable(TableModelDA);
        PopulateDentalAssistantTable(TableModelDA);
        JScrollPane ScrollPaneDA = new JScrollPane(dentalassistanttable);

        //Table to display Dental Hygienist information
        DefaultTableModel TableModelDH = new DefaultTableModel();
        TableModelDH.addColumn("Dental Hygienist ID");
        TableModelDH.addColumn("First Name");
        TableModelDH.addColumn("Last Name");
        TableModelDH.addColumn("Phone Number");

        JTable DentalHygienistTable = new JTable(TableModelDH);
        PopulateDentalHygienistTable(TableModelDH);
        JScrollPane ScrollPaneDH = new JScrollPane(DentalHygienistTable);

        //Table to display Dental Surgeon information
        DefaultTableModel TableModelDS = new DefaultTableModel();
        TableModelDS.addColumn("Dental Surgeon ID");
        TableModelDS.addColumn("First Name");
        TableModelDS.addColumn("Last Name");
        TableModelDS.addColumn("Phone Number");

        JTable DentalSurgeonTable = new JTable(TableModelDS);
        PopulateDentalSurgeonTable(TableModelDS);
        JScrollPane ScrollPaneDS = new JScrollPane(DentalSurgeonTable);

        //Table to display Dentist information
        DefaultTableModel TableModelDI = new DefaultTableModel();
        TableModelDI.addColumn("Dentist ID");
        TableModelDI.addColumn("First Name");
        TableModelDI.addColumn("Last Name");
        TableModelDI.addColumn("Phone Number");

        JTable DentisTable = new JTable(TableModelDI);
        PopulateDentistTable(TableModelDI);
        JScrollPane ScrollPaneD = new JScrollPane(DentisTable);

        //Table to display Procedure information
        DefaultTableModel TableModelPRO = new DefaultTableModel();
        TableModelPRO.addColumn("Procedure ID");
        TableModelPRO.addColumn("Name");
        TableModelPRO.addColumn("Notes");
        TableModelPRO.addColumn("Price");
 
        JTable ProcedureTable = new JTable(TableModelPRO);
        PopulateProcedureTable(TableModelPRO);
        JScrollPane ScrollPanepro = new JScrollPane(ProcedureTable);

        //Keep appointment, patient, and procedure tables together
        Box VerticalBox = Box.createVerticalBox();
        VerticalBox.add(ScrollPanepro);
        VerticalBox.add(ScrollPane);
        MainPanelAW.add(VerticalBox, BorderLayout.CENTER);

        //Keeps all dental tables together
        Box VerticalBox2 = Box.createVerticalBox();
        VerticalBox2.add(ScrollPaneD);
        VerticalBox2.add(ScrollPaneDH);
        VerticalBox2.add(ScrollPaneDA);
        VerticalBox2.add(ScrollPaneDS);
    
        MainPanelAW.add(VerticalBox2, BorderLayout.EAST);

    
        // Fields to add appointment info to database
        JPanel AppointmentFields = new JPanel(new GridLayout(6, 2, 10, 10));
        String[] Labels = {"Patient ID: (For Save or Search)", "Procedure ID:", "Procedure Occurrences:", "Dentist ID:", "Dental Hygienist ID:", "Dental Assistant ID:", 
                            "Dental Surgeon ID:", "Appointment Date (YYYY-MM-DD):", "Appointment Time (HH:mm):", "Notes:", "Canceled:", "Appointment ID: (For Delete or Search):"};
        JTextField[] Fields = new JTextField[Labels.length];
    
        for (int i = 0; i < Labels.length; i++) {
            AppointmentFields.add(new JLabel(Labels[i]));
            Fields[i] = new JTextField(20);
            AppointmentFields.add(Fields[i]);
        }

        MainPanelAW.add(AppointmentFields, BorderLayout.NORTH);
        
        //Buttons for appointment scheduling
        JPanel ButtonPanelAW = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        //Button to save appointment information to database
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveAppointmentInfo(Fields, TableModelAI);
            }
        });
        ButtonPanelAW.add(saveButton);
    
        //Button to clear fields 
        JButton ClearButtonAW = new JButton("Clear Fields");
        ClearButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClearFields(Fields);
            }
        });
        ButtonPanelAW.add(ClearButtonAW);

        //Button to delete appointments from the database
        JButton DeleteButtonAW = new JButton("Delete");
        DeleteButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteAppointment(Fields[11].getText(), TableModelAI, Fields, AppointmentWindow);
                
            }
        });
        ButtonPanelAW.add(DeleteButtonAW);

        //Button to patients
        JButton PatientButtonAW = new JButton("Patients");
        PatientButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                PatientWindow();
            }
            
        });
        ButtonPanelAW.add((PatientButtonAW));

        JButton SearchButtonAW = new JButton("Search");
        SearchButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                SearchAppointments(TableModelAI, Fields);
            }
            
        });
        ButtonPanelAW.add((SearchButtonAW));

        MainPanelAW.add(ButtonPanelAW, BorderLayout.SOUTH);
    
        AppointmentWindow.add(MainPanelAW);
        AppointmentWindow.setVisible(true);
        AppointmentTable.setDefaultEditor(Object.class, null);
    }

    //Function that searches appointments for table in AppointmentWindow
    public static void SearchAppointments(DefaultTableModel TableModel, JTextField[] Fields) {
        String patientIDText = Fields[0].getText();
        String appointmentIDText = Fields[11].getText();
    
        // Validate patientID using the Numeric function
        int patientID = -1;
        if (!patientIDText.isEmpty() && Numeric(patientIDText)) {
            patientID = Integer.parseInt(patientIDText);
        } else if (!patientIDText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Patient ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Validate appointmentID using the Numeric function
        int appointmentID = -1;
        if (!appointmentIDText.isEmpty() && Numeric(appointmentIDText)) {
            appointmentID = Integer.parseInt(appointmentIDText);
        } else if (!appointmentIDText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Appointment ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        TableModel.setRowCount(0);
    
        try {
            Connection connection = ConnectDPDB.getConnection();
            String sql = "SELECT * FROM Appointment WHERE (PatientID = ? OR ? = -1) AND (AppointmentID = ? OR ? = -1)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, patientID);
            statement.setInt(2, patientID);
            statement.setInt(3, appointmentID);
            statement.setInt(4, appointmentID);
    
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Object[] RowData = new Object[12];
                for (int i = 1; i <= 12; i++) {
                    RowData[i - 1] = resultSet.getObject(i);
                }
                TableModel.addRow(RowData);
            }
    
    
            connection.close();
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while searching appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Code that saves appointment info when save button is used in AppointmentWindow
    public static void SaveAppointmentInfo(JTextField[] Fields, DefaultTableModel TableModel) {
        // Check for empty fields, non-numeric inputs, incorrect time format, and incorrect date format.
        for (int i = 0; i < 9; i++) {
            if (i != 5 && i != 6 && Fields[i].getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Make sure to fill out all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ((i == 7 && !ValidDate(Fields[i].getText())) ||
                    (i == 8 && !ValidTime(Fields[i].getText()))) {
                JOptionPane.showMessageDialog(null, "Make sure to fill out all fields correctly with the correct format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        //Inserts info typed into AppointmentWindow into the database.
        try {
            Connection connection = ConnectDPDB.getConnection();
    
            String sql = "INSERT INTO Appointment (PatientID, ProcedureID, ProcedureOccurrences, DentistID, DentalHygienistID,"+
                         "DentalAssistantID, DentalSurgeonID, AppointmentDate, AppointmentTime, Notes, Canceled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            //convert inputs to match database column data types and exculdes some fields needed
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < Fields.length - 1; i++) {
                if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4) {
                    int FieldValue = Integer.parseInt(Fields[i].getText());
                    statement.setInt(i + 1, FieldValue);
                } else if ((i == 5 || i == 6)) { 
                    if (Fields[i].getText().isEmpty()) {
                        statement.setNull(i + 1, java.sql.Types.INTEGER);
                    } else {
                        int FieldValue = Integer.parseInt(Fields[i].getText());
                        statement.setInt(i + 1, FieldValue);
                    }
                } else if (i == 7) {
                    java.sql.Date AppointmentDate = java.sql.Date.valueOf(Fields[i].getText());
                    statement.setDate(i + 1, AppointmentDate);
                } else if (i == 8) {
                    LocalTime AppointmentTime = LocalTime.parse(Fields[i].getText(), DateTimeFormatter.ofPattern("HH:mm"));
                    Time sqlTime = Time.valueOf(AppointmentTime);
                    statement.setTime(i + 1, sqlTime);
                } else if (i == 10) {
                    boolean canceled = Fields[i].getText().equalsIgnoreCase("true");
                    statement.setBoolean(i + 1, canceled);
                } else {
                    statement.setString(i + 1, Fields[i].getText());
                }
            }
            
            statement.executeUpdate();
            connection.close();
            JOptionPane.showMessageDialog(null, "Appointment information saved successfully.");
            PopulateAppointmentTable(TableModel);
            ClearFields(Fields);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while saving appointment information: " + e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Fields ProcedureID, Procedure Occurrences, Dentist ID, Dental Hygienist ID,"+ 
                                            "Dental Assistant ID, Dental Surgeon ID must be integers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Code deletes patient information in the database. Is used in AppointmentWindow
    public static void DeleteAppointment(String AppointmentID, DefaultTableModel TableModel, JTextField[] Fields, JFrame ParentFrame) {

        if (!Numeric(AppointmentID)) {
            JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this appointment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {

            try {
                Connection connection = ConnectDPDB.getConnection();
                String sql = "DELETE FROM Appointment WHERE AppointmentID = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(AppointmentID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(null, "Appointment deleted successfully.");
                PopulateAppointmentTable(TableModel);
                ClearFields(Fields);
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error occurred while deleting appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    //Code that populates appointments table in AppointmentWindow
    public static void PopulateAppointmentTable(DefaultTableModel TableModel) {
        TableModel.setRowCount(0);
        try {
            Connection connection = ConnectDPDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT AppointmentID, PatientID, ProcedureID, ProcedureOccurrences, DentistID,"+
                                                         "DentalHygienistID, DentalAssistantID, DentalSurgeonID, AppointmentDate, AppointmentTime, Notes, Canceled FROM Appointment ORDER BY AppointmentDate DESC;");
    
            while (resultSet.next()) {
                String[] rowData = {
                        String.valueOf(resultSet.getInt(1)),
                        String.valueOf(resultSet.getInt(2)),
                        String.valueOf(resultSet.getInt(3)),
                        String.valueOf(resultSet.getInt(4)),
                        String.valueOf(resultSet.getInt(5)),
                        String.valueOf(resultSet.getInt(6)),
                        String.valueOf(resultSet.getInt(7)),
                        String.valueOf(resultSet.getInt(8)),
                        resultSet.getDate(9).toString(),
                        resultSet.getTime(10).toString(),
                        resultSet.getString(11),
                        resultSet.getString(12)
                };
                TableModel.addRow(rowData);
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while fetching appointment information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that populates dental assistant table in AppointmentWindow
    public static void PopulateDentalAssistantTable(DefaultTableModel TableModelDA){
        try {
            Connection connection = ConnectDPDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM DentalAssistant ORDER BY LastName ASC ");
    
            while (resultSet.next()) {
                String[] RowData = {
                        String.valueOf(resultSet.getInt(1)),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        String.valueOf(resultSet.getLong(4)),
                        
                };
                TableModelDA.addRow(RowData);
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while fetching dental assistant information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that populates dental hygienist table in AppointmentWindow
    public static void PopulateDentalHygienistTable(DefaultTableModel TableModelDH){
        try {
            Connection connection = ConnectDPDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM DentalHygienist ORDER BY LastName ASC");
    
            while (resultSet.next()) {
                String[] RowData = {
                        String.valueOf(resultSet.getInt(1)),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        String.valueOf(resultSet.getLong(4)),
                        
                };
                TableModelDH.addRow(RowData);
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while fetching dental hygienist information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that populates dental surgeon table in AppointmentWindow
    public static void PopulateDentalSurgeonTable(DefaultTableModel TableModelDS){
        try {
            Connection connection = ConnectDPDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM DentalSurgeon ORDER BY LastName ASC");
    
            while (resultSet.next()) {
                String[] RowData = {
                        String.valueOf(resultSet.getInt(1)),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        String.valueOf(resultSet.getLong(4)),
                        
                };
                TableModelDS.addRow(RowData);
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while fetching dental surgeon information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that populates dental surgeon table in AppointmentWindow
    public static void PopulateDentistTable(DefaultTableModel TableModelD){
        try {
            Connection connection = ConnectDPDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Dentist ORDER BY LastName ASC");
    
            while (resultSet.next()) {
                String[] RowData = {
                        String.valueOf(resultSet.getInt(1)),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        String.valueOf(resultSet.getLong(4)),
                        
                };
                TableModelD.addRow(RowData);
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while fetching dentist information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
  
    //Code that populates procedure table in AppointmentWindow
    public static void PopulateProcedureTable(DefaultTableModel TableModelPRO) {
        TableModelPRO.setRowCount(0);
    try {
        Connection connection = ConnectDPDB.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Procedure");
        while (resultSet.next()) {

            String[] RowData = {
                    String.valueOf(resultSet.getInt(1)),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
            };
            TableModelPRO.addRow(RowData);
        }

        connection.close();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error occurred while fetching procedure information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
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
            Connection connection = ConnectDPDB.getConnection();
            String sql = "SELECT * FROM Login WHERE Username = ? AND Password = ?";
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
    