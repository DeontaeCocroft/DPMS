package src;
//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.sql.*;

//logic for Billing window GUI
public class Billing_Window {

    //Code that populates bills table in BillWindow
    public static void PopulateBillTable(DefaultTableModel TableModelPBT, JFrame ParentFrame) {
        TableModelPBT.setRowCount(0);
        try {
            Connection connection = ConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT bill.bill_id, bill.appointment_id, bill.notes, bill.is_paid,\r\n" +
                            "       appointment.procedure_occurrences, appointment.procedure_id, procedure.price,\r\n" +
                            "       (appointment.procedure_occurrences * procedure.price) AS total\r\n" +
                            "FROM bill\r\n" +
                            "INNER JOIN appointment ON bill.appointment_id = appointment.appointment_id\r\n" +
                            "INNER JOIN procedure ON appointment.procedure_id = procedure.procedure_id\r\n" +
                            "ORDER BY bill.bill_id DESC;");

            while (resultSet.next()) {
                String[] RowData = {
                        String.valueOf(resultSet.getInt(1)),
                        String.valueOf(resultSet.getInt(2)),
                        resultSet.getString(3),
                        String.valueOf(resultSet.getBoolean(4)),
                        String.valueOf(resultSet.getInt(5)),
                        String.valueOf(resultSet.getInt(6)),
                        String.valueOf(resultSet.getDouble(7)),
                        String.valueOf(resultSet.getDouble(8)),
                        
                };
                TableModelPBT.addRow(RowData);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while fetching patient information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that saves Bill info when save button is used in BillWindow.
    public static void SaveBillInfo(JTextField[] Fields, DefaultTableModel TableModelBI, JFrame ParentFrame) {
        
        if (Fields[0].getText().trim().isEmpty() || !Patient_Window.Numeric(Fields[0].getText())) {
            JOptionPane.showMessageDialog(ParentFrame, "Please enter a valid numerical value for Appointment ID.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Connection connection = ConnectDB.getConnection();

            String sql = "INSERT INTO bill (appointment_id, is_paid, notes)  VALUES (?, ?, ?)";

            //convert inputs to match database column data types
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(Fields[0].getText()));
            statement.setBoolean(2, Boolean.parseBoolean(Fields[1].getText()));
            statement.setString(3, Fields[2].getText());

            statement.executeUpdate();
            connection.close();
            JOptionPane.showMessageDialog(ParentFrame, "Bill information saved successfully.");

            PopulateBillTable(TableModelBI, ParentFrame);
            Patient_Window.ClearFields(Fields);

        } catch (SQLException e) {
            if (e.getMessage().contains("violates foreign key constraint \"bill_appointment_id_fkey\"")) {
                JOptionPane.showMessageDialog(ParentFrame, "The specified Appointment ID does not exist", "Appointment ID Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(ParentFrame, "Error occurred while saving Bill information: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Delete bill information from database
    public static void DeleteBillInfo(String BillID, DefaultTableModel TableModelCDBI, JTextField[] Fields, JFrame ParentFrame ){
        
        if (!Patient_Window.Numeric(BillID)) {
            JOptionPane.showMessageDialog(ParentFrame, "Bill ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(ParentFrame, "Are you sure you want to delete Bill " + BillID + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION){
            try{
                Connection connection = ConnectDB.getConnection();
                String sql = "DELETE FROM bill WHERE bill_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(BillID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(ParentFrame, "Bill deleted successfully.");
                PopulateBillTable(TableModelCDBI, ParentFrame);
                Patient_Window.ClearFields(Fields);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(ParentFrame, "Error occurred while deleting patient: Patient has appointment or bills and cannot be deleted.");
            }
        }
    }

    //Function that searches appointments for table in AppointmentWindow
    public static void SearchBills(DefaultTableModel TableModel, JTextField[] Fields, JFrame ParentFrame) {
        String appointmentIDText = Fields[0].getText();
        String billIDtext = Fields[3].getText();
        

        // Validate appointmentID using the Numeric function
        int appointmentID = -1;
        if (!appointmentIDText.isEmpty() && Patient_Window.Numeric(appointmentIDText)) {
            appointmentID = Integer.parseInt(appointmentIDText);
        } else if (!appointmentIDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Appointment ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate patientID using the Numeric function
        int billID = -1;
        if (!billIDtext.isEmpty() && Patient_Window.Numeric(billIDtext)) {
            billID = Integer.parseInt(billIDtext);
        } else if (!billIDtext.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TableModel.setRowCount(0);

        try {
            Connection connection = ConnectDB.getConnection();
            String sql = "SELECT bill.bill_id, bill.appointment_id, bill.notes, bill.is_paid,\r\n" +
            "       appointment.procedure_occurrences, appointment.procedure_id, procedure.price,\r\n" +
            "       (appointment.procedure_occurrences * procedure.price) AS total\r\n" +
            "FROM bill\r\n" +
            "INNER JOIN appointment ON bill.appointment_id = appointment.appointment_id\r\n" +
            "INNER JOIN procedure ON appointment.procedure_id = procedure.procedure_id\r\n" +
            "WHERE (bill.bill_id = ? OR ? = -1) AND (bill.appointment_id = ? OR ? = -1)\r\n" +
            "ORDER BY bill.bill_id DESC;";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, billID);
            statement.setInt(2, billID);
            statement.setInt(3, appointmentID);
            statement.setInt(4, appointmentID);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Object[] RowData = new Object[8];
                for (int i = 1; i <= 8; i++) {
                    RowData[i - 1] = resultSet.getObject(i);
                }

                RowData[6] = resultSet.getDouble(7);
                RowData[7] = resultSet.getDouble(8);
                TableModel.addRow(RowData);
            }


            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while searching bills: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        }
    
    //Code that works to use certain information to mark a bill as paid
    public static void MarkIsPaid(DefaultTableModel TableModel, JTextField[] Fields, JFrame ParentFrame) {
        String billDText = Fields[3].getText();
        boolean isPaid = Boolean.parseBoolean(Fields[1].getText());
    
        // Validate Bill ID using the Numeric function
        int billID = -1;
        if (!billDText.isEmpty() && Patient_Window.Numeric(billDText)) {
            billID = Integer.parseInt(billDText);
        } else if (!billDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Bill ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Update the "is paid" field in the database
        try {
            Connection connection = ConnectDB.getConnection();
            String updateSql = "UPDATE bill SET is_paid = ? WHERE bill_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setBoolean(1, isPaid);
            updateStatement.setInt(2, billID);
    
            int rowsAffected = updateStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(ParentFrame, "Paid marked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                PopulateBillTable(TableModel, ParentFrame);
                Patient_Window.ClearFields(Fields);

            } else {
                JOptionPane.showMessageDialog(ParentFrame, "No bill found with the provided Bill ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while marking is Paid: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 

    //code that create pdf for patient bills
    @SuppressWarnings("resource")
    public static void searchBillsAndCreatePDF(int billID, JFrame parentFrame) {
        if (billID < 0) {
            JOptionPane.showMessageDialog(parentFrame, "Bill ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String pdfPath = "Bills/Bill_Report_ID_" + billID + ".pdf"; 
        String fontPath = "Fonts/Times New Roman.ttf";
    
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
    
            PDType0Font font = PDType0Font.load(document, new File(fontPath));
    
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
    
            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText("Detailed Bill Report");
            contentStream.newLine();
            contentStream.newLine();
    
            //Pull information from database.
            Connection connection = ConnectDB.getConnection();
            String sql = "SELECT bill.bill_id, bill.appointment_id, bill.notes, bill.is_paid, " +
                        "appointment.procedure_occurrences, appointment.procedure_id, procedure.price, " +
                        "(appointment.procedure_occurrences * procedure.price) AS total, " +
                        "patient.patient_id, patient.first_name, patient.last_name, " +
                        "patient.date_of_birth, patient.gender, patient.address, " +
                        "patient.city, patient.state, patient.zip_code, " +
                        "patient.insurance_company, patient.insurance_number, " +
                        "procedure.name, (procedure.notes) AS procedure_notes " +
                        "FROM bill " +
                        "INNER JOIN appointment ON bill.appointment_id = appointment.appointment_id " +
                        "INNER JOIN procedure ON appointment.procedure_id = procedure.procedure_id " +
                        "INNER JOIN patient ON appointment.patient_id = patient.patient_id " +
                        "WHERE bill.bill_id = ? " +
                        "ORDER BY bill.bill_id DESC";
    
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, billID);
            ResultSet resultSet = statement.executeQuery();
    
            if (!resultSet.next()) {
                JOptionPane.showMessageDialog(parentFrame, "Bill ID " + billID + " not found.", "Bill ID Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //copy database information into content stream
            do {
                contentStream.showText("Bill Number: " + resultSet.getInt("bill_id"));
                contentStream.newLine();
                contentStream.showText("Appointment Number: " + resultSet.getInt("appointment_id"));
                contentStream.newLine();
                contentStream.showText("Notes: " + resultSet.getString("notes"));
                contentStream.newLine();
                contentStream.showText("Is Paid: " + (resultSet.getBoolean("is_paid") ? "Yes" : "No"));
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Procedure: " + resultSet.getString("name"));
                contentStream.newLine();
                contentStream.showText("Procedure Notes: " + resultSet.getString("procedure_notes"));
                contentStream.newLine();
                contentStream.showText("Price: " + resultSet.getDouble("price"));
                contentStream.showText(" | Occurrences: " + resultSet.getInt("procedure_occurrences"));
                contentStream.showText(" | Total: " + resultSet.getDouble("total"));
                contentStream.newLine();
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Patient Number: " + resultSet.getInt("patient_id"));
                contentStream.showText(" | Patient First Name: " + resultSet.getString("first_name") + " | Patient Last Name: " + resultSet.getString("last_name"));
                contentStream.showText(" | DOB: " + resultSet.getString("date_of_birth"));
                contentStream.showText(" | Gender: " + resultSet.getString("gender"));
                contentStream.newLine();
                contentStream.showText("Address: " + resultSet.getString("address"));
                contentStream.showText(" | City: " + resultSet.getString("city"));
                contentStream.showText(" | State: " + resultSet.getString("state"));
                contentStream.showText(" | Zip Code: " + resultSet.getString("zip_code"));
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Insurance Company: " + resultSet.getString("insurance_company"));
                contentStream.showText(" | Insurance Policy Number: " + resultSet.getString("insurance_number"));
                contentStream.newLine();
                contentStream.newLine();
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Print Name: _____________________________________________________");
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Sign Name: _____________________________________________________");
                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Date: ________________________________");
    
            } while (resultSet.next());
    
             //convert content stream to pdf and save
            contentStream.endText();
            contentStream.close();
            resultSet.close();
            statement.close();
            connection.close();
    
            document.save(pdfPath);
            document.close();
            
    
            JOptionPane.showMessageDialog(parentFrame, "PDF created successfully at " + pdfPath, "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that displays help message in Bill Window 
    public static void HelpBilling(JFrame frame, JFrame ParentFrame){
        JOptionPane.showMessageDialog(ParentFrame, "To search Appointments only use Bill ID."+ 
        "\nEnsure all fields marked with * are filled out." + 
        "\nEnsure numerical value for all ID fields."+ 
        "\n To create bill pdf only fill out Bill ID field. "+
        "\nTo mark bill as paid only use Bill ID and Is Paid fields. Type 'true' in Is Paid field to mark bill as paid." + 
        " Type 'false' to mark as unpaid.", "Bill Help Window", JOptionPane.INFORMATION_MESSAGE);
    }
}