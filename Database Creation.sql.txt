CREATE TABLE login(
	login_id SERIAL PRIMARY KEY NOT NULL,
	username VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL
);

CREATE TABLE receptionist(
	receptionist_id SERIAL PRIMARY KEY NOT NULL,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	phone_number BIGINT NOT NULL 
);

CREATE TABLE dentist(
	dentist_id SERIAL PRIMARY KEY NOT NULL,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	phone_number BIGINT NOT NULL
);

CREATE TABLE dental_hygienist(
	dental_hygienist_id SERIAL PRIMARY KEY NOT NULL,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	phone_number BIGINT NOT NULL
);

CREATE TABLE dental_assistant(
	dental_assistant_id SERIAL PRIMARY KEY NOT NULL,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	phone_number BIGINT NOT NULL
);

CREATE TABLE dental_surgeon(
	dental_surgeon_id SERIAL PRIMARY KEY NOT NULL,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	phone_number BIGINT NOT NULL
);

CREATE TABLE patient(
	patient_id SERIAL PRIMARY KEY NOT NULL,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL,
	date_of_birth DATE NOT NULL,
	gender VARCHAR(2),
	address VARCHAR(255) NOT NULL,
	city VARCHAR(255) NOT NULL,
	state VARCHAR(255) NOT NULL,
	zip_code BIGINT NOT NULL,
	insurance_company VARCHAR(255),
	insurance_number BIGINT,
	notes VARCHAR(2000),
	xray_images VARCHAR(255)
);

CREATE TABLE prescription(
	prescription_id SERIAL PRIMARY KEY NOT NULL,
	name VARCHAR(255) NOT NULL,
	notes VARCHAR(2000) NOT NULL,
	price DOUBLE PRECISION NOT NULL,
	items_in_stock INT NOT NULL
);

CREATE TABLE prescription_bill_info(
	prescription_bill_info_id SERIAL PRIMARY KEY NOT NULL,
	patient_id INT,
	prescription_id INT,
	quantity INT NOT NULL,
	FOREIGN KEY (prescription_id) REFERENCES prescription(prescription_id),
	FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
	
);

CREATE TABLE procedure(
	procedure_id SERIAL PRIMARY KEY NOT NULL,
	name VARCHAR(255) NOT NULL,
	notes VARCHAR(2000) NOT NULL,
	price DOUBLE PRECISION NOT NULL
);

CREATE TABLE appointment(
	appointment_id  SERIAL PRIMARY KEY NOT NULL,
	patient_id INT NOT NULL,
	procedure_id INT,
	procedure_occurrences INT,
	dentist_id INT,
	dental_hygienist_id INT,
	dental_assistant_id INT,
	dental_surgeon_id INT,
	appointment_date DATE NOT NULL,
	appointment_time TIME NOT NULL,
	notes VARCHAR(2000),
	canceled BOOLEAN,
	FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
	FOREIGN KEY (procedure_id) REFERENCES procedure(procedure_id),
	FOREIGN KEY (dentist_id) REFERENCES dentist(dentist_id),
	FOREIGN KEY (dental_hygienist_id) REFERENCES dental_hygienist(dental_hygienist_id),
	FOREIGN KEY (dental_assistant_id) REFERENCES dental_assistant(dental_assistant_id),
	FOREIGN KEY (dental_surgeon_id) REFERENCES dental_surgeon(dental_surgeon_id)
);

CREATE TABLE bill(
	bill_id SERIAL PRIMARY KEY NOT NULL,
	appointment_id INT,
	is_paid BOOLEAN NOT NULL,
	notes VARCHAR(2000),
	FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id)
);

INSERT INTO dental_assistant (first_name, last_name, phone_number)
VALUES	('william', 'ramirez', '7085558901'),
	('ethan', 'cooper', '7085557891'),
   	('isabella', 'price', '7085558902');

INSERT INTO dental_hygienist (first_name, last_name, phone_number)
VALUES  ('sophia', 'anderson', '7085551234'),
   	('liam', 'campbell', '7085552345'),
   	('olivia', 'martinez', '7085553456');

INSERT INTO dental_surgeon (first_name, last_name, phone_number)
VALUES	('james', 'nelson', '7085550123');

INSERT INTO dentist (first_name, last_name, phone_number)
VALUES	('elijah', 'harris', '7085556789'),
	('mia', 'king', '7085559012');
	
INSERT INTO receptionist (first_name, last_name, phone_number)
VALUES	('lucas', 'bennett', '7085551236'),
   	('chloe', 'carter', '7085552347'),
   	('jackson', 'parker', '7085553458');

INSERT INTO patient(first_name, last_name, date_of_birth, gender, address, city, state, zip_code, insurance_company, insurance_number)
VALUES	('emily', 'johnson', '1987-05-12', 'F', '123 Oak Street', 'Chicago', 'IL', '60601', 'Guardian', '708555123'),
    	('michael', 'brown', '1992-09-25', 'M', '456 Maple Avenue', 'Evanston', 'IL', '60201', 'Delta Dental', '708555234'),
   	('sarah', 'williams', '1985-03-18', 'F', '789 Elm Street', 'Oak Park', 'IL', '60302', 'Blue Cross', '708555345'),
    	('christopher', 'patel', '2009-01-30', 'M', '2020 Walnut Drive', 'Evanston', 'IL', '60202', 'Aetna', '708555456'),
   	('samantha', 'nguyen', '1983-07-29', 'F', '1212 Cedar Lane', 'Chicago', 'IL', '60618', 'Cigna', '708555567'),
    	('william', 'rodriguez', '1978-12-15', 'M', '1414 Birch Avenue', 'Cicero', 'IL', '60804', 'MetLife Dental', '708555678'),
    	('olivia', 'martinez', '1989-06-03', 'F', '1616 Pine Street', 'Berwyn', 'IL', '60402', 'Guardian', '708555789'),
    	('ethan', 'garcia', '1980-04-17', 'M', '1818 Oak Avenue', 'Chicago', 'IL', '60610', 'Delta Dental', '708555890'),
    	('ava', 'patel', '1995-01-30', 'F', '2020 Walnut Drive', 'Evanston', 'IL', '60202', 'Blue Cross', '708555901'),
    	('noah', 'kim', '1986-08-22', 'M', '2222 Cherry Lane', 'Oak Park', 'IL', '60301', 'Aetna', '708555012');

INSERT INTO login (username, password)
VALUES	('wRamirez729', 'P@ssw0rd123!'),
	('eCooper415', 'SecurePass456$'),
    	('iPrice821', 'StrongPwd789*'),
	('sAnderson203', 'SafePassword101%'),
	('lCampbell544', 'Access1234#'),
    	('oMartinez697', 'SecretPass567&'),
	('jNelson808', 'HiddenPwd333#'),
	('eHarris927', 'ProtectedPwd999$'),
    	('mKing652', 'StrongPass777*'),
	('lBennett419', 'SecurePwd246#'),
	('cCarter821', 'PrivatePass888&'),
	('jParker385', 'Confidential123@'),
	('Admin', 'Test');

INSERT INTO prescription (name, notes, price, items_in_stock)
VALUES	('Fluoride Rinse', 'Recommended for daily use to strengthen tooth enamel and prevent cavities.', 15, 50),
    	('Antimicrobial Mouthwash', 'Used to reduce bacteria and inflammation in the mouth, especially for patients with gum disease.', 20, 30),
	('Tooth Desensitizer Gel', 'Applied to sensitive teeth to provide relief from pain triggered by hot, cold, or sweet foods and drinks.', 25, 20),
	('Oral Antibiotic Tablets', 'Prescribed for the treatment of severe dental infections or abscesses.', 30, 10),
    	('Prescription Strength Fluoride Toothpaste', 'High concentration fluoride toothpaste for patients with high risk of cavities or tooth decay.', 18, 40),
    	('Oral Analgesic Solution', 'Liquid pain reliever for temporary relief of toothache or mouth soreness.', 12, 15),
	('Oral Anti-inflammatory Tablets', 'Prescribed to reduce inflammation and pain associated with dental procedures or conditions.', 35, 25),
    	('Oral Anti-fungal Rinse', 'Used to treat fungal infections in the mouth, such as oral thrush.', 22, 20),
    	('Prescription Dental Floss', 'Specialized dental floss for patients with tight spaces between teeth or orthodontic appliances.', 8, 60),
    	('Oral Antiseptic Spray', 'Spray to disinfect and soothe oral tissues, especially after oral surgery or trauma.', 28, 20);

INSERT INTO procedure (name, notes, price)
VALUES  ('Dental Cleaning (Prophylaxis)', 'Routine dental cleaning to remove plaque, tartar, and stains from the teeth. It helps prevent cavities and gum disease.', 150),
    	('Dental Filling (Composite)', 'Restoration of a decayed or damaged tooth using tooth-colored composite resin material.', 300),
    	('Dental Crown (Porcelain or Ceramic)', 'Restoration of a badly damaged or decayed tooth by covering it with a tooth-shaped cap.', 1000),
	('Root Canal Therapy', 'Treatment to remove infected or damaged pulp from inside the tooth and seal it to save the tooth.', 1200),
    	('Tooth Extraction', 'Removal of a severely damaged, decayed, or impacted tooth.', 200),
    	('Dental Implant', 'Surgical placement of an artificial tooth root into the jawbone to support a dental prosthesis.', 4000),
	('Dental Bridge', 'Replacement of one or more missing teeth by anchoring artificial teeth to adjacent natural teeth or implants.', 2000),
    	('Teeth Whitening (In-office)', 'Professional teeth whitening procedure performed in the dental office using bleaching agents.', 700),
    	('Periodontal Surgery (Gum Surgery)', 'Surgical treatment for severe gum disease to remove infected tissue and reshape the gums.', 1500);

INSERT INTO prescription_bill_info (patient_id, prescription_id, quantity)
VALUES	('1','1','1'),
    	('3','1','1'),
	('10','5','2');

INSERT INTO appointment (patient_id, procedure_id, procedure_occurrences, dentist_id, dental_hygienist_id, dental_assistant_id, appointment_date, appointment_time, notes)
VALUES	('1','1','1','1','1','1','2024-02-01','09:00:00','Teeth More Prone to Cavities. Needs Prescription 1.'),
    	('3','1','1','2','2','2','2024-02-01','10:00:00','Teeth More Prone to Cavities. Needs Prescription 1.'),
	('10','1','1','1','3','3','2024-02-01','10:30:00','Teeth Very Prone to Cavities. Needs Prescription 5(2).');


INSERT INTO appointment (patient_id, procedure_id, procedure_occurrences, dentist_id, dental_hygienist_id, dental_assistant_id, appointment_date, appointment_time)
VALUES	('2','1','1','2','1','1','2024-02-01','11:00:00'),
    	('4','1','1','1','2','2','2024-02-01','11:30:00'),
	('5','1','1','2','3','3','2024-02-01','12:00:00'),
	('6','1','1','1','1','1','2024-02-01','12:30:00'),
    	('7','1','1','2','2','2','2024-02-01','13:00:00');
		
INSERT INTO appointment (patient_id, procedure_id, procedure_occurrences, dentist_id, dental_hygienist_id, dental_assistant_id, dental_surgeon_id, appointment_date, appointment_time, notes)
VALUES	('8','9','1','1','1','1','1','2024-02-02','09:00:00','Extremely Infected Gums, Patient went under Periodontal Gum Surgery'),
    	('9','9','1','2','2','2','1','2024-02-02','11:00:00','Extremely Infected Gums, Patient went under Periodontal Gum Surgery');

INSERT INTO bill(appointment_id, is_paid)
VALUES	('1','true'),
   	('2','true'),
	('3','true'),
   	('4','true'),
	('5','true'),
	('6','true'),
	('7','true'),
   	('8','true'),
   	('9','true'),
	('10','true');
