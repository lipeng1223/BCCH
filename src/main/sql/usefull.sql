
# gets the customer_order with the most customer_order_items
select customer_order_id, count(*) from customer_order_item group by customer_order_id order by 2 desc limit 1;

# +-------------------+----------+
# | customer_order_id | count(*) |
# +-------------------+----------+
# |             16414 |     3898 |
# +-------------------+----------+
# 1 row in set (0.49 sec)



update inventory_item as ii set ii.onhand = (select sum(ri.available) from received_item as ri where ri.inventory_item_id = ii.id)  
where ii.id in (select rid.inventory_item_id from received_item as rid, received as r where rid.received_id = r.id and r.createTimeBc > '2012-07-10');


select count(ii.id) from inventory_item as ii where ii.id in (select rid.inventory_item_id from received_item as rid, received as r where rid.received_id = r.id and r.createTimeBc > '2012-07-10');

update inventory_item as ii set ii.available = ii.onhand - ii.commited;



# update customer_order_item as coi, customer_order as co, inventory_item as ii set ii.onhand = ii.onhand - sum(coi.quantity) 
# where coi.inventory_item_id = ii.id and co.id = coi.customer_order_id and co.posted = true and co.creditMemo = false;

# update inventory_item as ii set ii.commited = 0;
# update customer_order_item as coi, customer_order as co, inventory_item as ii set ii.commited = sum(coi.quantity) 
# where coi.inventory_item_id = ii.id and co.id = coi.customer_order_id and co.posted = false and co.creditMemo = false;

# update inventory_item as ii set ii.available = ii.onhand - ii.commited;

update received_item as ri, received as r set ri.date = r.po_date where ri.received_id = r.id;

# inventory on hand updates
update inventory_item as ii set ii.onhand = (select sum(ri.available) from received_item as ri where ri.inventory_item_id = ii.id)  
 where ii.id in (select rid.inventory_item_id from received_item as rid where rid.available > 0);
update inventory_item as ii set ii.onhand = 0
 where (select sum(ri.available) from received_item as ri where ri.inventory_item_id = ii.id) = 0;
update inventory_item as ii set ii.onhand = 0
 where (select sum(ri.available) from received_item as ri where ri.inventory_item_id = ii.id) is null;
update inventory_item as ii set ii.onhand = 0 where ii.onhand is null;
update inventory_item as ii set ii.commited = (select sum(coi.filled) from customer_order_item as coi, customer_order as co where coi.inventory_item_id = ii.id and coi.customer_order_id = co.id and co.posted = false and coi.credit = false);
update inventory_item as ii set ii.commited = 0 where ii.commited is null;
update inventory_item as ii set ii.available = ii.onhand - ii.commited;



mysql> update inventory_item as ii set ii.received_quantity = (select ri.quantity from received_item as ri, received as re where ri.inventory_item_id = ii.id and ri.received_id = re.id order by re.date desc limit 1) where ii.id in (select r.inventory_item_id from received_item as r where r.received_id = 21555);
Query OK, 599 rows affected (2.87 sec)
Rows matched: 637  Changed: 599  Warnings: 0

mysql> update inventory_item as ii set ii.lastpo_date = (select re.date from received_item as ri, received as re where ri.inventory_item_id = ii.id and ri.received_id = re.id order by re.date desc limit 1) where ii.id in (select r.inventory_item_id from received_item as r where r.received_id = 21555);
Query OK, 637 rows affected (2.91 sec)
Rows matched: 637  Changed: 637  Warnings: 0




# what orders don't have a total extended cost set
select count(co.id) from customer_order as co where 
(co.totalExtended is null or co.totalExtended = 0) and 
(select sum(coi.extended) from customer_order_item as coi where coi.customer_order_id = co.id) > 0;

select count(co.id) from customer_order as co where 
(co.totalExtended is null or co.totalExtended = 0) and 
(select coalesce(sum( coi.cost ), 0) from customer_order_item as coi where coi.customer_order_id = co.id) > 0;

# update total extended on all orders that have cost, but no extended for some reason
update customer_order as co set co.totalExtended = 
    (select coalesce(sum( coi.cost ), 0) from customer_order_item as coi where coi.customer_order_id = co.id)
where 
    (co.totalExtended is null or co.totalExtended = 0) and 
    (select coalesce(sum( coi.cost ), 0) from customer_order_item as coi where coi.customer_order_id = co.id) > 0;


# how many inventory items do not have a cost
select count(*) from inventory_item where received_price is null or received_price = 0;

# update inventory item cost if it is null or 0
update inventory_item as ii set ii.received_price = 
(select ri.cost from received_item as ri where ri.inventory_item_id = ii.id order by ri.createTimeBc desc limit 1) 
where (ii.received_price is null or ii.received_price = 0);


# get inventory items that have mismatched on hand with respect to received available
select ii.id from inventory_item as ii where ii.onhand > (select sum(ri.available) from received_item as ri where ri.inventory_item_id = ii.id) 
and ii.id in (select rid.inventory_item_id from received_item as rid where rid.available > 0) limit 100;

