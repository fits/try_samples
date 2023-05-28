CREATE DATABASE sample;

USE sample;

CREATE TABLE items (
    id VARCHAR(10) NOT NULL,
    price INT NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO
    items(id, price)
VALUES
    ('item-1', 20),
    ('item-2', 68),
    ('item-3', 15),
    ('item-4', 43),
    ('item-5', 37);