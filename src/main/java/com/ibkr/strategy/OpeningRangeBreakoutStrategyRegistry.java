package com.ibkr.strategy;

import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Registry for instantiating multiple Opening Range Breakout strategy beans, each with distinct
 * opening range window, allowing simultaneous strategy testing.
 */
@Component
@RequiredArgsConstructor
public class OpeningRangeBreakoutStrategyRegistry {

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 5 mins opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy SPY5MinORBStrategy() {
    return new OpeningRangeBreakoutStrategy("5-min-orb", LocalTime.of(9, 35));
  }

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 15 mins opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy SPY15MinORBStrategy() {
    return new OpeningRangeBreakoutStrategy("15-min-orb", LocalTime.of(9, 45));
  }

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 30 mins opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy SPY30MinORBStrategy() {
    return new OpeningRangeBreakoutStrategy("30-min-orb", LocalTime.of(10, 0));
  }

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 4 hours opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy SPY4HourORBStrategy() {
    return new OpeningRangeBreakoutStrategy("4-hour-orb", LocalTime.of(13, 30));
  }
}
