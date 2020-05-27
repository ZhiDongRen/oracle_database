
CREATE OR REPLACE PROCEDURE Rotate_image
    (id IN NUMBER)
IS
    obj ORDSYS.ORDImage;
BEGIN
    SELECT image INTO obj FROM product
    WHERE id = id FOR UPDATE;

    obj.process('rotate=90');

    UPDATE product SET image = obj WHERE id = id;

    COMMIT;
END;
/



CREATE OR REPLACE TRIGGER ROTATE_IMAGE 
BEFORE UPDATE OF ID,IMAGE ON PRODUCT 
    FOR EACH ROW
DECLARE
    obj ORDSYS.ORDImage;
BEGIN
    SELECT image INTO obj FROM product
    WHERE ID = id FOR UPDATE;
    obj.process('rotate=90');
    UPDATE IMAGE SET image = obj WHERE ID = id;

    COMMIT;
END;
