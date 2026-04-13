package com.ibkr.strategy.orb;

import static com.ibkr.strategy.orb.OrbConfig.FIFTEEN_MIN_ORB;
import static com.ibkr.strategy.orb.OrbConfig.FIVE_MIN_ORB;
import static com.ibkr.strategy.orb.OrbConfig.SIXTY_MIN_ORB;
import static com.ibkr.strategy.orb.OrbConfig.THIRTY_MIN_ORB;

import com.ibkr.strategy.indicators.Vwap;
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
  public OpeningRangeBreakoutStrategy fiveMinOrbStrategy() {
    return new OpeningRangeBreakoutStrategy(FIVE_MIN_ORB, publisher, new Vwap());
  }

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 15 mins opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy fifteenMinOrbStrategy() {
    return new OpeningRangeBreakoutStrategy(FIFTEEN_MIN_ORB, publisher, new Vwap());
  }

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 30 mins opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy thirtyMinOrbStrategy() {
    return new OpeningRangeBreakoutStrategy(THIRTY_MIN_ORB, publisher, new Vwap());
  }

  /**
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 60 mins opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy sixtyMinOrbStrategy() {
    return new OpeningRangeBreakoutStrategy(SIXTY_MIN_ORB, publisher, new Vwap());
  }
}
