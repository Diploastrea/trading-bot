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
public enum OrbConfig {

  FIFTEEN_MIN_ORB("spy-15m-orb", LocalTime.of(9, 45), new BarTickAggregator(1));

  private final String name;
  private final LocalTime cutoffTime;
  private final BarTickAggregator aggregator;
}
