drop table ESTATE2;

CREATE TABLE ESTATE2(
    building_id number,
    type VARCHAR(32),
    floor number,
    shape SDO_GEOMETRY,
    PRIMARY KEY(building_id,type)
    );

INSERT INTO USER_SDO_GEOM_METADATA VALUES (
    'ESTATE2','shape',
    -- X/Y axes in range 0-280 and accurancy 1 points (the size of the mesh is given, the accurancy needs to be lower than 1 point for circular trees)
	SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 280, 0.1), SDO_DIM_ELEMENT('Y', 0, 280, 0.1)),
	-- a local spatial reference system (not geographical; analytical functions will be without units)
	NULL
);

COMMIT;

-- check the validity 
-- with the custom accurance
SELECT building_id, SDO_GEOM.VALIDATE_GEOMETRY_WITH_CONTEXT(shape, 1) valid -- 0.1 is accurance
FROM ESTATE2;
-- without the custom accurance (with respect to the accurance set in the SDO_GEOM_METADATA
SELECT e.building_id, e.shape.ST_IsValid()
FROM estate2 e;