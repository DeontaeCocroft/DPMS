package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

//Code for prescription window
public class DPMSPrescription_Window {

    public static void PrescriptionWindow() {
        JFrame PrescriptionWindow = new JFrame("Prescription & Billing");

        //Make sure the prescription window doesn't appear behind main menu due to loading screen
        PrescriptionWindow.setAlwaysOnTop(!PrescriptionWindow.isAlwaysOnTop());
        PrescriptionWindow.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e){
                Timer timer = new Timer(250, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                    PrescriptionWindow.setAlwaysOnTop(false);
                     }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        //Icon for Prescription window
        ImageIcon LogoIcon = new ImageIcon("Images\\prescription.png");
        PrescriptionWindow.setIconImage(LogoIcon.getImage());

        //Size of window
        PrescriptionWindow.setSize(1800,600);
        PrescriptionWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        PrescriptionWindow.setLocationRelativeTo(null);

        JPanel MainPanelPreW = new JPanel(new BorderLayout());

        //Table to display prescription billing information
        DefaultTableModel TableModelPreBI = new DefaultTableModel();
        TableModelPreBI.addColumn("Prescription Bill Info ID");
        TableModelPreBI.addColumn("Patient ID");
        TableModelPreBI.addColumn("Prescription ID");
        TableModelPreBI.addColumn("Quantity");
        TableModelPreBI.addColumn("Total");

        
        JTable PrescriptionBillTable = new JTable(TableModelPreBI);

        //populate table with data from database
        PopulatePrescriptionBillTable(TableModelPreBI, PrescriptionWindow);

        JScrollPane ScrollPane = new JScrollPane(PrescriptionBillTable);
        MainPanelPreW.add(ScrollPane, BorderLayout.CENTER);

        //Table to display prescription information
        DefaultTableModel TableModelPreI = new DefaultTableModel();
        TableModelPreI.addColumn("Prescription ID");
        TableModelPreI.addColumn("Name");
        TableModelPreI.addColumn("Notes");
        TableModelPreI.addColumn("Price");
        TableModelPreI.addColumn("Items In Stock");

        JTable PrescriptionTable = new JTable(TableModelPreI);

        //Set size for specific columns
        PrescriptionTable.getColumnModel().getColumn(2).setPreferredWidth(500);
        PrescriptionTable.getColumnModel().getColumn(1).setPreferredWidth(150);

        //populate table with data from database
        PopulatePrescriptionTable(TableModelPreI, PrescriptionWindow);

        //Set layout of tables
        JScrollPane ScrollPane2 = new JScrollPane(PrescriptionTable);
        MainPanelPreW.add(ScrollPane2, BorderLayout.SOUTH);

        Box VerticalBox = Box.createVerticalBox();
        VerticalBox.add(ScrollPane);
        VerticalBox.add(ScrollPane2);
        MainPanelPreW.add(VerticalBox, BorderLayout.CENTER);

        //Fields to enter prescription bill info into to database
        JPanel PrescriptionFieldsPanel = new JPanel(new BorderLayout());
        JPanel PrescriptionFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 10);

        String[] Labels = {"*Patient ID (For Save or Search):", "*Prescription ID:", "*Quantity:", "Prescription Bill ID (For Search or Delete):"};

        JTextField[] Fields = new JTextField[Labels.length];

        for (int i = 0; i < Labels.length; i++) {
            JLabel label = new JLabel(Labels[i]);
            JTextField field = new JTextField();
            field.setPreferredSize(new Dimension(150, 30));
            gbc.gridx = 0;
            gbc.gridy = i;
            PrescriptionFields.add(label, gbc);
            gbc.gridx = 1;
            PrescriptionFields.add(field, gbc);
            Fields[i] = field;
        }

        PrescriptionFieldsPanel.add(PrescriptionFields, BorderLayout.NORTH);
        MainPanelPreW.add(PrescriptionFieldsPanel, BorderLayout.WEST);

        //Prescription Buttons
        JPanel ButtonPanelPreW = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        //Button to save prescription bill info into database
        JButton SaveButtonPreW = new JButton("Save");
        SaveButtonPreW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               SavePrescriptionBillInfo(Fields, TableModelPreBI, PrescriptionWindow);
               PopulatePrescriptionTable(TableModelPreI, PrescriptionWindow);
            }
        });
        ButtonPanelPreW.add(SaveButtonPreW);
        SaveButtonPreW.setBackground(Color.BLUE);
        SaveButtonPreW.setForeground(Color.WHITE);

        //Button to delete Prescription Bill Info
        JButton DeleteButtonPreW = new JButton("Delete");
        DeleteButtonPreW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeletePrescriptionBillInfo(Fields[3].getText(), TableModelPreBI, Fields, PrescriptionWindow);
                PopulatePrescriptionTable(TableModelPreI, PrescriptionWindow);
            }
        });
        ButtonPanelPreW.add(DeleteButtonPreW);
        DeleteButtonPreW.setBackground(Color.BLUE);
        DeleteButtonPreW.setForeground(Color.WHITE);

        //Button to search prescription bill
        JButton SearchButtonPreW = new JButton("Search");
        SearchButtonPreW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchPrescriptionBill(TableModelPreBI, Fields, PrescriptionWindow);
            }
        });
        ButtonPanelPreW.add(SearchButtonPreW);
        SearchButtonPreW.setBackground(Color.BLUE);
        SearchButtonPreW.setForeground(Color.WHITE);

        //Button to clear Prescription Bill info
        JButton ClearButtonPreW = new JButton("Clear Fields");
        ClearButtonPreW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DPMSPatient_Window.ClearFields(Fields);
            }
        });
        ButtonPanelPreW.add(ClearButtonPreW);
        ClearButtonPreW.setBackground(Color.BLUE);
        ClearButtonPreW.setForeground(Color.WHITE);

        //Button to patients
        JButton PatientButtonPreW = new JButton("Patients");
        PatientButtonPreW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (DPMSGui.count == 1){
                    // Show loading screen
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

        ButtonPanelPreW.add((PatientButtonPreW));
        PatientButtonPreW.setBackground(Color.BLUE);
        PatientButtonPreW.setForeground(Color.WHITE);

        //Button for Prescription Bill window help
        JButton HelpButtonPW = new JButton("Help");
        HelpButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                HelpPrescription(PrescriptionWindow, PrescriptionWindow);
            }
        });
        ButtonPanelPreW.add(HelpButtonPW);
        HelpButtonPW.setBackground(Color.BLUE);
        HelpButtonPW.setForeground(Color.WHITE);

        MainPanelPreW.add(ButtonPanelPreW, BorderLayout.SOUTH);
        PrescriptionWindow.add(MainPanelPreW);
        PrescriptionWindow.setVisible(true);
        PrescriptionBillTable.setDefaultEditor(Object.class, null);
        PrescriptionTable.setDefaultEditor(Object.class, null);

    }
    
    //Code that displays data into Prescription Bill table in window
    public static void PopulatePrescriptionBillTable(DefaultTableModel TableModelPPBT, JFrame ParentFrame){
        TableModelPPBT.setRowCount(0);

        try{
            Connection connection = DPMSConnectDB.getConnection();
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
            Connection connection = DPMSConnectDB.getConnection();
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
            if (Fields[i].getText().trim().isEmpty() || !DPMSPatient_Window.Numeric(Fields[i].getText())) {
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

    try (Connection connection = DPMSConnectDB.getConnection();
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
                DPMSPatient_Window.ClearFields(Fields);
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
        
        if (!DPMSPatient_Window.Numeric(PrescriptionBillInfoID)) {
            JOptionPane.showMessageDialog(ParentFrame, "Prescription Bill Info ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(ParentFrame, "Are you sure you want to delete Prescription Bill " + PrescriptionBillInfoID + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION){
            try{
                Connection connection = DPMSConnectDB.getConnection();
                String sql = "DELETE FROM prescription_bill_info WHERE prescription_bill_info_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(PrescriptionBillInfoID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(ParentFrame, "Prescription Bill deleted successfully.");
                PopulatePrescriptionBillTable(TableModelCDPBI, ParentFrame);
                DPMSPatient_Window.ClearFields(Fields);

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
            if (DPMSPatient_Window.Numeric(PrescriptionBillInfoIDText)) {
                PrescriptionBillInfoID = Integer.parseInt(PrescriptionBillInfoIDText);
            } else {
                JOptionPane.showMessageDialog(ParentFrame, "Prescription Bill ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
                return; 
            }
        }

        int PatientID = -1;
        if (!PatientIDText.isEmpty()) {
            if (DPMSPatient_Window.Numeric(PatientIDText)) {
                PatientID = Integer.parseInt(PatientIDText);
            } else {
                JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
                return; 
            }
}

        TableModelSPB.setRowCount(0);

        try {
            Connection connection = DPMSConnectDB.getConnection();
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

    //Code that displays help message for Prescription Bill window
    public static void HelpPrescription(JFrame frame, JFrame ParentFrame){
        JOptionPane.showMessageDialog(ParentFrame, "To search Prescription Bill only use Patient ID or Prescription Bill ID Fields."+ 
        "\nTo delete Prescription Bill only use Prescription Bill ID field."+ 
        "\nEnsure all fields marked with * are filled out. Ensure numerical value for Patient ID, Prescription ID, Quantity, and Prescription Bill ID.", "Prescription Help Window", JOptionPane.INFORMATION_MESSAGE);
    }
}