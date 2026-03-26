package com.ibkr.strategy;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;
import java.util.Objects;
import java.util.Optional;

/**
 * Due to limitation on TWS API side, which offers only 5-second real time bar ticks, this class
 * aggregates them into larger bars (e.g. 5-minute bar, 15-minute bar).
 */
public class BarTickAggregator {

  private final int barSize;
  private int barTickCounter;
  private RealTimeBarTick.Builder currentBarTick;

  /**
   * Initialises an aggregator for a specific bar duration.
   *
   * @param barSize target bar duration in minutes
   */
  public BarTickAggregator(int barSize) {
    this.barSize = barSize;
    this.barTickCounter = calculateBarTick();
  }

  /**
   * Processes a single 5-second bar tick by updating the current aggregated bar tick.
   *
   * @param tick the latest 5-second bar tick from TWS API
   * @return {@link Optional} containing the aggregated {@link RealTimeBarTick} if the window is
   * complete, otherwise {@link Optional#empty()}
   */
  public Optional<RealTimeBarTick> handleBarTick(RealTimeBarTick tick) {
    if (Objects.isNull(currentBarTick)) {
      currentBarTick = RealTimeBarTick.newBuilder(tick);
    }

    currentBarTick.setHigh(max(currentBarTick.getHigh(), tick.getHigh()));
    currentBarTick.setLow(min(currentBarTick.getLow(), tick.getLow()));

    if (--barTickCounter == 0) {
      currentBarTick.setClose(tick.getClose());
      RealTimeBarTick completedBarTick = currentBarTick.build();
      currentBarTick = null;
      barTickCounter = calculateBarTick();

      return Optional.of(completedBarTick);
    }

    return Optional.empty();
  }

  /**
   * Calculates the number of 5-second bar ticks required for the target bar size.
   *
   * @return tick count for each aggregated bar
   */
  private int calculateBarTick() {
    return (barSize * 60) / 5;
  }
}
