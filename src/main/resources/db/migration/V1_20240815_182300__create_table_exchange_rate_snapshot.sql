CREATE SEQUENCE s_exchange_rate_snapshot_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;

CREATE TABLE t_exchange_rate_snapshot
(
    id               BIGINT PRIMARY KEY DEFAULT nextval('s_exchange_rate_snapshot_id'),
    vendor_timestamp TIMESTAMP,
    server_timestamp TIMESTAMP,
    vendor           VARCHAR(255)
);