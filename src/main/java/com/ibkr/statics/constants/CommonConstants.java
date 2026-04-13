package com.ibkr.statics.constants;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Common constants for market timing and date formatting.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstants {

  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
  public static final ZoneId NY_TIME_ZONE = ZoneId.of("America/New_York");
  public static final LocalTime NYSE_OPEN = LocalTime.of(9, 30);
  public static final LocalTime LAST_TRADE_CUTOFF = LocalTime.of(12, 0);
}
