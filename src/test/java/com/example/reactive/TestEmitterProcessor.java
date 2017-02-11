package com.example.reactive;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;

/**
 * Reference:
 * https://github.com/reactor/reactor-samples/blob/master/src/main/java/org/projectreactor/samples/FluxSamples.java
 * 
 * @author <a href="mailto:paul.russo@jchart.com>Paul Russo</a>
 * @since Feb 4, 2017
 */
public class TestEmitterProcessor {
   
   private Logger logger = LoggerFactory.getLogger(TestEmitterProcessor.class);


   // Reactive Streams API: implements *Processor*
   private EmitterProcessor<String> processor;
   
   // define publishers
   // Reactive Streams API: Mono implements *Publisher*
   private Mono<String> publisher1 = null;
   private Mono<List<String>> publisher2A  = null;
   private Mono<List<String>> publisher2B  = null;
   private Mono<String> publisher3 = null;

   private Consumer<String> doNextConsumer = 
         s ->  logger.info("Consumed {}", s);

   // Explicitly define a Consumer to be used in multiple 'doOnNext'
   
   @Before
   public void setup() {
      processor = EmitterProcessor.<String> create().connect();
   }

   @Test
   public void testPublisher1() {
      declarePublisher1();
      String message = "Value 100";
      publishMessage(message);
      processor.onComplete(); // subscriber
      String s = publisher1.block(); // block until a next signal is received
      Assert.assertEquals("Value 100", s);
   }

   // Declaratively compose stream processing 
   private void declarePublisher1() {
      publisher1 = processor
         .doOnNext(doNextConsumer) // triggered when the Flux emits an item.
         .next() // emit only the first item emitted by this Flux
         .subscribe(); // start the chain and request unbounded demand
   }

   @Test
   public void testPublisher2() {
      declarePublisher2();
      
      for (int i = 0; i < 5; i++) {
         // publish values
         publishMessage("A" + i);
         publishMessage("B" + i);
      }
      processor.onComplete(); // subscriber

      // invoke Functions on publisherA 
      List<String> listA = publisher2A.block();

      // test A values
      String expectedA = "A0,A1,A2,A3,A4";
      String actualA = String.join(",", listA);
      Assert.assertEquals(expectedA,actualA);
  
      // invoke Functions on publisherB 
      List<String> listB = publisher2B.block();

      // test B values
      String expectedB = "B0,B1,B2,B3,B4";
      String actualB = String.join(",", listB);
      Assert.assertEquals(expectedB,actualB);

      // print A values and B values
      listA.forEach(s ->  logger.info(s));
      listB.forEach(s ->  logger.info(s));

   }

   // Declaratively compose stream processing 
   private void declarePublisher2() {
      Predicate<String> predicateA = s -> s.startsWith("A");
      publisher2A = processor
         .filter(predicateA)
         .doOnNext(doNextConsumer) 
         .collectList()
         .subscribe(); // observable: start the chain and request unbounded demand

      publisher2B = processor
         .filter(s -> s.startsWith("B")) // inline predicate
         .doOnNext(doNextConsumer)
         .collectList()
         .subscribe(); // observable: start the chain and request unbounded demand
   }
   
   @Test
   public void testPublisher3() {
      declarePublisher3();
      publishMessage("Value 1");
      processor.onNext("Value 1");
      processor.onComplete(); // subscriber
      String s = publisher3.block();
      Assert.assertEquals("VALUE 1", s);
   }

   // Declaratively compose stream processing 
   private void declarePublisher3() {
      publisher3 = processor
         .map(String::toUpperCase) // transform
         .doOnNext(doNextConsumer)
         .next()
         .subscribe(); // start the chain and request unbounded demand
   }
   
   private void publishMessage(String message) {
      processor.onNext(message); // data notification sent by the Publisher 
   }


}