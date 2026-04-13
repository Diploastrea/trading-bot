package com.ibkr.service;

import static com.ib.client.OrderStatus.Filled;
import static com.ibkr.statics.constants.CommonConstants.FORMATTER;

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

  private static final BigDecimal CONTRACT_MULTIPLIER = BigDecimal.valueOf(100);
  private final TradeRepository tradeRepository;

  /**
   * Handles {@link OpenOrderEvent} by setting order details and calculates profit and loss once
   * exit the position is closed.
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
      trade = findByIdOrThrow(openOrder.getOrderId());
      trade.setStrategyName(order.getOrderRef());
      trade.setDate(LocalDate.parse(contract.getLastTradeDateOrContractMonth(), FORMATTER));
      trade.setType(contract.getRight());
      trade.setSymbol(contract.getSymbol());
      trade.setQuantity(Integer.parseInt(order.getTotalQuantity()));
      trade.setStrike(BigDecimal.valueOf(contract.getStrike()));
      trade.setFee(BigDecimal.valueOf(orderState.getCommissionAndFees()));
    } else {
      trade = findByIdOrThrow(order.getParentId());
      trade.setFee(trade.getFee().add(BigDecimal.valueOf(orderState.getCommissionAndFees())));
      BigDecimal pnl = trade.getExit()
          .subtract(trade.getFill())
          .multiply(BigDecimal.valueOf(trade.getQuantity()))
          .multiply(CONTRACT_MULTIPLIER)
          .subtract(trade.getFee());
      trade.setPnl(pnl);
      log.info("Order ID {} closed with a PnL of: {}", trade.getOrderId(), trade.getPnl());
    }

    tradeRepository.save(trade);
  }

  /**
   * Handles {@link OrderStatusEvent} by creating a new record for each entry order and setting fill
   * price. For exit orders, exit price will be set instead.
   *
   * @param event containing order status details
   */
  @Transactional
  @EventListener
  public void handleOrderStatusEvent(OrderStatusEvent event) {
    OrderStatus orderStatus = event.orderStatus();
    long orderId = orderStatus.getOrderId();
    if (!Objects.equals(orderStatus.getStatus(), Filled.name())
        || tradeRepository.existsById(orderId)) {
      return;
    }

    boolean isEntryOrder = orderStatus.getParentId() == 0;
    double averagePrice = orderStatus.getAvgFillPrice();
    Trade trade;
    if (isEntryOrder) {
      log.info("Entry order ID {} filled at the average price of {}", orderId, averagePrice);
      trade = Trade.builder()
          .orderId(orderId)
          .fill(BigDecimal.valueOf(averagePrice))
          .build();
    } else {
      log.info("Exit order ID {} filled at the average price of {}", orderId, averagePrice);
      trade = findByIdOrThrow(orderStatus.getParentId());
      trade.setExit(BigDecimal.valueOf(averagePrice));
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
