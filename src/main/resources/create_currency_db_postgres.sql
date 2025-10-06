-- Drop database if it exists
DROP DATABASE IF EXISTS currency_db;

-- Create a new database
CREATE DATABASE currency_db;

-- Connect to the new database
\c currency_db;

-- Drop the currency table if it exists
DROP TABLE IF EXISTS currency;

-- Create the table
CREATE TABLE currency (
                          abbreviation CHAR(3) PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          rate_to_usd NUMERIC(10,6) NOT NULL
);

-- Insert sample currency data (rates as of October 2025, approximate)
INSERT INTO currency (abbreviation, name, rate_to_usd) VALUES
                                                           ('USD', 'United States Dollar', 1.000000),
                                                           ('EUR', 'Euro', 1.090000),
                                                           ('GBP', 'British Pound', 1.270000),
                                                           ('JPY', 'Japanese Yen', 0.006700),
                                                           ('AUD', 'Australian Dollar', 0.650000),
                                                           ('CAD', 'Canadian Dollar', 0.730000),
                                                           ('CHF', 'Swiss Franc', 1.100000),
                                                           ('CNY', 'Chinese Yuan', 0.137000);

-- Drop user if it exists
DO
$$
BEGIN
   IF EXISTS (SELECT FROM pg_roles WHERE rolname = 'appuser') THEN
DROP ROLE appuser;
END IF;
END
$$;

-- Create user with password
CREATE USER appuser WITH PASSWORD 'app_password';

-- Grant only necessary privileges (read and modify currency data)
GRANT CONNECT ON DATABASE currency_db TO appuser;
GRANT USAGE ON SCHEMA public TO appuser;
GRANT SELECT, INSERT, UPDATE ON currency TO appuser;

-- Allow appuser to see sequences (not strictly needed here)
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO appuser;
