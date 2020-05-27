insert into ESTATE2 VALUES(
    20,
    'PARK',
    1,
    SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(240,30,270,30,270,70,260,80,230,80,230,50,240,50,240,30)
	)  
);

insert into ESTATE2 VALUES(
    21,
    'GARDEN',
    1,
    SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003,3, 5,2003,4), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(90,120,160,170,
        125,130, 140,145, 125,160)
	)  
);

insert into ESTATE2 VALUES(
    22,
    'PLEASURE_GROUND',
    1,
    SDO_GEOMETRY(2004, NULL, NULL, 
		SDO_ELEM_INFO_ARRAY(1, 1003, 4), 
		SDO_ORDINATE_ARRAY(125,130, 140,145, 125,160)
	)  
);

insert into ESTATE2 VALUES(
    23,
    'MALL',
    3,
    SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1,1005,4, 1,2,2, 5,2,1, 11,2,2, 15,2,1), -- exterior rectangle (left-bottom, right-top)
		SDO_ORDINATE_ARRAY(110,30, 140,20, 170,30, 210,30, 210,50, 170,50, 140,70, 110,50, 50,50, 50,30, 110,30)
	)
);