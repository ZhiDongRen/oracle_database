insert into ESTATE2 VALUES(
    16,
    'GRASS_AREA_1',
    1,
    SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 3), -- exterior rectangle (left-bottom, right-top)
		SDO_ORDINATE_ARRAY(10,30,30,70)
	)
);

insert into ESTATE2 VALUES(
    17,
    'GRASS_AREA_2',
    1,
    SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 3), -- exterior rectangle (left-bottom, right-top)
		SDO_ORDINATE_ARRAY(40,70,70,80)
	)
);

insert into ESTATE2 VALUES(
    18,
    'GRASS_AREA_3',
    1,
    SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 3), -- exterior rectangle (left-bottom, right-top)
		SDO_ORDINATE_ARRAY(170,70,190,80)
	)
);

insert into ESTATE2 VALUES(
    19,
    'GRASS_AREA_4',
    1,
    SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 3), -- exterior rectangle (left-bottom, right-top)
		SDO_ORDINATE_ARRAY(200,60,220,80)
	)
);