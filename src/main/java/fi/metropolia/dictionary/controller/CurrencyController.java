package fi.metropolia.dictionary.controller;

import fi.metropolia.dictionary.model.Currency;
import fi.metropolia.dictionary.model.CurrencyModel;
import fi.metropolia.dictionary.view.CurrencyView;

public class CurrencyController {
    private final CurrencyModel model;
    private final CurrencyView view;

    public CurrencyController(CurrencyModel model, CurrencyView view) {
        this.model = model;
        this.view = view;
        view.setController(this);
        // REMOVED: refreshCurrencies() call from constructor
        // Currencies will be populated when view starts
    }

    public void refreshCurrencies() {
        view.populateCurrencies(model.getCurrencies());
    }

    public double convert(double amount, Currency from, Currency to) {
        return model.convert(amount, from, to);
    }

    public void updateCurrencyRate(String abbreviation, double newRate) {
        model.updateCurrencyRate(abbreviation, newRate);
        refreshCurrencies();
    }

    public void initialize() {
        // This will be called after view is fully initialized
        refreshCurrencies();
    }

    public void cleanup() {
        model.cleanup();
    }
}