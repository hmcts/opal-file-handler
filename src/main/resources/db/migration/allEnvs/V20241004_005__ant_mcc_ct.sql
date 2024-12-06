/**
*
* CGI OPAL Program
*
* MODULE      : ant_mcc_ct.sql
*
* DESCRIPTION : Create ANT_MCC_CT table  
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 04/10/2024    I Readman   1.0         PO-819 Create ANT_MCC_CT table  
*
**/
CREATE TABLE ant_mcc_ct
(
 area             varchar(60)     
,legacy_system    varchar(10)  
,ct               varchar(3)  
,mcc_ct           varchar(8)
);