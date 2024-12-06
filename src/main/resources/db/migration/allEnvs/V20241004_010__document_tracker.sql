/**
*
* CGI OPAL Program
*
* MODULE      : document_tracker.sql
*
* DESCRIPTION : Create DOCUMENT_TRACKER table
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 04/10/2024    I Readman   1.0         PO-819 Create DOCUMENT_TRACKER table
*
**/
CREATE TABLE document_tracker
(
 doc_id             bigint          not null     
,file_name          varchar(150)    not null
,source             varchar(150)    not null
,start_time         timestamp       not null
,end_time           timestamp    
,current_location   varchar(40)     
,bdu_success_count  bigint
,bdu_error_count    bigint
,bdu_total          bigint
,status             varchar(40)
,message            varchar(500)
,CONSTRAINT dt_doc_id_pk PRIMARY KEY (doc_id)
);

CREATE UNIQUE INDEX dt_file_name_idx ON document_tracker (file_name);
CREATE UNIQUE INDEX dt_start_time_idx ON document_tracker (start_time);
