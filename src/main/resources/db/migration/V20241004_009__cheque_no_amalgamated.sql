/**
*
* CGI OPAL Program
*
* MODULE      : cheque_no_amalgamated.sql
*
* DESCRIPTION : Create CHEQUE_NO_AMALGAMATED table
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 04/10/2024    I Readman   1.0         PO-819 Create CHEQUE_NO_AMALGAMATED table
*
**/
CREATE TABLE cheque_no_amalgamated
(
 amalgamated_ct         varchar(3)     not null     
,master_ct              varchar(3)     not null
,old_cheque_no          varchar(6)      not null
,new_cheque_no          varchar(6)      not null
,insert_timestamp       timestamp       not null
,update_timestamp       timestamp
,encountered_timestamp  timestamp
);

CREATE INDEX cna_amalgamated_ct_old_cheque_idx ON cheque_no_amalgamated (amalgamated_ct, old_cheque_no);