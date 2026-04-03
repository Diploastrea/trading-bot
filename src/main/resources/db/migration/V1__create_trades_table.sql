CREATE TABLE trades
(
    order_id      BIGINT        NOT NULL PRIMARY KEY,
    strategy_name VARCHAR(20)   NOT NULL,
    date          DATE          NOT NULL,
    type          VARCHAR(1)    NOT NULL,
    symbol        VARCHAR(4)    NOT NULL,
    quantity      INTEGER       NOT NULL,
    strike        NUMERIC(6, 2) NOT NULL,
    fill          NUMERIC(6, 2),
    exit          NUMERIC(6, 2),
    fee           NUMERIC(4, 2) NOT NULL,
    pnl           NUMERIC(6, 2)
);
