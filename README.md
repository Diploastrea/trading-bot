# Opening Range Breakout (ORB) Strategy Trading Bot

An automated, event-driven trading system built with Spring Boot and Interactive Brokers TWS API.
This bot is designed to execute ORB strategy on 0DTE [SPY](https://finance.yahoo.com/quote/SPY)
options as an intraday trading strategy.

## Prerequisites

- Java 25
- Installed TWS API
- IBKR account with options trading permission
- PostgreSQL

## Key Features

- Real-time data streaming leveraging TWS API market data subscriptions
- Automatically defines the opening range (high/low) after NYSE opens
- Detects breakouts or breakdowns and places an order for ATM call or put option respectively
- Automatically sets take profit and stop loss with 1:2 risk to reward ratio
- Records trades for post-trade data analysis
- Ability to run multiple ORB configurations at the same time for forward testing purposes
