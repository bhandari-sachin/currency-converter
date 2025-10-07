package fi.metropolia.currency_converter.model;

import fi.metropolia.currency_converter.dao.CurrencyDAO;
import fi.metropolia.currency_converter.dao.TransactionDAO;
import java.util.List;

/**
 * Model class containing business logic for currency conversion.
 */
public class CurrencyModel {
    private final CurrencyDAO currencyDAO;
    private final TransactionDAO transactionDAO;
    private List<Currency> currencies;

    public CurrencyModel() {
        currencyDAO = new CurrencyDAO();
        transactionDAO = new TransactionDAO(CurrencyDAO.getEntityManagerFactory());
        refreshCurrencies();
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void refreshCurrencies() {
        this.currencies = currencyDAO.getAllCurrencies();
    }

    /**
     * Converts an amount from one currency to another.
     * Also stores the transaction in the database.
     *
     *
     * If rates are "to USD", to convert from currency A to B:
     * amount_B = amount_A * (rate_A / rate_B)
     *
     * Example: 100 EUR to USD, if EUR rate = 1.1 (meaning 1 EUR = 1.1 USD)
     * Result = 100 * (1.1 / 1.0) = 110 USD ✓
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

        // FIXED: Correct conversion formula
        double result = amount * (from.getRateToUSD() / to.getRateToUSD());

        // Store the transaction in the database
        try {
            Transaction transaction = new Transaction(from, to, amount, result);
            transactionDAO.saveTransaction(transaction);
        } catch (Exception e) {
            // Log the error but don't fail the conversion
            System.err.println("Warning: Failed to save transaction: " + e.getMessage());
        }

        return result;
    }

    public void updateCurrencyRate(String abbreviation, double newRate) {
        if (newRate <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }

        currencyDAO.updateCurrencyRate(abbreviation, newRate);

        // Update the cached currency object
        currencies.stream()
                .filter(c -> c.getAbbreviation().equals(abbreviation))
                .findFirst()
                .ifPresent(c -> c.setRateToUSD(newRate));
    }

    public double getExchangeRate(String abbreviation) {
        return currencyDAO.getExchangeRate(abbreviation);
    }

    /**
     * Inserts a new currency into the database.
     */
    public void insertCurrency(Currency currency) {
        if (currency.getRateToUSD() <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }
        currencyDAO.insertCurrency(currency);
        refreshCurrencies(); // Refresh to include the new currency
    }

    /**
     * Checks if a currency with the given abbreviation already exists.
     */
    public boolean currencyExists(String abbreviation) {
        return currencyDAO.currencyExists(abbreviation);
    }
}