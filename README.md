# Overview

This is my Dental Practice Management System I created using Java Swing, Java, and PostgreSQL, which is connected to AWS. This program allows users to save, delete, and view dental practice information such as patient, appointment, billing, and prescription information. This program is convenient to use and eliminates paperwork or the need to use Excel spreadsheets to store a lot of data.

DEMO
- Youtube Link (https://youtu.be/HPtsfpu4tJU)


# Implementation

Login Screen:
- Login accepts user input for a username and password. The input is validated through the database, and if the username and password are found, the main menu will be presented. If the username and/or password are incorrect, an error message will display.

Main Menu:
- A Welcome message appears, which greets the user that's logged in. Also, there are three options. Option one, "Patients", which accesses the patient window. Option two, "Appointment Scheduling", which accesses the appointment window, Option three, "Billing", which accesses the billing window, Option four, "Prescription and Prescription Billing", which accesses prescription and prescription billing info, and Option five, "Logout", which closes all windows and returns users to the Login Screen.

Patients:
 - Here, a few fields are provided to enter information for new patients. All fields, unless stated otherwise, must be filled out. Also, fields like DOB, zipcode, state, insurance number, and patient ID have specific requirements to ensure data is entered correctly. If 
  fields are filled incorrectly or missing, an error will display. If everything is correct, the patient will be added to the database when the save button is clicked.
  
 - The Clear fields button just clears all fields in the window.
   
 - Update button allows user to enter Patient ID and edit any information the patient has.
  
 - Delete allows the user to delete a patient from the database using their Patient ID. If an int isn't entered or the field is blank when the delete button is pressed, an error will occur. Also, the delete button has a confirmation to prevent accidents.
  
 - Search uses fields First Name, Last Name, or Patient ID to find a patient in the database. Nulls are accepted here, but Patient ID must be an int. If it isn't, an error message will trigger.
  
 - Help button provides information on how to fill out fields.
  
Appointment Scheduling:
 - Here, a few fields are provided to enter information for new appointments. All fields, unless stated otherwise, must be filled out. Also, Appointment Time, Appointment Date, and fields that end with ID have specific requirements to ensure data is entered correctly. If 
  fields are filled incorrectly or missing, an error will display. If everything is correct, the appointment will be added to the database when the save button is clicked.
  
 - The Clear button just clears all fields in the window.
  
 - Delete allows the user to delete an appointment from the database using their appointment ID. If an int isn't entered or the field is blank when the delete button is pressed, an error will occur. Also, the delete button has a confirmation to prevent accidents.
  
 - The Patients Button simply pops up the patient window in case a user needs to find a patient while creating an appointment.
  
 - Cancel Appointment button uses Canceled field and Appointment ID field to mark Appointment as canceled or not canceled. "true" marks as canceled "False" marks as not canceled.
  
 - Search uses Patient ID or Appointment ID to find an appointment in the database. Nulls are accepted here, but the two fields must be an int. If it isn't, an error message will trigger.
  
 - Help button provides information on how to fill out fields.
  
Billing:
 - Here, a few fields are provided to enter information for new bills. All fields, unless stated otherwise, must be filled out. Appointment ID, Is Paid, and Bill ID have specific requirements to ensure data is entered correctly. If fields are not filled out correctly, an 
  error message will be prompted. If everything is correct, the Bill will be added to the database when the save button is clicked.
  
 - The Clear button just clears all fields in the window.
  
 - Delete allows the user to delete a bill from the database using their Bill ID. If an int isn't entered or the field is blank when the delete button is pressed, an error will occur. Also, the delete button has a confirmation to prevent accidents.
  
 - Search uses Bill ID or Appointment ID to find a Bill in the database. Nulls are accepted here, but the two fields must be an int. If it isn't, an error message will trigger.
  
 - The appointment button simply pops up the appointment window in case a user needs to find a specific appointment for billing.
   
 - The Save Bill PDF button takes the information from the Bill ID field and turns the bill into a pdf.
  
 - Help button provides information on how to fill out fields.
  
 - Mark As Paid button uses Is Paid field and Bill ID field to mark bill as paid or unpaid. "true" marks as paid "False" marks as not paid.
  
Prescription & Prescription Billing:
 - Here, a few fields are provided to enter information for new bills. All fields, unless stated otherwise, must be filled out. Patient ID, Prescription ID, Quantity, and Prescription Bill ID have specific requirements to ensure data is entered correctly. If fields are 
  not filled out correctly, an error message will be prompted. If everything is correct, the prescription bill will be added to the database when the save button is clicked.
  
 - The Clear button just clears all fields in the window.
  
 - Delete allows the user to delete a bill from the database using their Prescription Bill ID. If an int isn't entered or the field is blank when the delete button is pressed, an error will occur. Also, the delete button has a confirmation to prevent accidents.
  
 - Search uses Prescription Bill ID or Patient ID to find a Prescription Bill in the database. Nulls are accepted here, but the two fields must be an int. If it isn't, an error message will trigger.
  
 - Patients Button simply pops up the patient window in case a user needs to find a patient while creating a prescription bill.
   
 - The Save Bill PDF button takes the information from the Prescription Bill ID field and turns the bill into a pdf.
  
 - Help button provides information on how to fill out fields.
  
Logout:
 - Closes all existing windows and returns user to login screen for user authentication.
