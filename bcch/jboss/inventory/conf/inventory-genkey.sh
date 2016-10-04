#!/bin/sh

echo "generating key"

rm inventory.keystore
keytool -genkey -alias inventory.bookcountryclearinghouse.com -keystore inventory.keystore -validity 9999

echo 
echo "finished"
