CREATE SEQUENCE t_exchange_rate_seq
    START WITH 1
    INCREMENT BY 25
    NO MINVALUE
    NO MAXVALUE CACHE 25;

CREATE TABLE t_exchange_rate
(
    id              BIGINT PRIMARY KEY DEFAULT nextval('t_exchange_rate_seq'),
    base_currency   VARCHAR(255)   NOT NULL,
    target_currency VARCHAR(255)   NOT NULL,
    rate            DECIMAL(19, 4) NOT NULL,
    timestamp       TIMESTAMP      NOT NULL,
    snapshot_id     BIGINT         NOT NULL
);

ALTER TABLE t_exchange_rate
    ADD CONSTRAINT fk_exchange_rate__exchange_rate_snapshot_id
        FOREIGN KEY (snapshot_id)
            REFERENCES t_exchange_rate_snapshot (id);

CREATE INDEX idx_exchange_rate__base_currency ON t_exchange_rate (base_currency);
CREATE INDEX idx_exchange_rate__target_currency ON t_exchange_rate (target_currency);
CREATE INDEX idx_exchange_rate__timestamp ON t_exchange_rate (timestamp);
CREATE INDEX idx_exchange_rate__snapshot_id ON t_exchange_rate (snapshot_id);