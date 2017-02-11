#!/bin/bash
## stop kafka server will not work install in long dir path
KAFKA_HOME=/opt/kafka
ACTION=$1
function print_usage() {
    echo usage 'servers.sh start|stop'
    exit
}
if [ -z "$ACTION" ] 
then 
   print_usage
fi
if [ "$ACTION" != 'start' ] && [ "$ACTION" != 'stop' ]
then 
   print_usage
fi

echo "$ACTION servers"

if [ "$ACTION" == 'start' ] 
then
   $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties > /tmp/zookeeper.log &
   sleep 3
   $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties > /tmp/kafka.log &
else
   $KAFKA_HOME/bin/kafka-server-stop.sh 
   sleep 3
   $KAFKA_HOME/bin/zookeeper-server-stop.sh
fi

tail -f /tmp/kafka.log
