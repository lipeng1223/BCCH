#!/bin/sh

kill -9 `cat socketserver.pid`

export CLASSPATH=build:src
export CLASSPATH=$CLASSPATH:lib/commons-beanutils-1.7.jar
export CLASSPATH=$CLASSPATH:lib/commons-collections-3.2.jar
export CLASSPATH=$CLASSPATH:lib/commons-digester-1.7.jar
export CLASSPATH=$CLASSPATH:lib/commons-lang-2.1.jar
export CLASSPATH=$CLASSPATH:lib/commons-logging-1.0.4.jar
export CLASSPATH=$CLASSPATH:lib/log4j.jar
export CLASSPATH=$CLASSPATH:lib/mysql-connector-java-5.0.3-bin.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/antlr-2.7.6.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/asm.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/asm-attrs.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/c3p0-0.9.1.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/cglib-2.1.3.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/dom4j-1.6.1.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/ehcache-1.2.3.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/hibernate3.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/jdbc2_0-stdext.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/jta.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/oscache-2.1.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/swarmcache-1.0rc2.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/xerces-2.6.2.jar
export CLASSPATH=$CLASSPATH:lib/hibernate3/xml-apis.jar
export CLASSPATH=$CLASSPATH:lib/amazon/AWSECommerceService.jar
export CLASSPATH=$CLASSPATH:lib/amazon/axis.jar
export CLASSPATH=$CLASSPATH:lib/amazon/commons-codec-1.3.jar
export CLASSPATH=$CLASSPATH:lib/amazon/commons-discovery-0.2.jar
export CLASSPATH=$CLASSPATH:lib/amazon/commons-httpclient-3.0-rc4.jar
export CLASSPATH=$CLASSPATH:lib/amazon/commons-logging-1.0.4.jar
export CLASSPATH=$CLASSPATH:lib/amazon/jaxrpc.jar
export CLASSPATH=$CLASSPATH:lib/amazon/saaj.jar
export CLASSPATH=$CLASSPATH:lib/amazon/wsdl4j-1.5.1.jar

nohup java -Xmx256M -Xms64M com.bc.ss.sockets.ScannerSocket &
echo $! > socketserver.pid