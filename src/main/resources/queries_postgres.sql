-- Connect to the correct database
\c currency_db;

-- 1. Retrieve all currencies
SELECT * FROM currency;

-- 2. Retrieve the currency with abbreviation EUR
SELECT * FROM currency WHERE abbreviation = 'EUR';

-- 3. Retrieve the number of currencies in the database
SELECT COUNT(*) AS currency_count FROM currency;

-- 4. Retrieve the currency with the highest exchange rate
SELECT * FROM currency
ORDER BY rate_to_usd DESC
    LIMIT 1;
