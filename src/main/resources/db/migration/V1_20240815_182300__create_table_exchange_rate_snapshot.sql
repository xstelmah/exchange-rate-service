CREATE SEQUENCE t_exchange_rate_snapshot_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

CREATE TABLE t_exchange_rate_snapshot
(
    id               BIGINT PRIMARY KEY DEFAULT nextval('t_exchange_rate_snapshot_seq'),
    vendor_timestamp TIMESTAMP,
    server_timestamp TIMESTAMP,
    vendor           VARCHAR(255)
);