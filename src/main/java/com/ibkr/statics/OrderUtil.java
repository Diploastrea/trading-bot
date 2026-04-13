package com.ibkr.statics;

import static com.ib.client.Decimal.ONE;
import static com.ib.client.OrderType.LMT;
import static com.ib.client.OrderType.STP;
import static com.ib.client.Types.Action.BUY;
import static com.ib.client.Types.Action.SELL;
import static com.ibkr.statics.constants.CommonConstants.FORMATTER;
import static com.ibkr.statics.constants.CommonConstants.NY_TIME_ZONE;
import static java.lang.Math.floor;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.Types.SecType;
import com.ibkr.client.IBClient;
import java.time.LocalDate;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to generate order related objects for TWS API.
 */
@Slf4j
@UtilityClass
public class OrderUtil {

  /**
   * Creates and returns a 0DTE option contract.
   *
   * @param strike option strike price
   * @param right  option type (i.e. call or put)
   * @return {@link Contract} with given price and right
   */
  public static Contract createOptionContract(double strike, String right) {
    Contract contract = new Contract();
    contract.symbol("SPY");
    contract.secType(SecType.OPT);
    contract.exchange("SMART");
    contract.currency("USD");
    contract.lastTradeDateOrContractMonth(LocalDate.now(NY_TIME_ZONE).format(FORMATTER));
    contract.strike(strike);
    contract.right(right);
    contract.multiplier("100");

    log.info("Creating {} option contract with strike price of {}.", right, strike);
    return contract;
  }

  /**
   * Creates and returns a bracket order with limit entry price, a limit take profit and a stop loss
   * exit.
   *
   * @param bidPrice current market bid price
   * @param askPrice current market ask price
   * @param risk     percentage of entry price used to define take profit and stop loss
   * @return {@link List} of {@link Order} with take profit and stop loss attached to parent order
   */
  public static List<Order> createBracketOrder(double bidPrice, double askPrice, double risk) {
    int parentOrderId = IBClient.getNextRequestId();
    double midPrice = (bidPrice + askPrice) / 2;
    double limitPrice = floor(midPrice * 100) / 100;
    double optionRisk = limitPrice * risk;
    double tpLimitPrice = floor((limitPrice + (optionRisk * 2)) * 100.0) / 100.0;
    double slLimitPrice = floor((limitPrice - optionRisk) * 100.0) / 100.0;

    Order parent = new Order();
    parent.orderId(parentOrderId);
    parent.action(BUY);
    parent.orderType(LMT);
    parent.lmtPrice(limitPrice);
    parent.totalQuantity(ONE);
    parent.orderRef(Thread.currentThread().getName());
    parent.transmit(false);

    Order takeProfit = new Order();
    takeProfit.orderId(IBClient.getNextRequestId());
    takeProfit.parentId(parentOrderId);
    takeProfit.action(SELL);
    takeProfit.orderType(LMT);
    takeProfit.lmtPrice(tpLimitPrice);
    takeProfit.totalQuantity(ONE);
    takeProfit.transmit(false);

    Order stopLoss = new Order();
    stopLoss.orderId(IBClient.getNextRequestId());
    stopLoss.parentId(parentOrderId);
    stopLoss.action(SELL);
    stopLoss.orderType(STP);
    stopLoss.auxPrice(slLimitPrice);
    stopLoss.totalQuantity(ONE);
    stopLoss.transmit(true);

    log.info("Creating bracket order - limit price: {}, take profit: {}, stop loss: {}", limitPrice,
        tpLimitPrice, slLimitPrice);
    return List.of(parent, takeProfit, stopLoss);
  }
}
