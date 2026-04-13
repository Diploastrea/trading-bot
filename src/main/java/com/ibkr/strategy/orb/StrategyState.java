package com.ibkr.strategy.orb;

/**
 * Enums of different ORB strategy phases to track the progression of a trade from the initial
 * opening range discovery period through pullback analysis to execution.
 */
public enum StrategyState {

  OPENING_RANGE,
  BREAKOUT,
  PULLBACK,
  RETRACEMENT
}
