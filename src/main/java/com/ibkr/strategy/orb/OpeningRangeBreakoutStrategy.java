package com.ibkr.strategy.orb;

import static com.ib.client.Types.Right.Call;
import static com.ib.client.Types.Right.Put;
import static com.ibkr.statics.DateTimeUtil.isAfterOpeningRangeWindow;
import static com.ibkr.statics.DateTimeUtil.isAfterTradingWindow;
import static com.ibkr.statics.DateTimeUtil.isWithinOpeningRangeWindow;
import static com.ibkr.statics.OrderUtil.createBracketOrder;
import static com.ibkr.statics.OrderUtil.createOptionContract;
import static com.ibkr.strategy.orb.StrategyState.BREAKOUT;
import static com.ibkr.strategy.orb.StrategyState.OPENING_RANGE;
import static com.ibkr.strategy.orb.StrategyState.PULLBACK;
import static com.ibkr.strategy.orb.StrategyState.RETRACEMENT;
import static java.lang.Math.ceil;

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
import com.ibkr.strategy.indicators.FibonacciRetracement;
import com.ibkr.strategy.indicators.Vwap;
import jakarta.annotation.Nullable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.DoubleAccumulator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Trading strategy that tracks the price range during the initial market opening period and
 * capitalises on early market momentum.
 */
@Slf4j
public class OpeningRangeBreakoutStrategy extends AbstractStrategy {

  private final DoubleAccumulator PRICE_HIGH = new DoubleAccumulator(Math::max,
      Double.NEGATIVE_INFINITY);
  private final DoubleAccumulator PRICE_LOW = new DoubleAccumulator(Math::min,
      Double.POSITIVE_INFINITY);
  private final LocalTime openingRangeCutoff;
  private final BarTickAggregator aggregator;
  private final ApplicationEventPublisher publisher;
  private final List<RealTimeBarTick> lookbackWindow = new ArrayList<>(2);
  private final Vwap vwap;
  private Contract currentContract;
  private FibonacciRetracement fib;
  private StrategyState state = OPENING_RANGE;
  private boolean isPositionOpen = false;
  private boolean isBreakoutActive = false;
  private boolean isBullishTrend = false;
  private double askPrice;
  private double bidPrice;
  private double risk;
  private int spotPriceRequestId;

  /**
   * Initialises an ORB strategy instance with a specific cutoff time for the opening range.
   *
   * @param orbConfig enum with specific strategy configurations
   * @param publisher event publisher for downstream orders and data requests
   */
  public OpeningRangeBreakoutStrategy(OrbConfig orbConfig, ApplicationEventPublisher publisher,
      Vwap vwap) {
    super(orbConfig.getName());
    this.openingRangeCutoff = orbConfig.getCutoffTime();
    this.aggregator = orbConfig.getAggregator();
    this.publisher = publisher;
    this.vwap = vwap;
  }

  @Override
  public void onRealTimeBarTick(RealTimeBarTick tick) {
    if (isPositionOpen || isAfterTradingWindow(tick)) {
      return;
    }

    vwap.update(tick);
    aggregator.handleBarTick(tick).ifPresent(barTick -> {
      log.debug("Current state: {}, price close: {}", state, barTick.getClose());
      switch (state) {
        case OPENING_RANGE -> updateOpeningRange(barTick);
        case BREAKOUT -> detectBreakout(barTick);
        case PULLBACK -> detectPullback(barTick);
        case RETRACEMENT -> detectRetracement(barTick);
      }
      updateLookbackWindow(barTick);
    });
  }

  @Override
  public void onTickPrice(TickPrice tick) {
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
      List<Order> bracketOrder = createBracketOrder(bidPrice, askPrice, risk);
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
   * Pushes the latest bar tick to the sliding window to identify potential trend reversal.
   *
   * @param tick the latest aggregated bar tick
   */
  private void updateLookbackWindow(RealTimeBarTick tick) {
    if (lookbackWindow.size() == 2) {
      lookbackWindow.removeFirst();
    }
    lookbackWindow.add(tick);
  }

  /**
   * Updates the opening range high and low price based on the current tick.
   *
   * @param tick aggregated bar tick
   */
  private void updateOpeningRange(RealTimeBarTick tick) {
    if (isAfterOpeningRangeWindow(tick, openingRangeCutoff)) {
      log.info("Opening range finalized - high: {}, low: {}", PRICE_HIGH, PRICE_LOW);
      detectBreakout(tick);
      return;
    }

    if (isWithinOpeningRangeWindow(tick, openingRangeCutoff)) {
      PRICE_HIGH.accumulate(tick.getHigh());
      PRICE_LOW.accumulate(tick.getLow());
      log.debug("Updating opening range, high: {}, low: {}", PRICE_HIGH, PRICE_LOW);
    }
  }

  /**
   * Detects breakout by evaluating the price point of an aggregated bar tick against the
   * established opening range and the current VWAP.
   *
   * <p>If a breakout is confirmed, transitions to the next phase {@code PULLBACK}, waiting for
   * price to pullback and identifies overall market trend indicated by {@code isBullishTrend}. If
   * the price retraces more than 61.8%, the trend is invalidated and breakout detection starts
   * again only if price enters opening range.
   *
   * @param tick aggregated bar tick
   */
  private void detectBreakout(RealTimeBarTick tick) {
    state = BREAKOUT;
    double close = tick.getClose();
    if (close <= PRICE_HIGH.doubleValue() && close >= PRICE_LOW.doubleValue()) {
      isBreakoutActive = false;
      return;
    }
    // trend invalidated but price still outside opening range
    if (isBreakoutActive) {
      return;
    }

    double vwap = this.vwap.compute();
    if (close > PRICE_HIGH.doubleValue() && close > vwap) {
      log.info("Bullish price breakout above VWAP {}, waiting for price pullback...", vwap);
      isBreakoutActive = true;
      isBullishTrend = true;
      state = PULLBACK;
      return;
    }

    if (close < PRICE_LOW.doubleValue() && close < vwap) {
      log.info("Bearish price breakdown below VWAP {}, waiting for price pullback...", vwap);
      isBreakoutActive = true;
      isBullishTrend = false;
      state = PULLBACK;
    }
  }

  /**
   * Detects price pullback before the market trend resumes to identify potential entry point for
   * better risk-reward ratio.
   *
   * <p>Identifies peak for bullish trend or trough for bearish trend. Once confirmed, marks
   * Fibonacci retracement levels and transitions to the next phase {@code RETRACEMENT}.
   *
   * @param tick aggregated bar tick
   */
  private void detectPullback(RealTimeBarTick tick) {
    RealTimeBarTick pivot = lookbackWindow.get(1);
    if (isBullishTrend && isTrendReversing(tick, true)) {
      fib = new FibonacciRetracement(pivot.getHigh(), PRICE_LOW.doubleValue(), true);
      log.info("Fib retracement levels: 0% - {}, 38.2% - {}, 50% - {}, 61.8% - {}, 100% - {}",
          pivot.getHigh(), fib.fib382(), fib.fib500(), fib.fib618(), PRICE_LOW.doubleValue());
      state = RETRACEMENT;
      return;
    }

    if (!isBullishTrend && isTrendReversing(tick, false)) {
      fib = new FibonacciRetracement(PRICE_HIGH.doubleValue(), pivot.getLow(), false);
      log.info("Fib retracement levels: 0% - {}, 38.2% - {}, 50% - {}, 61.8% - {}, 100% - {}",
          pivot.getLow(), fib.fib382(), fib.fib500(), fib.fib618(), PRICE_HIGH.doubleValue());
      state = RETRACEMENT;
    }
  }

  /**
   * Detects price retracement signaling potential trend continuation after a period of pullback.
   *
   * <p>Identifies trough for bullish trend or peak for bearish trend. Once confirmed, requests for
   * option market data snapshot to establish option price and places limit price order.
   *
   * @param tick aggregated bar tick
   */
  private void detectRetracement(RealTimeBarTick tick) {
    Contract contract = null;
    if ((isBullishTrend && isTrendReversing(tick, false)) ||
        (!isBullishTrend && isTrendReversing(tick, true))) {
      contract = checkPriceIsInFibZone(tick);
    }

    if (Objects.nonNull(contract)) {
      spotPriceRequestId = IBClient.getNextRequestId();
      publisher.publishEvent(new RequestMarketDataSnapshotEvent(spotPriceRequestId, contract));
      currentContract = contract;
    }
  }

  /**
   * Evaluates potential trend reversal by identifying peak or trough with the help of
   * {@code lookbackWindow}, which stores the previous two bar ticks.
   *
   * @param tick           aggregated bar tick
   * @param isBullishTrend {@code true} if the trend is bullish, else {@code false}
   * @return {@code true} if trend is reversing, else {@code false}
   */
  private boolean isTrendReversing(RealTimeBarTick tick, boolean isBullishTrend) {
    RealTimeBarTick left = lookbackWindow.get(0);
    RealTimeBarTick pivot = lookbackWindow.get(1);
    double leftMidpoint = (left.getOpen() + left.getClose()) / 2;
    double pivotMidpoint = (pivot.getOpen() + pivot.getClose()) / 2;
    double currentMidpoint = (tick.getOpen() + tick.getClose()) / 2;

    return isBullishTrend ? ((currentMidpoint < pivotMidpoint) && (pivotMidpoint > leftMidpoint))
        : ((currentMidpoint > pivotMidpoint) && (pivotMidpoint < leftMidpoint));
  }

  /**
   * Checks how much the price retraced back and sets risk factor accordingly. If price retraces
   * more than 61.8%, invalidates the trend and waits for price to enter opening range again.
   *
   * @param tick aggregated bar tick
   * @return option {@link Contract} if price is within entry zone, else {@code null}
   */
  @Nullable
  private Contract checkPriceIsInFibZone(RealTimeBarTick tick) {
    double price = isBullishTrend ? tick.getLow() : tick.getHigh();
    double buffer = 0.05;

    boolean trendInvalidated =
        isBullishTrend ? (price <= fib.fib618() + buffer) : (price >= fib.fib618() - buffer);
    if (trendInvalidated) {
      log.info("Price breached Fib level 61.8%, invalidating trend...");
      state = BREAKOUT;
      return null;
    }

    if (isBullishTrend) {
      if (price <= fib.fib382() + buffer) {
        log.info("Bullish trend - price entered Fib level 38.2%, setting risk to 50%");
        risk = 0.5;
      } else if (price <= fib.fib500() + buffer) {
        log.info("Bullish trend - price entered Fib level 50%, setting risk to 30%");
        risk = 0.3;
      } else {
        return null;
      }
    } else {
      if (price >= fib.fib382() - buffer) {
        log.info("Bearish trend - price entered Fib level 38.2%, setting risk to 50%");
        risk = 0.5;
      } else if (price >= fib.fib500() - buffer) {
        log.info("Bearish trend - price entered Fib level 50%, setting risk to 30%");
        risk = 0.3;
      } else {
        return null;
      }
    }

    return isBullishTrend ? createOptionContract(ceil(price), Call.name())
        : createOptionContract(ceil(price), Put.name());
  }
}
