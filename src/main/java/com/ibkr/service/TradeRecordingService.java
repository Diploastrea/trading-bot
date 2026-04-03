package com.ibkr.service;

import static com.ib.client.OrderStatus.Filled;
import static com.ibkr.strategy.AbstractStrategy.dtf;

import com.ib.client.protobuf.ContractProto.Contract;
import com.ib.client.protobuf.OpenOrderProto.OpenOrder;
import com.ib.client.protobuf.OrderProto.Order;
import com.ib.client.protobuf.OrderStateProto.OrderState;
import com.ib.client.protobuf.OrderStatusProto.OrderStatus;
import com.ibkr.dao.Trade;
import com.ibkr.dao.repository.TradeRepository;
import com.ibkr.events.OpenOrderEvent;
import com.ibkr.events.OrderStatusEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Service responsible for recording trades, including calculating fees and PnL.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeRecordingService {

  private final TradeRepository tradeRepository;

  /**
   * Handles {@link OpenOrderEvent} by creating a new record for entry orders or updates the fee for
   * exit orders.
   *
   * @param event containing order details and the underlying financial instrument
   */
  @Transactional
  @EventListener
  public void handleOpenOrderEvent(OpenOrderEvent event) {
    OpenOrder openOrder = event.openOrder();
    OrderState orderState = openOrder.getOrderState();
    BigDecimal fee = BigDecimal.valueOf(orderState.getCommissionAndFees());
    String orderStatus = orderState.getStatus();
    if (!Objects.equals(orderStatus, Filled.name()) || fee.compareTo(BigDecimal.ZERO) == 0) {
      return;
    }

    Order order = openOrder.getOrder();
    boolean isEntryOrder = order.getParentId() == 0;
    Contract contract = openOrder.getContract();
    Trade trade;
    if (isEntryOrder) {
      trade = Trade.builder()
          .orderId((long) openOrder.getOrderId())
          .strategyName(order.getOrderRef())
          .date(LocalDate.parse(contract.getLastTradeDateOrContractMonth(), dtf))
          .type(contract.getRight())
          .symbol(contract.getSymbol())
          .quantity(Integer.parseInt(order.getTotalQuantity()))
          .strike(BigDecimal.valueOf(contract.getStrike()))
          .fee(BigDecimal.valueOf(orderState.getCommissionAndFees()))
          .build();
    } else {
      trade = findByIdOrThrow(order.getParentId());
      trade.setFee(trade.getFee().add(BigDecimal.valueOf(orderState.getCommissionAndFees())));
    }

    tradeRepository.save(trade);
  }

  /**
   * Handles an {@link OrderStatusEvent} by updating fill or exit prices and calculates PnL for
   * closed positions.
   *
   * @param event containing order status details
   */
  @Transactional
  @EventListener
  public void handleOrderStatusEvent(OrderStatusEvent event) {
    OrderStatus orderStatus = event.orderStatus();
    if (!Objects.equals(orderStatus.getStatus(), Filled.name())) {
      return;
    }

    boolean isEntryOrder = orderStatus.getParentId() == 0;
    long orderId = isEntryOrder ? orderStatus.getOrderId() : orderStatus.getParentId();
    Trade trade = findByIdOrThrow(orderId);
    if (isEntryOrder) {
      log.info("Entry order ID {} filled at the average price of {}", orderStatus.getOrderId(),
          orderStatus.getAvgFillPrice());
      trade.setFill(BigDecimal.valueOf(orderStatus.getAvgFillPrice()));
    } else {
      log.info("Exit order ID {} filled at the average price of {}", orderStatus.getOrderId(),
          orderStatus.getAvgFillPrice());
      trade.setExit(BigDecimal.valueOf(orderStatus.getAvgFillPrice()));
      BigDecimal pnl = trade.getExit()
          .subtract(trade.getFill())
          .multiply(BigDecimal.valueOf(trade.getQuantity()))
          .multiply(BigDecimal.valueOf(100))
          .subtract(trade.getFee());
      trade.setPnl(pnl);
    }

    tradeRepository.save(trade);
  }

  /**
   * Finds and returns {@link Trade} entity by order ID or throws {@link EntityNotFoundException}.
   *
   * @param id order ID
   * @return {@link Trade} entity
   * @throws EntityNotFoundException if no entity is found
   */
  private Trade findByIdOrThrow(long id) {
    return tradeRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("No record found for order ID: " + id));
  }
}
