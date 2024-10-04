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
,relationship               varchar(10)     not null
,status                     varchar(1)
,insert_date_time_stamp     timestamp
,update_date_time_stamp     timestamp
,CONSTRAINT anr_id_pk PRIMARY KEY (id1, id2)
,CONSTRAINT anr_id1_fk FOREIGN KEY (id1) REFERENCES account_number_translation2 (id)
,CONSTRAINT anr_id2_fk FOREIGN KEY (id2) REFERENCES account_number_translation2 (id)
,CONSTRAINT anr_relationship_ck CHECK (relationship IN ('APR','CON','LEG'))
,CONSTRAINT anr_status_ck CHECK (status IN ('A','I'))
);

CREATE INDEX anr_id1_idx ON ac_number_rel (id1);
CREATE INDEX anr_id2_idx ON ac_number_rel (id2);

COMMENT ON COLUMN ac_number_rel.relationship IS 'Type of relationship between records identified by ID1 & ID2';
COMMENT ON COLUMN ac_number_rel.insert_date_time_stamp IS 'Timestamp for when the record was inserted (session start time)';
COMMENT ON COLUMN ac_number_rel.update_date_time_stamp IS 'Timestamp for when the record was updated, e.g. CLOSE_STATUS, (session start time)';