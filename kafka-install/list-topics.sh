#!/bin/bash
KAFKA_HOME=./kafka
TOPIC=$1
$KAFKA_HOME/bin/kafka-topics.sh --list --zookeeper localhost:2181
