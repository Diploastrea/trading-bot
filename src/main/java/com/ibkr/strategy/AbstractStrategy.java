package com.ibkr.strategy;

import com.ibkr.events.BarTickEvent;
import com.ibkr.events.TickPriceEvent;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
  protected static final LocalTime LAST_TRADE_CUTOFF = LocalTime.of(14, 0);
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
   * Dispatches incoming bar ticks to the strategy's executor.
   *
   * @param event published market data event
   */
  @EventListener
  private void handleBarTickEvent(BarTickEvent event) {
    safeExecute(() -> onRealTimeBarTickEvent(event.realTimeBarTick()));
  }

  /**
   * Dispatches incoming tick price to the strategy's executor.
   *
   * @param event published market data event
   */
  @EventListener
  private void handleTickPriceEvent(TickPriceEvent event) {
    safeExecute(() -> onTickPriceEvent(event.tickPrice()));
  }

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
