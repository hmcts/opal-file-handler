/**
*
* CGI OPAL Program
*
* MODULE      : cheque_bank_amalgamated.sql
*
* DESCRIPTION : Create CHEQUE_BANK_AMALGAMATED table
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 04/10/2024    I Readman   1.0         PO-819 Create CHEQUE_BANK_AMALGAMATED table
*
**/
CREATE TABLE cheque_bank_amalgamated
(
 amalgamated_ct         varchar(3)     not null     
,master_ct              varchar(3)     not null
,old_bank_acc           varchar(8)      not null
,old_sort_code          varchar(6)      not null
,master_bank_acc        varchar(8)      not null
,master_sort_code       varchar(6)      not null
,insert_timestamp       timestamp       not null
,update_timestamp       timestamp
);

CREATE INDEX cba_amalgamated_master_ct_idx ON cheque_bank_amalgamated (amalgamated_ct, master_ct);