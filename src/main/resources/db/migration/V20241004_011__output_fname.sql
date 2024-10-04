/**
*
* CGI OPAL Program
*
* MODULE      : output_fname.sql
*
* DESCRIPTION : Create OUTPUT_FNAME table
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 04/10/2024    I Readman   1.0         PO-819 Create OUTPUT_FNAME table
*
**/
CREATE TABLE output_fname
(
 doc_id             bigint          not null     
,fname              varchar(150)    not null
,generated          timestamp       not null
,status             varchar(10)
,full_path          varchar(500)    not null
);

CREATE INDEX of_doc_id_idx ON output_fname (doc_id);