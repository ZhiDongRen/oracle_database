DROP TABLE TITLE;
DROP TABLE PRODUCT;
DROP TABLE car;
--==========================================
-- CAR THIS TABLE NOT USEFUL
--==========================================
DROP SEQUENCE "CAR_SEQ";
CREATE SEQUENCE  "CAR_SEQ"   INCREMENT BY 1 START WITH 1001;

CREATE TABLE CAR (
        id NUMBER NOT null,
        title VARCHAR2(64),
	   
	 PRIMARY KEY (id)
);

DROP SEQUENCE product_seq;

CREATE SEQUENCE product_seq
START WITH 1 INCREMENT BY 1;

COMMIT;
DROP TABLE product;

CREATE TABLE product (
	id NUMBER,
	title VARCHAR2(64),
	image ORDSYS.ORDImage,
	image_si ORDSYS.SI_StillImage,
	image_ac ORDSYS.SI_AverageColor,
	image_ch ORDSYS.SI_ColorHistogram,
	image_pc ORDSYS.SI_PositionalColor,
	image_tx ORDSYS.SI_Texture,
     PRIMARY KEY (id)

);

   

