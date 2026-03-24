package com.ibkr.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that provides an {@link ExecutorService} bean.
 */
@Configuration
public class ExecutorConfiguration {

  /**
   * Creates and returns {@link ExecutorService} bean, which executes each separate task in a
   * lightweight virtual thread.
   *
   * @return an {@link ExecutorService} backed by virtual threads
   */
  @Bean
  public ExecutorService virtualThreadExecutor() {
    return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("virtual-", 1).factory());
  }
}
