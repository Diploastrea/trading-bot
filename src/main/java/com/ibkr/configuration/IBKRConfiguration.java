package com.ibkr.configuration;

import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Configuration class for TWS API components required for TCP communication with TWS API.
 */
@Configuration
public class IBKRConfiguration {

  /**
   * Creates and returns the signal object used to synchronise the message processing loop.
   *
   * @return a new {@link EJavaSignal} instance
   */
  @Bean
  public EJavaSignal eJavaSignal() {
    return new EJavaSignal();
  }

  /**
   * Creates and returns socket client for sending requests to TWS API.
   *
   * <p>Note: The {@link EWrapper} parameter is marked with {@link Lazy} to break the
   * circular dependency chain (IBClient -> EClientSocket -> EWrapper -> IBClient).
   * This allows the Spring ApplicationContext to initialise successfully by providing
   * a proxy for the wrapper during the initial bean construction.
   *
   * @param wrapper the callback handler for TWS API responses
   * @param signal the synchronisation signal for the message reader
   * @return a new {@link EClientSocket} instance
   */
  @Bean
  public EClientSocket eClientSocket(@Lazy EWrapper wrapper, EJavaSignal signal) {
    return new EClientSocket(wrapper, signal);
  }
}
