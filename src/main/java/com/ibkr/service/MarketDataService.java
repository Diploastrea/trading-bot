package com.ibkr.service;

import com.ib.client.Contract;
import com.ibkr.client.IBClient;
import com.ibkr.domain.MarketDataSubscriptionEvent;
import com.ibkr.strategy.Strategy;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Service responsible for subscription to market data feeds via TWS API.
 */
@Service
@RequiredArgsConstructor
public class MarketDataService {

  private final IBClient ib;
  private final List<Strategy> strategies;

  /**
   * Processes market data subscription requests by aggregating unique contracts from all registered
   * {@link Strategy} instances to prevent redundant API calls and potential pacing violations.
   */
  @EventListener(MarketDataSubscriptionEvent.class)
  public void requestMarketData() {
    Set<Contract> contracts = strategies.stream().map(Strategy::getContract)
        .collect(Collectors.toSet());
    contracts.forEach(contract -> ib.client()
        .reqRealTimeBars(ib.getNextRequestId(), contract, 5, "TRADES", false, null)
    );
  }
}
