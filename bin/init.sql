-- Create opal-file-db and user
CREATE DATABASE "opal-file-db";
CREATE USER "opal-file" WITH ENCRYPTED PASSWORD 'opal-file';
GRANT ALL PRIVILEGES ON DATABASE "opal-file-db" TO "opal-file";

