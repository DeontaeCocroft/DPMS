package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//Code for appointment window
public class DPMSAppointment_Window {

    public static void AppointmentWindow() {
        JFrame AppointmentWindow = new JFrame("Appointment Scheduling");

        //Make sure the appointment window doesn't appear behind main menu due to loading screen
        AppointmentWindow.setAlwaysOnTop(!AppointmentWindow.isAlwaysOnTop());
        AppointmentWindow.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e){
                Timer timer = new Timer(250, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                    AppointmentWindow.setAlwaysOnTop(false);
                     }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        //Appointment window Icon
        ImageIcon LogoIcon = new ImageIcon("Images\\appointment.jpg"); 
        AppointmentWindow.setIconImage(LogoIcon.getImage());
        
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

        //Set size of specific column
        AppointmentTable.getColumnModel().getColumn(10).setPreferredWidth(300);

        //Populate table with information
        PopulateAppointmentTable(TableModelAI, AppointmentWindow);

        JScrollPane ScrollPane = new JScrollPane(AppointmentTable);
        MainPanelAW.add(ScrollPane, BorderLayout.CENTER);

        //Table to display Dental Assistant information
        DefaultTableModel TableModelDA = new DefaultTableModel();
        TableModelDA.addColumn("DentalAssistant ID");
        TableModelDA.addColumn("First Name");
        TableModelDA.addColumn("Last Name");
        TableModelDA.addColumn("Phone Number");

        JTable dentalassistanttable = new JTable(TableModelDA);

        //Populate table with information
        PopulateDentalAssistantTable(TableModelDA, AppointmentWindow);
        JScrollPane ScrollPaneDA = new JScrollPane(dentalassistanttable);

        //Table to display Dental Hygienist information
        DefaultTableModel TableModelDH = new DefaultTableModel();
        TableModelDH.addColumn("Dental Hygienist ID");
        TableModelDH.addColumn("First Name");
        TableModelDH.addColumn("Last Name");
        TableModelDH.addColumn("Phone Number");

        JTable DentalHygienistTable = new JTable(TableModelDH);

        //Populate table with information
        PopulateDentalHygienistTable(TableModelDH, AppointmentWindow);
        JScrollPane ScrollPaneDH = new JScrollPane(DentalHygienistTable);

        //Table to display Dental Surgeon information
        DefaultTableModel TableModelDS = new DefaultTableModel();
        TableModelDS.addColumn("Dental Surgeon ID");
        TableModelDS.addColumn("First Name");
        TableModelDS.addColumn("Last Name");
        TableModelDS.addColumn("Phone Number");

        JTable DentalSurgeonTable = new JTable(TableModelDS);

        //Populate table with information
        PopulateDentalSurgeonTable(TableModelDS, AppointmentWindow);
        JScrollPane ScrollPaneDS = new JScrollPane(DentalSurgeonTable);

        //Table to display Dentist information
        DefaultTableModel TableModelDI = new DefaultTableModel();
        TableModelDI.addColumn("Dentist ID");
        TableModelDI.addColumn("First Name");
        TableModelDI.addColumn("Last Name");
        TableModelDI.addColumn("Phone Number");

        JTable DentistTable = new JTable(TableModelDI);

        //Populate table with information
        PopulateDentistTable(TableModelDI, AppointmentWindow);
        JScrollPane ScrollPaneD = new JScrollPane(DentistTable);

        //Table to display Procedure information
        DefaultTableModel TableModelPRO = new DefaultTableModel();
        TableModelPRO.addColumn("Procedure ID");
        TableModelPRO.addColumn("Name");
        TableModelPRO.addColumn("Notes");
        TableModelPRO.addColumn("Price");

        JTable ProcedureTable = new JTable(TableModelPRO);

        //Set size of specific columns
        ProcedureTable.getColumnModel().getColumn(2).setPreferredWidth(600);
        ProcedureTable.getColumnModel().getColumn(3).setPreferredWidth(50);

        //Populate table with information
        PopulateProcedureTable(TableModelPRO, AppointmentWindow);
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
        String[] Labels = {"*Patient ID (For Save or Search):", "*Procedure ID:", "*Procedure Occurrences:", "*Dentist ID:", "*Dental Hygienist ID:", "Dental Assistant ID:", 
                            "Dental Surgeon ID:", "*Appointment Date (YYYY-MM-DD):", "*Appointment Time (24HR HH:MM):", "Notes:", "Canceled (For Save or Cancel) (Type " + "'true'" +" to cancel"+ "):", "Appointment ID (For Delete, Search, or Cancel):"};
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
                SaveAppointmentInfo(Fields, TableModelAI, AppointmentWindow);
            }
        });
        ButtonPanelAW.add(saveButton);
        saveButton.setBackground(Color.BLUE);
        saveButton.setForeground(Color.WHITE);
    
        //Button to clear appointment info fields 
        JButton ClearButtonAW = new JButton("Clear Fields");
        ClearButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DPMSPatient_Window.ClearFields(Fields);
            }
        });
        ButtonPanelAW.add(ClearButtonAW);
        ClearButtonAW.setBackground(Color.BLUE);
        ClearButtonAW.setForeground(Color.WHITE);

        //Button to delete appointments from the database
        JButton DeleteButtonAW = new JButton("Delete");
        DeleteButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteAppointment(Fields[11].getText(), TableModelAI, Fields, AppointmentWindow);
                
            }
        });
        ButtonPanelAW.add(DeleteButtonAW);
        DeleteButtonAW.setBackground(Color.BLUE);
        DeleteButtonAW.setForeground(Color.WHITE);

        //Button to patients window
        JButton PatientButtonAW = new JButton("Patients");
        PatientButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){

                //Triggers loading screen
                if (DPMSGui.count == 1){
                    DPMSGui.SetLoadingScreen(this);
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            DPMSPatient_Window.PatientWindow();
                            return null;
                        }
                        @Override
                        protected void done() {
                            DPMSGui.closeLoadingScreen();
                        }
                    };
                    worker.execute();
                }
            }
        });
        
        ButtonPanelAW.add((PatientButtonAW));
        PatientButtonAW.setBackground(Color.BLUE);
        PatientButtonAW.setForeground(Color.WHITE);

        //Button to search patients
        JButton SearchButtonAW = new JButton("Search");
        SearchButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                SearchAppointments(TableModelAI, Fields, AppointmentWindow);
            }
            
        });
        ButtonPanelAW.add((SearchButtonAW));
        SearchButtonAW.setBackground(Color.BLUE);
        SearchButtonAW.setForeground(Color.WHITE);

        //Button to mark patient appointment as canceled or not canceled
        JButton CancelButtonAW = new JButton("Cancel Appointment");
        CancelButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                CancelAppointments(TableModelAI, Fields, AppointmentWindow);
            }
            
        });
        ButtonPanelAW.add((CancelButtonAW));
        CancelButtonAW.setBackground(Color.BLUE);
        CancelButtonAW.setForeground(Color.WHITE);

        //Button for patient window help
        JButton HelpButtonAW = new JButton("Help");
        HelpButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                HelpAppointment(AppointmentWindow, AppointmentWindow);
            }
        });
        ButtonPanelAW.add(HelpButtonAW);
        HelpButtonAW.setBackground(Color.BLUE);
        HelpButtonAW.setForeground(Color.WHITE);

        MainPanelAW.add(ButtonPanelAW, BorderLayout.SOUTH);
        AppointmentWindow.add(MainPanelAW);
        AppointmentWindow.setVisible(true);
        AppointmentTable.setDefaultEditor(Object.class, null);
    }

    //Code that searches appointments for table in AppointmentWindow
    public static void SearchAppointments(DefaultTableModel TableModel, JTextField[] Fields, JFrame ParentFrame) {
        String patientIDText = Fields[0].getText();
        String appointmentIDText = Fields[11].getText();
    
        // Validate patientID using the Numeric function
        int patientID = -1;
        if (!patientIDText.isEmpty() && DPMSPatient_Window.Numeric(patientIDText)) {
            patientID = Integer.parseInt(patientIDText);
        } else if (!patientIDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Validate appointmentID using the Numeric function
        int appointmentID = -1;
        if (!appointmentIDText.isEmpty() && DPMSPatient_Window.Numeric(appointmentIDText)) {
            appointmentID = Integer.parseInt(appointmentIDText);
        } else if (!appointmentIDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Appointment ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        TableModel.setRowCount(0);
    
        try {
            Connection connection = DPMSConnectDB.getConnection();
            String sql = "SELECT * FROM appointment WHERE (patient_id = ? OR ? = -1) AND (appointment_id = ? OR ? = -1) ORDER BY appointment_date DESC;";
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
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while searching appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Code that saves appointment info when save button is used in AppointmentWindow
    public static void SaveAppointmentInfo(JTextField[] Fields, DefaultTableModel TableModel, JFrame ParentFrame) {

        // Check for empty fields, non-numeric inputs, incorrect time format, and incorrect date format.
        for (int i = 0; i < 9; i++) {
            if (i != 5 && i != 6 && Fields[i].getText().isEmpty()) {
                JOptionPane.showMessageDialog(ParentFrame, "Make sure to fill out all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ((i == 7 && !DPMSPatient_Window.ValidDate(Fields[i].getText())) ||
                    (i == 8 && !DPMSPatient_Window.ValidTime(Fields[i].getText()))) {
                JOptionPane.showMessageDialog(ParentFrame, "Make sure to fill out all fields correctly with the correct format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        //Inserts info typed into AppointmentWindow into the database.
        try {
            Connection connection = DPMSConnectDB.getConnection();
    
            String sql = "INSERT INTO appointment (patient_id, procedure_id, procedure_occurrences, dentist_id, dental_hygienist_id,"+
                         "dental_assistant_id, dental_surgeon_id, appointment_date, appointment_time, notes, canceled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            //convert inputs to match database column data types and excludes some fields needed
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
            JOptionPane.showMessageDialog(ParentFrame, "Appointment information saved successfully.");
            PopulateAppointmentTable(TableModel, ParentFrame);
            DPMSPatient_Window.ClearFields(Fields);

        } catch (SQLException e) {
            if (e.getMessage().contains("violates foreign key constraint \"appointment_patient_id_fkey\"")) {
                JOptionPane.showMessageDialog(ParentFrame, "The specified Patient ID does not exist.", "Patient ID Error", JOptionPane.ERROR_MESSAGE);
            }
            else if (e.getMessage().contains("violates foreign key constraint \"appointment_procedure_id_fkey\"")) {
                JOptionPane.showMessageDialog(ParentFrame, "The specified Procedure ID does not exist.", "Procedure ID Error", JOptionPane.ERROR_MESSAGE);
            } 
            else if (e.getMessage().contains("violates foreign key constraint \"appointment_dentist_id_fkey\"")) {
                JOptionPane.showMessageDialog(ParentFrame, "The specified Dentist ID does not exist.", "Dentist ID Error", JOptionPane.ERROR_MESSAGE);
            } 
            else if (e.getMessage().contains("violates foreign key constraint \"appointment_dental_hygienist_id_fkey\"")) {
                JOptionPane.showMessageDialog(ParentFrame, "The specified Dental Hygienist ID does not exist.", "Dental Hygienist ID Error", JOptionPane.ERROR_MESSAGE);
            } 
            else if (e.getMessage().contains("violates foreign key constraint \"appointment_dental_assistant_id_fkey\"")) {
                JOptionPane.showMessageDialog(ParentFrame, "The specified Dental Assistant ID does not exist.", "Dental Assistant ID Error", JOptionPane.ERROR_MESSAGE);
            } 
            else if (e.getMessage().contains("violates foreign key constraint \"appointment_dental_surgeon_id_fkey\"")) {
                JOptionPane.showMessageDialog(ParentFrame, "The specified Dental Surgeon ID does not exist.", "Dental Surgeon ID Error", JOptionPane.ERROR_MESSAGE);
            } 
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(ParentFrame, "Fields Patient ID, Procedure ID, Procedure Occurrences, Dentist ID, Dental Hygienist ID, "+ 
                                            "Dental Assistant ID, Dental Surgeon ID must be integers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    //Code deletes patient information in the database. Is used in AppointmentWindow
    public static void DeleteAppointment(String AppointmentID, DefaultTableModel TableModel, JTextField[] Fields, JFrame ParentFrame) {

        if (!DPMSPatient_Window.Numeric(AppointmentID)) {
            JOptionPane.showMessageDialog(ParentFrame, "Appointment ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(ParentFrame, "Are you sure you want to delete Appointment " + AppointmentID + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {

            try {
                Connection connection = DPMSConnectDB.getConnection();
                String sql = "DELETE FROM appointment WHERE appointment_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(AppointmentID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(ParentFrame, "Appointment deleted successfully.");
                PopulateAppointmentTable(TableModel, ParentFrame);
                DPMSPatient_Window.ClearFields(Fields);
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(ParentFrame, "Error occurred while deleting appointment: Appointment is tied to bill and cannot be deleted.");
            }
        }
    }
    
    //Code that populates appointments table in AppointmentWindow
    public static void PopulateAppointmentTable(DefaultTableModel TableModel, JFrame ParentFrame) {
        TableModel.setRowCount(0);
        try {
            Connection connection = DPMSConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT appointment_id, patient_id, procedure_id, procedure_occurrences, dentist_id,"+
                                                         "dental_hygienist_id, dental_assistant_id, dental_surgeon_id, appointment_date, appointment_time, notes, canceled FROM appointment ORDER BY appointment_date DESC;");
    
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
                        String.valueOf(resultSet.getBoolean(12)),
                };
                TableModel.addRow(rowData);
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while fetching appointment information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that works to use specific information to cancel appointments
    public static void CancelAppointments(DefaultTableModel TableModel, JTextField[] Fields,JFrame ParentFrame) {
        String appointmentIDText = Fields[11].getText();
        boolean canceled = Boolean.parseBoolean(Fields[10].getText());
    
        // Validate appointmentID using the Numeric function
        int appointmentID = -1;
        if (!appointmentIDText.isEmpty() && DPMSPatient_Window.Numeric(appointmentIDText)) {
            appointmentID = Integer.parseInt(appointmentIDText);
        } else if (!appointmentIDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Appointment ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Update the "canceled" field in the database
        try {
            Connection connection = DPMSConnectDB.getConnection();
            String updateSql = "UPDATE appointment SET canceled = ? WHERE appointment_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setBoolean(1, canceled);
            updateStatement.setInt(2, appointmentID);
    
            int rowsAffected = updateStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(ParentFrame, "Appointment canceled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                PopulateAppointmentTable(TableModel, ParentFrame);
            } else {
                JOptionPane.showMessageDialog(ParentFrame, "No appointment found with the provided Appointment ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while canceling appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that populates dental assistant table in AppointmentWindow
    public static void PopulateDentalAssistantTable(DefaultTableModel TableModelDA, JFrame ParentFrame){
        try {
            Connection connection = DPMSConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM dental_assistant ORDER BY last_name ASC");
    
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
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while fetching dental assistant information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that populates dental hygienist table in AppointmentWindow
    public static void PopulateDentalHygienistTable(DefaultTableModel TableModelDH, JFrame ParentFrame){
        try {
            Connection connection = DPMSConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM dental_hygienist ORDER BY last_name ASC");
    
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
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while fetching dental hygienist information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that populates dental surgeon table in AppointmentWindow
    public static void PopulateDentalSurgeonTable(DefaultTableModel TableModelDS, JFrame ParentFrame){
        try {
            Connection connection = DPMSConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM dental_surgeon ORDER BY last_name ASC");

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
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while fetching dental surgeon information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that populates dental surgeon table in AppointmentWindow
    public static void PopulateDentistTable(DefaultTableModel TableModelD, JFrame ParentFrame){
        try {
            Connection connection = DPMSConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM dentist ORDER BY last_name ASC");
    
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
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while fetching dentist information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
  
    //Code that populates procedure table in AppointmentWindow
    public static void PopulateProcedureTable(DefaultTableModel TableModelPRO, JFrame ParentFrame) {
        TableModelPRO.setRowCount(0);
    try {
        Connection connection = DPMSConnectDB.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM procedure");
        while (resultSet.next()) {

            String[] RowData = {
                    String.valueOf(resultSet.getInt(1)),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    String.valueOf(resultSet.getDouble(4)),
            };
            TableModelPRO.addRow(RowData);
        }

        connection.close();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(ParentFrame, "Error occurred while fetching procedure information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    //Code that displays help message in Appointment Window
    public static void HelpAppointment(JFrame frame, JFrame ParentFrame){
        JOptionPane.showMessageDialog(ParentFrame, "To search Appointments only use Patient ID or Appointment ID Fields."+ 
        "\nTo delete Appointment only use Appointment ID field."+ 
        "\nEnsure all fields marked with * are filled out. Ensure correct date format for Appointment Date." + 
        "\nEnsure numerical value for all ID fields."+ 
        "\nTo mark appointment as canceled only use Appointment ID and Canceled fields. Type 'true' in Canceled field to cancel appointment." + 
        " Type 'false' to uncancel.", "Appointment Help Window", JOptionPane.INFORMATION_MESSAGE);
    }

}