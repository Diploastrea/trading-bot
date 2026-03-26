package com.ibkr.strategy;

import com.ibkr.events.BarTickEvent;
import com.ibkr.events.TickPriceEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.event.EventListener;

/**
 * Common parent class for all strategies providing thread isolation and event ordering.
 *
 * <p>Each instance maintains a private, single-threaded virtual executor to process market data
 * ticks sequentially without blocking other strategies or the market data feed.
 */
public abstract class AbstractStrategy implements Strategy {

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
    executor.execute(() -> onBarTickEvent(event.realTimeBarTick()));
  }

  /**
   * Dispatches incoming tick price to the strategy's executor.
   *
   * @param event published market data event
   */
  @EventListener
  private void handleTickPriceEvent(TickPriceEvent event) {
    executor.execute(() -> onTickPriceEvent(event.tickPrice()));
  }
}
