package src;
//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//GUI for prescription window
public class Prescription_Window_GUI {
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
        Prescription_Window.PopulatePrescriptionBillTable(TableModelPreBI, PrescriptionWindow);

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
        Prescription_Window.PopulatePrescriptionTable(TableModelPreI, PrescriptionWindow);

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
                Prescription_Window.SavePrescriptionBillInfo(Fields, TableModelPreBI, PrescriptionWindow);
                Prescription_Window.PopulatePrescriptionTable(TableModelPreI, PrescriptionWindow);
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
                Prescription_Window.DeletePrescriptionBillInfo(Fields[3].getText(), TableModelPreBI, Fields, PrescriptionWindow);
                Prescription_Window.PopulatePrescriptionTable(TableModelPreI, PrescriptionWindow);
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
                Prescription_Window.SearchPrescriptionBill(TableModelPreBI, Fields, PrescriptionWindow);
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
                Patient_Window.ClearFields(Fields);
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
                if (Login_MainMenu_Gui.count == 1){
                    // Show loading screen
                    Login_MainMenu_Gui.SetLoadingScreen(this);
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            Patient_Window_GUI.PatientWindow();
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

        ButtonPanelPreW.add((PatientButtonPreW));
        PatientButtonPreW.setBackground(Color.BLUE);
        PatientButtonPreW.setForeground(Color.WHITE);

        //Button for Prescription Bill window help
        JButton HelpButtonPW = new JButton("Help");
        HelpButtonPW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                Prescription_Window.HelpPrescription(PrescriptionWindow, PrescriptionWindow);
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
    
}
