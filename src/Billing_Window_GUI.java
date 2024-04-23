package src;
//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

//GUI for billing window
public class Billing_Window_GUI {
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
        ImageIcon LogoIcon = new ImageIcon("Images/billing.png"); 
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
        Billing_Window.PopulateBillTable(TableModelB, BillWindow);
        JScrollPane ScrollPane = new JScrollPane(BillTable);
        MainPanelB.add(ScrollPane, BorderLayout.CENTER);

        //Set size for specific column
        BillTable.getColumnModel().getColumn(4).setPreferredWidth(130);

        // Fields to enter all Billing info to database
        JPanel BillFieldsPanel = new JPanel(new BorderLayout());
        JPanel BillFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 10);

        String[] Labels = {"*Appointment ID (For Save and Search):", "Is Paid (For Save and Mark as Paid):", "Notes:", "Bill ID (For Search, Delete, Save as PDF, or Mark as Paid):"};
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
                Billing_Window.SaveBillInfo(Fields, TableModelB, BillWindow);
            }
        });
        ButtonPanelBW.add(SaveButtonBW);
        SaveButtonBW.setBackground(Color.BLUE);
        SaveButtonBW.setForeground(Color.WHITE);
        SaveButtonBW.setBorderPainted(false);
        SaveButtonBW.setOpaque(true);

        //Button to clear Bill info fields
        JButton ClearButtonBW = new JButton("Clear Fields");
        ClearButtonBW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Patient_Window.ClearFields(Fields);
            }
        });
        ButtonPanelBW.add(ClearButtonBW);
        ClearButtonBW.setBackground(Color.BLUE);
        ClearButtonBW.setForeground(Color.WHITE);
        ClearButtonBW.setBorderPainted(false);
        ClearButtonBW.setOpaque(true);

        //Button to delete bill info into database
        JButton DeleteButtonBW = new JButton("Delete");
        DeleteButtonBW .addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Billing_Window.DeleteBillInfo(Fields[3].getText(), TableModelB, Fields, BillWindow);
            }
        });
        ButtonPanelBW.add(DeleteButtonBW );
        DeleteButtonBW .setBackground(Color.BLUE);
        DeleteButtonBW .setForeground(Color.WHITE);
        DeleteButtonBW.setBorderPainted(false);
        DeleteButtonBW.setOpaque(true);



        //Button to search Bills
        JButton SearchButtonBW = new JButton("Search");
            SearchButtonBW.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Billing_Window.SearchBills(TableModelB, Fields, BillWindow);
                }
            });
            ButtonPanelBW.add(SearchButtonBW);
            SearchButtonBW.setBackground(Color.BLUE);
            SearchButtonBW.setForeground(Color.WHITE);
            SearchButtonBW.setBorderPainted(false);
            SearchButtonBW.setOpaque(true);

        
           
        //Button to open appointments window
        JButton AppointmentBW = new JButton("Appointments");

            AppointmentBW.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){

                    //Triggers loading screen
                    if (Login_MainMenu_Gui.count == 1){
                        Login_MainMenu_Gui.SetLoadingScreen(this);
                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                Appointment_Window_GUI.AppointmentWindow();
                                return null;
                            }
                            @Override
                            protected void done() {
                                Login_MainMenu_Gui.closeLoadingScreen();
                            }
                        };
                        worker.execute();
                    }
                }
            });
            ButtonPanelBW.add((AppointmentBW));
            AppointmentBW.setBackground(Color.BLUE);
            AppointmentBW.setForeground(Color.WHITE);
            AppointmentBW.setBorderPainted(false);
            AppointmentBW.setOpaque(true);

        //Button to mark bill as paid or unpaid
        JButton IsPaidButtonBW = new JButton("Mark As Paid");
            IsPaidButtonBW.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    Billing_Window.MarkIsPaid(TableModelB, Fields, BillWindow);
                }
                
            });
            ButtonPanelBW.add((IsPaidButtonBW));
            IsPaidButtonBW.setBackground(Color.BLUE);
            IsPaidButtonBW.setForeground(Color.WHITE);
            IsPaidButtonBW.setBorderPainted(false);
            IsPaidButtonBW.setOpaque(true);

        //Button to create bill pdf/
        JButton billpdfBW = new JButton("Save Bill PDF");
        billpdfBW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int billID = Integer.parseInt(Fields[3].getText().trim());
                    Billing_Window.searchBillsAndCreatePDF(billID, BillWindow);
                    Fields[3].setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid integer for Bill ID.",
                                                  "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        ButtonPanelBW.add(billpdfBW);
        billpdfBW.setBackground(Color.BLUE);
        billpdfBW.setForeground(Color.WHITE);
        billpdfBW.setBorderPainted(false);
        billpdfBW.setOpaque(true);
        
        //Button to display information for help
        JButton HelpButtonBW = new JButton("Help");
            HelpButtonBW.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    Billing_Window.HelpBilling(BillWindow, BillWindow);
                }
            });
            ButtonPanelBW.add((HelpButtonBW));
            HelpButtonBW.setBackground(Color.BLUE);
            HelpButtonBW.setForeground(Color.WHITE);
            HelpButtonBW.setBorderPainted(false);
            HelpButtonBW.setOpaque(true);

        MainPanelB.add(ButtonPanelBW, BorderLayout.SOUTH);
        BillWindow.add(MainPanelB);
        BillWindow.setVisible(true);
        BillTable.setDefaultEditor(Object.class, null);
    }
    
}
