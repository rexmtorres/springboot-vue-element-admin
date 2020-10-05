drop database oauth2;
create database oauth2;
ALTER SCHEMA `oauth2`  DEFAULT CHARACTER SET utf8 ;
grant all on oauth2.* to 'springuser'@'%';
