#!/bin/bash
TOPIC=$1

# Create Kafka topic
$KAFKA_HOME/bin/kafka-topics.sh --zookeeper localhost:2181 --create --replication-factor 1 --partitions 2 --topic $TOPIC
