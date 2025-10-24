-- Probation areas - NPS
insert into organisation(organisation_id, code, description)
values (organisation_id_seq.nextval, 'NPS', 'National Probation Service');
insert into probation_area(probation_area_id, code, description, organisation_id, selectable)
values (probation_area_id_seq.nextval, 'N01', 'NPS London', organisation_id_seq.currval, 'Y');
insert into probation_area(probation_area_id, code, description, organisation_id, selectable)
values (probation_area_id_seq.nextval, 'N02', 'NPS North East', organisation_id_seq.currval, 'Y');
insert into probation_area(probation_area_id, code, description, organisation_id, selectable)
values (probation_area_id_seq.nextval, 'N03', 'NPS North West', organisation_id_seq.currval, 'Y');
insert into probation_area(probation_area_id, code, description, organisation_id, selectable)
values (probation_area_id_seq.nextval, 'AVS', 'Avon & Somerset', organisation_id_seq.currval, 'N');

-- Probation areas - PO1 (C01, C02)
insert into organisation(organisation_id, code, description)
values (organisation_id_seq.nextval, 'PO1', 'Parent Organisation 1');
insert into probation_area(probation_area_id, code, description, organisation_id, selectable)
values (probation_area_id_seq.nextval, 'C01', 'CRC London', organisation_id_seq.currval, 'Y');
insert into probation_area(probation_area_id, code, description, organisation_id, selectable)
values (probation_area_id_seq.nextval, 'C02', 'CRC North East', organisation_id_seq.currval, 'Y');

-- Probation areas - PO2 (C03)
insert into organisation(organisation_id, code, description)
values (organisation_id_seq.nextval, 'PO2', 'Parent Organisation 2');
insert into probation_area(probation_area_id, code, description, organisation_id, selectable)
values (probation_area_id_seq.nextval, 'C03', 'CRC North West', organisation_id_seq.currval, 'Y');

-- Establishments
insert into organisation(organisation_id, code, description)
values (organisation_id_seq.nextval, 'EST', 'Establishment Org');
insert into probation_area(probation_area_id, code, description, organisation_id, establishment, selectable)
values (probation_area_id_seq.nextval, 'ACI', 'Altcourse (HMP)', organisation_id_seq.currval, 'Y', 'Y');
insert into probation_area(probation_area_id, code, description, organisation_id, establishment, selectable)
values (probation_area_id_seq.nextval, 'BFI', 'Bedford (HMP)', organisation_id_seq.currval, 'Y', 'Y');
insert into probation_area(probation_area_id, code, description, organisation_id, establishment, selectable)
values (probation_area_id_seq.nextval, 'NWI', 'Norwich (HMP & YOI)', organisation_id_seq.currval, 'Y', 'Y');
insert into probation_area(probation_area_id, code, description, organisation_id, establishment, selectable)
values (probation_area_id_seq.nextval, 'PVI', 'Pentonville (HMP)', organisation_id_seq.currval, 'Y', 'Y');

-- Probation Delivery Units
insert into borough (borough_id, code, description, selectable, probation_area_id)
values (borough_id_seq.nextval, 'B1', 'Borough A', 'N',
        ( select probation_area_id from probation_area where code = 'N01' ));
insert into borough (borough_id, code, description, selectable, probation_area_id)
values (borough_id_seq.nextval, 'B2', 'Borough B', 'Y',
        ( select probation_area_id from probation_area where code = 'N02' ));

-- Local Admin Units
insert into district (district_id, code, description, selectable, borough_id)
values (district_id_seq.nextval, 'LAU1', 'Local Admin Unit A', 'Y', ( select borough_id from borough where code = 'B1' ));
insert into district (district_id, code, description, selectable, borough_id)
values (district_id_seq.nextval, 'LAU2', 'Local Admin Unit B', 'Y', ( select borough_id from borough where code = 'B2' ));

-- Sub-contracted Providers
insert into sc_provider(sc_provider_id, code, description, active_flag, provider_id)
values (sc_provider_id_seq.nextval, 'N01SC1', 'NPS London SC 1', 1, ( select probation_area_id from probation_area where code = 'N01' ));
insert into sc_provider(sc_provider_id, code, description, active_flag, provider_id)
values (sc_provider_id_seq.nextval, 'N01SC2', 'NPS London SC 2', 1, ( select probation_area_id from probation_area where code = 'N01' ));
insert into sc_provider(sc_provider_id, code, description, active_flag, provider_id)
values (sc_provider_id_seq.nextval, 'N01SC3', 'NPS London SC 3', 1, ( select probation_area_id from probation_area where code = 'N01' ));
insert into sc_provider(sc_provider_id, code, description, active_flag, provider_id)
values (sc_provider_id_seq.nextval, 'N03SC3', 'NPS North West SC 1', 1, ( select probation_area_id from probation_area where code = 'N02' ));
insert into sc_provider(sc_provider_id, code, description, active_flag, provider_id)
values (sc_provider_id_seq.nextval, 'C01SC1', 'CRC London SC 1', 1, ( select probation_area_id from probation_area where code = 'C01' ));
insert into sc_provider(sc_provider_id, code, description, active_flag, provider_id)
values (sc_provider_id_seq.nextval, 'C01SC2', 'CRC London SC 2', 1, ( select probation_area_id from probation_area where code = 'C01' ));
insert into sc_provider(sc_provider_id, code, description, active_flag, provider_id)
values (sc_provider_id_seq.nextval, 'C02SC1', 'CRC North East SC 1', 1, ( select probation_area_id from probation_area where code = 'C02' ));
insert into sc_provider(sc_provider_id, code, description, active_flag, provider_id)
values (sc_provider_id_seq.nextval, 'C02SC2', 'CRC North East SC 1 (inactive)', 0, ( select probation_area_id from probation_area where code = 'C02' ));
insert into sc_provider(sc_provider_id, code, description, active_flag, end_date, provider_id)
values (sc_provider_id_seq.nextval, 'C02SC3', 'CRC North East SC 1 (end-dated)', 1, current_timestamp - 1, ( select probation_area_id from probation_area where code = 'C02' ));

-- Ref data
insert into r_reference_data_master (reference_data_master_id, code_set_name)
values (reference_data_master_id_seq.nextval, 'OFFICER GRADE');
insert into r_standard_reference_list (standard_reference_list_id, code_value, code_description, selectable, reference_data_master_id)
values (standard_reference_list_id_seq.nextval, 'GRADE1', 'Grade 1', 'Y', ( select reference_data_master_id from r_reference_data_master where code_set_name = 'OFFICER GRADE' ));
insert into r_standard_reference_list (standard_reference_list_id, code_value, code_description, selectable, reference_data_master_id)
values (standard_reference_list_id_seq.nextval, 'GRADE2', 'Grade 2', 'Y', ( select reference_data_master_id from r_reference_data_master where code_set_name = 'OFFICER GRADE' ));
insert into r_standard_reference_list (standard_reference_list_id, code_value, code_description, selectable, reference_data_master_id)
values (standard_reference_list_id_seq.nextval, 'GRADE3', 'Grade 3', 'N', ( select reference_data_master_id from r_reference_data_master where code_set_name = 'OFFICER GRADE' ));
insert into r_reference_data_master (reference_data_master_id, code_set_name)
values (reference_data_master_id_seq.nextval, 'DOMAIN EVENT TYPE');
insert into r_standard_reference_list (standard_reference_list_id, code_value, code_description, selectable, reference_data_master_id)
values (standard_reference_list_id_seq.nextval, 'probation-user.username.changed', 'probation-user.username.changed', 'Y',
        ( select reference_data_master_id from r_reference_data_master where code_set_name = 'DOMAIN EVENT TYPE' ));
insert into r_standard_reference_list (standard_reference_list_id, code_value, code_description, selectable, reference_data_master_id)
values (standard_reference_list_id_seq.nextval, 'probation.staff.updated', 'probation.staff.updated', 'Y',
        ( select reference_data_master_id from r_reference_data_master where code_set_name = 'DOMAIN EVENT TYPE' ));

-- Users/Staff
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'dummy', null, 'staff', 'N01A000', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, sc_provider_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Test', null, 'User', 'N01A001', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        ( select sc_provider_id from sc_provider where code = 'N01SC1' ), current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id, last_accessed_datetime)
values (user_id_seq.nextval, 0, 'Test', null, 'User', null, 'test.user', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A001' ), current_date);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id, last_accessed_datetime)
values (user_id_seq.nextval, 0, 'Inactive', null, 'User', current_timestamp - 10, 'test.user.inactive', ( select organisation_id from organisation where code = 'NPS' ), 0, null,
        current_date);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id, last_accessed_datetime)
values (user_id_seq.nextval, 0, 'Inactive', null, 'User (DB)', current_timestamp - 10, 'test.user.inactive.dbonly', ( select organisation_id from organisation where code = 'NPS' ),
        0, null, current_date);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id, last_accessed_datetime)
values (user_id_seq.nextval, 0, 'Test', null, 'User (Local)', null, 'test.user.local', ( select organisation_id from organisation where code = 'NPS' ), 0, null,
        current_timestamp - 30);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id, last_accessed_datetime)
values (user_id_seq.nextval, 0, 'Test', null, 'User (Private)', null, 'test.user.private', ( select organisation_id from organisation where code = 'PO1' ), 0, null, current_date);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Joe', null, 'Bloggs', 'N01A002', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id, created_by_user_id, created_datetime,
                   last_updated_user_id, last_updated_datetime)
values (user_id_seq.nextval, 0, 'Joe', null, 'Bloggs', null, 'Joe.Bloggs', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A002' ), 1, current_timestamp - 10, 1, current_timestamp - 1);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jane', null, 'Bloggs', 'N01A003', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id, created_by_user_id, created_datetime,
                   last_updated_user_id, last_updated_datetime)
values (user_id_seq.nextval, 0, 'Jane', null, 'Bloggs', null, 'Jane.Bloggs', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A003' ), 1, current_timestamp - 7, 1, current_timestamp - 2);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jim', null, 'Blogs', 'N01A004', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jim', null, 'Blogs', null, 'Jim.Blogs', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A004' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Tiffiny', null, 'Thrasher', 'N01A005', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Tiffiny', null, 'Thrasher', null, 'Tiffiny.Thrasher', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A005' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Yon', null, 'Yingling', 'N01A006', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Yon', null, 'Yingling', null, 'Yon.Yingling', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A006' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Emma', null, 'Elks', 'N01A007', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Emma', null, 'Elks', null, 'Emma.Elks', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A007' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Brittaney', null, 'Bohl', 'N01A008', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Brittaney', null, 'Bohl', null, 'Brittaney.Bohl', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A008' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Patrina', null, 'Pendergraft', 'N01A009', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Patrina', null, 'Pendergraft', null, 'Patrina.Pendergraft', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A009' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Kieth', null, 'Kallas', 'N01A010', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Kieth', null, 'Kallas', null, 'Kieth.Kallas', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A010' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Melany', null, 'Matte', 'N01A011', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Melany', null, 'Matte', null, 'Melany.Matte', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A011' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Kathlyn', null, 'Kass', 'N01A012', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Kathlyn', null, 'Kass', null, 'Kathlyn.Kass', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A012' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Shawanna', null, 'Savedra', 'N01A013', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Shawanna', null, 'Savedra', null, 'Shawanna.Savedra', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A013' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Hortencia', null, 'Heinecke', 'N01A014', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Hortencia', null, 'Heinecke', null, 'Hortencia.Heinecke', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A014' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Zella', null, 'Zajac', 'N01A015', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Zella', null, 'Zajac', null, 'Zella.Zajac', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A015' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Daniele', null, 'Dodge', 'N01A016', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Daniele', null, 'Dodge', null, 'Daniele.Dodge', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A016' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Luanna', null, 'Lannon', 'N01A017', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Luanna', null, 'Lannon', null, 'Luanna.Lannon', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A017' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Ela', null, 'Espy', 'N01A018', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Ela', null, 'Espy', null, 'Ela.Espy', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A018' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Bibi', null, 'Baumgarten', 'N01A019', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Bibi', null, 'Baumgarten', null, 'Bibi.Baumgarten', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A019' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Creola', null, 'Chaffin', 'N01A020', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Creola', null, 'Chaffin', null, 'Creola.Chaffin', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A020' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Pennie', null, 'Pembleton', 'N01A021', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Pennie', null, 'Pembleton', null, 'Pennie.Pembleton', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A021' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jada', null, 'Jankowski', 'N01A022', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jada', null, 'Jankowski', null, 'Jada.Jankowski', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A022' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Delmer', null, 'Dishon', 'N01A023', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Delmer', null, 'Dishon', null, 'Delmer.Dishon', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A023' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Joanna', null, 'Jong', 'N01A024', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Joanna', null, 'Jong', null, 'Joanna.Jong', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A024' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Margarita', null, 'Mchugh', 'N01A025', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Margarita', null, 'Mchugh', null, 'Margarita.Mchugh', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A025' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Casimira', null, 'Celestine', 'N01A026', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Casimira', null, 'Celestine', null, 'Casimira.Celestine', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A026' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Efrain', null, 'Elsberry', 'N01A027', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Efrain', null, 'Elsberry', null, 'Efrain.Elsberry', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A027' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Kiana', null, 'Kovacs', 'N01A028', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Kiana', null, 'Kovacs', null, 'Kiana.Kovacs', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A028' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Austin', null, 'Apo', 'N01A029', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Austin', null, 'Apo', null, 'Austin.Apo', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A029' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jason', null, 'Jellison', 'N01A030', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jason', null, 'Jellison', null, 'Jason.Jellison', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A030' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Raymundo', null, 'Rehberg', 'N01A031', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Raymundo', null, 'Rehberg', null, 'Raymundo.Rehberg', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A031' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Evan', null, 'Emery', 'N01A032', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Evan', null, 'Emery', null, 'Evan.Emery', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A032' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Joette', null, 'Judon', 'N01A033', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Joette', null, 'Judon', null, 'Joette.Judon', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A033' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Shanae', null, 'Scarbrough', 'N01A034', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Shanae', null, 'Scarbrough', null, 'Shanae.Scarbrough', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A034' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Onie', null, 'Olivarria', 'N01A035', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Onie', null, 'Olivarria', null, 'Onie.Olivarria', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A035' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Bethann', null, 'Bowersox', 'N01A036', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Bethann', null, 'Bowersox', null, 'Bethann.Bowersox', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A036' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Emerson', null, 'Eisenberg', 'N01A037', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Emerson', null, 'Eisenberg', null, 'Emerson.Eisenberg', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A037' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Freeda', null, 'Frizzell', 'N01A038', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Freeda', null, 'Frizzell', null, 'Freeda.Frizzell', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A038' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Deana', null, 'Dennie', 'N01A039', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Deana', null, 'Dennie', null, 'Deana.Dennie', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A039' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Mozelle', null, 'Miraglia', 'N01A040', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Mozelle', null, 'Miraglia', null, 'Mozelle.Miraglia', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A040' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Georgianna', null, 'Giroir', 'N01A041', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Georgianna', null, 'Giroir', null, 'Georgianna.Giroir', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A041' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Palmer', null, 'Parramore', 'N01A042', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Palmer', null, 'Parramore', null, 'Palmer.Parramore', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A042' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Lois', null, 'Loper', 'N01A043', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Lois', null, 'Loper', null, 'Lois.Loper', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A043' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Shawnda', null, 'Sawicki', 'N01A044', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Shawnda', null, 'Sawicki', null, 'Shawnda.Sawicki', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A044' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Loreen', null, 'Lorence', 'N01A045', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Loreen', null, 'Lorence', null, 'Loreen.Lorence', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A045' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Kellee', null, 'Kerner', 'N01A046', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Kellee', null, 'Kerner', null, 'Kellee.Kerner', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A046' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Josiah', null, 'Jeanbaptiste', 'N01A047', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Josiah', null, 'Jeanbaptiste', null, 'Josiah.Jeanbaptiste', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A047' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Coy', null, 'Caswell', 'N01A048', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Coy', null, 'Caswell', null, 'Coy.Caswell', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A048' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Sirena', null, 'Somma', 'N01A049', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Sirena', null, 'Somma', null, 'Sirena.Somma', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A049' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Allan', null, 'Amey', 'N01A050', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Allan', null, 'Amey', null, 'Allan.Amey', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A050' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Amee', null, 'Alsup', 'N01A051', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Amee', null, 'Alsup', null, 'Amee.Alsup', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A051' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Raleigh', null, 'Ricketts', 'N01A052', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Raleigh', null, 'Ricketts', null, 'Raleigh.Ricketts', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A052' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Linn', null, 'Liebsch', 'N01A053', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Linn', null, 'Liebsch', null, 'Linn.Liebsch', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A053' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Mitsuko', null, 'Moisan', 'N01A054', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Mitsuko', null, 'Moisan', null, 'Mitsuko.Moisan', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A054' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Latashia', null, 'Lothrop', 'N01A055', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Latashia', null, 'Lothrop', null, 'Latashia.Lothrop', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A055' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Silva', null, 'Schumann', 'N01A056', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Silva', null, 'Schumann', null, 'Silva.Schumann', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A056' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Yajaira', null, 'Yim', 'N01A057', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Yajaira', null, 'Yim', null, 'Yajaira.Yim', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A057' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Melony', null, 'Martyn', 'N01A058', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Melony', null, 'Martyn', null, 'Melony.Martyn', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A058' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Lelah', null, 'Lepley', 'N01A059', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Lelah', null, 'Lepley', null, 'Lelah.Lepley', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A059' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Marlana', null, 'Messana', 'N01A060', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Marlana', null, 'Messana', null, 'Marlana.Messana', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A060' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Deonna', null, 'Deaver', 'N01A061', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Deonna', null, 'Deaver', null, 'Deonna.Deaver', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A061' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Arline', null, 'Aleman', 'N01A062', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Arline', null, 'Aleman', null, 'Arline.Aleman', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A062' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Graciela', null, 'Guimond', 'N01A063', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Graciela', null, 'Guimond', null, 'Graciela.Guimond', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A063' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jamila', null, 'Jenks', 'N01A064', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jamila', null, 'Jenks', null, 'Jamila.Jenks', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A064' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Lakita', null, 'Lofton', 'N01A065', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Lakita', null, 'Lofton', null, 'Lakita.Lofton', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A065' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jimmie', null, 'Jewell', 'N01A066', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jimmie', null, 'Jewell', null, 'Jimmie.Jewell', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A066' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Renay', null, 'Rape', 'N01A067', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Renay', null, 'Rape', null, 'Renay.Rape', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A067' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Carlo', null, 'Charron', 'N01A068', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Carlo', null, 'Charron', null, 'Carlo.Charron', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A068' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Marcus', null, 'Mcguirk', 'N01A069', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Marcus', null, 'Mcguirk', null, 'Marcus.Mcguirk', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A069' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Pennie', null, 'Puente', 'N01A070', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Pennie', null, 'Puente', null, 'Pennie.Puente', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A070' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Sue', null, 'Shaner', 'N01A071', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Sue', null, 'Shaner', null, 'Sue.Shaner', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A071' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Denny', null, 'Dalton', 'N01A072', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Denny', null, 'Dalton', null, 'Denny.Dalton', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A072' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Sudie', null, 'Sipos', 'N01A073', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Sudie', null, 'Sipos', null, 'Sudie.Sipos', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A073' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jules', null, 'Jeffers', 'N01A074', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jules', null, 'Jeffers', null, 'Jules.Jeffers', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A074' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Alicia', null, 'Alfrey', 'N01A075', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Alicia', null, 'Alfrey', null, 'Alicia.Alfrey', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A075' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Ammie', null, 'Aquirre', 'N01A076', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Ammie', null, 'Aquirre', null, 'Ammie.Aquirre', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A076' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jacquetta', null, 'Janecek', 'N01A077', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jacquetta', null, 'Janecek', null, 'Jacquetta.Janecek', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A077' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Marvis', null, 'Molinar', 'N01A078', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Marvis', null, 'Molinar', null, 'Marvis.Molinar', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A078' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Leana', null, 'Leisinger', 'N01A079', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Leana', null, 'Leisinger', null, 'Leana.Leisinger', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A079' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Ladawn', null, 'Lanman', 'N01A080', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Ladawn', null, 'Lanman', null, 'Ladawn.Lanman', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A080' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Remedios', null, 'Redwood', 'N01A081', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Remedios', null, 'Redwood', null, 'Remedios.Redwood', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A081' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jutta', null, 'Jansson', 'N01A082', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jutta', null, 'Jansson', null, 'Jutta.Jansson', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A082' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Arminda', null, 'Altman', 'N01A083', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Arminda', null, 'Altman', null, 'Arminda.Altman', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A083' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Scottie', null, 'Stemen', 'N01A084', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Scottie', null, 'Stemen', null, 'Scottie.Stemen', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A084' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Sonny', null, 'Sober', 'N01A085', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Sonny', null, 'Sober', null, 'Sonny.Sober', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A085' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Leia', null, 'Leaman', 'N01A086', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Leia', null, 'Leaman', null, 'Leia.Leaman', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A086' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Beverlee', null, 'Basye', 'N01A087', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Beverlee', null, 'Basye', null, 'Beverlee.Basye', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A087' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Faith', null, 'Farrington', 'N01A088', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Faith', null, 'Farrington', null, 'Faith.Farrington', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A088' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Hollie', null, 'Hornick', 'N01A089', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Hollie', null, 'Hornick', null, 'Hollie.Hornick', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A089' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jess', null, 'Jalbert', 'N01A090', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jess', null, 'Jalbert', null, 'Jess.Jalbert', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A090' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Sherri', null, 'Swords', 'N01A091', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Sherri', null, 'Swords', null, 'Sherri.Swords', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A091' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Delena', null, 'Dauer', 'N01A092', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Delena', null, 'Dauer', null, 'Delena.Dauer', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A092' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Signe', null, 'Stoddart', 'N01A093', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Signe', null, 'Stoddart', null, 'Signe.Stoddart', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A093' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Sophia', null, 'Stocker', 'N01A094', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Sophia', null, 'Stocker', null, 'Sophia.Stocker', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A094' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Concepcion', null, 'Chunn', 'N01A095', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Concepcion', null, 'Chunn', null, 'Concepcion.Chunn', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A095' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Sha', null, 'Sak', 'N01A096', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Sha', null, 'Sak', null, 'Sha.Sak', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A096' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Louetta', null, 'Levy', 'N01A097', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Louetta', null, 'Levy', null, 'Louetta.Levy', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A097' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Reva', null, 'Rhoads', 'N01A098', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Reva', null, 'Rhoads', null, 'Reva.Rhoads', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A098' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Kirstie', null, 'Kinsman', 'N01A099', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Kirstie', null, 'Kinsman', null, 'Kirstie.Kinsman', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A099' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Serita', null, 'Schulz', 'N01A100', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Serita', null, 'Schulz', null, 'Serita.Schulz', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A100' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Johnathon', null, 'Joerling', 'N01A101', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Johnathon', null, 'Joerling', null, 'Johnathon.Joerling', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A101' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Maximo', null, 'Marcos', 'N01A102', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Maximo', null, 'Marcos', null, 'Maximo.Marcos', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A102' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Cheree', null, 'Castonguay', 'N01A103', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Cheree', null, 'Castonguay', null, 'Cheree.Castonguay', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A103' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Ruthanne', null, 'Rake', 'N01A104', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Ruthanne', null, 'Rake', null, 'Ruthanne.Rake', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A104' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Martin', null, 'Mowers', 'N01A105', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Martin', null, 'Mowers', null, 'Martin.Mowers', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A105' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Rachell', null, 'Roses', 'N01A106', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Rachell', null, 'Roses', null, 'Rachell.Roses', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A106' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jessie', null, 'Jeffreys', 'N01A107', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jessie', null, 'Jeffreys', null, 'Jessie.Jeffreys', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A107' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Ressie', null, 'Reddy', 'N01A108', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Ressie', null, 'Reddy', null, 'Ressie.Reddy', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A108' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Allie', null, 'Alarcon', 'N01A109', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Allie', null, 'Alarcon', null, 'Allie.Alarcon', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A109' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Lida', null, 'Lieser', 'N01A110', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Lida', null, 'Lieser', null, 'Lida.Lieser', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A110' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Dora', null, 'Delph', 'N01A111', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Dora', null, 'Delph', null, 'Dora.Delph', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A111' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Cathey', null, 'Courtemanche', 'N01A112', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Cathey', null, 'Courtemanche', null, 'Cathey.Courtemanche', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A112' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Dirk', null, 'Desrosiers', 'N01A113', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Dirk', null, 'Desrosiers', null, 'Dirk.Desrosiers', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A113' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Glen', null, 'Gama', 'N01A114', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Glen', null, 'Gama', null, 'Glen.Gama', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A114' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Sandi', null, 'Sons', 'N01A115', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Sandi', null, 'Sons', null, 'Sandi.Sons', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A115' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Sarina', null, 'Schroyer', 'N01A116', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Sarina', null, 'Schroyer', null, 'Sarina.Schroyer', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A116' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Maira', null, 'Meggs', 'N01A117', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Maira', null, 'Meggs', null, 'Maira.Meggs', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A117' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Vicki', null, 'Velarde', 'N01A118', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Vicki', null, 'Velarde', null, 'Vicki.Velarde', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A118' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Rashida', null, 'Roma', 'N01A119', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Rashida', null, 'Roma', null, 'Rashida.Roma', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A119' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Richie', null, 'Rafferty', 'N01A120', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Richie', null, 'Rafferty', null, 'Richie.Rafferty', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A120' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Adria', null, 'Alger', 'N01A121', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Adria', null, 'Alger', null, 'Adria.Alger', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A121' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Donetta', null, 'Dorado', 'N01A122', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Donetta', null, 'Dorado', null, 'Donetta.Dorado', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A122' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Milda', null, 'Mesta', 'N01A123', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Milda', null, 'Mesta', null, 'Milda.Mesta', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A123' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Cythia', null, 'Condrey', 'N01A124', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Cythia', null, 'Condrey', null, 'Cythia.Condrey', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A124' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Nery', null, 'Nawrocki', 'N01A125', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Nery', null, 'Nawrocki', null, 'Nery.Nawrocki', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A125' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Brant', null, 'Bair', 'N01A126', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Brant', null, 'Bair', null, 'Brant.Bair', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A126' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Paulene', null, 'Pappas', 'N01A127', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Paulene', null, 'Pappas', null, 'Paulene.Pappas', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A127' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Thurman', null, 'Tallarico', 'N01A128', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Thurman', null, 'Tallarico', null, 'Thurman.Tallarico', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A128' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Tawnya', null, 'Townsel', 'N01A129', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Tawnya', null, 'Townsel', null, 'Tawnya.Townsel', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A129' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Dorsey', null, 'Delay', 'N01A130', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Dorsey', null, 'Delay', null, 'Dorsey.Delay', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A130' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Zina', null, 'Zenon', 'N01A131', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Zina', null, 'Zenon', null, 'Zina.Zenon', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A131' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Aisha', null, 'Alvidrez', 'N01A132', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Aisha', null, 'Alvidrez', null, 'Aisha.Alvidrez', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A132' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Lionel', null, 'Landreth', 'N01A133', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Lionel', null, 'Landreth', null, 'Lionel.Landreth', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A133' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jaleesa', null, 'Journey', 'N01A134', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jaleesa', null, 'Journey', null, 'Jaleesa.Journey', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A134' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Autumn', null, 'Aungst', 'N01A135', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Autumn', null, 'Aungst', null, 'Autumn.Aungst', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A135' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Bobbie', null, 'Belanger', 'N01A136', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Bobbie', null, 'Belanger', null, 'Bobbie.Belanger', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A136' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Valda', null, 'Vandegrift', 'N01A137', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Valda', null, 'Vandegrift', null, 'Valda.Vandegrift', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A137' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Randal', null, 'Rathke', 'N01A138', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Randal', null, 'Rathke', null, 'Randal.Rathke', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A138' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jame', null, 'Jameson', 'N01A139', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jame', null, 'Jameson', null, 'Jame.Jameson', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A139' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Lisette', null, 'Lazaro', 'N01A140', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Lisette', null, 'Lazaro', null, 'Lisette.Lazaro', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A140' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Willy', null, 'Westhoff', 'N01A141', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Willy', null, 'Westhoff', null, 'Willy.Westhoff', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A141' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Mariella', null, 'Mark', 'N01A142', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Mariella', null, 'Mark', null, 'Mariella.Mark', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A142' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Porsha', null, 'Paugh', 'N01A143', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Porsha', null, 'Paugh', null, 'Porsha.Paugh', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A143' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Pat', null, 'Platero', 'N01A144', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Pat', null, 'Platero', null, 'Pat.Platero', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A144' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Pearle', null, 'Pegues', 'N01A145', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Pearle', null, 'Pegues', null, 'Pearle.Pegues', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A145' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Julieta', null, 'Jacquet', 'N01A146', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Julieta', null, 'Jacquet', null, 'Julieta.Jacquet', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A146' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Darcey', null, 'Denker', 'N01A147', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Darcey', null, 'Denker', null, 'Darcey.Denker', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A147' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Justina', null, 'Jawad', 'N01A148', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Justina', null, 'Jawad', null, 'Justina.Jawad', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A148' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Charita', null, 'Colton', 'N01A149', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Charita', null, 'Colton', null, 'Charita.Colton', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A149' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Shanae', null, 'Sickles', 'N01A150', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Shanae', null, 'Sickles', null, 'Shanae.Sickles', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A150' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Emma', null, 'Esteban', 'N01A151', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Emma', null, 'Esteban', null, 'Emma.Esteban', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A151' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Chau', null, 'Chaffee', 'N01A152', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Chau', null, 'Chaffee', null, 'Chau.Chaffee', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A152' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Brynn', null, 'Brumm', 'N01A153', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Brynn', null, 'Brumm', null, 'Brynn.Brumm', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A153' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Vickie', null, 'Veiga', 'N01A154', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Vickie', null, 'Veiga', null, 'Vickie.Veiga', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A154' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Kathe', null, 'Kingsley', 'N01A155', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Kathe', null, 'Kingsley', null, 'Kathe.Kingsley', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A155' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Johnathon', null, 'Jungers', 'N01A156', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Johnathon', null, 'Jungers', null, 'Johnathon.Jungers', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A156' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Robby', null, 'Riesgo', 'N01A157', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Robby', null, 'Riesgo', null, 'Robby.Riesgo', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A157' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Andra', null, 'Abril', 'N01A158', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Andra', null, 'Abril', null, 'Andra.Abril', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A158' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Kimberli', null, 'Kimmell', 'N01A159', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Kimberli', null, 'Kimmell', null, 'Kimberli.Kimmell', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A159' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Verda', null, 'Valenta', 'N01A160', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Verda', null, 'Valenta', null, 'Verda.Valenta', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A160' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Randall', null, 'Rowell', 'N01A161', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Randall', null, 'Rowell', null, 'Randall.Rowell', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A161' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Keneth', null, 'Kennerson', 'N01A162', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Keneth', null, 'Kennerson', null, 'Keneth.Kennerson', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A162' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Krissy', null, 'Knapp', 'N01A163', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Krissy', null, 'Knapp', null, 'Krissy.Knapp', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A163' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Corinne', null, 'Cleaver', 'N01A164', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Corinne', null, 'Cleaver', null, 'Corinne.Cleaver', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A164' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Ranee', null, 'Retana', 'N01A165', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Ranee', null, 'Retana', null, 'Ranee.Retana', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A165' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Moses', null, 'Mcroy', 'N01A166', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Moses', null, 'Mcroy', null, 'Moses.Mcroy', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A166' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Allyson', null, 'Allinder', 'N01A167', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Allyson', null, 'Allinder', null, 'Allyson.Allinder', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A167' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Abdul', null, 'Austria', 'N01A168', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Abdul', null, 'Austria', null, 'Abdul.Austria', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A168' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Eulalia', null, 'Echeverria', 'N01A169', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Eulalia', null, 'Echeverria', null, 'Eulalia.Echeverria', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A169' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Majorie', null, 'Mcginley', 'N01A170', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Majorie', null, 'Mcginley', null, 'Majorie.Mcginley', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A170' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jong', null, 'Jinks', 'N01A171', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jong', null, 'Jinks', null, 'Jong.Jinks', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A171' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Dominque', null, 'Draeger', 'N01A172', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Dominque', null, 'Draeger', null, 'Dominque.Draeger', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A172' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Alpha', null, 'Abadie', 'N01A173', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Alpha', null, 'Abadie', null, 'Alpha.Abadie', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A173' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Ivory', null, 'Inouye', 'N01A174', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Ivory', null, 'Inouye', null, 'Ivory.Inouye', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A174' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jeffery', null, 'June', 'N01A175', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jeffery', null, 'June', null, 'Jeffery.June', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A175' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Coralee', null, 'Cody', 'N01A176', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Coralee', null, 'Cody', null, 'Coralee.Cody', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A176' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Willie', null, 'Wieland', 'N01A177', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Willie', null, 'Wieland', null, 'Willie.Wieland', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A177' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Leonor', null, 'Labat', 'N01A178', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Leonor', null, 'Labat', null, 'Leonor.Labat', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A178' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Denese', null, 'Duchesne', 'N01A179', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Denese', null, 'Duchesne', null, 'Denese.Duchesne', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A179' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Waldo', null, 'Wasden', 'N01A180', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Waldo', null, 'Wasden', null, 'Waldo.Wasden', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A180' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Hope', null, 'Heer', 'N01A181', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Hope', null, 'Heer', null, 'Hope.Heer', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A181' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Alan', null, 'Aubin', 'N01A182', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Alan', null, 'Aubin', null, 'Alan.Aubin', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A182' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Reginald', null, 'Reese', 'N01A183', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Reginald', null, 'Reese', null, 'Reginald.Reese', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A183' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Bari', null, 'Bonomo', 'N01A184', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Bari', null, 'Bonomo', null, 'Bari.Bonomo', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A184' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Tanja', null, 'Truehart', 'N01A185', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Tanja', null, 'Truehart', null, 'Tanja.Truehart', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A185' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Terrie', null, 'Ton', 'N01A186', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Terrie', null, 'Ton', null, 'Terrie.Ton', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A186' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Kirsten', null, 'Klenke', 'N01A187', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Kirsten', null, 'Klenke', null, 'Kirsten.Klenke', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A187' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Neda', null, 'Nance', 'N01A188', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Neda', null, 'Nance', null, 'Neda.Nance', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A188' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Lakeisha', null, 'Labat', 'N01A189', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Lakeisha', null, 'Labat', null, 'Lakeisha.Labat', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A189' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Iraida', null, 'Iadarola', 'N01A190', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Iraida', null, 'Iadarola', null, 'Iraida.Iadarola', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A190' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Jaclyn', null, 'Jelley', 'N01A191', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Jaclyn', null, 'Jelley', null, 'Jaclyn.Jelley', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A191' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Virgilio', null, 'Valles', 'N01A192', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Virgilio', null, 'Valles', null, 'Virgilio.Valles', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A192' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Daphne', null, 'Dewees', 'N01A193', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Daphne', null, 'Dewees', null, 'Daphne.Dewees', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A193' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Daniel', null, 'Detty', 'N01A194', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Daniel', null, 'Detty', null, 'Daniel.Detty', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A194' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Rachell', null, 'Ralls', 'N01A195', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Rachell', null, 'Ralls', null, 'Rachell.Ralls', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A195' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Perla', null, 'Palmquist', 'N01A196', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Perla', null, 'Palmquist', null, 'Perla.Palmquist', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A196' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Shanel', null, 'Saucedo', 'N01A197', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Shanel', null, 'Saucedo', null, 'Shanel.Saucedo', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A197' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Gearldine', null, 'Grasty', 'N01A198', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Gearldine', null, 'Grasty', null, 'Gearldine.Grasty', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A198' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Marivel', null, 'Miraglia', 'N01A199', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Marivel', null, 'Miraglia', null, 'Marivel.Miraglia', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A199' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Shenita', null, 'Sletten', 'N01A200', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Shenita', null, 'Sletten', null, 'Shenita.Sletten', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A200' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Golda', null, 'Galvin', 'N01A201', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Golda', null, 'Galvin', null, 'Golda.Galvin', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A201' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Suzette', null, 'Smail', 'N01A202', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Suzette', null, 'Smail', null, 'Suzette.Smail', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A202' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'Mavis', null, 'Mehler', 'N01A203', ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ),
        current_timestamp - 10, null);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, 'Mavis', null, 'Mehler', null, 'Mavis.Mehler', ( select organisation_id from organisation where code = 'NPS' ), 0,
        ( select staff_id from staff where officer_code = 'N01A203' ));
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'test.user.duplicate-staff-inactive', null, 'test.user.duplicate-staff-inactive', 'N01A204',
        ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ), current_timestamp - 10, current_timestamp - 1);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'test.user.duplicate-staff-inactive2', null, 'test.user.duplicate-staff-inactive2', 'N01A204',
        ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ), current_timestamp - 10, current_timestamp - 1);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'test.user.duplicate-staff-active', null, 'test.user.duplicate-staff-active', 'N01A205',
        ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ), current_timestamp - 10, null);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'test.user.duplicate-staff-active2', null, 'test.user.duplicate-staff-active2', 'N01A205',
        ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ), current_timestamp - 10, null);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'test.user.duplicate-staff-active3', null, 'test.user.duplicate-staff-active3', 'N01A206',
        ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ), current_timestamp - 10, null);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'test.user.duplicate-staff-inactive3', null, 'test.user.duplicate-staff-inactive3', 'N01A206',
        ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ), current_timestamp - 10, current_timestamp - 1);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'test.user.duplicate-staff-inactive4', null, 'test.user.duplicate-staff-inactive4', 'N01A206',
        ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ), current_timestamp - 10, current_timestamp - 1);
insert into staff (staff_id, row_version, forename, forename2, surname, officer_code, staff_grade_id, start_date, end_date)
values (staff_id_seq.nextval, 0, 'test.user.duplicate-staff-inactive5', null, 'test.user.duplicate-staff-inactive5', 'N01A207',
        ( select standard_reference_list_id from r_standard_reference_list where code_value = 'GRADE1' ), current_timestamp - 10, current_timestamp - 1);
insert into user_ (user_id, row_version, forename, forename2, surname, end_date, distinguished_name, organisation_id, private, staff_id)
values (user_id_seq.nextval, 0, '[Data Maintenance]', null, '[Data Maintenance]', null, '[Data Maintenance]', null, 0, null);

-- History
insert into user_note (user_note_id, row_version, user_id, last_updated_user_id, last_updated_datetime, notes)
values (user_note_id_seq.nextval, 0, ( select user_id from user_ where distinguished_name = 'Joe.Bloggs' ), ( select user_id from user_ where distinguished_name = 'Jane.Bloggs' ),
        current_timestamp - 1.1,
        'Granted access to Fileshare 1, following request "XX-12345".' || chr(10) || 'Also corrected a typo in the surname as per conversation with the user''s manager.');
insert into user_note (user_note_id, row_version, user_id, last_updated_user_id, last_updated_datetime, notes)
values (user_note_id_seq.nextval, 0, ( select user_id from user_ where distinguished_name = 'Joe.Bloggs' ), ( select user_id from user_ where distinguished_name = 'Jane.Bloggs' ),
        current_timestamp - 3.3, null);
insert into user_note (user_note_id, row_version, user_id, last_updated_user_id, last_updated_datetime, notes)
values (user_note_id_seq.nextval, 0, ( select user_id from user_ where distinguished_name = 'Joe.Bloggs' ), ( select user_id from user_ where distinguished_name = 'Jane.Bloggs' ),
        current_timestamp - 4.4, null);
insert into user_note (user_note_id, row_version, user_id, last_updated_user_id, last_updated_datetime, notes)
values (user_note_id_seq.nextval, 0, ( select user_id from user_ where distinguished_name = 'Joe.Bloggs' ), ( select user_id from user_ where distinguished_name = 'Mavis.Mehler' ),
        current_timestamp - 9.9, 'Added user management roles, to allow access to UMT.');
insert into user_note (user_note_id, row_version, user_id, last_updated_user_id, last_updated_datetime, notes)
values (user_note_id_seq.nextval, 0, ( select user_id from user_ where distinguished_name = 'Joe.Bloggs' ),
        ( select user_id from user_ where distinguished_name = '[Data Maintenance]' ), current_timestamp - 9.9, 'Added user management roles, to allow access to UMT.');

-- Datasets
insert into probation_area_user (probation_area_id, user_id)
values (( select probation_area_id from probation_area where code = 'N01' ), ( select user_id from user_ where distinguished_name = 'test.user' ));
insert into probation_area_user (probation_area_id, user_id)
values (( select probation_area_id from probation_area where code = 'N02' ), ( select user_id from user_ where distinguished_name = 'test.user' ));
insert into probation_area_user (probation_area_id, user_id)
values (( select probation_area_id from probation_area where code = 'N03' ), ( select user_id from user_ where distinguished_name = 'test.user' ));
insert into probation_area_user (probation_area_id, user_id)
values (( select probation_area_id from probation_area where code = 'N03' ), ( select user_id from user_ where distinguished_name = 'Jane.Bloggs' ));
insert into probation_area_user (probation_area_id, user_id)
values (( select probation_area_id from probation_area where code = 'C01' ), ( select user_id from user_ where distinguished_name = 'test.user.private' ));
insert into probation_area_user (probation_area_id, user_id)
values (( select probation_area_id from probation_area where code = 'C02' ), ( select user_id from user_ where distinguished_name = 'test.user.private' ));

-- Teams
insert into team (team_id, code, description, probation_area_id, end_date)
values (team_id_seq.nextval, 'N01TST', 'Test team (Ended)', ( select probation_area_id from probation_area where code = 'N01' ), current_timestamp);
insert into team (team_id, code, description, probation_area_id, district_id)
values (team_id_seq.nextval, 'N01TST', 'Test team', ( select probation_area_id from probation_area where code = 'N01' ),
        ( select district_id from district where code = 'LAU2' ));
insert into team (team_id, code, description, probation_area_id, district_id)
values (team_id_seq.nextval, 'N02TST', 'Other team', ( select probation_area_id from probation_area where code = 'N02' ),
        ( select district_id from district where code = 'LAU1' ));
insert into team (team_id, code, description, probation_area_id, district_id)
values (team_id_seq.nextval, 'N03TST', 'Another', ( select probation_area_id from probation_area where code = 'N03' ),
        ( select district_id from district where code = 'LAU1' ));
insert into staff_team (staff_id, team_id)
values (( select staff_id from staff where officer_code = 'N01A001' ), ( select team_id from team where code = 'N01TST' and end_date is null ));
insert into staff_team (staff_id, team_id)
values (( select staff_id from staff where officer_code = 'N01A002' ), ( select team_id from team where code = 'N01TST' and end_date is null ));
insert into staff_team (staff_id, team_id)
values (( select staff_id from staff where officer_code = 'N01A003' ), ( select team_id from team where code = 'N01TST' and end_date is null ));
insert into staff_team (staff_id, team_id)
values (( select staff_id from staff where officer_code = 'N01A001' ), ( select team_id from team where code = 'N02TST' ));
insert into staff_team (staff_id, team_id)
values (( select staff_id from staff where officer_code = 'N01A201' ), ( select team_id from team where code = 'N02TST' ));
insert into staff_team (staff_id, team_id)
values (( select staff_id from staff where officer_code = 'N01A202' ), ( select team_id from team where code = 'N02TST' ));
insert into staff_team (staff_id, team_id)
values (( select staff_id from staff where officer_code = 'N01A203' ), ( select team_id from team where code = 'N02TST' ));
insert into staff_team (staff_id, team_id)
values (( select staff_id from staff where officer_code = 'N01A001' ), ( select team_id from team where code = 'N03TST' ));
