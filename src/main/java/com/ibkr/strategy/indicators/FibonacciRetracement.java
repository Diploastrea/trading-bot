package com.ibkr.strategy.indicators;

/**
 * Represents Fibonacci retracement levels for a given price range, used to identify potential
 * support and resistance levels.
 *
 * <p>This record calculates key retracement levels (38.2%, 50.0%, and 61.8%) based on the market
 * direction. For bullish trend, levels are calculated downward from the high and vice versa for
 * bearish trend.
 *
 * @param swingHigh      the highest price point of the swing
 * @param swingLow       the lowest price point of the swing
 * @param isBullishTrend {@code true} if the trend is bullish, else {@code false}
 */
public record FibonacciRetracement(double swingHigh, double swingLow, boolean isBullishTrend) {

  /**
   * Calculates and returns the 38.2% retracement level.
   *
   * @return price at the 38.2% retracement level
   */
  public double fib382() {
    return isBullishTrend ? swingHigh - (range() * 0.382) : swingLow + (range() * 0.382);
  }

  /**
   * Calculates and returns the 50% retracement level.
   *
   * @return price at the 50% retracement level
   */
  public double fib500() {
    return isBullishTrend ? swingHigh - (range() * 0.5) : swingLow + (range() * 0.5);
  }

  /**
   * Calculates and returns the 61.8% retracement level.
   *
   * @return price at the 61.8% retracement level
   */
  public double fib618() {
    return isBullishTrend ? swingHigh - (range() * 0.618) : swingLow + (range() * 0.618);
  }

  /**
   * Calculates and returns the price range of the expansion.
   *
   * @return price range between swing high and swing low
   */
  private double range() {
    return swingHigh - swingLow;
  }
}
