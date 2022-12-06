DROP TABLE IF EXISTS PAROLEE;
CREATE TABLE PAROLEE(
ID              LONG PRIMARY KEY AUTO_INCREMENT,
FIRSTNAME           VARCHAR(255),
LASTNAME           VARCHAR(255),
GENDER           VARCHAR(10),
DATEOFBIRTH    DATE
);

INSERT INTO PAROLEE (ID, FIRSTNAME, LASTNAME, GENDER, DATEOFBIRTH) VALUES(1,'Michael','Miller','Male','1902-02-24');
INSERT INTO PAROLEE (ID, FIRSTNAME, LASTNAME, GENDER, DATEOFBIRTH) VALUES(2,'Sarah','Corner','Female','1945-11-15');
INSERT INTO PAROLEE (ID, FIRSTNAME, LASTNAME, GENDER, DATEOFBIRTH) VALUES(3,'Peter','Milton','Male','1953-10-06');
INSERT INTO PAROLEE (ID, FIRSTNAME, LASTNAME, GENDER, DATEOFBIRTH) VALUES(4,'Danny','Moore','Male','1913-07-11');
INSERT INTO PAROLEE (ID, FIRSTNAME, LASTNAME, GENDER, DATEOFBIRTH) VALUES(5,'Ulrich','Fritag','Male','1924-05-02');