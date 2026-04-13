package com.ibkr.strategy;

import com.ib.client.Contract;
import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;
import com.ib.client.protobuf.TickPriceProto.TickPrice;

/**
 * Common interface for all trading strategies.
 */
public interface Strategy {

  /**
   * Handles callback invoked whenever a bar tick is received from TWS API.
   *
   * <p>This method is processed asynchronously within its own serialized executor to ensure thread
   * isolation and chronological ordering of price data.
   *
   * @param realTimeBarTick object containing OHLC data
   */
  void onRealTimeBarTick(RealTimeBarTick realTimeBarTick);

  /**
   * Handles callback invoked whenever a tick price is received from TWS API.
   *
   * <p>This method is processed asynchronously within its own serialized executor to ensure thread
   * isolation and chronological ordering of price data.
   *
   * @param tickPrice object containing price data
   */
  void onTickPrice(TickPrice tickPrice);

  /**
   * Defines the financial instrument that the strategy requires for execution.
   *
   * @return {@link Contract} object used for market data subscriptions
   */
  Contract getContractDetails();
}
