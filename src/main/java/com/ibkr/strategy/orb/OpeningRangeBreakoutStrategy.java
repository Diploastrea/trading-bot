package com.ibkr.strategy.orb;

import static com.ib.client.Decimal.ONE;
import static com.ib.client.OrderType.LMT;
import static com.ib.client.OrderType.STP;
import static com.ib.client.Types.Action.BUY;
import static com.ib.client.Types.Action.SELL;
import static com.ib.client.Types.Right.Call;
import static com.ib.client.Types.Right.Put;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.Types.SecType;
import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;
import com.ib.client.protobuf.TickPriceProto.TickPrice;
import com.ibkr.client.IBClient;
import com.ibkr.events.PlaceOrderEvent;
import com.ibkr.events.RequestMarketDataSnapshotEvent;
import com.ibkr.strategy.AbstractStrategy;
import com.ibkr.strategy.BarTickAggregator;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.DoubleAccumulator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Trading strategy that tracks the price range during the initial market opening period and
 * capitalises on early market momentum.
 */
@Slf4j
public class OpeningRangeBreakoutStrategy extends AbstractStrategy {

  private static final ZoneId NY_TIME_ZONE = ZoneId.of("America/New_York");
  private static final LocalTime NYSE_OPEN = LocalTime.of(9, 30);
  private static final LocalTime LAST_TRADE_CUTOFF = LocalTime.of(14, 0);
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
  private final DoubleAccumulator PRICE_HIGH = new DoubleAccumulator(Math::max,
      Double.NEGATIVE_INFINITY);
  private final DoubleAccumulator PRICE_LOW = new DoubleAccumulator(Math::min,
      Double.POSITIVE_INFINITY);
  private final LocalTime openingRangeCutoff;
  private final BarTickAggregator aggregator;
  private final ApplicationEventPublisher publisher;
  private boolean isOpeningRangeFinalized = false;
  private boolean isPositionOpen = false;
  private int spotPriceRequestId;
  private Contract currentContract;
  private double bidPrice;
  private double askPrice;

  /**
   * Initialises an ORB strategy instance with a specific cutoff time for the opening range.
   *
   * @param strategyName       strategy name used for logging and monitoring
   * @param openingRangeCutoff cutoff time at which the opening range is finalised
   * @param barSize            bar size used for breakout detection (in minutes)
   * @param publisher          event publisher for downstream orders and data requests
   */
  public OpeningRangeBreakoutStrategy(String strategyName, LocalTime openingRangeCutoff,
      int barSize, ApplicationEventPublisher publisher) {
    super(strategyName);
    this.openingRangeCutoff = openingRangeCutoff;
    this.aggregator = new BarTickAggregator(barSize);
    this.publisher = publisher;
  }

  @Override
  public void onBarTickEvent(RealTimeBarTick tick) {
    LocalTime currentTime = Instant.ofEpochSecond(tick.getTime()).atZone(NY_TIME_ZONE)
        .toLocalTime();
    // Recording opening range phase
    if (currentTime.isAfter(NYSE_OPEN) && currentTime.isBefore(openingRangeCutoff)) {
      updateOpeningRange(tick);
      return;
    }
    // Detecting price breakout phase
    if (currentTime.isAfter(openingRangeCutoff) && currentTime.isBefore(LAST_TRADE_CUTOFF)) {
      if (!isOpeningRangeFinalized) {
        log.info("Opening range finalized - high: {}, low: {}", PRICE_HIGH, PRICE_LOW);
        isOpeningRangeFinalized = true;
      }
      if (!isPositionOpen) {
        Optional<RealTimeBarTick> optionalTick = aggregator.handleBarTick(tick);
        optionalTick.ifPresent(this::detectBreakout);
      }
    }
  }

  @Override
  public void onTickPriceEvent(TickPrice tick) {
    if (tick.getReqId() != spotPriceRequestId) {
      return;
    }

    if (tick.getTickType() == 1) {
      bidPrice = tick.getPrice();
    } else if (tick.getTickType() == 2) {
      askPrice = tick.getPrice();
    }

    if (bidPrice > 0 && askPrice > 0 && !isPositionOpen) {
      isPositionOpen = true;
      List<Order> bracketOrder = createBracketOrder(bidPrice, askPrice);
      publisher.publishEvent(new PlaceOrderEvent(currentContract, bracketOrder));
    }
  }

  @Override
  public Contract getContractDetails() {
    Contract contract = new Contract();
    contract.symbol("SPY");
    contract.secType(SecType.STK);
    contract.exchange("SMART");
    contract.currency("USD");
    contract.conid(756733);
    return contract;
  }

  /**
   * Updates the high and low price based on the current tick.
   */
  private void updateOpeningRange(RealTimeBarTick tick) {
    PRICE_HIGH.accumulate(tick.getHigh());
    PRICE_LOW.accumulate(tick.getLow());
    log.debug("Updating opening range, high: {}, low: {}", PRICE_HIGH, PRICE_LOW);
  }

  /**
   * Evaluates the price point of an aggregated bar tick against the established opening range.
   * Triggers an option market data snapshot request if a breakout is confirmed.
   *
   * @param tick aggregated bar tick
   */
  private void detectBreakout(RealTimeBarTick tick) {
    log.debug("Processing aggregated bar tick for {} strategy.", Thread.currentThread().getName());

    Contract contract = null;
    if (tick.getClose() > PRICE_HIGH.doubleValue()) {
      log.info(
          "Detected bullish price breakout, creating call option contract with strike price of {}.",
          ceil(tick.getClose()));
      contract = createOptionContract(ceil(tick.getClose()), Call.name());
    } else if (tick.getClose() < PRICE_LOW.doubleValue()) {
      log.info(
          "Detected bearish price breakout, creating put option contract with strike price of {}.",
          floor(tick.getClose()));
      contract = createOptionContract(floor(tick.getClose()), Put.name());
    }

    if (Objects.nonNull(contract)) {
      spotPriceRequestId = IBClient.getNextRequestId();
      publisher.publishEvent(new RequestMarketDataSnapshotEvent(spotPriceRequestId, contract));
      currentContract = contract;
    }
  }

  /**
   * Creates and returns a 0DTE option contract.
   *
   * @param strike option strike price
   * @param right  option type (i.e. call or put)
   * @return {@link Contract} with given price and right
   */
  private Contract createOptionContract(double strike, String right) {
    Contract contract = new Contract();
    contract.symbol("SPY");
    contract.secType(SecType.OPT);
    contract.exchange("SMART");
    contract.currency("USD");
    contract.lastTradeDateOrContractMonth(LocalDate.now(NY_TIME_ZONE).format(dtf));
    contract.strike(strike);
    contract.right(right);
    contract.multiplier("100");
    return contract;
  }

  /**
   * Creates and returns a bracket order with limit entry price, a limit take profit and a stop loss
   * exit.
   *
   * @param bidPrice current market bid price
   * @param askPrice current market ask price
   * @return {@link List} of {@link Order} with take profit and stop loss attached to parent order
   */
  private List<Order> createBracketOrder(double bidPrice, double askPrice) {
    int parentOrderId = IBClient.getNextRequestId();
    double midPrice = (bidPrice + askPrice) / 2;
    double limitPrice = Math.floor(midPrice * 100) / 100;
    double tpLimitPrice = Math.floor((limitPrice * 2) * 100.0) / 100.0;
    double slLimitPrice = Math.floor((limitPrice * 0.50) * 100.0) / 100.0;

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
