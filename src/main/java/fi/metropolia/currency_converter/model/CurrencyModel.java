// CurrencyModel.java (updated)
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

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void refreshCurrencies() {
        this.currencies = dao.getAllCurrencies();
    }

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

        return amount * (to.getRateToUSD() / from.getRateToUSD());
    }

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

    public double getExchangeRate(String abbreviation) {
        return dao.getExchangeRate(abbreviation);
    }

    /**
     * Inserts a new currency into the database.
     */
    public void insertCurrency(Currency currency) {
        if (currency.getRateToUSD() <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }
        dao.insertCurrency(currency);
        refreshCurrencies(); // Refresh to include the new currency
    }

    /**
     * Checks if a currency with the given abbreviation already exists.
     */
    public boolean currencyExists(String abbreviation) {
        return dao.currencyExists(abbreviation);
    }
}