This is my Dental Practice Management System I created using Java Swing, Java, and PostgreSQL. All code is in this repo.
A Brief explanation of how it works.

Login Screen
  Login accepts user input for a username and password. The input is validated through the database and if the username and password are found, the main menu will be presented. If the username and/or password is incorrect, an error message will display.

Main Menu
  A Welcome message appears which greets the user that's logged in. Also, there are three options. Option one, "Patients", which accesses the patient window. Option two, "Appointment Scheduling", which accesses the appointment window.
  Option three, "Logout", which closes all windows and returns users to the Login Screen.

Patients
  Here, a few fields are provided to enter information for new patients. All fields, unless stated otherwise, must be filled out. Also, fields like DOB, zipcode, state, insurance number, and patient ID have specific requirements
  to ensure data is entered correctly. If fields are filled incorrectly or missing, an error will display. If everything is correct, the patient will be added to the database when the save button is clicked. 
  
  The Clear fields button just clears all fields in the window.
  
  Delete allows the user to delete a patient from the database using their Patient ID. If an int isn't entered or the field is blank when the delete button is pressed, an error will occur. Also, the delete button has a confirmation to prevent accidents. 
  
  Search uses fields First Name, Last Name, or Patient ID to find a patient in the database. Nulls are accepted here but Patient ID must be an int if entered. If it isn't, an error message will trigger.

Appointment Scheduling
  Here, a few fields are provided to enter information for new appointments. All fields, unless stated otherwise, must be filled out. Also, Appointment Time, Appointment Date. and fields that end with ID, have specific requirements
  to ensure data is entered correctly. If fields are filled incorrectly or missing, an error will display. If everything is correct, the appointment will be added to the database when the save button is clicked.
  
  The Clear button just clears all fields in the window.
  
  Delete allows the user to delete an appointment from the database using their appointment ID. If an int isn't entered or the field is blank when the delete button is pressed, an error will occur. Also, the delete button has a confirmation to prevent accidents. 

  The Patients Button simply pops up the patient window in case a user needs to find a patient while creating an appointment.
  
  Search uses Patient ID or Appointment ID to find an appointment in the database. Nulls are accepted here but the two fields must be an int if entered. If it isn't, an error message will trigger.

