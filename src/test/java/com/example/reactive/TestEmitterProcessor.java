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
 * https://github.com/reactor/reactor-samples/blob/master/src/main/java/org/projectreactor/samples/FluxSamples.java
 * 
 * @author <a href="mailto:paul.russo@jchart.com>Paul Russo</a>
 * @since Feb 4, 2017
 */
public class TestEmitterProcessor {
   
   private Logger logger = LoggerFactory.getLogger(TestEmitterProcessor.class);


   // Reactive Streams API: implements *Processor*
   private EmitterProcessor<String> processor;

   // Explicitly define a Consumer to be used in multiple 'doOnNext'
   private Consumer<String> doNextConsumer = 
         s ->  logger.info("Consumed {}", s);


   @Before
   public void setup() {
      processor = EmitterProcessor.<String> create().connect();
   }

   // Reactive Streams API: Mono implements *Publisher*

   @Test
   public void testMonoStingValue() {
      // Mono implements Reactive Streams Publisher
      Mono<String> monoPublisher = processor
            .doOnNext(doNextConsumer) // triggered when the Flux emits an item.
            .next() // emit only the first item emitted by this Flux.
            .subscribe(); // start the chain and request unbounded demand

      // publish a value
      processor.onNext("Value 100"); // data notification sent by the Publisher 
      processor.onComplete(); // subscriber
      String s = monoPublisher.block(); // block until a next signal is received
      Assert.assertEquals("Value 100", s);
   }

   @Test
   public void testMonoFilterListValue() {

      Predicate<String> predicateA = s -> s.startsWith("A");
      Predicate<String> predicateB = s -> s.startsWith("B");

      Mono<List<String>> publisherA = processor
            .filter(predicateA)
            .doOnNext(doNextConsumer) 
            .collectList()
            .subscribe(); // start the chain and request unbounded demand

      Mono<List<String>> publisherB = processor
            .filter(predicateB)
            .doOnNext(doNextConsumer)
            .collectList()
            .subscribe(); // start the chain and request unbounded demand

      for (int i = 0; i < 5; i++) {
         // publish values
         processor.onNext("A" + i); // subscriber onNext
         processor.onNext("B" + i); 
      }
      processor.onComplete(); // subscriber

      // invoke Functions on publisherA 
      List<String> listA = publisherA.block();

      // test A values
      String expectedA = "A0,A1,A2,A3,A4";
      String actualA = String.join(",", listA);
      Assert.assertEquals(expectedA,actualA);
  
      // invoke Functions on publisherB 
      List<String> listB = publisherB.block();

      // test B values
      String expectedB = "B0,B1,B2,B3,B4";
      String actualB = String.join(",", listB);
      Assert.assertEquals(expectedB,actualB);

      // print A values and B values
      listA.forEach(s ->  logger.info(s));
      listB.forEach(s ->  logger.info(s));

   }

   @Test
   public void testTransform() {

      Mono<String> monoPublisher = processor
            .map(String::toUpperCase) // transform
            .doOnNext(doNextConsumer)
            .next()
            .subscribe(); // start the chain and request unbounded demand

      // publish a value
      processor.onNext("Value 1");
      processor.onComplete(); // subscriber
      String s = monoPublisher.block();
      Assert.assertEquals("VALUE 1", s);
   }

}