package com.ibkr.strategy;

import com.ib.client.Contract;
import com.ib.client.Types.SecType;
import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.DoubleAccumulator;
import lombok.extern.slf4j.Slf4j;

/**
 * Trading strategy that tracks the price range during the initial market opening period and
 * capitalises on early market momentum.
 */
@Slf4j
public class OpeningRangeBreakoutStrategy extends AbstractStrategy {

  private static final ZoneId NY_ZONE_ID = ZoneId.of("America/New_York");
  private static final LocalTime NYSE_OPEN_TIME = LocalTime.of(9, 30);
  private final DoubleAccumulator PRICE_HIGH = new DoubleAccumulator(Math::max,
      Double.NEGATIVE_INFINITY);
  private final DoubleAccumulator PRICE_LOW = new DoubleAccumulator(Math::min,
      Double.POSITIVE_INFINITY);
  private final LocalTime openingRangeCutoff;

  public OpeningRangeBreakoutStrategy(String strategyName, LocalTime openingRangeCutoff) {
    super(strategyName);
    this.openingRangeCutoff = openingRangeCutoff;
  }

  @Override
  public void onBarTickEvent(RealTimeBarTick tick) {
    LocalTime currentTime = Instant.ofEpochSecond(tick.getTime()).atZone(NY_ZONE_ID)
        .toLocalTime();
    if (currentTime.isAfter(NYSE_OPEN_TIME) && currentTime.isBefore(openingRangeCutoff)) {
      updateOpeningRange(tick);
    }
  }

  @Override
  public Contract getContract() {
    Contract contract = new Contract();
    contract.symbol("SPY");
    contract.secType(SecType.STK);
    contract.exchange("SMART");
    contract.currency("USD");
    contract.conid(756733);
    return contract;
  }

  /**
   * Updates the high and low price based on the current tick.
   */
  private void updateOpeningRange(RealTimeBarTick tick) {
    log.debug("Updating opening range, high: {}, low: {}", PRICE_HIGH, PRICE_LOW);
    PRICE_HIGH.accumulate(tick.getHigh());
    PRICE_LOW.accumulate(tick.getLow());
  }
}
