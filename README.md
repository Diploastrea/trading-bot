# Opening Range Breakout (ORB) Strategy Trading Bot

An automated, event-driven trading system built with Spring Boot and Interactive Brokers TWS API.
This bot is designed to execute ORB strategy on [SPY](https://finance.yahoo.com/quote/SPY) 0DTE
options with high concurrency.

## Key Features

- ORB Strategy Engine: Automatically defines the opening range (high/low), detects breakouts and
  executes orders.
- 0DTE Options Focus: Specifically targets SPY same-day expiration options to capture rapid intraday
  momentum.
- Event-Driven Architecture: Decoupled components process market data, strategy signals, and order
  execution asynchronously via internal event buses.
- Real-Time Data Streaming: Leverages TWS API's market data subscriptions for real-time price
  updates.
- High Performance with Virtual Threads: Uses Java Virtual Threads to handle concurrent data points
  without the overhead of traditional platform threads.

### 🚧 WORK IN PROGRESS 🚧