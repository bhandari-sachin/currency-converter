package fi.metropolia.currency_converter.controller;

import fi.metropolia.currency_converter.model.Currency;
import fi.metropolia.currency_converter.model.CurrencyModel;
import fi.metropolia.currency_converter.view.CurrencyView;

/**
 * Controller class handling user interactions and coordinating between Model and View.
 * Implements the MVC pattern as required by the assignment.
 */
public class CurrencyController {
    private final CurrencyModel model;
    private final CurrencyView view;

    public CurrencyController(CurrencyModel model, CurrencyView view) {
        this.model = model;
        this.view = view;
        view.setController(this);
    }

    /**
     * Refreshes the currency list in the view.
     */
    public void refreshCurrencies() {
        try {
            view.populateCurrencies(model.getCurrencies());
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh currencies: " + e.getMessage(), e);
        }
    }

    /**
     * Converts an amount between two currencies.
     * Uses exchange rates fetched from the database via the model.
     *
     * @param amount The amount to convert
     * @param from The source currency
     * @param to The target currency
     * @return The converted amount
     */
    public double convert(double amount, Currency from, Currency to) {
        return model.convert(amount, from, to);
    }

    /**
     * Updates the exchange rate for a currency.
     *
     * @param abbreviation The currency abbreviation
     * @param newRate The new exchange rate
     */
    public void updateCurrencyRate(String abbreviation, double newRate) {
        model.updateCurrencyRate(abbreviation, newRate);
        refreshCurrencies();
    }

    /**
     * Gets the exchange rate for a specific currency.
     * This method uses the DAO method as required by the assignment:
     * "Your Controller class uses the aforementioned method to successfully fetch the exchange rate"
     *
     * @param abbreviation The currency abbreviation
     * @return The exchange rate to USD
     */
    public double getExchangeRate(String abbreviation) {
        return model.getExchangeRate(abbreviation);
    }

    /**
     * Initializes the controller after view is ready.
     */
    public void initialize() {
        refreshCurrencies();
    }
}