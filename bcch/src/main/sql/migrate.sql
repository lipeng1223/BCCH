SET FOREIGN_KEY_CHECKS=0;

-- trigger drops
drop trigger if exists order_item_update_trigger;
drop trigger if exists inventory_item_update_trigger;
drop trigger if exists received_item_update_trigger;
drop table if exists bri_count;

-- user 
alter table users rename to user;
alter table user drop primary key;
alter table user add column id bigint primary key auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime, add column active boolean;
alter table user modify column active boolean;
update user set active = true;
alter table user add column email varchar(255), add column firstName varchar(128), add column lastName varchar(128), add column employeeId varchar(64);


drop table if exists audit;
create table audit (id bigint not null auto_increment, parentTableId bigint, auditTime datetime, auditMessage varchar(255), username varchar(255), auditAction varchar(255), previousValue1 varchar(255), 
previousValue2 varchar(255), previousValue3 varchar(255), previousValue4 varchar(255), previousValue5 varchar(255), previousValue6 varchar(255), 
previousValue7 varchar(255), previousValue8 varchar(255), previousValue9 varchar(255), previousValue10 varchar(255), previousValue11 varchar(255), 
previousValue12 varchar(255), previousValue13 varchar(255), previousValue14 varchar(255), previousValue15 varchar(255), previousValue16 varchar(255), 
previousValue17 varchar(255), previousValue18 varchar(255), previousValue19 varchar(255), previousValue20 varchar(255), currentValue1 varchar(255), 
currentValue2 varchar(255), currentValue3 varchar(255), currentValue4 varchar(255), currentValue5 varchar(255), currentValue6 varchar(255), 
currentValue7 varchar(255), currentValue8 varchar(255), currentValue9 varchar(255), currentValue10 varchar(255), currentValue11 varchar(255), 
currentValue12 varchar(255), currentValue13 varchar(255), currentValue14 varchar(255), currentValue15 varchar(255), currentValue16 varchar(255), 
currentValue17 varchar(255), currentValue18 varchar(255), currentValue19 varchar(255), currentValue20 varchar(255), columnName1 varchar(255), 
columnName2 varchar(255), columnName3 varchar(255), columnName4 varchar(255), columnName5 varchar(255), columnName6 varchar(255), columnName7 varchar(255), 
columnName8 varchar(255), columnName9 varchar(255), columnName10 varchar(255), columnName11 varchar(255), columnName12 varchar(255), columnName13 varchar(255), 
columnName14 varchar(255), columnName15 varchar(255), columnName16 varchar(255), columnName17 varchar(255), columnName18 varchar(255), columnName19 varchar(255), 
columnName20 varchar(255), tableId bigint, tableName varchar(255), user_id bigint, primary key (id)) type=InnoDB;
alter table audit add index FK58D9BDB80DB89E (user_id), add constraint FK58D9BDB80DB89E foreign key (user_id) references user (id);



-- userrole
alter table userroles rename to userrole;
alter table userrole change column userRoles role varchar(64);
alter table userrole modify column id bigint auto_increment;
alter table userrole add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime, add column user_id bigint;
update userrole as ur set ur.user_id = (select u.id from user as u where u.username = ur.username);


-- change up passwords
update user set pin=1001 where username = 'ahenry';
update user set pin=1002 where username = 'arjuna';     
update user set pin=1003 where username = 'becki';      
update user set pin=1004 where username = 'bell';       
update user set pin=1006 where username = 'bobh';       
update user set pin=1008 where username = 'donna';      
update user set pin=1009 where username = 'jerry';      
update user set pin=1011 where username = 'Joe';        
update user set pin=1012 where username = 'john';       
update user set pin=1013 where username = 'kelley';     
update user set pin=1014 where username = 'manager';    
update user set pin=1015 where username = 'mike';       
update user set pin=1016 where username = 'randy';      
update user set pin=1017 where username = 'retail';     
update user set pin=1018 where username = 'richard';    
update user set pin=1019 where username = 'salessystem';
update user set pin=1020 where username = 'susan';      
update user set pin=1021 where username = 'teri';       
update user set pin=1022 where username = 'tim';        
update user set pin=1023 where username = 'tina';       
update user set pin=1024 where username = 'tmegela';   
update user set pin=1026 where username = 'tracy';      
update user set pin=1027 where username = 'Walt';       
update user set pin=1028 where username = 'warehouse';  
update user set pin=1029 where username = 'andrewb';  
update user set pin=1030 where username = 'Darryl';  
update user set pin=1031 where username = 'Diane';  
update user set pin=1032 where username = 'Krystal';

update user set password = md5(password);

insert into userrole (user_id, role, versionbc) values (12, 'BcManifestAdmin', 1);
insert into userrole (user_id, role, versionbc) values (12, 'BcManifestViewer', 1);
insert into userrole (user_id, role, versionbc) values (11, 'BcManifestAdmin', 1);
insert into userrole (user_id, role, versionbc) values (11, 'BcManifestViewer', 1);
insert into userrole (user_id, role, versionbc) values (7, 'BcManifestAdmin', 1);
insert into userrole (user_id, role, versionbc) values (7, 'BcManifestViewer', 1);
insert into userrole (user_id, role, versionbc) values (14, 'BcManifestAdmin', 1);
insert into userrole (user_id, role, versionbc) values (14, 'BcManifestViewer', 1);
insert into userrole (user_id, role, versionbc) values (23, 'BcManifestAdmin', 1);
insert into userrole (user_id, role, versionbc) values (23, 'BcManifestViewer', 1);


# kelly
delete from userrole where user_id = 15;
insert into userrole (user_id, role, versionbc) values (15, 'WebUser', 1);
insert into userrole (user_id, role, versionbc) values (15, 'SystemAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcInvAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcInvViewer', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcManifestAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcManifestViewer', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcRecAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcRecViewer', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcOrderAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcOrderViewer', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcCustomerAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcCustomerViewer', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcVendorAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcVendorViewer', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcUserAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcSalesRepAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BcUserViewer', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BellInvAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BellInvViewer', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BellRecAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BellRecViewer', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BellOrderAdmin', 1);
insert into userrole (user_id, role, versionbc) values (15, 'BellOrderViewer', 1);

# teri
delete from userrole where user_id = 25;
insert into userrole (user_id, role, versionbc) values (25, 'WebUser', 1);
insert into userrole (user_id, role, versionbc) values (25, 'SystemAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcInvAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcInvViewer', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcManifestAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcManifestViewer', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcRecAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcRecViewer', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcOrderAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcOrderViewer', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcCustomerAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcCustomerViewer', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcVendorAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcVendorViewer', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcUserAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcSalesRepAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BcUserViewer', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BellInvAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BellInvViewer', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BellRecAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BellRecViewer', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BellOrderAdmin', 1);
insert into userrole (user_id, role, versionbc) values (25, 'BellOrderViewer', 1);


# manager
delete from userrole where user_id = 17;
insert into userrole (user_id, role, versionbc) values (17, 'WebUser', 1);
insert into userrole (user_id, role, versionbc) values (17, 'SystemAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcInvAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcInvViewer', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcManifestAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcManifestViewer', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcRecAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcRecViewer', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcOrderAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcOrderViewer', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcCustomerAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcCustomerViewer', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcVendorAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcVendorViewer', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcUserAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcSalesRepAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BcUserViewer', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BellInvAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BellInvViewer', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BellRecAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BellRecViewer', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BellOrderAdmin', 1);
insert into userrole (user_id, role, versionbc) values (17, 'BellOrderViewer', 1);

# bob
delete from userrole where user_id = 6;
insert into userrole (user_id, role, versionbc) values (6, 'WebUser', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BcInvViewer', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BcManifestViewer', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BcRecViewer', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BcOrderViewer', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BcCustomerViewer', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BcVendorViewer', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BcUserViewer', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BellInvAdmin', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BellInvViewer', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BellRecAdmin', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BellRecViewer', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BellOrderAdmin', 1);
insert into userrole (user_id, role, versionbc) values (6, 'BellOrderViewer', 1);

# mick
delete from userrole where user_id = 18;
insert into userrole (user_id, role, versionbc) values (18, 'WebUser', 1);
insert into userrole (user_id, role, versionbc) values (18, 'SystemAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcInvAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcInvViewer', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcManifestAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcManifestViewer', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcRecAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcRecViewer', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcOrderAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcOrderViewer', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcCustomerAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcCustomerViewer', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcVendorAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcVendorViewer', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcUserAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcSalesRepAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BcUserViewer', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BellInvAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BellInvViewer', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BellRecAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BellRecViewer', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BellOrderAdmin', 1);
insert into userrole (user_id, role, versionbc) values (18, 'BellOrderViewer', 1);


-- vendor and vendor_skid_type
alter table vendor_skid_type drop foreign key vendor_skid_type_ibfk_1, drop foreign key vendor_skid_type_ibfk_2;
alter table vendor_skid_type modify column id bigint auto_increment, modify column vendor_id bigint;
alter table vendor_skid_type add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;

alter table break_received drop foreign key break_received_ibfk_1, drop foreign key break_received_ibfk_2, drop foreign key break_received_ibfk_3;
alter table received drop foreign key received_ibfk_1, drop foreign key received_ibfk_2;

alter table vendor modify column id bigint auto_increment;

alter table vendor add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update vendor set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table vendor drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;


alter table break_received_item drop foreign key break_received_item_ibfk_1, drop foreign key break_received_item_ibfk_2, drop foreign key break_received_item_ibfk_3;
alter table break_received_item modify column id bigint auto_increment, modify column break_received_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update break_received_item set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table break_received_item drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;

alter table break_received_item add column boolskid boolean default false;
update break_received_item set boolskid = true where skid = 1;
alter table break_received_item drop column skid, change boolskid skid boolean default false;

alter table break_received_item add column boolbreakroom boolean default false;
update break_received_item set boolbreakroom = true where breakRoom = 1;
alter table break_received_item drop column breakRoom, change boolbreakroom breakRoom boolean default false;

alter table break_received modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime, modify column vendor_id bigint;
update break_received set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table break_received drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;

alter table break_received add column boolskid boolean default false;
update break_received set boolskid = true where skid = 1;
alter table break_received drop column skid, change boolskid skid boolean default false;

-- received and received item
alter table received_item drop foreign key received_item_ibfk_1, drop foreign key received_item_ibfk_2;

alter table received_item modify column id bigint auto_increment, modify column received_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update received_item set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table received_item drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date, modify column isbn13 char(13);

alter table skid drop foreign key skid_ibfk_1, drop foreign key skid_ibfk_2;;

alter table received modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime, modify column vendor_id bigint, modify column parent_id bigint;
update received set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table received drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;


-- manifest and manifest_item
alter table manifest_item drop foreign key manifest_item_ibfk_1;

alter table manifest_item modify column id bigint auto_increment, modify column manifest_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime, add column bin varchar(64);
update manifest_item set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table manifest_item drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date, modify column isbn13 char(13);
update manifest_item set cond="hurt" where cond is null;

alter table manifest modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update manifest set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table manifest drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;

alter table manifest add column totalitems int, add column totalquantity int;
update manifest set totalitems = 0, totalquantity = 0;
update manifest as m set m.totalitems = (select count(mi.id) from manifest_item as mi where mi.manifest_id = m.id);
update manifest as m set m.totalquantity = (select sum(mi.quantity) from manifest_item as mi where mi.manifest_id = m.id);
update manifest set totalitems = 0 where totalitems is null;
update manifest set totalquantity = 0 where totalquantity is null;

-- publisher and publisher_imprint
alter table publisher_imprint drop foreign key publisher_imprint_ibfk_1;

alter table publisher_imprint modify column id bigint auto_increment, modify column publisher_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;

alter table publisher modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;



-- customer, customer_order, customer_shipping, customer_order_item
alter table customer_order drop foreign key customer_order_ibfk_1, drop foreign key customer_order_ibfk_2, drop foreign key customer_order_ibfk_3, drop foreign key customer_order_ibfk_4;
alter table customer_shipping drop foreign key customer_shipping_ibfk_1, drop foreign key customer_shipping_ibfk_2;
alter table customer_shipping modify column id bigint auto_increment, modify column customer_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update customer_shipping set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table customer_shipping drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;
alter table customer_shipping add column work_ext varchar(15);


alter table customer_order change posted_by_date posted_by_date datetime default null;

alter table customer_order change terms terms varchar(50);

alter table customer_order_item drop foreign key customer_order_item_ibfk_1, drop foreign key customer_order_item_ibfk_2, drop foreign key customer_order_item_ibfk_3;
alter table customer_order_item modify column id bigint auto_increment, modify column customer_order_id bigint, modify column inventory_item_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime; 
update customer_order_item set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table customer_order_item drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date,  modify column isbn13 char(13);

alter table customer_order modify column id bigint auto_increment, modify column customer_id bigint, modify column customer_shipping_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update customer_order set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table customer_order drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;

alter table customer_order add column tax numeric(19,2), add column balanceDue  numeric(19,2), add column totalPricePreTax  numeric(19,2), add column totalTax  numeric(19,2); 

alter table customer_order add column boolposted boolean default false;
update customer_order set boolposted = true where posted = 1;
alter table customer_order drop column posted, change boolposted posted boolean default false;

alter table customer_order add column boolshipped boolean default false;
update customer_order set boolshipped = true where shipped = 1;
alter table customer_order drop column shipped, change boolshipped shipped boolean default false;
update customer_order set shipped = true where ship_date is not null;

alter table received_item add column inventory_item_id bigint(20) DEFAULT NULL;
update received_item as ri set ri.inventory_item_id = (select ii.id from inventory_item as ii where ii.isbn = ri.isbn and ii.cond = ri.cond);

alter table bell_received_item add column inventory_id bigint(20) DEFAULT NULL;
update bell_received_item as ri set ri.inventory_id = (select ii.id from bell_inventory as ii where ii.isbn = ri.isbn);

alter table received add column boolposted boolean default false, add column boolskid boolean default false, add column totalExtendedCost decimal(19,2);
alter table received add column boolskidBreak boolean default false;
update received set boolposted = true where posted = 1;
update received set boolskid = true where skid = 1;
update received set boolskidBreak = true where skidBreak = 1;
alter table received drop column posted, change boolposted posted boolean default false;
alter table received drop column skid, change boolskid skid boolean default false;
alter table received drop column skidBreak, change boolskidBreak skidBreak boolean default false;


alter table received_item add column boolbreakroom boolean default false, add column boolskid boolean default false, add column extendedCost decimal(19,2);
update received_item set boolbreakroom = true where breakroom = 1;
update received_item set boolskid = true where skid = "true";
alter table received_item drop column skid, change boolskid skid boolean default false;
alter table received_item drop column breakroom, change boolbreakroom breakroom boolean default false;

alter table inventory_item add column boolbellbook boolean default false, add column boolskid boolean default false, add column boolrestricted boolean default false, add column boolpendingReceiving boolean default false, add column boolhe boolean default false, add column sellPricePercentList float;
update inventory_item set boolbellbook = true where bellbook = 1;
update inventory_item set boolskid = true where skid = 1;
update inventory_item set boolrestricted = true where restricted = 1;
update inventory_item set boolpendingReceiving = true where pendingReceiving = 1;
update inventory_item set boolhe = true where he = 1;
update inventory_item set sellPricePercentList = (selling_price / list_price)*100 where selling_price is not null and list_price is not null and list_price > 0 and selling_price > 0;
alter table inventory_item drop column bellbook, change boolbellbook bellbook boolean default false;
alter table inventory_item drop column skid, change boolskid skid boolean default false;
alter table inventory_item drop column restricted, change boolrestricted restricted boolean default false;
alter table inventory_item drop column pendingReceiving, change boolpendingReceiving pendingReceiving boolean default false;
alter table inventory_item drop column he, change boolhe he boolean default false;


alter table bell_sku add column boolbellbook boolean default false, add column boolskid boolean default false;
update bell_sku set boolbellbook = true where bell_book = 1;
update bell_sku set boolskid = true where skid = 1;
alter table bell_sku drop column bell_book, change boolbellbook bell_book boolean default false;
alter table bell_sku drop column skid, change boolskid skid boolean default false;


alter table customer add column boolbookclub boolean, add column boolbookfair boolean, add column boolmaillist boolean, add column booltax boolean, add column boolbackorder boolean, add column boolhold boolean;
update customer set boolbookclub = false;
update customer set boolbookclub = true where bookclub = 'Y';
update customer set boolbookfair = false;
update customer set boolbookfair = true where bookfair = 'Y';
update customer set boolmaillist = false;
update customer set boolmaillist = true where maillist = 'Y';
update customer set booltax = false;
update customer set booltax = true where tax = 'Y';
update customer set boolbackorder = false;
update customer set boolbackorder = true where backorder = 1;
update customer set boolhold = false;
update customer set boolhold = true where hold = 1;
alter table customer drop column bookclub, drop column bookfair, drop column maillist, drop column tax, drop column backorder, drop column hold;
alter table customer add column bookclub boolean default false, add column bookfair boolean default false, add column maillist boolean default false, add column tax boolean default false, add column backorder boolean default false, add column hold boolean default false;
update customer set bookclub = boolbookclub, bookfair = boolbookfair, maillist = boolmaillist, tax = booltax, backorder = boolbackorder, hold = boolhold;
alter table customer drop column boolbookclub, drop column boolbookfair, drop column boolmaillist, drop column booltax, drop column boolbackorder, drop column boolhold;


alter table customer modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update customer set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table customer drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;

alter table bell_customer add column boolbookclub boolean, add column boolbookfair boolean, add column boolmaillist boolean, add column booltax boolean, add column boolbackorder boolean, add column boolhold boolean;
update bell_customer set boolbookclub = false;
update bell_customer set boolbookclub = true where bookclub = 'Y';
update bell_customer set boolbookfair = false;
update bell_customer set boolbookfair = true where bookfair = 'Y';
update bell_customer set boolmaillist = false;
update bell_customer set boolmaillist = true where maillist = 'Y';
update bell_customer set booltax = false;
update bell_customer set booltax = true where tax = 'Y';
update bell_customer set boolbackorder = false;
update bell_customer set boolbackorder = true where backorder = 1;
update bell_customer set boolhold = false;
update bell_customer set boolhold = true where hold = 1;
alter table bell_customer drop column bookclub, drop column bookfair, drop column maillist, drop column tax, drop column backorder, drop column hold;
alter table bell_customer add column bookclub boolean default false, add column bookfair boolean default false, add column maillist boolean default false, add column tax boolean default false, add column backorder boolean default false, add column hold boolean default false;
update bell_customer set bookclub = boolbookclub, bookfair = boolbookfair, maillist = boolmaillist, tax = booltax, backorder = boolbackorder, hold = boolhold;
alter table bell_customer drop column boolbookclub, drop column boolbookfair, drop column boolmaillist, drop column booltax, drop column boolbackorder, drop column boolhold;


alter table amz_category drop foreign key amz_category_ibfk_1;
alter table amz_subject drop foreign key amz_subject_ibfk_1;
alter table cart_item drop foreign key cart_item_ibfk_2;
alter table cart_item drop foreign key cart_item_ibfk_4;
alter table cart_item drop foreign key cart_item_ibfk_6;
alter table inventory_item drop index cat1_index, drop index cat2_index, drop index cat3_index, drop index cat4_index;
alter table inventory_item drop foreign key inventory_item_ibfk_1, drop foreign key inventory_item_ibfk_2;
alter table inventory_item modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime, modify column skid_id bigint;
update inventory_item set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table inventory_item drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;
alter table inventory_item modify column isbn10 char(10), modify column isbn13 char(13);


alter table invoice_number modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;




alter table bell_cost modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;

alter table bell_order_item drop foreign key bell_order_item_ibfk_1, drop foreign key bell_order_item_ibfk_2;
alter table bell_order_item modify column id bigint auto_increment, modify column inventory_id bigint, modify column bell_order_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update bell_order_item set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table bell_order_item drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;


alter table bell_order drop foreign key bell_order_ibfk_1, drop foreign key bell_order_ibfk_2;
alter table bell_order modify column id bigint auto_increment, modify column customer_id bigint, modify column shipping_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;

alter table bell_order add column boolcustomerVisit boolean, add column boolcreditMemo boolean, add column boolposted boolean;
update bell_order set boolcustomerVisit = false, boolcreditMemo = false, boolposted = false;
update bell_order set boolcustomerVisit = true where customerVisit = 1;
update bell_order set boolcreditMemo = true where creditMemo = 1;
update bell_order set boolposted = true where posted = 1;
alter table bell_order drop column customerVisit, drop column creditMemo, drop column posted;
alter table bell_order change boolcustomerVisit customerVisit boolean default false;
alter table bell_order change boolcreditMemo creditMemo boolean default false;
alter table bell_order change boolposted posted boolean default false;
update bell_order set posted = false where posted is null;

alter table bell_order_item add column boolcredit boolean default false;
update bell_order_item set boolcredit = false;
update bell_order_item set boolcredit = true where credit = 1;
alter table bell_order_item drop column credit;
alter table bell_order_item change boolcredit credit boolean default false;

alter table bell_customer modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update bell_customer set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table bell_customer drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;


alter table bell_customer_shipping modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update bell_customer_shipping set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table bell_customer_shipping drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;


alter table bell_received_item drop foreign key bell_received_item_ibfk_1;
alter table bell_received_item modify column id bigint auto_increment, modify column received_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update bell_received_item set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table bell_received_item drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;

alter table bell_received_item add column booldeleted boolean default false, add column extendedCost decimal(19,2), add column boolbreakroom boolean default false;
update bell_received_item set booldeleted = false, boolbreakroom = false;
update bell_received_item set booldeleted = true where deleted = 1;
update bell_received_item set boolbreakroom = true where breakroom = 1;
alter table bell_received_item drop column deleted, drop column breakroom;
alter table bell_received_item change booldeleted deleted boolean default false;
alter table bell_received_item change boolbreakroom breakroom boolean default false;

alter table bell_received_item add column boolskid boolean default false;
update bell_received_item set boolskid = true where skid = "true";
alter table bell_received_item drop column skid, change boolskid skid boolean default false;

alter table bell_received modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update bell_received set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table bell_received drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;

alter table bell_received add column boolposted boolean default false;
update bell_received set boolposted = false;
update bell_received set boolposted = true where posted = 1;
alter table bell_received drop column posted;
alter table bell_received change boolposted posted boolean default false;

alter table bell_received add column boolskid boolean default false;
update bell_received set boolskid = false;
update bell_received set boolskid = true where skid = 1;
alter table bell_received drop column skid;
alter table bell_received change boolskid skid boolean default false;

alter table bell_vendor modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;
update bell_vendor set lastUpdate = last_update, lastUpdateBy = last_update_by, createTimeBc = entered_date;
alter table bell_vendor drop column last_update, drop column last_update_by, drop column entered_by, drop column entered_date;

alter table bell_sku drop foreign key bell_sku_ibfk_1;
alter table bell_sku modify column id bigint auto_increment, modify column inventory_id bigint, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;

alter table bell_inventory modify column id bigint auto_increment, add column versionbc bigint, modify column lastUpdate datetime, modify column lastUpdateBy varchar(64), add column createTimeBc datetime;


alter table bell_inventory add column boolskid boolean default false, add column boolbell_book boolean default false, add column boolnoamazon boolean default false;
update bell_inventory set boolskid = true where skid = 1;
update bell_inventory set boolbell_book = true where bell_book = 1;
update bell_inventory set boolnoamazon = true where noamazon = 1;
alter table bell_inventory drop column skid, change boolskid skid boolean default false;
alter table bell_inventory drop column bell_book, change boolbell_book bell_book boolean default false;
alter table bell_inventory drop column noamazon, change boolnoamazon noamazon boolean default false;



alter table bell_invoice_number modify column id bigint auto_increment, add column versionbc bigint, add column lastUpdate datetime, add column lastUpdateBy varchar(64), add column createTimeBc datetime;

alter table bell_sku add column lowest boolean;
update bell_sku set lowest = false;
update bell_inventory set lowNew = lowNew / 100, lowUsed = lowUsed / 100, lowCollectible = lowCollectible / 100, lowRefurb = lowRefurb / 100;
update bell_sku as bs, bell_inventory as bi set bs.lowest = true where bs.inventory_id = bi.id and bs.sell_price <= bi.lowUsed;

-- big table changes
-- create table skid select * from inventory_item where skid = 1;
-- alter table customer_order_item add column skid_id bigint;
-- update customer_order_item as coi, skid as s set coi.skid_id = s.id, coi.inventory_item_id = null where coi.isbn = s.isbn;
-- TODO break up customer order and customer order item into old table - pull out old data 
-- delete from inventory_item where skid = 1;

update received_item set skid = false;
update received_item set skid = true where type = "Skid" or type = "Lbs";
update received_item set type = "Pieces" where type is null;

update customer_order_item set cost = 0 where cost is null;
update customer_order_item set filled = 0 where filled is null;
update customer_order_item set shipped_quantity = 0 where shipped_quantity is null;
update customer_order_item set quantity = 0 where quantity is null;
update customer_order_item set price = 0 where price is null;
update customer_order_item set extended = 0 where extended is null;
update customer_order_item set discount = 0 where discount is null;



-- SCHEMA update
alter table bell_customer_shipping add column bellCustomer tinyblob;
alter table customer_order add column totalPrice numeric(19,2),add column totalQuantity integer,add column totalNonShippedQuantity integer,add column totalCost numeric(19,2),add column totalExtended numeric(19,2),add column totalItems integer,add column totalPriceNonShipped numeric(19,2), add column creditMemoType varchar(25);
alter table customer_order_item add column totalPrice numeric(19,2),add column totalExtended numeric(19,2),add column totalPriceNonShipped numeric(19,2),add column latestCost float,add column totalExtendedNonShipped numeric(19,2);
alter table inventory_item add column numberOfPages integer,add column lastAmazonUpdate datetime,add column smallImage varchar(255),add column mediumImage varchar(255),add column length float,add column height float,add column width float,add column weight float;
alter table received add column totalQuantity integer,add column totalCost numeric(19,2),add column totalItems integer,add column totalOrderedQuantity integer,add column totalSellPrice numeric(19,2);
alter table bell_order add index FKCFAAF1123E405ADF (shipping_id), add constraint FKCFAAF1123E405ADF foreign key (shipping_id) references bell_customer_shipping (id);
alter table bell_order add index FKCFAAF1122CB59DE1 (customer_id), add constraint FKCFAAF1122CB59DE1 foreign key (customer_id) references bell_customer (id);
alter table bell_order_item add index FKCEA71E208C45F72F (bell_order_id), add constraint FKCEA71E208C45F72F foreign key (bell_order_id) references bell_order (id);
alter table bell_order_item add index FKCEA71E20F5C92033 (inventory_id), add constraint FKCEA71E20F5C92033 foreign key (inventory_id) references bell_inventory (id);
alter table bell_received_item add index FK6BE1B475C4A6BB01 (received_id), add constraint FK6BE1B475C4A6BB01 foreign key (received_id) references bell_received (id);
alter table bell_sku add index FK6172C7E1F5C92033 (inventory_id), add constraint FK6172C7E1F5C92033 foreign key (inventory_id) references bell_inventory (id);
alter table break_received add index FK10E17121E7834A7E (vendor_id), add constraint FK10E17121E7834A7E foreign key (vendor_id) references vendor (id);
alter table break_received_item add index FK65194F71669916B5 (break_received_id), add constraint FK65194F71669916B5 foreign key (break_received_id) references break_received (id);
alter table customer_order add index FK86DB8BAD7636ADB (customer_shipping_id), add constraint FK86DB8BAD7636ADB foreign key (customer_shipping_id) references customer_shipping (id);
alter table customer_order add index FK86DB8BAD48C8D9BE (customer_id), add constraint FK86DB8BAD48C8D9BE foreign key (customer_id) references customer (id);
alter table customer_order_item add index FKE845EC652C357AB9 (customer_order_id), add constraint FKE845EC652C357AB9 foreign key (customer_order_id) references customer_order (id);
alter table customer_order_item add index FKE845EC65C4D8D62F (inventory_item_id), add constraint FKE845EC65C4D8D62F foreign key (inventory_item_id) references inventory_item (id);
alter table customer_shipping add index FK1E6D88EF48C8D9BE (customer_id), add constraint FK1E6D88EF48C8D9BE foreign key (customer_id) references customer (id);
alter table manifest_item add index FK42638B23A2A6EC1E (manifest_id), add constraint FK42638B23A2A6EC1E foreign key (manifest_id) references manifest (id);
alter table publisher_imprint add index FK3DCFC8C622191BF6 (publisher_id), add constraint FK3DCFC8C622191BF6 foreign key (publisher_id) references publisher (id);
alter table received add index FKCFCBE9E1E7834A7E (vendor_id), add constraint FKCFCBE9E1E7834A7E foreign key (vendor_id) references vendor (id);
alter table received_item add index FK759C4EB1E0B9F6DE (received_id), add constraint FK759C4EB1E0B9F6DE foreign key (received_id) references received (id);
alter table received_item add index FK759C4EB1C4D8D62F (inventory_item_id), add constraint FK759C4EB1C4D8D62F foreign key (inventory_item_id) references inventory_item (id);
alter table userrole add index FKF02B8EC180DB89E (user_id), add constraint FKF02B8EC180DB89E foreign key (user_id) references user (id);
alter table vendor_skid_type add index FK58C7784FE7834A7E (vendor_id), add constraint FK58C7784FE7834A7E foreign key (vendor_id) references vendor (id);
alter table bell_received add column totalQuantity integer,add column totalCost numeric(19,2),add column totalItems integer,add column totalOrderedQuantity integer,add column totalSellPrice numeric(19,2), add column totalExtendedCost decimal(19,2);
alter table bell_order add column totalQuantity integer,add column totalNonShippedQuantity integer,add column totalCost numeric(19,2),add column totalExtended numeric(19,2),add column totalItems integer,add column totalPriceNonShipped numeric(19,2),add column totalTax numeric(19,2),add column balanceDue numeric(19,2),add column totalPricePreTax numeric(19,2);
alter table bell_order_item add column totalPrice numeric(19,2), add column totalExtended numeric(19,2), add column totalPriceNonShipped numeric(19,2), add column totalExtendedNonShipped numeric(19,2);


-- LAST THING add back in any foreign key constraints
-- alter table vendor_skid_type add foreign key(vendor_id) references vendor(id) on delete no action on update no action;
-- alter table break_received add foreign key(vendor_id) references vendor(id) on delete no action on update no action;
-- alter table break_received_item add foreign key(break_received_id) references break_received(id) on delete no action on update no action;
-- alter table received add foreign key(vendor_id) references vendor(id) on delete no action on update no action;
-- alter table received_item add foreign key(received_id) references received(id) on delete no action on update no action;
-- alter table manifest_item add foreign key(manifest_id) references manifest(id) on delete no action on update no action;
-- alter table customer_order add foreign key(customer_id) references customer(id) on delete no action on update no action;
-- alter table customer_order add foreign key(customer_shipping_id) references customer_shipping(id) on delete no action on update no action;
-- alter table customer_order_item add foreign key(customer_order_id) references customer_order(id) on delete no action on update no action;
-- alter table customer_order_item add foreign key(inventory_item_id) references inventory_item(id) on delete no action on update no action;
-- alter table customer_shipping add foreign key(customer_id) references customer(id) on delete no action on update no action;
-- alter table bell_order add foreign key(customer_id) references bell_customer(id) on delete no action on update no action;
-- alter table bell_order add foreign key(shipping_id) references bell_customer_shipping(id) on delete no action on update no action;
-- alter table bell_order_item add foreign key(inventory_id) references bell_inventory(id) on delete no action on update no action;
-- alter table bell_order_item add foreign key(bell_order_id) references bell_order(id) on delete no action on update no action;
-- alter table bell_received_item add foreign key(received_id) references bell_received(id) on delete no action on update no action;
-- alter table bell_sku add foreign key (inventory_id) references bell_inventory(id) on delete no action on update no action;

update userrole set role = 'BcInvAdmin' where role = 'InventoryManager';
update userrole set role = 'BcInvViewer' where role = 'InventoryViewer';
update userrole set role = 'BcRecAdmin' where role = 'ReceivingManager';
update userrole set role = 'BcRecViewer' where role = 'ReceivingViewer';
update userrole set role = 'BcOrderAdmin' where role = 'OrderManager';
update userrole set role = 'BcOrderViewer' where role = 'OrderViewer';
update userrole set role = 'BcCustomerAdmin' where role = 'CustomerManager';
update userrole set role = 'BcCustomerViewer' where role = 'CustomerViewer';
update userrole set role = 'BcVendorAdmin' where role = 'VendorManager';
update userrole set role = 'BcVendorViewer' where role = 'VendorViewer';
update userrole set role = 'BcUserAdmin' where role = 'UserManager';
update userrole set role = 'BcUserViewer' where role = 'UserViewer';
update userrole set role = 'BellInvAdmin' where role = 'BellwetherManager';
update userrole set role = 'BellInvViewer' where role = 'BellwetherViewer';

-- update userrole set role = 'BellRecAdmin' where role = '';
-- update userrole set role = 'BellRecViewer' where role = '';
-- update userrole set role = 'BellOrderAdmin' where role = '';
-- update userrole set role = 'BellOrderViewer' where role = '';

update inventory_item set versionbc = 1;
update customer_order set versionbc = 1;
update customer_order_item set versionbc = 1;
update received set versionbc = 1;
update received_item set versionbc = 1;

update bell_customer set versionbc = 1;
update bell_cost set versionbc = 1;
update bell_customer_shipping set versionbc = 1;
update bell_inventory set versionbc = 1;
update bell_invoice_number set versionbc = 1;
update bell_order set versionbc = 1;
update bell_order_item set versionbc = 1;
update bell_received set versionbc = 1;
update bell_received_item set versionbc = 1;
update bell_sku set versionbc = 1;
update bell_vendor set versionbc = 1;
update break_received set versionbc = 1;
update break_received_item set versionbc = 1;
update customer set versionbc = 1;
update customer_shipping set versionbc = 1;
update manifest set versionbc = 1;
update manifest_item set versionbc = 1;
update publisher set versionbc = 1;
update publisher_imprint set versionbc = 1;
-- update skid set versionbc = 1;
update user set versionbc = 1;
update userrole set versionbc = 1;
update vendor set versionbc = 1;
update vendor_skid_type set versionbc = 1;

update customer_order set tax = 1;
update customer_order as co set co.tax = 1.07 where (select c.tax from customer as c where c.id = co.customer_id) = true;
update customer_order set shipping_charges = 0 where shipping_charges is null;
update customer_order set deposit_ammount = 0 where deposit_ammount is null;
update customer_order set palleteCharge = 0 where palleteCharge is null;


update inventory_item set isbn10 = isbn where length(isbn) = 10 and isbn10 is null;


-- add back in bri_count
create table bri_count (id bigint not null auto_increment, versionbc bigint, lastUpdate datetime, 
lastUpdateBy varchar(255), createTimeBc datetime, pieces bit, countorlbs float, bri_id bigint, primary key (id)) type=InnoDB;
alter table bri_count add index FK5EAA7B69DCF7F7F0 (bri_id), add constraint FK5EAA7B69DCF7F7F0 foreign key (bri_id) references break_received_item (id);

update customer_order_item set creditDamage = true where credit = true and creditDamage is null and creditShortage is null and creditRecNoBill is null;
update customer_order_item set creditDamage = true where credit = true and creditDamage = false and creditShortage = false and creditRecNoBill = false;
update customer_order_item set price = -price where creditDamage = true or creditShortage = true;

-- add indexes
create index bin_index on customer_order_item (bin); 

SET FOREIGN_KEY_CHECKS = 1;

-- EXTRA CRAP - tools for figuring out some data

-- to get foreign keys for a table
-- show create table vendor_skid_type;

-- SELECT CONCAT( table_name, '.',
-- column_name, ' -> ',
-- referenced_table_name, '.',
-- referenced_column_name ) AS list_of_fks
-- FROM information_schema.KEY_COLUMN_USAGE
-- WHERE CONSTRAINT_SCHEMA = 'inventory' 
-- AND REFERENCED_TABLE_NAME is not null
-- ORDER BY TABLE_NAME, COLUMN_NAME;

-- constraint example
-- CONSTRAINT `publisher_imprint_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `publisher` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION


