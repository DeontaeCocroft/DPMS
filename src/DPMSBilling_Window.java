package src;
//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.sql.*;

//Code for Billing window
public class DPMSBilling_Window {

    public static void BillWindow() {
        JFrame BillWindow = new JFrame("Billing");

        //Make sure the billing window doesn't appear behind main menu due to loading screen
        BillWindow.setAlwaysOnTop(!BillWindow.isAlwaysOnTop());
        BillWindow.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e){
                Timer timer = new Timer(250, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        BillWindow.setAlwaysOnTop(false);
                     }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        //Billing window icon
        ImageIcon LogoIcon = new ImageIcon("Images\\billing.png"); 
        BillWindow.setIconImage(LogoIcon.getImage());

        // Size of window
        BillWindow.setSize(1400, 400);
        BillWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        BillWindow.setLocationRelativeTo(null);

        JPanel MainPanelB = new JPanel(new BorderLayout());

        // Table to display Billing information
        DefaultTableModel TableModelB = new DefaultTableModel();
        TableModelB.addColumn("Bill ID");
        TableModelB.addColumn("Appointment ID");
        TableModelB.addColumn("Notes");
        TableModelB.addColumn("Is Paid");
        TableModelB.addColumn("Procedure Occurrences");
        TableModelB.addColumn("Procedure ID");
        TableModelB.addColumn("Price");
        TableModelB.addColumn("Total");

        JTable BillTable = new JTable(TableModelB);

        // Populate table with data from database
        PopulateBillTable(TableModelB, BillWindow);
        JScrollPane ScrollPane = new JScrollPane(BillTable);
        MainPanelB.add(ScrollPane, BorderLayout.CENTER);

        //Set size for specific column
        BillTable.getColumnModel().getColumn(4).setPreferredWidth(130);

        // Fields to enter all Billing info to database
        JPanel BillFieldsPanel = new JPanel(new BorderLayout());
        JPanel BillFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 10);

        String[] Labels = {"*Appointment ID (For Save and Search):", "Is Paid (For Save and Mark as Paid):", "Notes:", "Bill ID (For Search, Delete, and Mark as Paid):"};
        JTextField[] Fields = new JTextField[Labels.length];

        for (int i = 0; i < Labels.length; i++) {
            JLabel label = new JLabel(Labels[i]);
            JTextField field = new JTextField();
            field.setPreferredSize(new Dimension(150, 30));
            gbc.gridx = 0;
            gbc.gridy = i;
            BillFields.add(label, gbc);
            gbc.gridx = 1;
            BillFields.add(field, gbc);
            Fields[i] = field;
        }

        BillFieldsPanel.add(BillFields, BorderLayout.NORTH);

        MainPanelB.add(BillFieldsPanel, BorderLayout.WEST);

        //Bill add buttons
        JPanel ButtonPanelBW = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        //Button to save bill info into database
        JButton SaveButtonBW = new JButton("Save");
        SaveButtonBW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveBillInfo(Fields, TableModelB, BillWindow);
            }
        });
        ButtonPanelBW.add(SaveButtonBW);
        SaveButtonBW.setBackground(Color.BLUE);
        SaveButtonBW.setForeground(Color.WHITE);

        //Button to delete bill info into database
        JButton DeleteButtonBW = new JButton("Delete");
        DeleteButtonBW .addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteBillInfo(Fields[3].getText(), TableModelB, Fields, BillWindow);
            }
        });
        ButtonPanelBW.add(DeleteButtonBW );
        DeleteButtonBW .setBackground(Color.BLUE);
        DeleteButtonBW .setForeground(Color.WHITE);


        //Button to clear Bill info fields
        JButton ClearButtonBW = new JButton("Clear Fields");
        ClearButtonBW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DPMSPatient_Window.ClearFields(Fields);
            }
        });
        ButtonPanelBW.add(ClearButtonBW);
        ClearButtonBW.setBackground(Color.BLUE);
        ClearButtonBW.setForeground(Color.WHITE);


        //Button to search Bills
        JButton SearchButtonBW = new JButton("Search");
            SearchButtonBW.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SearchBills(TableModelB, Fields, BillWindow);
                }
            });
            ButtonPanelBW.add(SearchButtonBW);
            SearchButtonBW.setBackground(Color.BLUE);
            SearchButtonBW.setForeground(Color.WHITE);

        //Button to mark bill as paid or unpaid
        JButton IsPaidButtonBW = new JButton("Mark As Paid");
            IsPaidButtonBW.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    MarkIsPaid(TableModelB, Fields, BillWindow);
                }
                
            });
            ButtonPanelBW.add((IsPaidButtonBW));
            IsPaidButtonBW.setBackground(Color.BLUE);
            IsPaidButtonBW.setForeground(Color.WHITE);
            
           
        //Button to open appointments window
        JButton AppointmentBW = new JButton("Appointments");

            AppointmentBW.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){

                    //Triggers loading screen
                    if (DPMSGui.count == 1){
                        DPMSGui.SetLoadingScreen(this);
                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                DPMSAppointment_Window.AppointmentWindow();
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
            ButtonPanelBW.add((AppointmentBW));
            AppointmentBW.setBackground(Color.BLUE);
            AppointmentBW.setForeground(Color.WHITE);
        
        //Button to display information for help
        JButton HelpButtonBW = new JButton("Help");
            HelpButtonBW.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    HelpBilling(BillWindow, BillWindow);
                }
            });
            ButtonPanelBW.add((HelpButtonBW));
            HelpButtonBW.setBackground(Color.BLUE);
            HelpButtonBW.setForeground(Color.WHITE);

        MainPanelB.add(ButtonPanelBW, BorderLayout.SOUTH);
        BillWindow.add(MainPanelB);
        BillWindow.setVisible(true);
        BillTable.setDefaultEditor(Object.class, null);
    }

    //Code that populates bills table in BillWindow
    public static void PopulateBillTable(DefaultTableModel TableModelPBT, JFrame ParentFrame) {
        TableModelPBT.setRowCount(0);
        try {
            Connection connection = DPMSConnectDB.getConnection();
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
        
        if (Fields[0].getText().trim().isEmpty() || !DPMSPatient_Window.Numeric(Fields[0].getText())) {
            JOptionPane.showMessageDialog(ParentFrame, "Please enter a valid numerical value for Appointment ID.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Connection connection = DPMSConnectDB.getConnection();

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
            DPMSPatient_Window.ClearFields(Fields);

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
        
        if (!DPMSPatient_Window.Numeric(BillID)) {
            JOptionPane.showMessageDialog(ParentFrame, "Bill ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(ParentFrame, "Are you sure you want to delete Bill " + BillID + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION){
            try{
                Connection connection = DPMSConnectDB.getConnection();
                String sql = "DELETE FROM bill WHERE bill_id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(BillID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(ParentFrame, "Bill deleted successfully.");
                PopulateBillTable(TableModelCDBI, ParentFrame);
                DPMSPatient_Window.ClearFields(Fields);

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
        if (!appointmentIDText.isEmpty() && DPMSPatient_Window.Numeric(appointmentIDText)) {
            appointmentID = Integer.parseInt(appointmentIDText);
        } else if (!appointmentIDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Appointment ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate patientID using the Numeric function
        int billID = -1;
        if (!billIDtext.isEmpty() && DPMSPatient_Window.Numeric(billIDtext)) {
            billID = Integer.parseInt(billIDtext);
        } else if (!billIDtext.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Patient ID must be a valid integer or blank.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TableModel.setRowCount(0);

        try {
            Connection connection = DPMSConnectDB.getConnection();
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
        if (!billDText.isEmpty() && DPMSPatient_Window.Numeric(billDText)) {
            billID = Integer.parseInt(billDText);
        } else if (!billDText.isEmpty()) {
            JOptionPane.showMessageDialog(ParentFrame, "Bill ID must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Update the "is paid" field in the database
        try {
            Connection connection = DPMSConnectDB.getConnection();
            String updateSql = "UPDATE bill SET is_paid = ? WHERE bill_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setBoolean(1, isPaid);
            updateStatement.setInt(2, billID);
    
            int rowsAffected = updateStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(ParentFrame, "Paid marked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                PopulateBillTable(TableModel, ParentFrame);
                DPMSPatient_Window.ClearFields(Fields);

            } else {
                JOptionPane.showMessageDialog(ParentFrame, "No bill found with the provided Bill ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ParentFrame, "Error occurred while marking is Paid: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that displays help message in Bill Window 
    public static void HelpBilling(JFrame frame, JFrame ParentFrame){
        JOptionPane.showMessageDialog(ParentFrame, "To search Appointments only use Bill ID."+ 
        "\nEnsure all fields marked with * are filled out." + 
        "\nEnsure numerical value for all ID fields."+ 
        "\nTo mark bill as paid only use Bill ID and Is Paid fields. Type 'true' in Is Paid field to mark bill as paid." + 
        " Type 'false' to mark as unpaid.", "Bill Help Window", JOptionPane.INFORMATION_MESSAGE);
    }
}