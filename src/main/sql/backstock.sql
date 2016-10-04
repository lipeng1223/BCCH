create table backstock_item (id bigint not null auto_increment, versionbc bigint, lastUpdate datetime, lastUpdateBy varchar(255), createTimeBc datetime, title varchar(255), isbn varchar(255), isbn13 varchar(255), totalQuantity integer, totalLocations integer, primary key (id)) engine=InnoDB;
create table backstock_location (id bigint not null auto_increment, versionbc bigint, lastUpdate datetime, lastUpdateBy varchar(255), createTimeBc datetime, location varchar(255), row varchar(255), quantity integer, tub varchar(255), backStockItem_id bigint, primary key (id)) engine=InnoDB;
alter table backstock_location add index FKA033576558373836 (backStockItem_id), add constraint FKA033576558373836 foreign key (backStockItem_id) references backstock_item (id);
alter table inventory_item add column backStock boolean default false;
update inventory_item set backStock = false;

