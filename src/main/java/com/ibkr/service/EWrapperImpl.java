package com.ibkr.client;

import com.ib.client.Bar;
import com.ib.client.CommissionAndFeesReport;
import com.ib.client.Contract;
import com.ib.client.ContractDescription;
import com.ib.client.ContractDetails;
import com.ib.client.Decimal;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.DepthMktDataDescription;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.FamilyCode;
import com.ib.client.HistogramEntry;
import com.ib.client.HistoricalSession;
import com.ib.client.HistoricalTick;
import com.ib.client.HistoricalTickBidAsk;
import com.ib.client.HistoricalTickLast;
import com.ib.client.NewsProvider;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.PriceIncrement;
import com.ib.client.SoftDollarTier;
import com.ib.client.TickAttrib;
import com.ib.client.TickAttribBidAsk;
import com.ib.client.TickAttribLast;
import com.ib.client.protobuf.AccountDataEndProto.AccountDataEnd;
import com.ib.client.protobuf.AccountSummaryEndProto.AccountSummaryEnd;
import com.ib.client.protobuf.AccountSummaryProto.AccountSummary;
import com.ib.client.protobuf.AccountUpdateMultiEndProto.AccountUpdateMultiEnd;
import com.ib.client.protobuf.AccountUpdateMultiProto.AccountUpdateMulti;
import com.ib.client.protobuf.AccountUpdateTimeProto.AccountUpdateTime;
import com.ib.client.protobuf.AccountValueProto.AccountValue;
import com.ib.client.protobuf.CommissionAndFeesReportProto;
import com.ib.client.protobuf.CompletedOrderProto.CompletedOrder;
import com.ib.client.protobuf.CompletedOrdersEndProto.CompletedOrdersEnd;
import com.ib.client.protobuf.ConfigResponseProto.ConfigResponse;
import com.ib.client.protobuf.ContractDataEndProto.ContractDataEnd;
import com.ib.client.protobuf.ContractDataProto.ContractData;
import com.ib.client.protobuf.CurrentTimeInMillisProto.CurrentTimeInMillis;
import com.ib.client.protobuf.CurrentTimeProto.CurrentTime;
import com.ib.client.protobuf.DisplayGroupListProto.DisplayGroupList;
import com.ib.client.protobuf.DisplayGroupUpdatedProto.DisplayGroupUpdated;
import com.ib.client.protobuf.ErrorMessageProto.ErrorMessage;
import com.ib.client.protobuf.ExecutionDetailsEndProto.ExecutionDetailsEnd;
import com.ib.client.protobuf.ExecutionDetailsProto.ExecutionDetails;
import com.ib.client.protobuf.FamilyCodesProto.FamilyCodes;
import com.ib.client.protobuf.FundamentalsDataProto.FundamentalsData;
import com.ib.client.protobuf.HeadTimestampProto.HeadTimestamp;
import com.ib.client.protobuf.HistogramDataProto.HistogramData;
import com.ib.client.protobuf.HistoricalDataEndProto.HistoricalDataEnd;
import com.ib.client.protobuf.HistoricalDataProto.HistoricalData;
import com.ib.client.protobuf.HistoricalDataUpdateProto.HistoricalDataUpdate;
import com.ib.client.protobuf.HistoricalNewsEndProto.HistoricalNewsEnd;
import com.ib.client.protobuf.HistoricalNewsProto.HistoricalNews;
import com.ib.client.protobuf.HistoricalScheduleProto.HistoricalSchedule;
import com.ib.client.protobuf.HistoricalTicksBidAskProto.HistoricalTicksBidAsk;
import com.ib.client.protobuf.HistoricalTicksLastProto.HistoricalTicksLast;
import com.ib.client.protobuf.HistoricalTicksProto.HistoricalTicks;
import com.ib.client.protobuf.ManagedAccountsProto.ManagedAccounts;
import com.ib.client.protobuf.MarketDataTypeProto.MarketDataType;
import com.ib.client.protobuf.MarketDepthExchangesProto.MarketDepthExchanges;
import com.ib.client.protobuf.MarketDepthL2Proto.MarketDepthL2;
import com.ib.client.protobuf.MarketDepthProto.MarketDepth;
import com.ib.client.protobuf.MarketRuleProto.MarketRule;
import com.ib.client.protobuf.NewsArticleProto.NewsArticle;
import com.ib.client.protobuf.NewsBulletinProto.NewsBulletin;
import com.ib.client.protobuf.NewsProvidersProto.NewsProviders;
import com.ib.client.protobuf.NextValidIdProto.NextValidId;
import com.ib.client.protobuf.OpenOrderProto.OpenOrder;
import com.ib.client.protobuf.OpenOrdersEndProto.OpenOrdersEnd;
import com.ib.client.protobuf.OrderBoundProto.OrderBound;
import com.ib.client.protobuf.OrderStatusProto.OrderStatus;
import com.ib.client.protobuf.PnLProto.PnL;
import com.ib.client.protobuf.PnLSingleProto.PnLSingle;
import com.ib.client.protobuf.PortfolioValueProto.PortfolioValue;
import com.ib.client.protobuf.PositionEndProto.PositionEnd;
import com.ib.client.protobuf.PositionMultiEndProto.PositionMultiEnd;
import com.ib.client.protobuf.PositionMultiProto.PositionMulti;
import com.ib.client.protobuf.PositionProto.Position;
import com.ib.client.protobuf.RealTimeBarTickProto.RealTimeBarTick;
import com.ib.client.protobuf.ReceiveFAProto.ReceiveFA;
import com.ib.client.protobuf.ReplaceFAEndProto.ReplaceFAEnd;
import com.ib.client.protobuf.RerouteMarketDataRequestProto.RerouteMarketDataRequest;
import com.ib.client.protobuf.RerouteMarketDepthRequestProto.RerouteMarketDepthRequest;
import com.ib.client.protobuf.ScannerDataProto.ScannerData;
import com.ib.client.protobuf.ScannerParametersProto.ScannerParameters;
import com.ib.client.protobuf.SecDefOptParameterEndProto.SecDefOptParameterEnd;
import com.ib.client.protobuf.SecDefOptParameterProto.SecDefOptParameter;
import com.ib.client.protobuf.SmartComponentsProto.SmartComponents;
import com.ib.client.protobuf.SoftDollarTiersProto.SoftDollarTiers;
import com.ib.client.protobuf.SymbolSamplesProto.SymbolSamples;
import com.ib.client.protobuf.TickByTickDataProto.TickByTickData;
import com.ib.client.protobuf.TickGenericProto.TickGeneric;
import com.ib.client.protobuf.TickNewsProto.TickNews;
import com.ib.client.protobuf.TickOptionComputationProto.TickOptionComputation;
import com.ib.client.protobuf.TickPriceProto.TickPrice;
import com.ib.client.protobuf.TickReqParamsProto.TickReqParams;
import com.ib.client.protobuf.TickSizeProto.TickSize;
import com.ib.client.protobuf.TickSnapshotEndProto.TickSnapshotEnd;
import com.ib.client.protobuf.TickStringProto.TickString;
import com.ib.client.protobuf.UpdateConfigResponseProto.UpdateConfigResponse;
import com.ib.client.protobuf.UserInfoProto.UserInfo;
import com.ib.client.protobuf.VerifyCompletedProto.VerifyCompleted;
import com.ib.client.protobuf.VerifyMessageApiProto.VerifyMessageApi;
import com.ib.client.protobuf.WshEventDataProto.WshEventData;
import com.ib.client.protobuf.WshMetaDataProto.WshMetaData;
import com.ibkr.client.IBClient;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is a primary callback handler for all messages received from TWS API.
 *
 * <p>TWS API uses event-driven architecture where responses to requests are delivered through
 * methods defined in {@link EWrapper}. These methods are invoked by the {@link EReader} thread and
 * blocking operations should be offloaded to a separate executor to avoid stalling the socket.
 *
 * <p>This class also stores {@code nextRequestId} given by TWS API, which is used to ensure each
 * request to the API is unique.
 *
 * <p>Note: Most callback methods are intentionally left unimplemented as they are not needed.
 */
@Slf4j
@RequiredArgsConstructor
public class EWrapperImpl implements EWrapper {

  private final AtomicInteger nextRequestId = new AtomicInteger();
  private final ExecutorService reconnectExecutor = Executors.newSingleThreadExecutor(
      Thread.ofPlatform().name("main").factory());
  private final IBClient client;

  public int nextRequestId() {
    return nextRequestId.getAndIncrement();
  }

  @Override
  public void tickPrice(int i, int i1, double v, TickAttrib tickAttrib) {

  }

  @Override
  public void tickSize(int i, int i1, Decimal decimal) {

  }

  @Override
  public void tickOptionComputation(int i, int i1, int i2, double v, double v1, double v2,
      double v3, double v4, double v5, double v6, double v7) {

  }

  @Override
  public void tickGeneric(int i, int i1, double v) {

  }

  @Override
  public void tickString(int i, int i1, String s) {

  }

  @Override
  public void tickEFP(int i, int i1, double v, String s, double v1, int i2, String s1, double v2,
      double v3) {

  }

  @Override
  public void orderStatus(int i, String s, Decimal decimal, Decimal decimal1, double v, long l,
      int i1, double v1, int i2, String s1, double v2) {

  }

  @Override
  public void openOrder(int i, Contract contract, Order order, OrderState orderState) {

  }

  @Override
  public void openOrderEnd() {

  }

  @Override
  public void updateAccountValue(String s, String s1, String s2, String s3) {

  }

  @Override
  public void updatePortfolio(Contract contract, Decimal decimal, double v, double v1, double v2,
      double v3, double v4, String s) {

  }

  @Override
  public void updateAccountTime(String s) {

  }

  @Override
  public void accountDownloadEnd(String s) {

  }

  @Override
  public void nextValidId(int requestId) {

  }

  @Override
  public void contractDetails(int i, ContractDetails contractDetails) {

  }

  @Override
  public void bondContractDetails(int i, ContractDetails contractDetails) {

  }

  @Override
  public void contractDetailsEnd(int i) {

  }

  @Override
  public void execDetails(int i, Contract contract, Execution execution) {

  }

  @Override
  public void execDetailsEnd(int i) {

  }

  @Override
  public void updateMktDepth(int i, int i1, int i2, int i3, double v, Decimal decimal) {

  }

  @Override
  public void updateMktDepthL2(int i, int i1, String s, int i2, int i3, double v, Decimal decimal,
      boolean b) {

  }

  @Override
  public void updateNewsBulletin(int i, int i1, String s, String s1) {

  }

  @Override
  public void managedAccounts(String accounts) {

  }

  @Override
  public void receiveFA(int i, String s) {

  }

  @Override
  public void historicalData(int i, Bar bar) {

  }

  @Override
  public void scannerParameters(String s) {

  }

  @Override
  public void scannerData(int i, int i1, ContractDetails contractDetails, String s, String s1,
      String s2, String s3) {

  }

  @Override
  public void scannerDataEnd(int i) {

  }

  @Override
  public void realtimeBar(int i, long l, double v, double v1, double v2, double v3, Decimal decimal,
      Decimal decimal1, int i1) {

  }

  @Override
  public void currentTime(long l) {

  }

  @Override
  public void fundamentalData(int i, String s) {

  }

  @Override
  public void deltaNeutralValidation(int i, DeltaNeutralContract deltaNeutralContract) {

  }

  @Override
  public void tickSnapshotEnd(int i) {

  }

  @Override
  public void marketDataType(int i, int i1) {

  }

  @Override
  public void commissionAndFeesReport(CommissionAndFeesReport commissionAndFeesReport) {

  }

  @Override
  public void position(String s, Contract contract, Decimal decimal, double v) {

  }

  @Override
  public void positionEnd() {

  }

  @Override
  public void accountSummary(int requestId, String accountId, String tag, String value,
      String currency) {

  }

  @Override
  public void accountSummaryEnd(int requestId) {

  }

  @Override
  public void verifyMessageAPI(String s) {

  }

  @Override
  public void verifyCompleted(boolean b, String s) {

  }

  @Override
  public void verifyAndAuthMessageAPI(String s, String s1) {

  }

  @Override
  public void verifyAndAuthCompleted(boolean b, String s) {

  }

  @Override
  public void displayGroupList(int i, String s) {

  }

  @Override
  public void displayGroupUpdated(int i, String s) {

  }

  /**
   * Callback invoked when TWS API encounters an exception.
   *
   * @param e exception
   */
  @Override
  public void error(Exception e) {
    log.error("IB exception: {}", e.getMessage(), e);
  }

  /**
   * Callback invoked when TSW API send a general error or informational message.
   *
   * @param errorMessage error message
   */
  @Override
  public void error(String errorMessage) {
    log.error("IB error: {}", errorMessage);
  }

  @Override
  public void error(int errorId, long timestamp, int errorCode, String errorMsg, String s1) {

  }

  /**
   * Callback invoked when a connection fails for any reason.
   */
  @Override
  public void connectionClosed() {
    log.error("TWS API connection closed, attempting to reconnect...");
    reconnectExecutor.execute(client::connect);
  }

  @Override
  public void connectAck() {

  }

  @Override
  public void positionMulti(int i, String s, String s1, Contract contract, Decimal decimal,
      double v) {

  }

  @Override
  public void positionMultiEnd(int i) {

  }

  @Override
  public void accountUpdateMulti(int i, String s, String s1, String s2, String s3, String s4) {

  }

  @Override
  public void accountUpdateMultiEnd(int i) {

  }

  @Override
  public void securityDefinitionOptionalParameter(int i, String s, int i1, String s1, String s2,
      Set<String> set, Set<Double> set1) {

  }

  @Override
  public void securityDefinitionOptionalParameterEnd(int i) {

  }

  @Override
  public void softDollarTiers(int i, SoftDollarTier[] softDollarTiers) {

  }

  @Override
  public void familyCodes(FamilyCode[] familyCodes) {

  }

  @Override
  public void symbolSamples(int i, ContractDescription[] contractDescriptions) {

  }

  @Override
  public void historicalDataEnd(int i, String s, String s1) {

  }

  @Override
  public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {

  }

  @Override
  public void tickNews(int i, long l, String s, String s1, String s2, String s3) {

  }

  @Override
  public void smartComponents(int i, Map<Integer, Entry<String, Character>> map) {

  }

  @Override
  public void tickReqParams(int i, double v, String s, int i1) {

  }

  @Override
  public void newsProviders(NewsProvider[] newsProviders) {

  }

  @Override
  public void newsArticle(int i, int i1, String s) {

  }

  @Override
  public void historicalNews(int i, String s, String s1, String s2, String s3) {

  }

  @Override
  public void historicalNewsEnd(int i, boolean b) {

  }

  @Override
  public void headTimestamp(int i, String s) {

  }

  @Override
  public void histogramData(int i, List<HistogramEntry> list) {

  }

  @Override
  public void historicalDataUpdate(int i, Bar bar) {

  }

  @Override
  public void rerouteMktDataReq(int i, int i1, String s) {

  }

  @Override
  public void rerouteMktDepthReq(int i, int i1, String s) {

  }

  @Override
  public void marketRule(int i, PriceIncrement[] priceIncrements) {

  }

  @Override
  public void pnl(int i, double v, double v1, double v2) {

  }

  @Override
  public void pnlSingle(int i, Decimal decimal, double v, double v1, double v2, double v3) {

  }

  @Override
  public void historicalTicks(int i, List<HistoricalTick> list, boolean b) {

  }

  @Override
  public void historicalTicksBidAsk(int i, List<HistoricalTickBidAsk> list, boolean b) {

  }

  @Override
  public void historicalTicksLast(int i, List<HistoricalTickLast> list, boolean b) {

  }

  @Override
  public void tickByTickAllLast(int i, int i1, long l, double v, Decimal decimal,
      TickAttribLast tickAttribLast, String s, String s1) {

  }

  @Override
  public void tickByTickBidAsk(int i, long l, double v, double v1, Decimal decimal,
      Decimal decimal1, TickAttribBidAsk tickAttribBidAsk) {

  }

  @Override
  public void tickByTickMidPoint(int i, long l, double v) {

  }

  @Override
  public void orderBound(long l, int i, int i1) {

  }

  @Override
  public void completedOrder(Contract contract, Order order, OrderState orderState) {

  }

  @Override
  public void completedOrdersEnd() {

  }

  @Override
  public void replaceFAEnd(int i, String s) {

  }

  @Override
  public void wshMetaData(int i, String s) {

  }

  @Override
  public void wshEventData(int i, String s) {

  }

  @Override
  public void historicalSchedule(int i, String s, String s1, String s2,
      List<HistoricalSession> list) {

  }

  @Override
  public void userInfo(int i, String s) {

  }

  @Override
  public void currentTimeInMillis(long l) {

  }

  @Override
  public void orderStatusProtoBuf(OrderStatus orderStatus) {

  }

  @Override
  public void openOrderProtoBuf(OpenOrder openOrder) {

  }

  @Override
  public void openOrdersEndProtoBuf(OpenOrdersEnd openOrdersEnd) {

  }

  /**
   * Callback invoked when TWS encounters an error. However, if the error ID is -1, the message is
   * typically informational (e.g. connectivity status).
   *
   * @param errorMessage error message
   */
  @Override
  public void errorProtoBuf(ErrorMessage errorMessage) {
    if (errorMessage.getId() == -1) {
      log.info("Error protobuf: {}", errorMessage);
    } else {
      log.error("Error protobuf: {}", errorMessage);
    }
  }

  @Override
  public void execDetailsProtoBuf(ExecutionDetails executionDetails) {

  }

  @Override
  public void execDetailsEndProtoBuf(ExecutionDetailsEnd executionDetailsEnd) {

  }

  @Override
  public void completedOrderProtoBuf(CompletedOrder completedOrder) {

  }

  @Override
  public void completedOrdersEndProtoBuf(CompletedOrdersEnd completedOrdersEnd) {

  }

  @Override
  public void orderBoundProtoBuf(OrderBound orderBound) {

  }

  @Override
  public void contractDataProtoBuf(ContractData contractData) {

  }

  @Override
  public void bondContractDataProtoBuf(ContractData contractData) {

  }

  @Override
  public void contractDataEndProtoBuf(ContractDataEnd contractDataEnd) {

  }

  @Override
  public void tickPriceProtoBuf(TickPrice tickPrice) {

  }

  @Override
  public void tickSizeProtoBuf(TickSize tickSize) {

  }

  @Override
  public void tickOptionComputationProtoBuf(TickOptionComputation tickOptionComputation) {

  }

  @Override
  public void tickGenericProtoBuf(TickGeneric tickGeneric) {

  }

  @Override
  public void tickStringProtoBuf(TickString tickString) {

  }

  @Override
  public void tickSnapshotEndProtoBuf(TickSnapshotEnd tickSnapshotEnd) {

  }

  @Override
  public void updateMarketDepthProtoBuf(MarketDepth marketDepth) {

  }

  @Override
  public void updateMarketDepthL2ProtoBuf(MarketDepthL2 marketDepthL2) {

  }

  @Override
  public void marketDataTypeProtoBuf(MarketDataType marketDataType) {

  }

  @Override
  public void tickReqParamsProtoBuf(TickReqParams tickReqParams) {

  }

  @Override
  public void updateAccountValueProtoBuf(AccountValue accountValue) {

  }

  @Override
  public void updatePortfolioProtoBuf(PortfolioValue portfolioValue) {

  }

  @Override
  public void updateAccountTimeProtoBuf(AccountUpdateTime accountUpdateTime) {

  }

  @Override
  public void accountDataEndProtoBuf(AccountDataEnd accountDataEnd) {

  }

  /**
   * Callback invoked after initial connection to TWS API is completed with the accounts currently
   * in session.
   *
   * @param managedAccounts accounts in session
   */
  @Override
  public void managedAccountsProtoBuf(ManagedAccounts managedAccounts) {
    log.info("Accounts: {}", managedAccounts);
  }

  @Override
  public void positionProtoBuf(Position position) {

  }

  @Override
  public void positionEndProtoBuf(PositionEnd positionEnd) {

  }

  /**
   * Callback invoked when an account summary update is received.
   *
   * @param accountSummary protobuf message
   */
  @Override
  public void accountSummaryProtoBuf(AccountSummary accountSummary) {
    log.info("Request ID: {}. Account summary: {}", accountSummary.getReqId(), accountSummary);
  }

  /**
   * Callback invoked after all account summary data has been delivered.
   *
   * @param accountSummaryEnd protobuf message
   */
  @Override
  public void accountSummaryEndProtoBuf(AccountSummaryEnd accountSummaryEnd) {
    log.info("Request ID: {}. End of account summary.", accountSummaryEnd.getReqId());
  }

  @Override
  public void positionMultiProtoBuf(PositionMulti positionMulti) {

  }

  @Override
  public void positionMultiEndProtoBuf(PositionMultiEnd positionMultiEnd) {

  }

  @Override
  public void accountUpdateMultiProtoBuf(AccountUpdateMulti accountUpdateMulti) {

  }

  @Override
  public void accountUpdateMultiEndProtoBuf(AccountUpdateMultiEnd accountUpdateMultiEnd) {

  }

  @Override
  public void historicalDataProtoBuf(HistoricalData historicalData) {

  }

  @Override
  public void historicalDataUpdateProtoBuf(HistoricalDataUpdate historicalDataUpdate) {

  }

  @Override
  public void historicalDataEndProtoBuf(HistoricalDataEnd historicalDataEnd) {

  }

  @Override
  public void realTimeBarTickProtoBuf(RealTimeBarTick realTimeBarTick) {

  }

  @Override
  public void headTimestampProtoBuf(HeadTimestamp headTimestamp) {

  }

  @Override
  public void histogramDataProtoBuf(HistogramData histogramData) {

  }

  @Override
  public void historicalTicksProtoBuf(HistoricalTicks historicalTicks) {

  }

  @Override
  public void historicalTicksBidAskProtoBuf(HistoricalTicksBidAsk historicalTicksBidAsk) {

  }

  @Override
  public void historicalTicksLastProtoBuf(HistoricalTicksLast historicalTicksLast) {

  }

  @Override
  public void tickByTickDataProtoBuf(TickByTickData tickByTickData) {

  }

  @Override
  public void updateNewsBulletinProtoBuf(NewsBulletin newsBulletin) {

  }

  @Override
  public void newsArticleProtoBuf(NewsArticle newsArticle) {

  }

  @Override
  public void newsProvidersProtoBuf(NewsProviders newsProviders) {

  }

  @Override
  public void historicalNewsProtoBuf(HistoricalNews historicalNews) {

  }

  @Override
  public void historicalNewsEndProtoBuf(HistoricalNewsEnd historicalNewsEnd) {

  }

  @Override
  public void wshMetaDataProtoBuf(WshMetaData wshMetaData) {

  }

  @Override
  public void wshEventDataProtoBuf(WshEventData wshEventData) {

  }

  @Override
  public void tickNewsProtoBuf(TickNews tickNews) {

  }

  @Override
  public void scannerParametersProtoBuf(ScannerParameters scannerParameters) {

  }

  @Override
  public void scannerDataProtoBuf(ScannerData scannerData) {

  }

  @Override
  public void fundamentalsDataProtoBuf(FundamentalsData fundamentalsData) {

  }

  @Override
  public void pnlProtoBuf(PnL pnL) {

  }

  @Override
  public void pnlSingleProtoBuf(PnLSingle pnLSingle) {

  }

  @Override
  public void receiveFAProtoBuf(ReceiveFA receiveFA) {

  }

  @Override
  public void replaceFAEndProtoBuf(ReplaceFAEnd replaceFAEnd) {

  }

  @Override
  public void commissionAndFeesReportProtoBuf(
      CommissionAndFeesReportProto.CommissionAndFeesReport commissionAndFeesReport) {

  }

  @Override
  public void historicalScheduleProtoBuf(HistoricalSchedule historicalSchedule) {

  }

  @Override
  public void rerouteMarketDataRequestProtoBuf(RerouteMarketDataRequest rerouteMarketDataRequest) {

  }

  @Override
  public void rerouteMarketDepthRequestProtoBuf(
      RerouteMarketDepthRequest rerouteMarketDepthRequest) {

  }

  @Override
  public void secDefOptParameterProtoBuf(SecDefOptParameter secDefOptParameter) {

  }

  @Override
  public void secDefOptParameterEndProtoBuf(SecDefOptParameterEnd secDefOptParameterEnd) {

  }

  @Override
  public void softDollarTiersProtoBuf(SoftDollarTiers softDollarTiers) {

  }

  @Override
  public void familyCodesProtoBuf(FamilyCodes familyCodes) {

  }

  @Override
  public void symbolSamplesProtoBuf(SymbolSamples symbolSamples) {

  }

  @Override
  public void smartComponentsProtoBuf(SmartComponents smartComponents) {

  }

  @Override
  public void marketRuleProtoBuf(MarketRule marketRule) {

  }

  @Override
  public void userInfoProtoBuf(UserInfo userInfo) {

  }

  /**
   * Sets the next valid request ID and signals the {@link IBClient} that the initial connection and
   * handshake have been fully initialised and TWS API is ready to process requests. Any requests
   * sent prior to handshake completion may be dropped by TWS API.
   *
   * @param nextValidId next valid request ID
   */
  @Override
  public void nextValidIdProtoBuf(NextValidId nextValidId) {
    client.setNextValidId(nextValidId.getOrderId());
    client.connectionReady();
  }

  @Override
  public void currentTimeProtoBuf(CurrentTime currentTime) {

  }

  @Override
  public void currentTimeInMillisProtoBuf(CurrentTimeInMillis currentTimeInMillis) {

  }

  @Override
  public void verifyMessageApiProtoBuf(VerifyMessageApi verifyMessageApi) {

  }

  @Override
  public void verifyCompletedProtoBuf(VerifyCompleted verifyCompleted) {

  }

  @Override
  public void displayGroupListProtoBuf(DisplayGroupList displayGroupList) {

  }

  @Override
  public void displayGroupUpdatedProtoBuf(DisplayGroupUpdated displayGroupUpdated) {

  }

  @Override
  public void marketDepthExchangesProtoBuf(MarketDepthExchanges marketDepthExchanges) {

  }

  @Override
  public void configResponseProtoBuf(ConfigResponse configResponse) {

  }

  @Override
  public void updateConfigResponseProtoBuf(UpdateConfigResponse updateConfigResponse) {

  }
}
