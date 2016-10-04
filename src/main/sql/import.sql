
drop database if exists inventory;
create database inventory;
use inventory;

SET AUTOCOMMIT = 0;
SET FOREIGN_KEY_CHECKS=0;

SOURCE inventorynoimages.sql;

SET FOREIGN_KEY_CHECKS = 1;
COMMIT;
SET AUTOCOMMIT = 1;