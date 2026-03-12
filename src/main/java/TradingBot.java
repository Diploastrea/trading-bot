import com.ib.controller.AccountSummaryTag;
import com.ibkr.client.IBClient;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for the trading bot application.
 */
@Slf4j
public class TradingBot {

  void main() throws Exception {
    IBClient ib = new IBClient();
    ib.connect();

    log.info("calling acc summary");
    ib.client()
        .reqAccountSummary(ib.getNextValidId(), "All", AccountSummaryTag.NetLiquidation.name());
  }
}
