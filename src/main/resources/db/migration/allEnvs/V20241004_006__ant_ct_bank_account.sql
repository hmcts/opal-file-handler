/**
*
* CGI OPAL Program
*
* MODULE      : ant_ct_bank_account.sql
*
* DESCRIPTION : Create ANT_CT_BANK_ACCOUNT table
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 04/10/2024    I Readman   1.0         PO-819 Create ANT_CT_BANK_ACCOUNT table
*
**/
CREATE TABLE ant_ct_bank_account
(
 ct               varchar(3)     
,sortcode         varchar(6)  
,account_no       varchar(8)  
,dwp_court_code   varchar(20)
);

CREATE UNIQUE INDEX acba_dwp_court_code_idx ON ant_ct_bank_account (dwp_court_code);
