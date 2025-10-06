// AddCurrencyView.java
package fi.metropolia.currency_converter.view;

import fi.metropolia.currency_converter.controller.CurrencyController;
import fi.metropolia.currency_converter.model.Currency;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * View for adding new currencies to the database.
 */
public class AddCurrencyView {
    private final CurrencyController controller;
    private final Stage stage;
    private TextField abbreviationField;
    private TextField nameField;
    private TextField rateField;
    private Label messageLabel;

    public AddCurrencyView(CurrencyController controller) {
        this.controller = controller;
        this.stage = new Stage();
        initialize();
    }

    private void initialize() {
        stage.setTitle("Add New Currency");

        // Create form components
        abbreviationField = new TextField();
        abbreviationField.setPromptText("e.g., EUR");
        abbreviationField.setMaxWidth(80);

        nameField = new TextField();
        nameField.setPromptText("e.g., Euro");

        rateField = new TextField();
        rateField.setPromptText("e.g., 0.85");

        messageLabel = new Label();

        Button addButton = new Button("Add Currency");
        Button cancelButton = new Button("Cancel");

        // Set button actions
        addButton.setOnAction(e -> onAddCurrency());
        cancelButton.setOnAction(e -> stage.close());

        // Create layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Abbreviation (3 letters):"), 0, 0);
        grid.add(abbreviationField, 1, 0);

        grid.add(new Label("Currency Name:"), 0, 1);
        grid.add(nameField, 1, 1);

        grid.add(new Label("Exchange Rate to USD:"), 0, 2);
        grid.add(rateField, 1, 2);

        grid.add(addButton, 0, 3);
        grid.add(cancelButton, 1, 3);

        grid.add(messageLabel, 0, 4, 2, 1);

        Scene scene = new Scene(grid, 400, 250);
        stage.setScene(scene);
    }

    private void onAddCurrency() {
        try {
            String abbreviation = abbreviationField.getText().trim().toUpperCase();
            String name = nameField.getText().trim();
            String rateText = rateField.getText().trim();

            // Validation
            if (abbreviation.isEmpty() || name.isEmpty() || rateText.isEmpty()) {
                showMessage("Please fill in all fields.", true);
                return;
            }

            if (abbreviation.length() != 3) {
                showMessage("Abbreviation must be exactly 3 letters.", true);
                return;
            }

            if (controller.currencyExists(abbreviation)) {
                showMessage("Currency " + abbreviation + " already exists.", true);
                return;
            }

            double rate = Double.parseDouble(rateText);
            if (rate <= 0) {
                showMessage("Exchange rate must be positive.", true);
                return;
            }

            // Create and insert new currency
            Currency newCurrency = new Currency(abbreviation, name, rate);
            controller.insertCurrency(newCurrency);

            showMessage("✅ Currency " + abbreviation + " added successfully!", false);

            // Clear fields for next entry
            abbreviationField.clear();
            nameField.clear();
            rateField.clear();

        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid numeric rate.", true);
        } catch (IllegalArgumentException ex) {
            showMessage(ex.getMessage(), true);
        } catch (Exception ex) {
            showMessage("Error adding currency: " + ex.getMessage(), true);
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    public void show() {
        stage.show();
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}