package com.ibkr;

import com.ib.client.Types.SecType;
import com.ibkr.client.IBClient;
import com.ibkr.service.MarketDataService;
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
  private final MarketDataService marketDataService;

  static void main(String[] args) {
    SpringApplication.run(TradingBotApplication.class, args);
  }

  @Override
  public void run(@NonNull ApplicationArguments args) {
    ib.connect();
    marketDataService.requestLiveMarketData("SPY", SecType.STK, "ARCA", "USD", 756733);
  }
}
