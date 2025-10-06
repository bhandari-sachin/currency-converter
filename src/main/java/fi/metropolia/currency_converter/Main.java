package fi.metropolia.currency_converter;

import fi.metropolia.currency_converter.controller.CurrencyController;
import fi.metropolia.currency_converter.model.CurrencyModel;
import fi.metropolia.currency_converter.view.CurrencyView;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Main application class for the Currency Converter.
 * Implements MVC pattern with database integration.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        CurrencyView view = null;

        try {
            // Initialize model (connects to database)
            CurrencyModel model = new CurrencyModel();

            // Initialize view
            view = new CurrencyView();

            // Initialize controller (connects model and view)
            CurrencyController controller = new CurrencyController(model, view);

            // Start the view
            view.start(stage);

        } catch (RuntimeException e) {
            // Log error to console
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();

            // Show error dialog to user (Assignment requirement #6)
            showDatabaseErrorDialog(e.getMessage());

            // If view was created, show error there too
            if (view != null) {
                view.showDatabaseError(e.getMessage());
            }

            // Exit application as we cannot function without database
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("An unexpected error occurred: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Shows an error dialog when database connection fails.
     * This fulfills assignment requirement #6:
     * "Your application displays an appropriate error message in the user interface
     * if the database is not available."
     *
     * @param message The error message
     */
    private void showDatabaseErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Connection Error");
        alert.setHeaderText("Cannot connect to the database");
        alert.setContentText(
                "The application cannot start because the database is unavailable.\n\n" +
                        "Error: " + message + "\n\n" +
                        "Please check:\n" +
                        "- PostgreSQL server is running\n" +
                        "- Database connection settings in database.properties\n" +
                        "- Network connectivity"
        );
        alert.showAndWait();
    }

    /**
     * Shows a general error dialog.
     *
     * @param message The error message
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Application Error");
        alert.setHeaderText("Failed to start application");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        System.out.println("Application is closing...");
    }

    public static void main(String[] args) {
        launch(args);
    }
}