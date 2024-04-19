package src;
//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//Logic for appointment window GUI
public class Appointment_Window {


    public static final String AppointmentWindow = null;
    //Code that searches appointments for table in AppointmentWindow
    public static void SearchAppointments(DefaultTableModel TableModel, JTextField[] Fields, JFrame ParentFrame) {
        String patientIDText = Fields[0].getText();
        String appointmentIDText = Fields[11].getText();
    
        // Validate patientID using the Numeric function
        int patientID = -1;
        if (!patientIDText.isEmpty() && Patient_Window.Numeric(patientIDText)) {
            patientID = Integer.parseInt(patientIDText);
        } else if (!patientIDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Validate appointmentID using the Numeric function
        int appointmentID = -1;
        if (!appointmentIDText.isEmpty() && Patient_Window.Numeric(appointmentIDText)) {
            appointmentID = Integer.parseInt(appointmentIDText);
        } else if (!appointmentIDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Appointment ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        TableModel.setRowCount(0);
    
        try {
            Connection connection = ConnectDB.getConnection();
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
        try {
            Connection connection = ConnectDB.getConnection();
    
            // Check for empty fields, non-numeric inputs, incorrect time format, and incorrect date format.
            for (int i = 0; i < 9; i++) {
                if (i != 5 && i != 6 && Fields[i].getText().isEmpty()) {
                    JOptionPane.showMessageDialog(ParentFrame, "Make sure to fill out all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if ((i == 7 && !Patient_Window.ValidDate(Fields[i].getText())) ||
                        (i == 8 && !Patient_Window.ValidTime(Fields[i].getText()))) {
                    JOptionPane.showMessageDialog(ParentFrame, "Make sure to fill out all fields correctly with the correct format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
    
            // Check if there's a 30-minute gap between appointments for dental workers.
            LocalTime newAppointmentTime = LocalTime.parse(Fields[8].getText(), DateTimeFormatter.ofPattern("HH:mm"));
            java.sql.Date newAppointmentDate = java.sql.Date.valueOf(Fields[7].getText());
    
            // Calculate the time 30 minutes before the new appointment.
            LocalTime thirtyMinutesBefore = newAppointmentTime.minusMinutes(29);
            Time thirtyMinutesBeforeTime = Time.valueOf(thirtyMinutesBefore);
    
            // Calculate the time 30 minutes after the new appointment.
            LocalTime thirtyMinutesAfter = newAppointmentTime.plusMinutes(29);
            Time thirtyMinutesAfterTime = Time.valueOf(thirtyMinutesAfter);
    
            String sqlOverlapCheck = "SELECT COUNT(*) FROM appointment WHERE appointment_date = ? AND " +
                     "(dentist_id = ? OR dental_hygienist_id = ? OR dental_assistant_id = ? OR dental_surgeon_id = ?) AND " +
                     "((appointment_time >= ? AND appointment_time <= ?) OR (appointment_time <= ? AND appointment_time >= ?))";
            PreparedStatement statementOverlapCheck = connection.prepareStatement(sqlOverlapCheck);
    
            statementOverlapCheck.setDate(1, newAppointmentDate);
            statementOverlapCheck.setInt(2, Integer.parseInt(Fields[3].getText()));
            statementOverlapCheck.setInt(3, Integer.parseInt(Fields[4].getText())); 
            if (Fields[5].getText().isEmpty()) {
                statementOverlapCheck.setNull(4, java.sql.Types.INTEGER);
            } else {
                statementOverlapCheck.setInt(4, Integer.parseInt(Fields[5].getText())); 
            }
            if (Fields[6].getText().isEmpty()) {
                statementOverlapCheck.setNull(5, java.sql.Types.INTEGER);
            } else {
                statementOverlapCheck.setInt(5, Integer.parseInt(Fields[6].getText())); 
            }
            statementOverlapCheck.setTime(6, thirtyMinutesBeforeTime);
            statementOverlapCheck.setTime(7, thirtyMinutesAfterTime);
            statementOverlapCheck.setTime(8, thirtyMinutesBeforeTime);
            statementOverlapCheck.setTime(9, thirtyMinutesAfterTime);
    
            ResultSet resultSetOverlapCheck = statementOverlapCheck.executeQuery();
            resultSetOverlapCheck.next();
            int count = resultSetOverlapCheck.getInt(1);
    
            if (count != 0) {
                JOptionPane.showMessageDialog(ParentFrame, "There must be at least 30 minutes between appointments for dental workers.", "Dental Worker Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            //Inserts info typed into AppointmentWindow into the database.

            String sql = "INSERT INTO appointment (patient_id, procedure_id, procedure_occurrences, dentist_id, dental_hygienist_id," +
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
            Patient_Window.ClearFields(Fields);
    
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
            JOptionPane.showMessageDialog(ParentFrame, "Fields Patient ID, Procedure ID, Procedure Occurrences, Dentist ID, Dental Hygienist ID, " +
                                            "Dental Assistant ID, Dental Surgeon ID must be integers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    //Code deletes patient information in the database. Is used in AppointmentWindow
    public static void DeleteAppointment(String AppointmentID, DefaultTableModel TableModel, JTextField[] Fields, JFrame ParentFrame) {

        if (!Patient_Window.Numeric(AppointmentID)) {
            JOptionPane.showMessageDialog(ParentFrame, "Appointment ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(ParentFrame, "Are you sure you want to delete Appointment " + AppointmentID + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {

            try {
                Connection connection = ConnectDB.getConnection();
                String sql = "DELETE FROM appointment WHERE appointment_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(AppointmentID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(ParentFrame, "Appointment deleted successfully.");
                PopulateAppointmentTable(TableModel, ParentFrame);
                Patient_Window.ClearFields(Fields);
                
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
            Connection connection = ConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT appointment_id, patient_id, procedure_id, procedure_occurrences, dentist_id,"+
                                                         "dental_hygienist_id, dental_assistant_id, dental_surgeon_id, appointment_date, appointment_time, notes, canceled FROM appointment ORDER BY appointment_date DESC, appointment_time DESC;");
    
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
        if (!appointmentIDText.isEmpty() && Patient_Window.Numeric(appointmentIDText)) {
            appointmentID = Integer.parseInt(appointmentIDText);
        } else if (!appointmentIDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Appointment ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Update the "canceled" field in the database
        try {
            Connection connection = ConnectDB.getConnection();
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
            Connection connection = ConnectDB.getConnection();
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
            Connection connection = ConnectDB.getConnection();
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
            Connection connection = ConnectDB.getConnection();
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
            Connection connection = ConnectDB.getConnection();
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
        Connection connection = ConnectDB.getConnection();
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
        "\nEnsure dental specialists have a 30 min gap between appointments or appointment cannot be created."+
        "\nEnsure all fields marked with * are filled out. Ensure correct date format for Appointment Date." + 
        "\nEnsure numerical value for all ID fields."+ 
        "\nTo mark appointment as canceled only use Appointment ID and Canceled fields. Type 'true' in Canceled field to cancel appointment." + 
        " Type 'false' to uncancel.", "Appointment Help Window", JOptionPane.INFORMATION_MESSAGE);
    }

}
