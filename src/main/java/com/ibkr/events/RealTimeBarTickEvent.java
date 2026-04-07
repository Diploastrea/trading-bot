package com.ibkr.events;

import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;

/**
 * Event published when a new bar tick is received from TWS API.
 *
 * @param realTimeBarTick object containing OHLC data
 */
public record RealTimeBarTickEvent(RealTimeBarTick realTimeBarTick) {

}
