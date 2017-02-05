package com.example.reactive;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

/**
 * https://github.com/reactor/reactor-samples/blob/master/src/main/java/org/projectreactor/samples/MonoSamples.java
 *
 * @author <a href="mailto:paul.russo@jchart.com>Paul Russo</a>
 * @since Feb 4, 2017
 */
public class TestMonoProcessor {

   private Logger logger = LoggerFactory.getLogger(TestMonoProcessor.class);
   
   // extension that implements stateful semantics
   private MonoProcessor<String> processor = MonoProcessor.create();
   
   // returns "at most one"
   private Mono<String> monoResult;
   
   @Before
   public void setUp() {
      monoResult = processor
            .doOnSuccess(p -> logger.info("Promise completed {}", p))
            .doOnTerminate((s, e) -> logger.info("Got value: {}", s))
            .doOnError(t -> logger.error(t.getMessage(), t));
   }
   
   @Test
   public void test1() {
      String monoText = "Test1";
      processor.subscribe(); // start the chain and request unbounded demand.
      processor.onNext(monoText);
      String result = monoResult.block();
      Assert.assertEquals(monoText, result);
   }

   @Test
   public void test2() {
      String monoText = "Test2";
      processor.subscribe(); // start the chain and request unbounded demand.
      processor.onNext(monoText);
      String result = monoResult.block();
      Assert.assertEquals(monoText, result);
   }

}
