package com.example.reactive;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
/**
 * @author <a href="mailto:paul.russo@jchart.com>Paul Russo</a>
 * @since Feb 4, 2017
 */
public class TestFlux {

   @Test
   public void testFluxToSream() {
      List<String> list = Arrays.asList("A1", "A2", "A3", "B4");
      Stream<?> stream = Flux.fromStream(list.stream())
            .timeout(Duration.ofSeconds(30))
            .log("test")
            .toStream();
      stream.forEach(s -> System.out.println(s));
   }

	@Test
	public void testListToFlux() {

		List<String> list = Arrays.asList("A1", "A2", "A3", "B4");
		Flux<String> flux = Flux.fromIterable(list); // potentially unbounded stream
		
		// test if *all* values start with 'A'
		Predicate<String> predicateA = s -> s.startsWith("A");
		Mono<Boolean> testAs = flux.all(predicateA);
		Boolean a = testAs.block();
		Assert.assertFalse(a);

		// test if *any* values start with 'B'
		Predicate<String> predicateB = s -> s.startsWith("B");
		Mono<Boolean> testBs = flux.any(predicateB);
		Boolean b = testBs.block();
		Assert.assertTrue(b);

	   StepVerifier.create(flux)
         .expectNext("A1", "A2", "A3", "B4")
         .expectComplete()
         .verify();
 

	}
	
}
