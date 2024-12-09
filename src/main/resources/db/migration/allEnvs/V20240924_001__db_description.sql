/**
*
* CGI OPAL Program
*
* MODULE      : database_comment.sql
*
* DESCRIPTION : Create Initial Migration File 
*
* VERSION HISTORY:
*
* Date          Author      Version     Nature of Change
* ----------    -------     --------    ----------------------------------------------------------------------------
* 24/09/2024    I Readman   1.0         PO-793 Create File Handler Database
*
**/
-- Database build required an inital migration file so adding a description to the database
COMMENT ON DATABASE "opal-file-db" IS 'Opal File Handler Database';
