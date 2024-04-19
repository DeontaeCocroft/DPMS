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

//Logic for prescription window GUI
public class Prescription_Window {
    
    //Code that displays data into Prescription Bill table in window
    public static void PopulatePrescriptionBillTable(DefaultTableModel TableModelPPBT, JFrame ParentFrame){
        TableModelPPBT.setRowCount(0);

        try{
            Connection connection = ConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT prescription_bill_info.prescription_bill_info_id, prescription_bill_info.patient_id,\r\n" +
                                "    prescription.prescription_id, prescription_bill_info.quantity,\r\n" +
                                "    (prescription.price * prescription_bill_info.quantity) AS total\r\n" +
                                "FROM prescription_bill_info\r\n" +
                                "INNER JOIN prescription ON prescription_bill_info.prescription_id = prescription.prescription_id\r\n" +
                                "ORDER BY prescription_bill_info.prescription_bill_info_id DESC;");

            while (resultSet.next()){
                String[] RowData = {
                    String.valueOf(resultSet.getInt(1)),
                    String.valueOf(resultSet.getInt(2)),
                    String.valueOf(resultSet.getInt(3)),
                    String.valueOf(resultSet.getInt(4)),
                    String.valueOf(resultSet.getDouble(5)),
                };
                 TableModelPPBT.addRow(RowData);
            }

            connection.close();
        }   catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while fetching prescription bill information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    //Code that displays data into Prescription table in window
    public static void PopulatePrescriptionTable(DefaultTableModel TableModelPPT, JFrame ParentFrame){
        TableModelPPT.setRowCount(0);
        
        try{
            Connection connection = ConnectDB.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT prescription.prescription_id, prescription.name, prescription.notes,\r\n" +
                                "prescription.price, (prescription.items_in_stock - (SELECT COALESCE(SUM(prescription_bill_info.quantity), 0)\r\n" +
                                "FROM prescription_bill_info\r\n" +
                                "WHERE prescription_bill_info.prescription_id = prescription.prescription_id)) as items_in_stock\r\n" +
                                "FROM prescription\r\n" +
                                "ORDER BY prescription.prescription_id;");

            while (resultSet.next()){
                String[] RowData = {
                    String.valueOf(resultSet.getInt(1)),
                    String.valueOf(resultSet.getString(2)),
                    String.valueOf(resultSet.getString(3)),
                    String.valueOf(resultSet.getDouble(4)),
                    String.valueOf(resultSet.getInt(5)),
                };
                 TableModelPPT.addRow(RowData);
            }

            connection.close();
        }   catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while fetching prescription information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    //Code that saves prescription bill info from fields into the database
    public static void SavePrescriptionBillInfo(JTextField[] Fields, DefaultTableModel TableModelPreI, JFrame ParentFrame) {

        //Checks for correct format and empty field
        for (int i = 0; i < Fields.length - 1; i++) {
            if (Fields[i].getText().trim().isEmpty() || !Patient_Window.Numeric(Fields[i].getText())) {
                JOptionPane.showMessageDialog(ParentFrame, "Please make sure fields are filled out correctly and enter a numeric value for all fields with *.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    
    //Check for adequate stock before adding bill
    int prescriptionID = Integer.parseInt(Fields[1].getText().trim());
    int quantity = Integer.parseInt(Fields[2].getText().trim());

    String checkStockSql = "SELECT (prescription.items_in_stock - (SELECT COALESCE(SUM(prescription_bill_info.quantity), 0) " +
                            "FROM prescription_bill_info " +
                            "WHERE prescription_bill_info.prescription_id = ?)) as adjusted_stock " +
                            "FROM prescription WHERE prescription.prescription_id = ?";

    try (Connection connection = ConnectDB.getConnection();
         PreparedStatement checkStockStmt = connection.prepareStatement(checkStockSql)) {
        
        checkStockStmt.setInt(1, prescriptionID);
        checkStockStmt.setInt(2, prescriptionID);
        ResultSet rs = checkStockStmt.executeQuery();
        
        //Inserts types info from prescription bill fields into database is stock is adequate
        if (rs.next() && rs.getInt("adjusted_stock") >= quantity) {
            String insertSql = "INSERT INTO prescription_bill_info (patient_id, prescription_id, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {

                //Converts input to match data type
                insertStmt.setInt(1, Integer.parseInt(Fields[0].getText().trim())); 
                insertStmt.setInt(2, prescriptionID);
                insertStmt.setInt(3, quantity);

                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(ParentFrame, "Prescription Bill information saved successfully.");
                Patient_Window.ClearFields(Fields);
                PopulatePrescriptionBillTable(TableModelPreI, ParentFrame);
            }
        } else {
            JOptionPane.showMessageDialog(ParentFrame, "Creating this bill would result in negative stock for the prescription, therefore cannot be saved.", "Stock Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    } catch (SQLException e) {
        if (e.getMessage().contains("violates foreign key constraint \"prescription_bill_info_patient_id_fkey\"")) {
            JOptionPane.showMessageDialog(ParentFrame, "The specified Patient ID does not exist.", "Patient ID error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while checking stock or saving Prescription Bill information: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    

    //Code that delete prescription bill info from database using info from fields
    public static void DeletePrescriptionBillInfo(String PrescriptionBillInfoID, DefaultTableModel TableModelCDPBI, JTextField[] Fields, JFrame ParentFrame ){
        
        if (!Patient_Window.Numeric(PrescriptionBillInfoID)) {
            JOptionPane.showMessageDialog(ParentFrame, "Prescription Bill Info ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(ParentFrame, "Are you sure you want to delete Prescription Bill " + PrescriptionBillInfoID + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION){
            try{
                Connection connection = ConnectDB.getConnection();
                String sql = "DELETE FROM prescription_bill_info WHERE prescription_bill_info_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(PrescriptionBillInfoID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(ParentFrame, "Prescription Bill deleted successfully.");
                PopulatePrescriptionBillTable(TableModelCDPBI, ParentFrame);
                Patient_Window.ClearFields(Fields);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(ParentFrame, "Error occurred while deleting Prescription Bill.");
            }
        }
    }

    //code that searches prescription bill information using certain information
    public static void SearchPrescriptionBill(DefaultTableModel TableModelSPB, JTextField[] Fields, JFrame ParentFrame){
  
        String PatientIDText = Fields[0].getText();
        String PrescriptionBillInfoIDText = Fields[3].getText();

        int PrescriptionBillInfoID = -1;

        ////Checks for correct format and empty field
        if (!PrescriptionBillInfoIDText.isEmpty()) {
            if (Patient_Window.Numeric(PrescriptionBillInfoIDText)) {
                PrescriptionBillInfoID = Integer.parseInt(PrescriptionBillInfoIDText);
            } else {
                JOptionPane.showMessageDialog(ParentFrame, "Prescription Bill ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
                return; 
            }
        }

        int PatientID = -1;
        if (!PatientIDText.isEmpty()) {
            if (Patient_Window.Numeric(PatientIDText)) {
                PatientID = Integer.parseInt(PatientIDText);
            } else {
                JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
                return; 
            }
}

        TableModelSPB.setRowCount(0);

        try {
            Connection connection = ConnectDB.getConnection();
            String sql = "SELECT prescription_bill_info.prescription_bill_info_id, prescription_bill_info.patient_id,\r\n" +
                                "    prescription.prescription_id, prescription_bill_info.quantity,\r\n" +
                                "    (prescription.price * prescription_bill_info.quantity) AS total\r\n" +
                                "FROM prescription_bill_info\r\n" +
                                "INNER JOIN prescription ON prescription_bill_info.prescription_id = prescription.prescription_id\r\n" +
                                "WHERE (prescription_bill_info.patient_id = ? OR ? = -1) AND (prescription_bill_info.prescription_bill_info_id = ? OR ? = -1) \r\n" +
                                "ORDER BY prescription_bill_info.prescription_bill_info_id DESC;";
                                
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, PatientID);
            statement.setInt(2, PatientID);
            statement.setInt(3, PrescriptionBillInfoID);
            statement.setInt(4, PrescriptionBillInfoID);
            

            ResultSet resultSet = statement.executeQuery();

            
            while (resultSet.next()) {
                Object[] RowData = new Object[5];
                for (int i = 1; i <= 5; i++) {
                    RowData[i - 1] = resultSet.getObject(i);
                }
                TableModelSPB.addRow(RowData);
            }

            connection.close();

        }catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while searching Prescription Bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    //code that creates a pdf for patients prescription bills
    @SuppressWarnings("resource")
    public static void searchPrescriptionBillsAndCreatePDF(int prescriptionBillID, JFrame parentFrame) {
        if (prescriptionBillID < 0) {
            JOptionPane.showMessageDialog(parentFrame, "Prescription Bill ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String pdfPath = "Prescription Bills/Prescription_Bill_Report_ID_" + prescriptionBillID + ".pdf"; 
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
            contentStream.showText("Detailed Prescription Bill Report");
            contentStream.newLine();
            contentStream.newLine();
            
            //Pull information from database.
            Connection connection = ConnectDB.getConnection();
            String sql = "SELECT prescription_bill_info.prescription_bill_info_id, prescription_bill_info.patient_id,\r\n" +
                        "    prescription.prescription_id, prescription_bill_info.quantity,\r\n" +
                        "    prescription.price, (prescription.price * prescription_bill_info.quantity) AS total,\r\n" +
                        "    patient.first_name, patient.last_name, patient.date_of_birth, patient.gender, patient.address,\r\n" +
                        "    patient.city, patient.state, patient.zip_code, patient.insurance_company, patient.insurance_number,\r\n" +
                        "    prescription.name AS prescription_name, prescription.notes AS prescription_notes\r\n" +
                        "FROM prescription_bill_info\r\n" +
                        "INNER JOIN prescription ON prescription_bill_info.prescription_id = prescription.prescription_id\r\n" +
                        "INNER JOIN patient ON prescription_bill_info.patient_id = patient.patient_id\r\n" +
                        "WHERE prescription_bill_info.prescription_bill_info_id = ? " +
                        "ORDER BY prescription_bill_info.prescription_bill_info_id DESC";

    
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, prescriptionBillID);
            ResultSet resultSet = statement.executeQuery();
    
            if (!resultSet.next()) {
                JOptionPane.showMessageDialog(parentFrame, "Prescription Bill ID " + prescriptionBillID + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            //copy database information into content stream
            do {
                contentStream.showText("Prescription Bill Number: " + prescriptionBillID);
                contentStream.newLine();
                contentStream.showText("Prescription Number: " + resultSet.getInt("prescription_id"));
                contentStream.newLine();
                contentStream.showText("Prescription Name: " + resultSet.getString("prescription_name"));
                contentStream.newLine();
                contentStream.showText("Prescription Notes: " + resultSet.getString("prescription_notes"));
                contentStream.newLine();
                contentStream.showText("Price: " + resultSet.getDouble("price"));
                contentStream.newLine();
                contentStream.showText("Quantity: " + resultSet.getInt("quantity"));
                contentStream.newLine();
                contentStream.showText("Total: " + resultSet.getDouble("total"));
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
    

    //Code that displays help message for Prescription Bill window
    public static void HelpPrescription(JFrame frame, JFrame ParentFrame){
        JOptionPane.showMessageDialog(ParentFrame, "To search Prescription Bill only use Patient ID or Prescription Bill ID Fields."+ 
        "\nTo delete Prescription Bill only use Prescription Bill ID field."+ 
        "\nOnly fill out Prescription Bill ID to save pdf"+
        "\nEnsure all fields marked with * are filled out. Ensure numerical value for Patient ID, Prescription ID, Quantity, and Prescription Bill ID.", "Prescription Help Window", JOptionPane.INFORMATION_MESSAGE);
    }
}