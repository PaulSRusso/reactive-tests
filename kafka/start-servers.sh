#!/bin/bash
KAFKA_HOME=/opt/kafka
echo "Starting Zookeepear..." 
$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties > /tmp/zookeeper.log &

echo "Starting Kafka...";
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties > /tmp/kafka.log &
