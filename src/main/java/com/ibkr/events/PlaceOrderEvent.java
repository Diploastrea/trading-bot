package com.ibkr.events;

import com.ib.client.Contract;
import com.ib.client.Order;
import java.util.List;

/**
 * Event published to place a market order.
 *
 * @param contract details for the financial instrument
 * @param orders   {@link List} of {@link Order} to be placed for the contract
 */
public record PlaceOrderEvent(Contract contract, List<Order> orders) {

}
