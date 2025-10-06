package fi.metropolia.currency_converter.view;

import fi.metropolia.currency_converter.model.Currency;
import fi.metropolia.currency_converter.controller.CurrencyController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.List;

/**
 * View class for the Currency Converter application.
 * Handles all UI components and user interactions.
 */
public class CurrencyView {
    private CurrencyController controller;
    private ComboBox<Currency> fromCurrencyBox;
    private ComboBox<Currency> toCurrencyBox;
    private TextField amountField;
    private TextField newRateField;
    private Label resultLabel;
    private Button addCurrencyButton;

    public void setController(CurrencyController controller) {
        this.controller = controller;
    }

    /**
     * Populates the currency dropdowns with data from the model.
     *
     * @param currencies List of currencies to display
     */
    public void populateCurrencies(List<Currency> currencies) {
        // Check if UI components are initialized
        if (fromCurrencyBox == null || toCurrencyBox == null) {
            return;
        }

        // Save current selections
        Currency currentFrom = fromCurrencyBox.getValue();
        Currency currentTo = toCurrencyBox.getValue();

        // Update items
        fromCurrencyBox.getItems().setAll(currencies);
        toCurrencyBox.getItems().setAll(currencies);

        // Restore selections if possible
        if (currentFrom != null) {
            // Find matching currency by abbreviation
            currencies.stream()
                    .filter(c -> c.getAbbreviation().equals(currentFrom.getAbbreviation()))
                    .findFirst()
                    .ifPresent(c -> fromCurrencyBox.getSelectionModel().select(c));
        } else if (!currencies.isEmpty()) {
            fromCurrencyBox.getSelectionModel().selectFirst();
        }

        if (currentTo != null) {
            currencies.stream()
                    .filter(c -> c.getAbbreviation().equals(currentTo.getAbbreviation()))
                    .findFirst()
                    .ifPresent(c -> toCurrencyBox.getSelectionModel().select(c));
        } else if (currencies.size() > 1) {
            toCurrencyBox.getSelectionModel().select(1);
        }
    }

    /**
     * Starts the application UI.
     *
     * @param stage The primary stage
     */
    public void start(Stage stage) {
        stage.setTitle("💱 Currency Converter (JPA + MVC)");

        // Initialize UI components FIRST
        fromCurrencyBox = new ComboBox<>();
        toCurrencyBox = new ComboBox<>();
        amountField = new TextField();
        newRateField = new TextField();
        resultLabel = new Label("Converted amount will appear here");
        addCurrencyButton = new Button("Add New Currency");

        Button convertButton = new Button("Convert");
        Button updateRateButton = new Button("Update Rate");
        Button refreshButton = new Button("Refresh Currencies");

        // Set button actions
        convertButton.setOnAction(e -> onConvert());
        updateRateButton.setOnAction(e -> onUpdateRate());
        refreshButton.setOnAction(e -> onRefresh());
        addCurrencyButton.setOnAction(e -> onAddCurrency());

        // Set prompt texts
        amountField.setPromptText("Enter amount");
        newRateField.setPromptText("Enter new rate");

        // Create layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        // Conversion section
        grid.add(new Label("From:"), 0, 0);
        grid.add(fromCurrencyBox, 1, 0);
        grid.add(refreshButton, 2, 0);

        grid.add(new Label("To:"), 0, 1);
        grid.add(toCurrencyBox, 1, 1);

        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);

        grid.add(convertButton, 0, 3);
        grid.add(resultLabel, 1, 3, 2, 1);

        // Update rate section
        grid.add(new Separator(), 0, 5, 3, 1);
        grid.add(new Label("Update Rate:"), 0, 6, 2, 1);
        grid.add(newRateField, 1, 6);
        grid.add(updateRateButton, 1, 7);

        // Currency management section
        grid.add(new Separator(), 0, 9, 3, 1);
        grid.add(new Label("Currency Management:"), 0, 10, 2, 1);
        grid.add(addCurrencyButton, 0, 11);

        Scene scene = new Scene(grid, 520, 450); // Increased height for new sections
        stage.setScene(scene);

        // NOW initialize the currencies after UI is fully set up
        if (controller != null) {
            controller.initialize();
        }

        stage.show();
    }

    /**
     * Handles the convert button action.
     */
    private void onConvert() {
        try {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                showError("Please enter an amount.");
                return;
            }

            double amount = Double.parseDouble(amountText);
            Currency from = fromCurrencyBox.getValue();
            Currency to = toCurrencyBox.getValue();

            if (from == null || to == null) {
                showError("Please select both currencies.");
                return;
            }

            if (from.equals(to)) {
                showError("Please select different currencies.");
                return;
            }

            double result = controller.convert(amount, from, to);
            resultLabel.setText(String.format("%,.2f %s = %,.2f %s",
                    amount, from.getAbbreviation(), result, to.getAbbreviation()));

        } catch (NumberFormatException ex) {
            showError("Please enter a valid numeric amount.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Conversion error: " + ex.getMessage());
        }
    }

    /**
     * Handles the update rate button action.
     */
    private void onUpdateRate() {
        try {
            Currency selected = fromCurrencyBox.getValue();
            if (selected == null) {
                showError("Please select a currency to update.");
                return;
            }

            String rateText = newRateField.getText().trim();
            if (rateText.isEmpty()) {
                showError("Please enter a new rate.");
                return;
            }

            double newRate = Double.parseDouble(rateText);
            controller.updateCurrencyRate(selected.getAbbreviation(), newRate);
            resultLabel.setText("✅ Updated " + selected.getAbbreviation() + " rate to " + newRate);
            newRateField.clear();

        } catch (NumberFormatException ex) {
            showError("Please enter a valid numeric rate.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Update error: " + ex.getMessage());
        }
    }

    /**
     * Handles the refresh button action.
     */
    private void onRefresh() {
        try {
            controller.refreshCurrencies();
            resultLabel.setText("✅ Currency list refreshed");
        } catch (Exception ex) {
            showError("Refresh error: " + ex.getMessage());
        }
    }

    /**
     * Handles the add currency button action.
     * Opens the AddCurrencyView window for adding new currencies.
     */
    private void onAddCurrency() {
        try {
            // This will open the AddCurrencyView window and wait for it to close
            controller.showAddCurrencyWindow();

            // After the AddCurrencyView window closes, refresh the currency list
            // to show any newly added currencies in the dropdowns
            controller.refreshCurrencies();
            resultLabel.setText("✅ Currency list updated with new currencies");

        } catch (Exception ex) {
            showError("Error adding currency: " + ex.getMessage());
        }
    }

    /**
     * Displays an error message in the UI.
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        resultLabel.setText("⚠️ " + message);
    }

    /**
     * Displays an error message to the user when database is unavailable.
     * Required by assignment point 6.
     *
     * @param message The error message
     */
    public void showDatabaseError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText("Cannot connect to database");
        alert.setContentText(message + "\n\nPlease check your database connection and try again.");
        alert.showAndWait();
    }

    /**
     * Gets the current selected from currency.
     * This can be useful for the AddCurrencyView to set default values.
     */
    public Currency getSelectedFromCurrency() {
        return fromCurrencyBox.getValue();
    }

    /**
     * Gets the current selected to currency.
     */
    public Currency getSelectedToCurrency() {
        return toCurrencyBox.getValue();
    }
}