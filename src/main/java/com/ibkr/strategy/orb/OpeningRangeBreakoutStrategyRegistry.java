package com.ibkr.strategy.orb;

import static com.ibkr.strategy.orb.ORBStrategyName.FIFTEEN_MIN_ORB;
import static com.ibkr.strategy.orb.ORBStrategyName.FIVE_MIN_ORB;
import static com.ibkr.strategy.orb.ORBStrategyName.FOUR_HOUR_ORB;
import static com.ibkr.strategy.orb.ORBStrategyName.THIRTY_MIN_ORB;

import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Registry for instantiating multiple Opening Range Breakout strategy beans, each with distinct
 * opening range window, allowing simultaneous strategy testing.
 */
@Component
@RequiredArgsConstructor
public class OpeningRangeBreakoutStrategyRegistry {

  private final ApplicationEventPublisher publisher;

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 5 mins opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy SPY5MinORBStrategy() {
    return new OpeningRangeBreakoutStrategy(FIVE_MIN_ORB.getName(), LocalTime.of(9, 35), 1,
        publisher);
  }

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 15 mins opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy SPY15MinORBStrategy() {
    return new OpeningRangeBreakoutStrategy(FIFTEEN_MIN_ORB.getName(), LocalTime.of(9, 45), 3,
        publisher);
  }

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 30 mins opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy SPY30MinORBStrategy() {
    return new OpeningRangeBreakoutStrategy(THIRTY_MIN_ORB.getName(), LocalTime.of(10, 0), 5,
        publisher);
  }

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 4 hours opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy SPY4HourORBStrategy() {
    return new OpeningRangeBreakoutStrategy(FOUR_HOUR_ORB.getName(), LocalTime.of(13, 30), 15,
        publisher);
  }
}
