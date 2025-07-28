CREATE TABLE card (
                      id NVARCHAR(50) PRIMARY KEY,
                      status NVARCHAR(20),
                      processing_request_id NVARCHAR(50),
                      card_number NVARCHAR(20)
);
CREATE TABLE pin (
                     id NVARCHAR(50) PRIMARY KEY,
                     pin_code NVARCHAR(10),
                     status NVARCHAR(20),
                     processing_request_id NVARCHAR(50)
);
-- CARD
WITH Numbers AS (
    SELECT TOP (500000) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
FROM sys.all_objects a CROSS JOIN sys.all_objects b
    )
INSERT INTO card (id, status, processing_request_id, card_number)
SELECT
    CONCAT('card_', n),
    CASE n % 3
        WHEN 0 THEN 'PENDING'
        WHEN 1 THEN 'PROCESSED'
        ELSE 'FAILED'
        END,
    CONCAT('request_', n % 1000),
    RIGHT('0000000000000000' + CAST(CAST(ABS(CHECKSUM(NEWID())) % 10000000000000000 AS VARCHAR(16)) AS VARCHAR), 16)
FROM Numbers;
-- PIN
WITH Numbers AS (
    SELECT TOP (500000) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
FROM sys.all_objects a CROSS JOIN sys.all_objects b
    )
INSERT INTO pin (id, pin_code, status, processing_request_id)
SELECT
    CONCAT('pin_', n),
    RIGHT('0000' + CAST(CAST(ABS(CHECKSUM(NEWID())) % 10000 AS VARCHAR(4)) AS VARCHAR), 4),
    CASE n % 3
        WHEN 0 THEN 'PENDING'
        WHEN 1 THEN 'PROCESSED'
        ELSE 'FAILED'
        END,
    CONCAT('request_', n % 1000)
FROM Numbers;

select count(*) FROM pin;
