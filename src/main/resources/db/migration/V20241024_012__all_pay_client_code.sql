/**
*
* CGI OPAL Program
*
* MODULE      : all_pay_client_code.sql
*
* DESCRIPTION : Create ALL_PAY_CLIENT_CODE table
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 24/10/2024    D Clarke   1.0         PO-410 Create ALL_PAY_CLIENT_CODE table
*
**/
CREATE TABLE all_pay_client_code
(
 business_unit_id   varchar(4)    not null
,all_pay_client_code    varchar(4)    not null
);

CREATE PRIMARY KEY all_pay_client_code_pk ON all_pay_client_code (business_unit_id);
