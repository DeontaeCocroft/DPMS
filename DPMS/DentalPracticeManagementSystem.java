import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DentalPracticeManagementSystem extends JFrame implements ActionListener {
    private JButton patientButton;
    private JButton appointmentButton;

    //Creation of the main menu, appointment, and paitent buttons.
    public DentalPracticeManagementSystem() {
        setTitle("Patient Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Menu
        JPanel mainPanel = new JPanel(new GridLayout(3, 1));
        mainPanel.setBackground(Color.GRAY);

        // Main Menu Title
        JLabel titleLabel = new JLabel("Main Menu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        //Patient Button
        patientButton = new JButton("Patients");
        patientButton.addActionListener(this);
        patientButton.setBackground(Color.GREEN);
        patientButton.setForeground(Color.GREEN);
        patientButton.setOpaque(true);
        mainPanel.add(patientButton);

        //Appointment Button
        appointmentButton = new JButton("Appointment Scheduling");
        appointmentButton.addActionListener(this);
        appointmentButton.setBackground(Color.BLUE);
        appointmentButton.setForeground(Color.BLUE);
        mainPanel.add(appointmentButton);
        appointmentButton.setOpaque(true);

        add(mainPanel);
        setVisible(true);
    }


    //Main code the works to add, view, and delete customer information. Uses methods populatePatientTable, savePatientInfo, and confirmDeletePatient.
    private void createPatientDialog() {
        JFrame patientDialog = new JFrame("Add/Delete Patient");

        // Size of window
        int currentWidth = 800;
        int newWidth = (int) (currentWidth * 1.9);
        patientDialog.setSize(newWidth, 600);
        patientDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        patientDialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Table to display patient information
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Patient ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("DOB");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Address");
        tableModel.addColumn("City");
        tableModel.addColumn("State");
        tableModel.addColumn("Zip Code");
        tableModel.addColumn("Insurance");
        tableModel.addColumn("Insurance Number");

        JTable patientTable = new JTable(tableModel);

        // Populate table with data from database
        populatePatientTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Fields to all patient info to database
        JPanel infoPanel = new JPanel(new GridLayout(12, 2, 10, 10));

        String[] labels = {"First Name:", "Last Name:", "DOB (YYYY-MM-DD):", "Gender:", "Address:", "City:", "State:", "Zip Code:", "Insurance:", "Insurance Number:", "Patient ID (for delete):"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            infoPanel.add(new JLabel(labels[i]));
            fields[i] = new JTextField();
            infoPanel.add(fields[i]);
        }

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        //Patient add/delete buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        //Button to save patient info into database
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePatientInfo(fields, tableModel);
            }
        });
        buttonPanel.add(saveButton);

        //Button to clear patient info
        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields(fields);
            }
        });
        buttonPanel.add(clearButton);

        //Button to delete patients
        JButton deleteButton = new JButton("Confirm Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmDeletePatient(fields[10].getText(), tableModel, fields, patientDialog);
            }
        });
        buttonPanel.add(deleteButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        patientDialog.add(mainPanel);
        patientDialog.setVisible(true);
        patientTable.setDefaultEditor(Object.class, null);
    }

    //Code that works to add, delete, and view appointment information. Uses methods saveAppointmentInfo, confirmDeleteAppointment, and populateAppointmentInfo
    private void createAppointmentDialog() {
        JFrame appointmentDialog = new JFrame("Appointment Scheduling");
    
        // Size of window
        int currentWidth = 800;
        int newWidth = (int) (currentWidth * 1.9);
        appointmentDialog.setSize(newWidth, 600);
        appointmentDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        appointmentDialog.setLocationRelativeTo(null);
    
        JPanel mainPanel = new JPanel(new BorderLayout());
    
        // Table to display Appointment information
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Appointment ID");
        tableModel.addColumn("Patient ID");
        tableModel.addColumn("Procedure ID");
        tableModel.addColumn("Procedure Occurrences");
        tableModel.addColumn("Dentist ID");
        tableModel.addColumn("Dental Hygienist ID");
        tableModel.addColumn("Dental Assistant ID");
        tableModel.addColumn("Dental Surgeon ID");
        tableModel.addColumn("Appointment Date");
        tableModel.addColumn("Appointment Time");
        tableModel.addColumn("Notes");
        tableModel.addColumn("Canceled");
    
        JTable appointmentTable = new JTable(tableModel);
    
        // Populate table with data from database
        populateAppointmentTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    
        // Fields to add appointment info to database
        JPanel infoPanel = new JPanel(new GridLayout(12, 2, 10, 10));
        String[] labels = {"Patient ID:", "Procedure ID:", "Procedure Occurrences:", "Dentist ID:", "Dental Hygienist ID:", "Dental Assistant ID:", "Dental Surgeon ID:", "Appointment Date (YYYY-MM-DD):", "Appointment Time (HH:mm):", "Notes:", "Canceled:", "Appointment ID (for Delete):"};
        JTextField[] fields = new JTextField[labels.length];
    
        for (int i = 0; i < labels.length; i++) {
            infoPanel.add(new JLabel(labels[i]));
            fields[i] = new JTextField();
            infoPanel.add(fields[i]);
        }
    
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        //Buttons for appointment scheduling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        //Button to save appointment information to database
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAppointmentInfo(fields, tableModel);
            }
        });
        buttonPanel.add(saveButton);
    
        //Button to clear fields 
        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields(fields);
            }
        });
        buttonPanel.add(clearButton);

        //Button to delete appointments from the database
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmDeleteAppointment(fields[11].getText(), tableModel);
            }
        });
        buttonPanel.add(deleteButton);
    
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        appointmentDialog.add(mainPanel);
        appointmentDialog.setVisible(true);
        appointmentTable.setDefaultEditor(Object.class, null);
    }
    
    //Code that saves patient info when save button is used in createPatientDialog.
    private void savePatientInfo(JTextField[] fields, DefaultTableModel tableModel) {
        // Check for empty fields, non-numeric inputs, and incorrect date format.
        for (int i = 0; i < fields.length; i++) {
            if (i != 10 && fields[i].getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Make sure to fill out all fields correctly and enter numbers for zip code and insurance number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ((i == 2 && !isValidDate(fields[i].getText())) || 
                    (i == 7 || i == 9) && !isNumeric(fields[i].getText())) {
                JOptionPane.showMessageDialog(null, "Make sure to fill out all fields correctly and enter numbers for zip code and insurance number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        //Inserts info typed into createPatientDialog into the database.
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/test", "postgres", "Char8305");
    
            String sql = "INSERT INTO Patient (FirstName, LastName, DOB, Gender, Address, City, State, ZipCode, Insurance, InsuranceNumber) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            //convert inputs to match database column data types and exculdes some fields needed
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < fields.length - 1; i++) { 
                if (i == 2) {
                    java.sql.Date dobDate = java.sql.Date.valueOf(fields[i].getText());
                    statement.setDate(i + 1, dobDate); 

                } else if (i == 7 || i == 9) {
                    statement.setLong(i + 1, Long.parseLong(fields[i].getText()));
                } else {
                    statement.setString(i + 1, fields[i].getText());
                }
            }
    
            statement.executeUpdate();
            connection.close();
            JOptionPane.showMessageDialog(null, "Patient information saved successfully.");
    
            populatePatientTable(tableModel);
            clearFields(fields);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while saving patient information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Code that saves appointment info when save button is used in createAppointmentDialog
    private void saveAppointmentInfo(JTextField[] fields, DefaultTableModel tableModel) {
        // Check for empty fields, non-numeric inputs, incorrect time format, and incorrect date format.
        for (int i = 0; i < 9; i++) {
            if (fields[i].getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Make sure to fill out all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                return; 
            }
            if ((i == 7 && !isValidDate(fields[i].getText())) || 
                    (i == 8 && !isValidTime(fields[i].getText()))) { 
                JOptionPane.showMessageDialog(null, "Make sure to fill out all fields correctly with the correct format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        //Inserts info typed into createAppointmentDialog into the database.
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/test", "postgres", "Char8305");
    
            String sql = "INSERT INTO Appointment (PatientID, ProcedureID, ProcedureOccurrences, DentistID, DentalHygienistID, DentalAssistantID, DentalSurgeonID, AppointmentDate, AppointmentTime, Notes, Canceled) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            //convert inputs to match database column data types and exculdes some fields needed
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < fields.length - 1; i++) {
                if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6) {
                    int fieldValue = Integer.parseInt(fields[i].getText()); 
                    statement.setInt(i + 1, fieldValue); 
                } else if (i == 7) {
                    java.sql.Date appointmentDate = java.sql.Date.valueOf(fields[i].getText());
                    statement.setDate(i + 1, appointmentDate);
                } else if (i == 8) {
                    LocalTime appointmentTime = LocalTime.parse(fields[i].getText(), DateTimeFormatter.ofPattern("HH:mm"));
                    Time sqlTime = Time.valueOf(appointmentTime);
                    statement.setTime(i + 1, sqlTime);
                } else if (i == 10) {
                    boolean canceled = fields[i].getText().equalsIgnoreCase("true");
                    statement.setBoolean(i + 1, canceled);
                } else {
                    statement.setString(i + 1, fields[i].getText());
                }
            }
            
            statement.executeUpdate();
            connection.close();
            JOptionPane.showMessageDialog(null, "Appointment information saved successfully.");
            populateAppointmentTable(tableModel);
            clearFields(fields);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while saving appointment information: " + e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Fields ProcedureID, Procedure Occurrences, Dentist ID, Dental Hygienist ID, Dental Assistant ID, Dental Surgeon ID must be integers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Code deletes patient information in the database. Is used in the createAppointmentDialog
    private void confirmDeleteAppointment(String appointmentID, DefaultTableModel tableModel) {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this appointment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {

            try {
                Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/test", "postgres", "Char8305");
                String sql = "DELETE FROM Appointment WHERE AppointmentID = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(appointmentID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(null, "Appointment deleted successfully.");
                populateAppointmentTable(tableModel);
                

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error occurred while deleting appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    //Used to make sure date fields are correct. Used in createAppointmentDialog and createPatientDialog.
    private boolean isValidDate(String dateStr) {
        try {
            java.sql.Date.valueOf(dateStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //Used to make sure time field is correct. Used in createAppointmentDialog.
    private boolean isValidTime(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            if (parts.length != 2) {
                return false; 
            }
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
    
            return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
    
    //Used to make sure fields are numeric vale. This is used in createPatientDialog and createAppointmentDialog.
    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Long.parseLong(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    //Code that works with delete button to delete patients from createPatientDialog
    private void confirmDeletePatient(String patientID, DefaultTableModel tableModel, JTextField[] fields, JFrame parentFrame) {
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete this patient?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/test", "postgres", "Char8305");
                String sql = "DELETE FROM Patient WHERE PatientID = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(patientID));
                statement.executeUpdate();
                connection.close();
                JOptionPane.showMessageDialog(parentFrame, "Patient deleted successfully.");
                populatePatientTable(tableModel);
                clearFields(fields);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Error occurred while deleting patient: Patient has appointment or bills and cannot be deleted.");
            }
        }
    }

    //Code that populates patient table in createPatientDialog
    private void populatePatientTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/test", "postgres", "Char8305");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT PatientID, FirstName, LastName, DOB, Gender, Address, City, State, ZipCode, Insurance, InsuranceNumber FROM Patient");

            while (resultSet.next()) {
                String[] rowData = {
                        String.valueOf(resultSet.getInt(1)), // PatientID
                        resultSet.getString(2), // First Name
                        resultSet.getString(3), // Last Name
                        resultSet.getDate(4).toString(), // DOB
                        resultSet.getString(5), // Gender
                        resultSet.getString(6), // Address
                        resultSet.getString(7), // City
                        resultSet.getString(8), // State
                        String.valueOf(resultSet.getLong(9)), // Zip Code
                        resultSet.getString(10), // Insurance
                        String.valueOf(resultSet.getLong(11)) // Insurance Number
                };
                tableModel.addRow(rowData);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while fetching patient information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Code that populates appointments table in createAppointmentDialog
    private void populateAppointmentTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/test", "postgres", "Char8305");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT AppointmentID, PatientID, ProcedureID, ProcedureOccurrences, DentistID, DentalHygienistID, DentalAssistantID, DentalSurgeonID, AppointmentDate, AppointmentTime, Notes, Canceled FROM Appointment");
    
            while (resultSet.next()) {
                String[] rowData = {
                        String.valueOf(resultSet.getInt("AppointmentID")), // AppointmentID
                        String.valueOf(resultSet.getInt("PatientID")),
                        String.valueOf(resultSet.getInt("ProcedureID")),
                        String.valueOf(resultSet.getInt("ProcedureOccurrences")),
                        String.valueOf(resultSet.getInt("DentistID")),
                        String.valueOf(resultSet.getInt("DentalHygienistID")),
                        String.valueOf(resultSet.getInt("DentalAssistantID")),
                        String.valueOf(resultSet.getInt("DentalSurgeonID")),
                        resultSet.getDate("AppointmentDate").toString(),
                        resultSet.getTime("AppointmentTime").toString(),
                        resultSet.getString("Notes"),
                        resultSet.getString("Canceled")
                };
                tableModel.addRow(rowData);
            }
    
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while fetching appointment information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Code that clears fields
    private void clearFields(JTextField[] fields) {
        for (JTextField field : fields) {
            field.setText(""); // Clear text in each text field
        }
    }
    //Main calls starts program and calls main menu
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DentalPracticeManagementSystem();
            }
        });
    }

    //To call respective functions when buttons are pressed
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == patientButton) {
            createPatientDialog();
        } else if (e.getSource() == appointmentButton) {
            createAppointmentDialog();
        }
    }
}