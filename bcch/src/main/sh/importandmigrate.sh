#!/bin/sh

cd

# we should bring down jboss and then start it back up once it is complete
date
echo "Shutting down jboss..."
/opt/jboss/bin/shutdown.sh -S &

echo "Cleaning up old inventory backup..."
rm -f inventorynoimages.sql*

date
echo "Getting inventory backup from the snap server..."
wget ftp://anon:anon@192.168.1.89/SHARE1/Main_Backup/inventory/inventorynoimages.sql.gz

date
echo "Uncompressing the inventory backup..."
gzip -d inventorynoimages.sql.gz

date 
echo "Importing the inventory backup..."
mysql -u root --password=power21 < galaga/src/main/sql/import.sql

date
echo "Creating stored procedures..."
mysql --delimiter=// -u root --password=power21 inventory < galaga/src/main/sql/storedProcedures.sql

cd galaga/src/main/sql

date
echo "Running migration..."
mysql -u root --password=power21 inventory < migrate.sql

date
echo "Running migration updates..."
mysql -u root --password=power21 inventory < migrateupdates.sql

# bring jboss back up after migration
date
echo "Starting JBoss again..."
/opt/jboss/bin/run.sh -Djava.awt.headless=true -c inventory -b 0.0.0.0 > /dev/null 2> /dev/null &

echo "Finished"
