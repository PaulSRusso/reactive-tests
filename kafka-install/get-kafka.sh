#!/bin/bash
KAFKA_MINOR_VERSION=0.10.1.1
KAFKA_VERSION=2.11-$KAFKA_MINOR_VERSION
wget http://www-us.apache.org/dist/kafka/$KAFKA_MINOR_VERSION/kafka_$KAFKA_VERSION.tgz 
#tar xvzf kafka_$KAFKA_VERSION.tgz
#mv kafka_$KAFKA_VERSION kafka
#rm kafka_$KAFKA_VERSION.tgz
#sed -i "s/^#delete.topic.enable=true/delete.topic.enable=true/" ./kafka/config/server.properties
