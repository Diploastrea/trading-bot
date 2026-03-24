package com.ibkr.strategy;

import com.ib.client.Contract;
import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;

/**
 * Common interface for all trading strategies.
 */
public interface Strategy {

  /**
   * Callback invoked whenever a bar tick is received from TWS API.
   *
   * <p>This method is processed asynchronously within its own serialized executor to ensure thread
   * isolation and chronological ordering of price data.
   *
   * @param realTimeBarTick object containing OHLC and volume data
   */
  void onBarTickEvent(RealTimeBarTick realTimeBarTick);

  /**
   * Defines the financial instrument that the strategy requires for execution.
   *
   * @return {@link Contract} object used for market data subscriptions
   */
  Contract getContract();
}
