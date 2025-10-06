package fi.metropolia.currency_converter.model;

import fi.metropolia.currency_converter.dao.CurrencyDAO;
import java.util.List;

/**
 * Model class containing business logic for currency conversion.
 */
public class CurrencyModel {
    private final CurrencyDAO dao;
    private List<Currency> currencies;

    public CurrencyModel() {
        dao = new CurrencyDAO();
        refreshCurrencies();
    }

    /**
     * Gets the list of all currencies.
     * @return List of currencies
     */
    public List<Currency> getCurrencies() {
        return currencies;
    }

    /**
     * Refreshes the currency list from the database.
     */
    public void refreshCurrencies() {
        this.currencies = dao.getAllCurrencies();
    }

    /**
     * Converts an amount from one currency to another.
     *
     * @param amount The amount to convert
     * @param from The source currency
     * @param to The target currency
     * @return The converted amount
     * @throws IllegalArgumentException if parameters are invalid
     */
    public double convert(double amount, Currency from, Currency to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("From and To currencies cannot be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (from.getRateToUSD() <= 0 || to.getRateToUSD() <= 0) {
            throw new IllegalArgumentException("Exchange rates must be positive");
        }

        // Convert from source currency to USD, then from USD to target currency
        return amount * (to.getRateToUSD() / from.getRateToUSD());
    }

    /**
     * Updates the exchange rate for a currency in the database.
     *
     * @param abbreviation The currency abbreviation
     * @param newRate The new exchange rate
     * @throws IllegalArgumentException if rate is not positive
     */
    public void updateCurrencyRate(String abbreviation, double newRate) {
        if (newRate <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }

        dao.updateCurrencyRate(abbreviation, newRate);

        // Update the cached currency object
        currencies.stream()
                .filter(c -> c.getAbbreviation().equals(abbreviation))
                .findFirst()
                .ifPresent(c -> c.setRateToUSD(newRate));
    }

    /**
     * Gets the exchange rate for a specific currency from the database.
     * Uses the DAO method as required by the assignment.
     *
     * @param abbreviation The currency abbreviation
     * @return The exchange rate to USD
     */
    public double getExchangeRate(String abbreviation) {
        return dao.getExchangeRate(abbreviation);
    }
}