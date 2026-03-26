package com.ibkr.strategy.orb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enums of the standard time-based configurations for the ORB strategy.
 *
 * <p>Each constant represents a specific duration used to establish the initial opening price range
 * before breakout detection phase begins.
 */
@RequiredArgsConstructor
public enum ORBStrategyName {

  FIVE_MIN_ORB("5-min-orb"),
  FIFTEEN_MIN_ORB("15-min-orb"),
  THIRTY_MIN_ORB("30-min-orb"),
  FOUR_HOUR_ORB("4-hour-orb");

  @Getter
  private final String name;
}
