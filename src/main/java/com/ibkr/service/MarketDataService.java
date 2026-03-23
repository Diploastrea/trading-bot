package com.ibkr.service;

import com.ib.client.Contract;
import com.ib.client.Types.SecType;
import com.ibkr.client.IBClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for subscription to market data feeds via TWS API.
 */
@Service
@RequiredArgsConstructor
public class MarketDataService {

  private final IBClient ib;

  /**
   * Requests live market data stream for a specific contract.
   *
   * @param symbol       ticker symbol
   * @param securityType security type (e.g. stock, options)
   * @param exchange     exchange
   * @param currency     currency
   * @param contractId   contract ID
   */
  public void requestLiveMarketData(String symbol, SecType securityType, String exchange,
      String currency, int contractId) {
    Contract contract = new Contract();
    contract.symbol(symbol);
    contract.secType(securityType);
    contract.exchange(exchange);
    contract.currency(currency);
    contract.conid(contractId);

    ib.client().reqRealTimeBars(ib.getNextRequestId(), contract, 5, "TRADES", true, null);
  }
}
