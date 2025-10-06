// CurrencyController.java (updated)
package fi.metropolia.currency_converter.controller;

import fi.metropolia.currency_converter.model.Currency;
import fi.metropolia.currency_converter.model.CurrencyModel;
import fi.metropolia.currency_converter.view.AddCurrencyView;
import fi.metropolia.currency_converter.view.CurrencyView;

/**
 * Controller class handling user interactions and coordinating between Model and View.
 */
public class CurrencyController {
    private final CurrencyModel model;
    private final CurrencyView view;
    private AddCurrencyView addCurrencyView;

    public CurrencyController(CurrencyModel model, CurrencyView view) {
        this.model = model;
        this.view = view;
        view.setController(this);
    }

    public void refreshCurrencies() {
        try {
            view.populateCurrencies(model.getCurrencies());
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh currencies: " + e.getMessage(), e);
        }
    }

    public double convert(double amount, Currency from, Currency to) {
        return model.convert(amount, from, to);
    }

    public void updateCurrencyRate(String abbreviation, double newRate) {
        model.updateCurrencyRate(abbreviation, newRate);
        refreshCurrencies();
    }

    public double getExchangeRate(String abbreviation) {
        return model.getExchangeRate(abbreviation);
    }

    /**
     * Opens the add currency window.
     */
    public void showAddCurrencyWindow() {
        if (addCurrencyView == null) {
            addCurrencyView = new AddCurrencyView(this);
        }
        addCurrencyView.showAndWait();
        // Refresh currencies after the add window closes
        refreshCurrencies();
    }

    /**
     * Inserts a new currency into the database.
     */
    public void insertCurrency(Currency currency) {
        model.insertCurrency(currency);
    }

    /**
     * Checks if a currency with the given abbreviation already exists.
     */
    public boolean currencyExists(String abbreviation) {
        return model.currencyExists(abbreviation);
    }

    public void initialize() {
        refreshCurrencies();
    }
}