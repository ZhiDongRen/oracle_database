--Spatial index allow to optimize spatial queries by utilizing spatial operators

CREATE INDEX ESTATE2_spatial_index ON ESTATE2(shape)
INDEXTYPE IS MDSYS.SPATIAL_INDEX_V2;

