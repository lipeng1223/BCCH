#
#  In order to run this from the command line use the following syntax:
#  mysql --delimiter=// -u root -p inventory < storedProcedures.sql
#

# 
#  recalculates all of the CustomerOrder and CustomerOrderItems statistics
#
#  a call to this procedure will look like this:
#  >select updateAllCustomerOrder(1);
#
DROP FUNCTION IF EXISTS updateAllCustomerOrder//
CREATE FUNCTION updateAllCustomerOrder (customerOrderId bigint(20)) returns INT DETERMINISTIC
BEGIN

declare itemId bigint(20);
declare done boolean default 0;

# cursor for looping customerOrderItems
declare itemCursor cursor for select coi.id from customer_order_item as coi where coi.customer_order_id = customerOrderId;

-- Declare continue handler, stops the repeat loop
DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

# timing
#select now();
    
# open the cursor
open itemCursor;

REPEAT
    FETCH itemCursor into itemId;
    
    update customer_order_item as coi set 
    coi.totalPrice = (coi.price*coi.filled) - ( (coi.price*coi.filled) * (coi.discount/100.0) ), 
    coi.totalExtended = (coi.cost*coi.filled), 
    coi.totalPriceNonShipped = (coi.price*coi.quantity) - ( (coi.price*coi.quantity) * (coi.discount/100.0) ), 
    coi.totalExtendedNonShipped = (coi.cost*coi.quantity) - ( (coi.cost*coi.quantity) * (coi.discount/100.0) ) 
    where coi.id = itemId;

UNTIL done END REPEAT;

# close the cursor
close itemCursor;

# update the customer order

update customer_order as co set 
co.totalPricePreTax = (select coalesce(sum( (coi.price*coi.filled) - ( (coi.price*coi.filled) * (coi.discount/100.0) ) ), 0) from customer_order_item coi where coi.customer_order_id = co.id),
co.totalTax = (co.totalPricePreTax * co.tax) - co.totalPricePreTax,
co.totalPrice = (co.totalPricePreTax * co.tax) + co.shipping_charges + co.palleteCharge,
co.balanceDue = co.totalPrice - co.deposit_ammount,
co.totalPriceNonShipped = (select coalesce(sum( (coi.price*coi.quantity) - ( (coi.price*coi.quantity) * (coi.discount/100.0) ) ), 0) from customer_order_item coi where coi.customer_order_id = co.id), 
co.totalItems = (select count( coi.id ) from customer_order_item coi where coi.customer_order_id = co.id), 
co.totalQuantity = (select coalesce(sum( coi.filled ), 0) from customer_order_item coi where coi.customer_order_id = co.id),
co.totalNonShippedQuantity = (select coalesce(sum( coi.quantity ), 0) from customer_order_item coi where coi.customer_order_id = co.id), 
co.totalExtended = (select coalesce(sum( coi.extended ), 0) from customer_order_item coi where coi.customer_order_id = co.id)
where co.id = customerOrderId;

# timing
#select now();

return 1;
END//



DROP FUNCTION IF EXISTS updateAllBellOrder//
CREATE FUNCTION updateAllBellOrder (bellOrderId bigint(20)) returns INT DETERMINISTIC
BEGIN

declare itemId bigint(20);
declare done boolean default 0;

# cursor for looping customerOrderItems
declare itemCursor cursor for select boi.id from bell_order_item as boi where boi.bell_order_id = bellOrderId;

-- Declare continue handler, stops the repeat loop
DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

# timing
#select now();
    
# open the cursor
open itemCursor;

REPEAT
    FETCH itemCursor into itemId;

    update bell_order_item as boi set 
    boi.totalPrice = (boi.price*boi.filled) - ( (boi.price*boi.filled) * (boi.discount/100.0) ), 
    boi.totalExtended = (boi.cost*boi.filled), 
    boi.totalPriceNonShipped = (boi.price*boi.quantity) - ( (boi.price*boi.quantity) * (boi.discount/100.0) ), 
    boi.totalExtendedNonShipped = (boi.extended) - ( (boi.cost*boi.quantity) * (boi.discount/100.0) ) 
    where boi.id = itemId;

UNTIL done END REPEAT;

# close the cursor
close itemCursor;

# update the bell order

update bell_order as bo set 
bo.totalPrice = (select coalesce(sum( (boi.price*boi.filled) - ( (boi.price*boi.filled) * (boi.discount/100.0) ) ), 0) from bell_order_item boi where boi.bell_order_id = bo.id) + bo.shippingCharges,
bo.balanceDue = bo.totalPrice - bo.depositAmmount,
bo.totalPriceNonShipped = (select coalesce(sum( (boi.price*boi.quantity) - ( (boi.price*boi.quantity) * (boi.discount/100.0) ) ), 0) from bell_order_item boi where boi.bell_order_id = bo.id), 
bo.totalItems = (select count( boi.id ) from bell_order_item boi where boi.bell_order_id = bo.id), 
bo.totalQuantity = (select coalesce(sum( boi.filled ), 0) from bell_order_item boi where boi.bell_order_id = bo.id),
bo.totalNonShippedQuantity = (select coalesce(sum( boi.quantity ), 0) from bell_order_item boi where boi.bell_order_id = bo.id), 
bo.totalExtended = (select coalesce(sum( boi.extended ), 0) from bell_order_item boi where boi.bell_order_id = bo.id)
where bo.id = bellOrderId;

# timing
#select now();

return 1;
END//


#
# Updates the customerOrderItem data
#
DROP FUNCTION IF EXISTS updateCustomerOrderItem//
CREATE FUNCTION updateCustomerOrderItem (customerOrderItemId bigint(20)) returns INT DETERMINISTIC
BEGIN

	declare orderId bigint(20);

	select customer_order_id into orderId from customer_order_item where id = customerOrderItemId;
    update customer_order_item as coi set 
    coi.totalPrice = (coi.price*coi.filled) - ( (coi.price*coi.filled) * (coi.discount/100.0) ), 
    coi.totalExtended = (coi.cost*coi.filled), 
    coi.totalPriceNonShipped = (coi.price*coi.quantity) - ( (coi.price*coi.quantity) * (coi.discount/100.0) ), 
    coi.totalExtendedNonShipped = (coi.cost*coi.quantity) - ( (coi.cost*coi.quantity) * (coi.discount/100.0) ) 
    where coi.id = customerOrderItemId;
    
    
    # update the customer order
	update customer_order as co set 
	co.totalPricePreTax = (select coalesce(sum( (coi.price*coi.filled) - ( (coi.price*coi.filled) * (coi.discount/100.0) ) ), 0) from customer_order_item coi where coi.customer_order_id = co.id),
	co.totalTax = (co.totalPricePreTax * co.tax) - co.totalPricePreTax,
	co.totalPrice = (co.totalPricePreTax * co.tax) + co.shipping_charges + co.palleteCharge,
	co.balanceDue = co.totalPrice - co.deposit_ammount,
	co.totalPriceNonShipped = (select coalesce(sum( (coi.price*coi.quantity) - ( (coi.price*coi.quantity) * (coi.discount/100.0) ) ), 0) from customer_order_item coi where coi.customer_order_id = co.id), 
	co.totalItems = (select count( coi.id ) from customer_order_item coi where coi.customer_order_id = co.id), 
	co.totalQuantity = (select coalesce(sum( coi.filled ), 0) from customer_order_item coi where coi.customer_order_id = co.id),
	co.totalNonShippedQuantity = (select coalesce(sum( coi.quantity ), 0) from customer_order_item coi where coi.customer_order_id = co.id), 
	co.totalExtended = (select coalesce(sum( coi.extended ), 0) from customer_order_item coi where coi.customer_order_id = co.id)
	where co.id = orderId;
    
    
return 1;
END//


#
# Updates the bellOrderItem data
#
DROP FUNCTION IF EXISTS updateBellOrderItem//
CREATE FUNCTION updateBellOrderItem (bellOrderItemId bigint(20)) returns INT DETERMINISTIC
BEGIN

	declare orderId bigint(20);

	select bell_order_id into orderId from bell_order_item where id = bellOrderItemId;
    update bell_order_item as boi set 
    boi.totalPrice = (boi.price*boi.filled) - ( (boi.price*boi.filled) * (boi.discount/100.0) ), 
    boi.totalExtended = (boi.cost*boi.filled), 
    boi.totalPriceNonShipped = (boi.price*boi.quantity) - ( (boi.price*boi.quantity) * (boi.discount/100.0) ), 
    boi.totalExtendedNonShipped = (boi.cost*boi.quantity) - ( (boi.cost*boi.quantity) * (boi.discount/100.0) ) 
    where boi.id = bellOrderItemId;
    
    
    # update the customer order
	update bell_order as bo set 
	bo.totalPrice = (select coalesce(sum( (boi.price*boi.filled) - ( (boi.price*boi.filled) * (boi.discount/100.0) ) ), 0) from bell_order_item boi where boi.bell_order_id = bo.id) + bo.shippingCharges,
	bo.balanceDue = bo.totalPrice - bo.depositAmmount,
    bo.totalPriceNonShipped = (select coalesce(sum( (boi.price*boi.quantity) - ( (boi.price*boi.quantity) * (boi.discount/100.0) ) ), 0) from bell_order_item boi where boi.bell_order_id = bo.id), 
    bo.totalItems = (select count( boi.id ) from bell_order_item boi where boi.bell_order_id = bo.id), 
    bo.totalQuantity = (select coalesce(sum( boi.filled ), 0) from bell_order_item boi where boi.bell_order_id = bo.id),
    bo.totalNonShippedQuantity = (select coalesce(sum( boi.quantity ), 0) from bell_order_item boi where boi.bell_order_id = bo.id), 
    bo.totalExtended = (select coalesce(sum( boi.extended ), 0) from bell_order_item boi where boi.bell_order_id = bo.id)
	where bo.id = orderId;
    
    
return 1;
END//

#
# Updates the customerOrder data
#
DROP FUNCTION IF EXISTS updateCustomerOrder//
CREATE FUNCTION updateCustomerOrder (customerOrderId bigint(20)) returns INT DETERMINISTIC
BEGIN

    update customer_order as co set 
	co.totalPricePreTax = (select coalesce(sum( (coi.price*coi.filled) - ( (coi.price*coi.filled) * (coi.discount/100.0) ) ), 0) from customer_order_item coi where coi.customer_order_id = co.id),
	co.totalTax = (co.totalPricePreTax * co.tax) - co.totalPricePreTax,
	co.totalPrice = (co.totalPricePreTax * co.tax) + co.shipping_charges + co.palleteCharge,
	co.balanceDue = co.totalPrice - co.deposit_ammount,
    co.totalPriceNonShipped = (select coalesce(sum( (coi.price*coi.quantity) - ( (coi.price*coi.quantity) * (coi.discount/100.0) ) ), 0) from customer_order_item coi where coi.customer_order_id = co.id), 
    co.totalItems = (select count( coi.id ) from customer_order_item coi where coi.customer_order_id = co.id), 
    co.totalQuantity = (select coalesce(sum( coi.filled ), 0) from customer_order_item coi where coi.customer_order_id = co.id),
    co.totalNonShippedQuantity = (select coalesce(sum( coi.quantity ), 0) from customer_order_item coi where coi.customer_order_id = co.id), 
    co.totalExtended = (select coalesce(sum( coi.extended ), 0) from customer_order_item coi where coi.customer_order_id = co.id)
    where co.id = customerOrderId;
    
    
return 1;
END//


#
# Updates the bellOrder data
#
DROP FUNCTION IF EXISTS updateBellOrder//
CREATE FUNCTION updateBellOrder (bellOrderId bigint(20)) returns INT DETERMINISTIC
BEGIN

    update bell_order as bo set 
	bo.totalPrice = (select coalesce(sum( (boi.price*boi.filled) - ( (boi.price*boi.filled) * (boi.discount/100.0) ) ), 0) from bell_order_item boi where boi.bell_order_id = bo.id) + bo.shippingCharges,
	bo.balanceDue = bo.totalPrice - bo.depositAmmount,
    bo.totalPriceNonShipped = (select coalesce(sum( (boi.price*boi.quantity) - ( (boi.price*boi.quantity) * (boi.discount/100.0) ) ), 0) from bell_order_item boi where boi.bell_order_id = bo.id), 
    bo.totalItems = (select count( boi.id ) from bell_order_item boi where boi.bell_order_id = bo.id), 
    bo.totalQuantity = (select coalesce(sum( boi.filled ), 0) from bell_order_item boi where boi.bell_order_id = bo.id),
    bo.totalNonShippedQuantity = (select coalesce(sum( boi.quantity ), 0) from bell_order_item boi where boi.bell_order_id = bo.id), 
    bo.totalExtended = (select coalesce(sum( boi.extended ), 0) from bell_order_item boi where boi.bell_order_id = bo.id)
    where bo.id = bellOrderId;
    
return 1;
END//



#
# Updates the inventory item committed, available data
#
DROP FUNCTION IF EXISTS recalculateInvCommitted//
CREATE FUNCTION recalculateInvCommitted (invId bigint(20)) returns INT DETERMINISTIC
BEGIN

	update inventory_item as ii set
    ii.commited = ( select sum(coi.quantity) from customer_order_item as coi 
        where coi.inventory_item_id = ii.id and coi.credit = false and coi.customer_order_id in (select co.id from customer_order as co where co.posted = false))
	where ii.id = invId;

    update inventory_item as ii set
    ii.commited = 0
    where ii.id = invId and ii.commited is null;

    update inventory_item as ii set
    ii.available = ii.onhand - ii.commited
    where ii.id = invId;
	
return 1;
END//


#
# Updates the bell inventory item committed, available data
#
DROP FUNCTION IF EXISTS recalculateBellInvCommitted//
CREATE FUNCTION recalculateBellInvCommitted (invId bigint(20)) returns INT DETERMINISTIC
BEGIN

	update bell_inventory as ii set
    ii.committed = ( select sum(boi.quantity) from bell_order_item as boi 
        where boi.inventory_id = ii.id and boi.credit = false and boi.bell_order_id in (select bo.id from bell_order as bo where bo.posted = false))
	where ii.id = invId;

    update bell_inventory as ii set
    ii.committed = 0
    where ii.id = invId and ii.committed is null;

    update bell_inventory as ii set
    ii.available = ii.onhand - ii.committed
    where ii.id = invId;
	
return 1;
END//


#
# Updates the received data
#
DROP FUNCTION IF EXISTS updateReceived//
CREATE FUNCTION updateReceived (receivedId bigint(20)) returns INT DETERMINISTIC
BEGIN

    update received_item as ri set
    ri.extendedCost = ri.cost * ri.quantity 
    where ri.received_id = receivedId;
    
	update received as r set 
    r.totalItems = ( select count( id ) from received_item ri where ri.received_id = r.id ),
	r.totalQuantity = ( select sum( ri.quantity ) from received_item ri where ri.received_id = r.id ),
	r.totalOrderedQuantity = ( select sum( ri.ordered_quantity ) from received_item ri where ri.received_id = r.id ),
	r.totalCost = ( select sum( ri.cost ) from received_item ri where ri.received_id = r.id ),
    r.totalExtendedCost = ( select sum( ri.extendedCost ) from received_item ri where ri.received_id = r.id ),
	r.totalSellPrice = ( select sum( ri.sell_price * ri.quantity ) from received_item ri where ri.received_id = r.id ) 
    where r.id = receivedId;
    
return 1;
END//


#
# Updates the received data
#
DROP FUNCTION IF EXISTS updateBellReceived//
CREATE FUNCTION updateBellReceived (receivedId bigint(20)) returns INT DETERMINISTIC
BEGIN

    update bell_received_item as ri set
    ri.extendedCost = ri.cost * ri.quantity 
    where ri.received_id = receivedId;
    
	update bell_received as r set 
    r.totalItems = ( select count( id ) from bell_received_item ri where ri.received_id = r.id ),
	r.totalQuantity = ( select sum( ri.quantity ) from bell_received_item ri where ri.received_id = r.id ),
	r.totalOrderedQuantity = ( select sum( ri.ordered_quantity ) from bell_received_item ri where ri.received_id = r.id ),
	r.totalCost = ( select sum( ri.cost ) from bell_received_item ri where ri.received_id = r.id ),
    r.totalExtendedCost = ( select sum( ri.extendedCost ) from bell_received_item ri where ri.received_id = r.id ),
	r.totalSellPrice = ( select sum( ri.sell_price * ri.quantity ) from bell_received_item ri where ri.received_id = r.id ) 
    where r.id = receivedId;
    
return 1;
END//

#
#
#  TEMP function, meant as a one time function to update everything in the system
#
#
#

DROP PROCEDURE IF EXISTS tmpUpdateAllCustomerOrder//
CREATE PROCEDURE tmpUpdateAllCustomerOrder () DETERMINISTIC
BEGIN

declare orderId bigint(20);
declare done boolean default 0;
declare orderres int;

declare orderCursor cursor for select co.id from customer_order as co order by co.id desc;

DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

open orderCursor;

REPEAT
    FETCH orderCursor into orderId;
    select orderId, updateAllCustomerOrder(orderId);
UNTIL done END REPEAT;

close orderCursor;

END//


#
#
#  TEMP function, meant as a one time function to update everything in the system
#
#
#

DROP PROCEDURE IF EXISTS tmpUpdateAllBellOrder//
CREATE PROCEDURE tmpUpdateAllBellOrder () DETERMINISTIC
BEGIN

declare orderId bigint(20);
declare done boolean default 0;
declare orderres int;

declare orderCursor cursor for select bo.id from bell_order as bo where bo.category='Internal' order by bo.id desc;

DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

# first update all of the prices for credit items
# update bell_order_item set price = -price where credit = true;

open orderCursor;

REPEAT
    FETCH orderCursor into orderId;
    select orderId, updateAllBellOrder(orderId);
UNTIL done END REPEAT;

close orderCursor;

END//



#
#
#  TEMP function, meant as a one time function to update everything in the system
#
#
#

DROP PROCEDURE IF EXISTS tmpUpdateAllReceiving//
CREATE PROCEDURE tmpUpdateAllReceiving () DETERMINISTIC
BEGIN

declare rId bigint(20);
declare done boolean default 0;

declare recCursor cursor for select r.id from received as r order by r.id desc;

DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

open recCursor;

REPEAT
    FETCH recCursor into rId;
    select rId, updateReceived(rId);
UNTIL done END REPEAT;

close recCursor;

END//


DROP PROCEDURE IF EXISTS tmpUpdateAllBellReceiving//
CREATE PROCEDURE tmpUpdateAllBellReceiving () DETERMINISTIC
BEGIN

declare rId bigint(20);
declare done boolean default 0;

declare recCursor cursor for select r.id from bell_received as r order by r.id desc;

DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

open recCursor;

REPEAT
    FETCH recCursor into rId;
    select rId, updateBellReceived(rId);
UNTIL done END REPEAT;

close recCursor;

END//

