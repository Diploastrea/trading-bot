package com.ibkr.events;

import com.ib.client.protobuf.OrderStatusProto.OrderStatus;

/**
 * Event published when an order status update is received from TWS API.
 *
 * @param orderStatus object containing order status details
 */
public record OrderStatusEvent(OrderStatus orderStatus) {

}