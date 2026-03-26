package com.ibkr.events;

import com.ib.client.Contract;

/**
 * Event published to request the current spot price for a contract.
 *
 * @param requestId a unique ID used to track and correlate the market data request
 * @param contract  details for the financial instrument
 */
public record RequestMarketDataSnapshotEvent(int requestId, Contract contract) {

}
