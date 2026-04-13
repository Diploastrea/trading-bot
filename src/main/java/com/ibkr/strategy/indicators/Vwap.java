package com.ibkr.strategy.indicators;

import static com.ibkr.statics.DateTimeUtil.computeCurrentTime;
import static com.ibkr.statics.constants.CommonConstants.NYSE_OPEN;

import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;
import java.time.LocalTime;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * This class tracks cumulative price-volume and total volume starting from the {@code NYSE_OPEN}
 * time.
 *
 * <p>These values are used to calculate the Volume Weighted Average Price (VWAP) to determine
 * overall market trend. Price above VWAP signals bullish trend, whereas price below VWAP signals
 * bearish trend.
 */
public class Vwap {

  private final DoubleAccumulator CUMULATIVE_PV = new DoubleAccumulator(Double::sum, 0.0);
  private final DoubleAccumulator CUMULATIVE_VOL = new DoubleAccumulator(Double::sum, 0.0);

  /**
   * Updates cumulative price-volume and cumulative volume needed to calculate VWAP.
   *
   * @param tick object containing OHLC data
   */
  public void update(RealTimeBarTick tick) {
    LocalTime currentTime = computeCurrentTime(tick);
    if (currentTime.isAfter(NYSE_OPEN)) {
      double volume = Double.parseDouble(tick.getVolume());
      double price = Double.parseDouble(tick.getWAP());
      CUMULATIVE_PV.accumulate(volume * price);
      CUMULATIVE_VOL.accumulate(volume);
    }
  }

  /**
   * Calculates and returns VWAP value.
   *
   * @return VWAP value
   */
  public double compute() {
    double volume = CUMULATIVE_VOL.get();
    return (volume > 0) ? (CUMULATIVE_PV.get() / volume) : 0.0;
  }
}
