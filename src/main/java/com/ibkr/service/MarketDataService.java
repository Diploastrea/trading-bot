package com.ibkr.service;

import com.ibkr.client.IBClient;
import com.ibkr.events.MarketDataSubscriptionEvent;
import com.ibkr.events.RequestMarketDataSnapshotEvent;
import com.ibkr.strategy.Strategy;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Service responsible for subscription to market data feeds via TWS API.
 */
@Slf4j
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
  public void handleMarketDataSubscriptionEvent() {
    log.info("Subscribing to real time market data.");
    strategies.stream().map(Strategy::getContractDetails).collect(Collectors.toSet())
        .forEach(contract -> ib.client()
            .reqRealTimeBars(IBClient.getNextRequestId(), contract, 5, "TRADES", true, null)
        );
  }

  /**
   * Processes market data snapshot request for the given contract.
   *
   * @param event containing request ID and the contract
   */
  @EventListener
  public void handleRequestMarketDataSnapshotEvent(RequestMarketDataSnapshotEvent event) {
    ib.client().reqMktData(event.requestId(), event.contract(), "", true, false, null);
  }
}
