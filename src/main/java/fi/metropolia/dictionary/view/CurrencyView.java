package fi.metropolia.dictionary.view;

import fi.metropolia.dictionary.model.Currency;
import fi.metropolia.dictionary.controller.CurrencyController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.List;

public class CurrencyView {
    private CurrencyController controller;
    private ComboBox<Currency> fromCurrencyBox;
    private ComboBox<Currency> toCurrencyBox;
    private TextField amountField;
    private TextField newRateField;
    private Label resultLabel;

    public void setController(CurrencyController controller) {
        this.controller = controller;
    }

    public void populateCurrencies(List<Currency> currencies) {
        // Check if UI components are initialized
        if (fromCurrencyBox == null || toCurrencyBox == null) {
            return; // Exit early if components not ready
        }

        Currency currentFrom = fromCurrencyBox.getValue();
        Currency currentTo = toCurrencyBox.getValue();

        fromCurrencyBox.getItems().setAll(currencies);
        toCurrencyBox.getItems().setAll(currencies);

        // Restore selections if possible
        if (currentFrom != null) {
            fromCurrencyBox.getSelectionModel().select(currentFrom);
        } else if (!currencies.isEmpty()) {
            fromCurrencyBox.getSelectionModel().selectFirst();
        }

        if (currentTo != null) {
            toCurrencyBox.getSelectionModel().select(currentTo);
        } else if (currencies.size() > 1) {
            toCurrencyBox.getSelectionModel().select(1);
        }
    }

    public void start(Stage stage) {
        stage.setTitle("💱 Currency Converter (PostgreSQL + MVC)");

        // Initialize UI components FIRST
        fromCurrencyBox = new ComboBox<>();
        toCurrencyBox = new ComboBox<>();
        amountField = new TextField();
        newRateField = new TextField();
        resultLabel = new Label("Converted amount will appear here");

        Button convertButton = new Button("Convert");
        Button updateRateButton = new Button("Update Rate");
        Button refreshButton = new Button("Refresh Currencies");

        convertButton.setOnAction(e -> onConvert());
        updateRateButton.setOnAction(e -> onUpdateRate());
        refreshButton.setOnAction(e -> onRefresh());

        // Set prompt texts
        amountField.setPromptText("Enter amount");
        newRateField.setPromptText("Enter new rate");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        // Conversion section
        grid.add(new Label("From:"), 0, 0);
        grid.add(fromCurrencyBox, 1, 0);
        grid.add(new Label("To:"), 0, 1);
        grid.add(toCurrencyBox, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(convertButton, 0, 3);
        grid.add(resultLabel, 1, 3);

        grid.add(refreshButton, 2, 0);

        // Update rate section
        grid.add(new Label("Update Rate (for selected From currency):"), 0, 5);
        grid.add(newRateField, 1, 5);
        grid.add(updateRateButton, 1, 6);

        stage.setScene(new Scene(grid, 520, 350));

        // NOW initialize the currencies after UI is fully set up
        if (controller != null) {
            controller.refreshCurrencies();
        }

        stage.show();

        // Set close handler
        stage.setOnCloseRequest(e -> {
            if (controller != null) {
                controller.cleanup();
            }
        });
    }

    private void onConvert() {
        try {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                resultLabel.setText("⚠️ Please enter an amount.");
                return;
            }

            double amount = Double.parseDouble(amountText);
            Currency from = fromCurrencyBox.getValue();
            Currency to = toCurrencyBox.getValue();

            if (from == null || to == null) {
                resultLabel.setText("⚠️ Please select both currencies.");
                return;
            }

            if (from.equals(to)) {
                resultLabel.setText("⚠️ Please select different currencies.");
                return;
            }

            double result = controller.convert(amount, from, to);
            resultLabel.setText(String.format("%,.2f %s = %,.2f %s",
                    amount, from.getAbbreviation(), result, to.getAbbreviation()));

        } catch (NumberFormatException ex) {
            resultLabel.setText("⚠️ Please enter a valid numeric amount.");
        } catch (IllegalArgumentException ex) {
            resultLabel.setText("❌ " + ex.getMessage());
        } catch (Exception ex) {
            resultLabel.setText("❌ Conversion error: " + ex.getMessage());
        }
    }

    private void onUpdateRate() {
        try {
            Currency selected = fromCurrencyBox.getValue();
            if (selected == null) {
                resultLabel.setText("⚠️ Please select a currency to update.");
                return;
            }

            String rateText = newRateField.getText().trim();
            if (rateText.isEmpty()) {
                resultLabel.setText("⚠️ Please enter a new rate.");
                return;
            }

            double newRate = Double.parseDouble(rateText);
            controller.updateCurrencyRate(selected.getAbbreviation(), newRate);
            resultLabel.setText("✅ Updated " + selected.getAbbreviation() + " rate to " + newRate);
            newRateField.clear();

        } catch (NumberFormatException ex) {
            resultLabel.setText("⚠️ Please enter a valid numeric rate.");
        } catch (IllegalArgumentException ex) {
            resultLabel.setText("❌ " + ex.getMessage());
        } catch (Exception ex) {
            resultLabel.setText("❌ Update error: " + ex.getMessage());
        }
    }

    private void onRefresh() {
        try {
            controller.refreshCurrencies();
            resultLabel.setText("✅ Currency list refreshed");
        } catch (Exception ex) {
            resultLabel.setText("❌ Refresh error: " + ex.getMessage());
        }
    }
}