package com.ibkr.events;

import com.ib.client.protobuf.OpenOrderProto.OpenOrder;

/**
 * Event published when a new open order update is received from TWS API.
 *
 * @param openOrder object containing order details and the underlying financial instrument
 */
public record OpenOrderEvent(OpenOrder openOrder) {

}
