package com.ibkr.strategy.orb;

import static com.ibkr.strategy.orb.OrbConfig.FIFTEEN_MIN_ORB;

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
   * Creates and returns {@link OpeningRangeBreakoutStrategy} with 15 mins opening range window.
   *
   * @return an instance of {@link OpeningRangeBreakoutStrategy}
   */
  @Bean
  public OpeningRangeBreakoutStrategy fifteenMinOrbStrategy() {
    return new OpeningRangeBreakoutStrategy(FIFTEEN_MIN_ORB, publisher);
  }
}
