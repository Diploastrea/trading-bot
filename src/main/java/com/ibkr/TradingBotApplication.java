package com.ibkr;

import com.ibkr.client.IBClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot trading bot application.
 */
@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class TradingBotApplication implements ApplicationRunner {

  private final IBClient ib;

  static void main(String[] args) {
    SpringApplication.run(TradingBotApplication.class, args);
  }

  @Override
  public void run(@NonNull ApplicationArguments args) {
    ib.connect();
  }
}
