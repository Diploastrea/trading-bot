package com.ibkr.domain;

import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;

/**
 * Event published when a new bar tick is received from TWS API.
 *
 * @param realTimeBarTick object containing OHLC and volume data
 */
public record BarTickEvent(RealTimeBarTick realTimeBarTick) {

}
