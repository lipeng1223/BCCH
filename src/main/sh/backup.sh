#!/bin/bash

echo `date`
echo "Mysql dump..."

mysqldump -h localhost -u root --password=power21 --add-drop-table --add-locks --databases inventory --tables amz_category amz_category_ancestor amz_category_level amz_subject audit backorder_notification bell_cost bell_customer bell_customer_shipping bell_inventory bell_invoice_number bell_order bell_order_item bell_received bell_received_item bell_sku bell_vendor break_received break_received_item bri_count cart cart_item category customer customer_order customer_order_item customer_shipping inventory_item invoice_number manifest manifest_item publisher publisher_imprint received received_item saved_items saved_searches saved_selections skid user userrole vendor vendor_skid_type > ~/mysqlbackup/inventorynoimages.sql
SUCCESS=$?

echo `date`
echo "Mysql dump finished."
echo 


MYSQL="SUCCESS database dump"
SUBJ="SUCCESS"
if [ $SUCCESS -ne 0 ]
then
    MYSQL="FAILURE database dump"
    SUBJ="FAILURE"
fi

echo `date`
echo "GZip the inventory..."
cd ~/mysqlbackup
gzip -f inventorynoimages.sql
echo `date`
echo "GZip finished."
echo

echo `date`
echo "Copy inventorynoimages.sql.gz to /backup/..."

cp -f inventorynoimages.sql.gz /backup/

echo `date`
echo "Copy inventorynoimages.sql.gz to /mnt/winbackup/..."
cp -f inventorynoimages.sql.gz /mnt/winbackup/

MESSAGE="/tmp/message.txt"
echo "$MYSQL" > $MESSAGE
/bin/mail -s "$SUBJ bookcountry nightly inventory backup" "backups@midnightbluetech.com" < $MESSAGE
#/bin/mail -s "$SUBJ bookcountry nightly inventory backup" "megela@gmail.com" < $MESSAGE
rm $MESSAGE

