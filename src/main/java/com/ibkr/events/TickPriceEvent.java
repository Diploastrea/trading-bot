package com.ibkr.events;

import com.ib.client.protobuf.TickPriceProto.TickPrice;

/**
 * Event published when a new tick price is received from TWS API.
 *
 * @param tickPrice object containing price related data (e.g. bid price, ask price)
 */
public record TickPriceEvent(TickPrice tickPrice) {

}
