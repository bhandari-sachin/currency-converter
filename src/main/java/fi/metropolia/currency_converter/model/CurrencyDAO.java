package fi.metropolia.currency_converter.model;

import fi.metropolia.currency_converter.model.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    private Connection connection;

    public CurrencyDAO() {
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            this.connection = DriverManager.getConnection(
                    DatabaseConfig.getUrl(),
                    DatabaseConfig.getUser(),
                    DatabaseConfig.getPassword()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database connection", e);
        }
    }

    private Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initializeConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection issue", e);
        }
        return connection;
    }

    public List<Currency> getAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT abbreviation, name, rate_to_usd FROM currency ORDER BY abbreviation";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String abbr = rs.getString("abbreviation").trim();
                String name = rs.getString("name");
                double rate = rs.getDouble("rate_to_usd");
                currencies.add(new Currency(abbr, name, rate));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching currencies: " + e.getMessage(), e);
        }

        return currencies;
    }

    public void updateCurrencyRate(String abbreviation, double newRate) {
        String sql = "UPDATE currency SET rate_to_usd = ? WHERE abbreviation = ?";
        Connection conn = getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            stmt.setDouble(1, newRate);
            stmt.setString(2, abbreviation);
            int affectedRows = stmt.executeUpdate();
            conn.commit();

            if (affectedRows == 0) {
                throw new RuntimeException("Currency not found: " + abbreviation);
            }

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Error updating rate: " + e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}