/**
*
* CGI OPAL Program
*
* MODULE      : ac_number_rel.sql
*
* DESCRIPTION : Create AC_NUMBER_REL table
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 04/10/2024    I Readman   1.0         PO-819 Create AC_NUMBER_REL table
*
**/
CREATE TABLE ac_number_rel
(
 id1                        bigint          not null
,id2                        bigint          not null
,relationship               varchar(10)   
,status                     varchar(1)
,insert_date_time_stamp     timestamp
,update_date_time_stamp     timestamp
,CONSTRAINT anr_id_pk PRIMARY KEY (id1, id2)
,CONSTRAINT anr_relationship_ck CHECK (relationship IN ('APR','CON','LEG'))
,CONSTRAINT anr_status_ck CHECK (status IN ('A','I'))
);

CREATE INDEX anr_id1_idx ON ac_number_rel (id1);
CREATE INDEX anr_id2_idx ON ac_number_rel (id2);

COMMENT ON COLUMN ac_number_rel.id1 IS 'ID of a row in the ACCOUNT_NUMBER_TRANSLATION2 table, as a mapping with the ID2 column. ID1 is the child reference, e.g. consolidated AC number, APR';
COMMENT ON COLUMN ac_number_rel.id2 IS 'ID of a row in the ACCOUNT_NUMBER_TRANSLATION2 table, as a mapping with the ID1 column. ID2 is the parent reference, i.e. the AC number of def account to pay against';
COMMENT ON COLUMN ac_number_rel.relationship IS 'Type of relationship between records identified by ID1 & ID2';
COMMENT ON COLUMN ac_number_rel.insert_date_time_stamp IS 'Timestamp for when the record was inserted (session start time)';
COMMENT ON COLUMN ac_number_rel.update_date_time_stamp IS 'Timestamp for when the record was updated, e.g. CLOSE_STATUS, (session start time)';
