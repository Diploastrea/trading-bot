package com.ibkr.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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

  /**
   * Creates and returns {@link ScheduledExecutorService} bean, which uses a single platform thread
   * as a scheduler and stays idle until a task is due. When triggered, it spawns a new virtual
   * thread to execute the task.
   *
   * @return a {@link ScheduledExecutorService} virtual threads
   */
  @Bean
  public ScheduledExecutorService scheduledVirtualThreadExecutor() {
    return Executors.newScheduledThreadPool(1,
        Thread.ofVirtual().name("scheduled-virtual-", 1).factory());
  }
}
