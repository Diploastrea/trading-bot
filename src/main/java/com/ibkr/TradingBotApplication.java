package com.ibkr;

import com.ib.client.Contract;
import com.ib.client.Types.SecType;
import com.ibkr.client.IBClient;
import com.ibkr.service.EWrapperImpl;
import com.ibkr.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot trading bot application.
 */
@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class TradingBotApplication implements ApplicationRunner {

  private final IBClient ib;
  private final EWrapperImpl wrapper;
  private final MarketDataService marketDataService;

  static void main(String[] args) {
    SpringApplication.run(TradingBotApplication.class, args);
  }

  @Override
  public void run(@NonNull ApplicationArguments args) throws Exception {
    ib.connect();
    log.info("Start");

//    ib.client().reqMarketDataType(1);
    ib.client().
        reqAccountSummary(9001, "All",
            "AccountType,NetLiquidation,TotalCashValue,SettledCash,AccruedCash,BuyingPower,EquityWithLoanValue,PreviousEquityWithLoanValue,GrossPositionValue,ReqTEquity,ReqTMargin,SMA,InitMarginReq,MaintMarginReq,AvailableFunds,ExcessLiquidity,Cushion,FullInitMarginReq,FullMaintMarginReq,FullAvailableFunds,FullExcessLiquidity,LookAheadNextChange,LookAheadInitMarginReq ,LookAheadMaintMarginReq,LookAheadAvailableFunds,LookAheadExcessLiquidity,HighestSeverity,DayTradesRemaining,Leverage");
    var c = new Contract();
    c.symbol("SPY");
    c.secType(SecType.STK);
    c.exchange("BYX");
    c.currency("USD");
    c.conid(756733);
    ib.client().reqMktData(wrapper.nextRequestId(), c, "", false, false, null);

    Thread.currentThread().join();
  }
}
