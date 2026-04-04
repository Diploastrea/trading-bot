package com.ibkr.strategy.orb;

import com.ibkr.strategy.BarTickAggregator;
import java.time.LocalTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enums of configurations for the ORB strategy.
 *
 * <p>Each enum constant represents a specific configuration with strategy name for logging and
 * recording purposes, predefined opening range window and bar tick aggregator for breakout
 * detection.
 */
@Getter
@RequiredArgsConstructor
public enum ORBConfig {

  FIVE_MIN_ORB("5-min-orb", LocalTime.of(9, 35), new BarTickAggregator(1)),
  FIFTEEN_MIN_ORB("15-min-orb", LocalTime.of(9, 45), new BarTickAggregator(1)),
  THIRTY_MIN_ORB("30-min-orb", LocalTime.of(10, 0), new BarTickAggregator(3)),
  FOUR_HOUR_ORB("4-hour-orb", LocalTime.of(13, 30), new BarTickAggregator(5));

  private final String name;
  private final LocalTime cutoffTime;
  private final BarTickAggregator aggregator;
}
