import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.controller.AccountSummaryTag;
import com.ibkr.client.EWrapperImpl;
import com.ibkr.client.IBClient;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for the trading bot application.
 */
@Slf4j
public class TradingBot {

  void main() {
    EWrapperImpl wrapper = new EWrapperImpl();
    EJavaSignal signal = new EJavaSignal();
    EClientSocket client = new EClientSocket(wrapper, signal);
    IBClient ib = new IBClient(signal, client);
    ib.connect();

    ib.client()
        .reqAccountSummary(wrapper.requestId(), "All", AccountSummaryTag.NetLiquidation.name());
  }
}