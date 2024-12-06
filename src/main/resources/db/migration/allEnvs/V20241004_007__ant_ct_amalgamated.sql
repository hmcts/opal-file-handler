/**
*
* CGI OPAL Program
*
* MODULE      : ant_ct_amalgamated.sql
*
* DESCRIPTION : Create ANT_CT_AMALGAMATED table
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 04/10/2024    I Readman   1.0         PO-819 Create ANT_CT_AMALGAMATED table
*
**/
CREATE TABLE ant_ct_amalgamated
(
 amalgamated_ct         varchar(3)     not null     
,master_ct              varchar(3)     not null
,master_bank_account    varchar(8)     not null
,master_sort_code       varchar(6)     not null
,insert_timestamp       timestamp      not null
,update_timestamp       timestamp
);

CREATE UNIQUE INDEX acm_amalgamated_ct_idx ON ant_ct_amalgamated (amalgamated_ct);
