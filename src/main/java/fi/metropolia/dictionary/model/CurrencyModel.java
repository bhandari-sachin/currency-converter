package fi.metropolia.dictionary.model;

import java.util.List;

public class CurrencyModel {
    private final CurrencyDAO dao;
    private List<Currency> currencies;
    private long lastRefreshTime;
    private static final long CACHE_DURATION_MS = 30000; // 30 seconds cache

    public CurrencyModel() {
        dao = new CurrencyDAO();
        refreshCurrencies();
    }

    public List<Currency> getCurrencies() {
        // Refresh cache if stale
        if (System.currentTimeMillis() - lastRefreshTime > CACHE_DURATION_MS) {
            refreshCurrencies();
        }
        return currencies;
    }

    private void refreshCurrencies() {
        this.currencies = dao.getAllCurrencies();
        this.lastRefreshTime = System.currentTimeMillis();
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

        // Update cached currency instead of refreshing entire list
        currencies.stream()
                .filter(c -> c.getAbbreviation().equals(abbreviation))
                .findFirst()
                .ifPresent(c -> c.setRateToUSD(newRate));

        lastRefreshTime = System.currentTimeMillis(); // Reset cache timer
    }

    public void cleanup() {
        dao.close();
    }
}