CREATE TABLE trades
(
    order_id      BIGINT        NOT NULL PRIMARY KEY,
    strategy_name VARCHAR(20),
    date          DATE,
    type          VARCHAR(1),
    symbol        VARCHAR(4),
    quantity      INTEGER,
    strike        NUMERIC(6, 2),
    fill          NUMERIC(6, 2) NOT NULL,
    exit          NUMERIC(6, 2),
    fee           NUMERIC(8, 6),
    pnl           NUMERIC(12, 6)
);
