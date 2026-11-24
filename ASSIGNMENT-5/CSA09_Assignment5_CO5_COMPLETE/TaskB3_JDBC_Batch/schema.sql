CREATE DATABASE IF NOT EXISTS co5_transport;
USE co5_transport;

CREATE TABLE IF NOT EXISTS bus_pass (
    pass_id VARCHAR(30) PRIMARY KEY,
    reg_no VARCHAR(30) NOT NULL,
    name VARCHAR(100) NOT NULL,
    route_no INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    CHECK (route_no >= 0),
    CHECK (amount >= 0)
);
