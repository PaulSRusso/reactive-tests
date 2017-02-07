#!/bin/bash
KAFKA_HOME=/opt/kafka
TOPIC=$1
function print_usage() {
    echo usage 'list-topic.sh TOPIC_NAME'
    exit
}
if [ -z "$TOPIC" ] 
then 
   print_usage
fi
$KAFKA_HOME/bin/kafka-console-consumer.sh --from-beginning --topic $TOPIC --bootstrap-server localhost:9092 --from-beginning
