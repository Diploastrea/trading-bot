package com.ibkr.statics;

import static com.ibkr.statics.constants.CommonConstants.LAST_TRADE_CUTOFF;
import static com.ibkr.statics.constants.CommonConstants.NYSE_OPEN;
import static com.ibkr.statics.constants.CommonConstants.NY_TIME_ZONE;

import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;
import java.time.Instant;
import java.time.LocalTime;
import lombok.experimental.UtilityClass;

/**
 * Utility class to determine if a given {@link RealTimeBarTick} falls within a specific strategy
 * timing window (e.g. opening range or detecting breakout).
 */
@UtilityClass
public class DateTimeUtil {

  /**
   * Checks if the tick time is past the daily cutoff for opening new positions.
   *
   * @param tick object containing timestamp
   * @return {@code true} if the tick time is after {@code LAST_TRADE_CUTOFF}
   */
  public static boolean isAfterTradingWindow(RealTimeBarTick tick) {
    LocalTime currentTime = computeCurrentTime(tick);
    return currentTime.isAfter(LAST_TRADE_CUTOFF);
  }

  /**
   * Checks if the tick time is after the opening range window and before last trade cutoff, i.e.
   * tick time is within active trading window.
   *
   * @param tick               object containing timestamp
   * @param openingRangeCutoff end of opening range window
   * @return {@code true} if the tick time is within active trading window
   */
  public static boolean isAfterOpeningRangeWindow(RealTimeBarTick tick,
      LocalTime openingRangeCutoff) {
    LocalTime currentTime = computeCurrentTime(tick);
    return currentTime.isAfter(openingRangeCutoff) && currentTime.isBefore(LAST_TRADE_CUTOFF);
  }

  /**
   * Checks if the tick time is within the opening range window.
   *
   * @param tick               object containing timestamp
   * @param openingRangeCutoff end of opening range window
   * @return {@code true} if the tick time is within the opening range window
   */
  public static boolean isWithinOpeningRangeWindow(RealTimeBarTick tick,
      LocalTime openingRangeCutoff) {
    LocalTime currentTime = computeCurrentTime(tick);
    return currentTime.isAfter(NYSE_OPEN) && currentTime.isBefore(openingRangeCutoff);
  }

  /**
   * Converts epoch timestamp into New York local time.
   *
   * @param tick object containing timestamp
   * @return {@link LocalTime} in {@code America/New_York} timezone
   */
  public static LocalTime computeCurrentTime(RealTimeBarTick tick) {
    return Instant.ofEpochSecond(tick.getTime())
        .atZone(NY_TIME_ZONE)
        .toLocalTime();
  }
}
