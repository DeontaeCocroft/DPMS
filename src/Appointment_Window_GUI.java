package src;
//Created by Deontae Cocroft
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//GUI for appointment window
public class Appointment_Window_GUI {
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
        Appointment_Window.PopulateAppointmentTable(TableModelAI, AppointmentWindow);

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
        Appointment_Window.PopulateDentalAssistantTable(TableModelDA, AppointmentWindow);
        JScrollPane ScrollPaneDA = new JScrollPane(dentalassistanttable);

        //Table to display Dental Hygienist information
        DefaultTableModel TableModelDH = new DefaultTableModel();
        TableModelDH.addColumn("Dental Hygienist ID");
        TableModelDH.addColumn("First Name");
        TableModelDH.addColumn("Last Name");
        TableModelDH.addColumn("Phone Number");

        JTable DentalHygienistTable = new JTable(TableModelDH);

        //Populate table with information
        Appointment_Window.PopulateDentalHygienistTable(TableModelDH, AppointmentWindow);
        JScrollPane ScrollPaneDH = new JScrollPane(DentalHygienistTable);

        //Table to display Dental Surgeon information
        DefaultTableModel TableModelDS = new DefaultTableModel();
        TableModelDS.addColumn("Dental Surgeon ID");
        TableModelDS.addColumn("First Name");
        TableModelDS.addColumn("Last Name");
        TableModelDS.addColumn("Phone Number");

        JTable DentalSurgeonTable = new JTable(TableModelDS);

        //Populate table with information
        Appointment_Window.PopulateDentalSurgeonTable(TableModelDS, AppointmentWindow);
        JScrollPane ScrollPaneDS = new JScrollPane(DentalSurgeonTable);

        //Table to display Dentist information
        DefaultTableModel TableModelDI = new DefaultTableModel();
        TableModelDI.addColumn("Dentist ID");
        TableModelDI.addColumn("First Name");
        TableModelDI.addColumn("Last Name");
        TableModelDI.addColumn("Phone Number");

        JTable DentistTable = new JTable(TableModelDI);

        //Populate table with information
        Appointment_Window.PopulateDentistTable(TableModelDI, AppointmentWindow);
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
        Appointment_Window.PopulateProcedureTable(TableModelPRO, AppointmentWindow);
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
                Appointment_Window.SaveAppointmentInfo(Fields, TableModelAI, AppointmentWindow);
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
                Patient_Window.ClearFields(Fields);
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
                Appointment_Window.DeleteAppointment(Fields[11].getText(), TableModelAI, Fields, AppointmentWindow);
                
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
                if (Login_MainMenu_Gui.count == 1){
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
        
        ButtonPanelAW.add((PatientButtonAW));
        PatientButtonAW.setBackground(Color.BLUE);
        PatientButtonAW.setForeground(Color.WHITE);

        //Button to search patients
        JButton SearchButtonAW = new JButton("Search");
        SearchButtonAW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                Appointment_Window.SearchAppointments(TableModelAI, Fields, AppointmentWindow);
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
                Appointment_Window.CancelAppointments(TableModelAI, Fields, AppointmentWindow);
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
                Appointment_Window.HelpAppointment(AppointmentWindow, AppointmentWindow);
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
}
