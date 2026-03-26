package com.ibkr.service;

import com.ib.client.Order;
import com.ibkr.client.IBClient;
import com.ibkr.events.PlaceOrderEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final IBClient ib;

  /**
   * Processes order placement requests by submitting them to TWS API.
   *
   * @param event containing {@link List} of {@link Order} for given contract
   */
  @EventListener
  private void placeOrders(PlaceOrderEvent event) {
    event.orders()
        .forEach(order -> ib.client().placeOrder(order.orderId(), event.contract(), order));
    List<Integer> orderIds = event.orders().stream().map(Order::orderId).toList();
    log.info("Placing orders with ids: {}", orderIds);
  }
}
