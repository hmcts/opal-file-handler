/**
*
* CGI OPAL Program
*
* MODULE      : create_file_name_sequences.sql
*
* DESCRIPTION : Create the sequences that would be used to form part of the names of files
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ---------------------------------------------------------------------------------
* 31/10/2024    A Dennis    1.0         PO-943 Create the sequences that would be used to form part of the names of files
*
**/

CREATE SEQUENCE IF NOT EXISTS datalink_filename_seq INCREMENT 1 MINVALUE 1 MAXVALUE 99999999 START WITH 1 CYCLE CACHE 20;

CREATE SEQUENCE IF NOT EXISTS lif01201_filename_seq INCREMENT 1 MINVALUE 1 MAXVALUE 59999 START WITH 1 CYCLE CACHE 20;
