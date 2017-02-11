#!/bin/bash
KAFKA_HOME=./kafka
TOPIC=$1
# Create Kafka topic
$KAFKA_HOME/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic $TOPIC
