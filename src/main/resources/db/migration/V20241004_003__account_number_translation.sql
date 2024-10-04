/**
*
* CGI OPAL Program
*
* MODULE      : account_number_translation2.sql
*
* DESCRIPTION : Create ACCOUNT_NUMBER_TRANSLATION2 table  
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 04/10/2024    I Readman   1.0         PO-819 Create ACCOUNT_NUMBER_TRANSLATION2 table 
*
**/
CREATE TABLE account_number_translation2
(
 id                         bigint          not null
,mcc_ct                     varchar(8)      not null
,reference                  varchar(20)     not null
,category                   varchar(3)
,acc_type                   varchar(1)
,payment_type               varchar(3)
,close_status               varchar(1)
,reason_close_status        varchar(1)
,date_close_status          timestamp
,insert_date_time_stamp     timestamp
,update_date_time_stamp     timestamp
,CONSTRAINT ant2_id_pk PRIMARY KEY (id)
,CONSTRAINT ant2_category_ck CHECK (category IN ('APR','ACC'))
,CONSTRAINT ant2_acc_type_ck CHECK (acc_type IN ('F','M','C','R','S'))
,CONSTRAINT ant2_close_status_ck CHECK (close_status IN ('N','Y'))
,CONSTRAINT ant2_reason_close_status CHECK (reason_close_status IN ('Z'))
);

COMMENT ON COLUMN account_number_translation2.date_close_status IS 'Timestamp for when the CLOSE_STATUS was applied (session start time)';
COMMENT ON COLUMN account_number_translation2.insert_date_time_stamp IS 'Timestamp for when the record was inserted (session start time)';
COMMENT ON COLUMN account_number_translation2.update_date_time_stamp IS 'Timestamp for when the record was updated, e.g. CLOSE_STATUS, (session start time)';

CREATE INDEX ant2_mcc_ct_ref_idx ON account_number_translation2 (mcc_ct, reference);

CREATE SEQUENCE IF NOT EXISTS ant2_id_seq INCREMENT 1 START 1 MINVALUE 1 NO MAXVALUE CACHE 20 OWNED BY account_number_translation2.id;