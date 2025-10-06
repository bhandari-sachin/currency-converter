package fi.metropolia.currency_converter.dao;

import fi.metropolia.currency_converter.datasource.DatabaseConfig;
import fi.metropolia.currency_converter.model.Currency;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Currency entities.
 * Handles all database operations related to currencies.
 */
public class CurrencyDAO {

    /**
     * Creates a new database connection.
     * @return A new Connection object
     * @throws SQLException if connection fails
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword()
        );
    }

    /**
     * Retrieves the exchange rate for a specific currency.
     * Required by assignment: "Add a method for retrieving the exchange rate of a currency"
     *
     * @param abbreviation The currency abbreviation (e.g., "EUR", "USD")
     * @return The exchange rate to USD
     * @throws RuntimeException if currency not found or database error occurs
     */
    public double getExchangeRate(String abbreviation) {
        String sql = "SELECT rate_to_usd FROM currency WHERE abbreviation = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, abbreviation);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("rate_to_usd");
                }
                throw new RuntimeException("Currency not found: " + abbreviation);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching exchange rate for " + abbreviation + ": " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all currencies from the database.
     *
     * @return List of all currencies ordered by abbreviation
     * @throws RuntimeException if database error occurs
     */
    public List<Currency> getAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT abbreviation, name, rate_to_usd FROM currency ORDER BY abbreviation";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String abbr = rs.getString("abbreviation");
                if (abbr != null) {
                    abbr = abbr.trim();
                }
                String name = rs.getString("name");
                double rate = rs.getDouble("rate_to_usd");
                currencies.add(new Currency(abbr, name, rate));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching currencies: " + e.getMessage(), e);
        }

        return currencies;
    }

    /**
     * Updates the exchange rate for a specific currency.
     *
     * @param abbreviation The currency abbreviation
     * @param newRate The new exchange rate to USD
     * @throws RuntimeException if currency not found or database error occurs
     */
    public void updateCurrencyRate(String abbreviation, double newRate) {
        String sql = "UPDATE currency SET rate_to_usd = ? WHERE abbreviation = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            stmt.setDouble(1, newRate);
            stmt.setString(2, abbreviation);
            int affectedRows = stmt.executeUpdate();

            conn.commit();

            if (affectedRows == 0) {
                throw new RuntimeException("Currency not found: " + abbreviation);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating rate for " + abbreviation + ": " + e.getMessage(), e);
        }
    }
}