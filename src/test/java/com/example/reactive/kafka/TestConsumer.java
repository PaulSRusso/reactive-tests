
package com.example.reactive.kafka;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.Receiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;
import reactor.kafka.receiver.ReceiverRecord;

/**
 * See https://github.com/reactor/reactor-kafka/tree/master/reactor-kafka-samples/src/main/java/reactor/kafka/samples
 * 
 * @author <a href="mailto:paul.russo@jchart.com>Paul Russo</a>
 * @since Feb 6, 2017
 * 
 *  Start Zookeepeer and Kafka with the provided scripts in the kafka directory
 */
public class TestConsumer {

   private static final Logger log = LoggerFactory
         .getLogger(TestConsumer.class.getName());

   private static final String BOOTSTRAP_SERVERS = "localhost:9092";
   private static final String TOPIC = "test-topic";
   private static final String CLIENT_ID_CONFIG = "test-consumer";
   private static final String GROUP_ID_CONFIG = "test-group";

   private ReceiverOptions<Integer, String> receiverOptions = null;;
   private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");

   @Before
   public void setup() {

      // configure receiverOptions properties
      Map<String, Object> props = new HashMap<>();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
      props.put(ConsumerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONFIG);
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            IntegerDeserializer.class);
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class);
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

      // create receiverOptions
      receiverOptions = ReceiverOptions.create(props);
      
      Consumer<Collection<ReceiverPartition>> onAssignConsumer = 
            partitions -> log.debug("onPartitionsAssigned {}", partitions);
      
      Consumer<Collection<ReceiverPartition>> onRevokeConsumer = 
            partitions -> log.debug("onPartitionsRevoked {}", partitions);
      
      receiverOptions
            .subscription(Collections.singleton(TOPIC))
            .addAssignListener(onAssignConsumer)
            .addRevokeListener(onRevokeConsumer)
            .commitBatchSize(10);

   }

   @Test
   public void testConsumer() throws InterruptedException {
      int count = 100;
      CountDownLatch latch = new CountDownLatch(count);
      
      Consumer<ReceiverRecord<Integer, String>> messageConsumer = 
         getMessageConsumer(latch);

      // set receiverOptions  
      // start a Kafka consumer that consumes records 
      Flux<ReceiverRecord<Integer, String>> kafkaFlux = 
            Receiver.create(receiverOptions).receive();

      // request an unbounded demand
      Disposable disposable = kafkaFlux.subscribe(messageConsumer); 
      latch.await(count, TimeUnit.SECONDS);
      disposable.dispose();
   }

   private Consumer<ReceiverRecord<Integer, String>> getMessageConsumer (
         CountDownLatch latch) {
      return message -> {
         ReceiverOffset offset = message.offset();
         ConsumerRecord<Integer, String> record = message.record();
         System.out.printf(
               "Received message: topic-partition=%s offset=%d timestamp=%s key=%d value=%s\n",
               offset.topicPartition(), offset.offset(),
               dateFormat.format(new Date(record.timestamp())), record.key(),
               record.value());

         latch.countDown();
      };
   }

}
