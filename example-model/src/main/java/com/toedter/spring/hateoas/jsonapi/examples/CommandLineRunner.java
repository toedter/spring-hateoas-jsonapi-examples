package com.toedter.spring.hateoas.jsonapi.examples;

@FunctionalInterface
public interface CommandLineRunner {
  /**
   * Callback used to run the bean.
   * @param args incoming main method arguments
   * @throws Exception on error
   */
  void run(String... args) throws Exception;
}
