package com.example.reactive.kafka;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.kafka.sender.Sender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
/**
 * See https://github.com/reactor/reactor-kafka/tree/master/reactor-kafka-samples/src/main/java/reactor/kafka/samples
 * 
 * @author <a href="mailto:paul.russo@jchart.com>Paul Russo</a>
 * @since Feb 6, 2017
 * 
 *  Start Zookeepeer and Kafka with the provided scripts in the kafka directory
 */
public class TestProducer {

   private static final Logger log = LoggerFactory
         .getLogger(TestProducer.class.getName());

   private static final String BOOTSTRAP_SERVERS = "localhost:9092";
   private static final String CLIENT_ID_CONFIG = "test-producer";
   private static final String TOPIC = "test-topic";

   private Sender<Integer, String> sender = null;
   private SenderOptions<Integer, String> senderOptions = null;

   private final SimpleDateFormat dateFormat = new SimpleDateFormat(
         "HH:mm:ss:SSS z dd MMM yyyy");

   @Before
   public void setup() {

      // configure senderOptions
      Map<String, Object> props = new HashMap<>();
      props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
      props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
      props.put(ProducerConfig.ACKS_CONFIG, "all");
      props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            IntegerSerializer.class);
      props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
      senderOptions = SenderOptions.create(props);
   }

   @Test
   public void testProducer() throws InterruptedException {
      int count = 100;
      CountDownLatch latch = new CountDownLatch(count);

      // set senderOptions
      sender = Sender.create(senderOptions);
      Consumer<SenderResult<Integer>> senderResultConsumer = getSenderResultConsumer(latch);
      
      sender.send(
            Flux.range(1, count)
              .map(i -> // transform the count to a ProducerRecord
                 SenderRecord.create(new ProducerRecord<>(TOPIC, i, "Message_" + i), i)),true) 
              .doOnError(e -> log.error("Send failed", e))
              .subscribe(senderResultConsumer);
      latch.await(100, TimeUnit.SECONDS);
      sender.close();

   }

   private Consumer<SenderResult<Integer>> getSenderResultConsumer(
         CountDownLatch latch) {
      return result -> {
         RecordMetadata metadata = result.recordMetadata();
         System.out.printf(
               "Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
               result.correlationMetadata(), metadata.topic(),
               metadata.partition(), metadata.offset(),
               dateFormat.format(new Date(metadata.timestamp())));
         latch.countDown();
      };
   }

}
