package src;
//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



//Logic for patient window GUI
public class Patient_Window{
   
    //Code that saves patient info when save button is used in PatientWindow.
    public static void SavePatientInfo(JTextField[] Fields, DefaultTableModel TableModelPI, JFrame ParentFrame) {
        // Check for empty fields, non-numeric inputs, and incorrect date format.
        for (int i = 0; i < Fields.length; i++) {
            if (i != 11 && i != 12 && Fields[i].getText().isEmpty()) {
                JOptionPane.showMessageDialog(ParentFrame, "Make sure to fill out all fields correctly and enter numbers" +
                                                            " for zip code, insurance number, and phone number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if ((i == 2 && !ValidDate(Fields[i].getText())) || 
                    (i == 7 || i == 9 || i == 10) && !Numeric(Fields[i].getText())) {
                JOptionPane.showMessageDialog(ParentFrame, "Make sure to fill out all fields correctly and" + 
                                            " enter numbers for zip code, insurance number, and phone number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Insert info typed into PatientWindow into the database.
        try {
            Connection connection = ConnectDB.getConnection();
    
            String sql = "INSERT INTO patient (first_name, last_name, date_of_birth, gender, address, city, state, zip_code,"+
                            "insurance_company, insurance_number, phone_number, xray_images) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            // Convert inputs to match database column data types and exclude some fields needed.
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < Fields.length - 1; i++) { 

                if (i == 2) {
                    java.sql.Date dobDate = java.sql.Date.valueOf(Fields[i].getText());
                    statement.setDate(i + 1, dobDate); 
    
                } else if (i == 7 || i == 9 || i == 10) {
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

    //code that allows users to update any patient information
    public static void UpdatePatientInfo(JTextField[] Fields, DefaultTableModel TableModelPI, JFrame ParentFrame, String PatientID) {
        // Check if the Patient ID is numeric
        for (int i = 0; i < Fields.length; i++) {
            if ((i == 2 && !Fields[i].getText().isEmpty() && !ValidDate(Fields[i].getText())) || 
                    ((i == 7 || i == 9 || i == 10) && !Fields[i].getText().isEmpty() && !Numeric(Fields[i].getText()))) {
                JOptionPane.showMessageDialog(ParentFrame, "Make sure to fill out all fields correctly and" + 
                                            " enter numbers for zip code, insurance number, and phone number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (!Numeric(PatientID)) {
            JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        int patientIDInt;
        try {
            patientIDInt = Integer.parseInt(PatientID);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try (Connection connection = ConnectDB.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE patient SET ");
            List<Object> parameters = new ArrayList<>();
            String[] fieldNames = {"first_name", "last_name", "date_of_birth", "gender", "address", "city", "state", "zip_code", "insurance_company", "insurance_number", "phone_number", "xray_images"};
            
            boolean first = true;
            for (int i = 0; i < Fields.length - 1; i++) {
                if (!Fields[i].getText().isEmpty()) {
                    if (!first) {
                        sql.append(", ");
                    }
                    sql.append(fieldNames[i]).append(" = ?");
                    first = false;
                    if (i == 2 && ValidDate(Fields[i].getText())) {
                        parameters.add(java.sql.Date.valueOf(Fields[i].getText()));
                    } else if ((i == 7 || i == 9 || i == 10) && Numeric(Fields[i].getText())) {
                        parameters.add(Long.parseLong(Fields[i].getText()));
                    } else {
                        parameters.add(Fields[i].getText());
                    }
                }
            }
    
            sql.append(" WHERE patient_id = ?");
            parameters.add(patientIDInt);
    
            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < parameters.size(); i++) {
                    Object param = parameters.get(i);
                    if (param instanceof String) {
                        statement.setString(i + 1, (String) param);
                    } else if (param instanceof Long) {
                        statement.setLong(i + 1, (Long) param);
                    } else if (param instanceof java.sql.Date) {
                        statement.setDate(i + 1, (java.sql.Date) param);
                    } else if (param instanceof Integer) {
                        statement.setInt(i + 1, (Integer) param);
                    }
                }
    
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    JOptionPane.showMessageDialog(ParentFrame, "Patient ID not found", "Patient ID Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                JOptionPane.showMessageDialog(ParentFrame, "Patient information updated successfully.");
                PopulatePatientTable(TableModelPI, ParentFrame);
                ClearFields(Fields);
            }
        } catch (SQLException e) {
            if (e instanceof PSQLException && e.getMessage().contains("syntax error at or near")) {
                JOptionPane.showMessageDialog(ParentFrame, "Patient ID not found", "Patient ID Error", JOptionPane.ERROR_MESSAGE);
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(ParentFrame, "Error occurred while updating patient information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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
                Connection connection = ConnectDB.getConnection();
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
        String PatientIDText = Fields[12].getText();
    
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
            Connection connection = ConnectDB.getConnection();
            String sql = "SELECT * FROM patient WHERE UPPER(first_name) LIKE UPPER(?) AND UPPER(last_name) LIKE UPPER(?) AND (patient_id = ? OR ? = -1) ORDER BY last_name ASC";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + FirstName + "%");
            statement.setString(2, "%" + LastName + "%");
            statement.setInt(3, patientID);
            statement.setInt(4, patientID);
    
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                Object[] RowData = new Object[12];
                for (int i = 1; i <= 12; i++) {
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
            Connection connection = ConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT patient_id, first_name, last_name, date_of_birth, gender, address,"+
                                             "city, state, zip_code, insurance_company, insurance_number, phone_number, xray_images FROM patient ORDER BY last_name ASC");

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
                        String.valueOf(resultSet.getLong(11)),
                        String.valueOf(resultSet.getLong(12)),
                        resultSet.getString(13)
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
        "\nClick Xray Image link next to patient to view xray images"+
        "\nTo update patient information type Patient ID and put data into fields that need to be changed."+
        "\nEnsure all fields marked with * are filled out. Ensure correct date format for patient DOB and numerical value for Zip Code, Insurance Number, Phone Number, and Patient ID."+ 
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

    
    public static boolean ValidateLogin(String EnteredUsername, char[] EnteredPassword) {
        String Password = new String(EnteredPassword); 

        try {
            Connection connection = ConnectDB.getConnection();
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
