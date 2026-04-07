package com.ibkr.strategy;

import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;
import com.ibkr.events.RealTimeBarTickEvent;
import com.ibkr.events.TickPriceEvent;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.DoubleAccumulator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

/**
 * Common parent class for all strategies providing thread isolation and event ordering.
 *
 * <p>Each instance maintains a private, single-threaded virtual executor to process market data
 * ticks sequentially without blocking other strategies or the market data feed.
 */
@Slf4j
public abstract class AbstractStrategy implements Strategy {

  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
  protected static final ZoneId NY_TIME_ZONE = ZoneId.of("America/New_York");
  protected static final LocalTime NYSE_OPEN = LocalTime.of(9, 30);
  protected static final LocalTime LAST_TRADE_CUTOFF = LocalTime.of(12, 0);
  private final DoubleAccumulator CUMULATIVE_PV = new DoubleAccumulator(Double::sum, 0.0);
  private final DoubleAccumulator CUMULATIVE_VOL = new DoubleAccumulator(Double::sum, 0.0);
  private final ExecutorService executor;

  /**
   * Initialises named virtual thread executor for the strategy.
   *
   * @param strategyName identifier used for thread naming and logging
   */
  protected AbstractStrategy(String strategyName) {
    executor = Executors.newSingleThreadExecutor(Thread.ofVirtual().name(strategyName).factory());
  }

  /**
   * Calculates and returns VWAP value.
   *
   * @return VWAP value
   */
  protected double getVwap() {
    double volume = CUMULATIVE_VOL.get();
    return (volume > 0) ? (CUMULATIVE_PV.get() / volume) : 0.0;
  }

  /**
   * Updates VWAP and dispatches incoming bar ticks to the strategy's executor.
   *
   * @param event published market data event
   */
  @EventListener
  private void handleRealTimeBarTickEvent(RealTimeBarTickEvent event) {
    RealTimeBarTick tick = event.realTimeBarTick();
    safeExecute(() -> {
      updateVwap(tick);
      onRealTimeBarTick(tick);
    });
  }

  /**
   * Dispatches incoming tick price to the strategy's executor.
   *
   * @param event published market data event
   */
  @EventListener
  private void handleTickPriceEvent(TickPriceEvent event) {
    safeExecute(() -> onTickPrice(event.tickPrice()));
  }

  /**
   * Updates cumulative price volume and cumulative volume needed to calculate VWAP.
   *
   * @param tick object containing OHLC data
   */
  private void updateVwap(RealTimeBarTick tick) {
    LocalTime time = Instant.ofEpochSecond(tick.getTime()).atZone(NY_TIME_ZONE).toLocalTime();

    if (time.isAfter(NYSE_OPEN) && time.isBefore(LAST_TRADE_CUTOFF)) {
      double volume = Double.parseDouble(tick.getVolume());
      double price = Double.parseDouble(tick.getWAP());
      CUMULATIVE_PV.accumulate(volume * price);
      CUMULATIVE_VOL.accumulate(volume);
    }
  }

  /**
   * Submits a task to the strategy's executor, while ensuring exceptions are caught and logged.
   *
   * @param task {@link Runnable} task to be executed
   */
  private void safeExecute(Runnable task) {
    executor.execute(() -> {
      try {
        task.run();
      } catch (Exception e) {
        log.error("Exception while running task", e);
      }
    });
  }
}
