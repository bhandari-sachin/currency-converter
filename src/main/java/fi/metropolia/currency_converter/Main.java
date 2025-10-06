package fi.metropolia.currency_converter;

import fi.metropolia.currency_converter.controller.CurrencyController;
import fi.metropolia.currency_converter.model.CurrencyModel;
import fi.metropolia.currency_converter.view.CurrencyView;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Main application class for the Currency Converter.
 * Implements MVC pattern with JPA database integration.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        CurrencyView view = null;

        try {
            // Load database properties first
            loadDatabaseProperties();

            // Initialize model (connects to database via JPA)
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
     * Loads database properties from database.properties file and sets them as system properties.
     * This allows persistence.xml to use ${db.url}, ${db.user}, ${db.password} placeholders.
     */
    private void loadDatabaseProperties() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("database.properties")) {

            if (input == null) {
                System.out.println("No database.properties found, using default configuration");
                setDefaultDatabaseProperties();
                return;
            }

            Properties props = new Properties();
            props.load(input);

            // Set system properties for JPA to use
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            if (url != null) {
                System.setProperty("db.url", url);
                System.out.println("Loaded db.url: " + url);
            }
            if (user != null) {
                System.setProperty("db.user", user);
                System.out.println("Loaded db.user: " + user);
            }
            if (password != null) {
                System.setProperty("db.password", password);
                System.out.println("Loaded db.password: [HIDDEN]");
            }

            System.out.println("✅ Database properties loaded successfully from database.properties");

        } catch (IOException e) {
            System.out.println("❌ Could not load database.properties, using defaults");
            setDefaultDatabaseProperties();
        }
    }

    /**
     * Sets default database properties if no properties file is found.
     */
    private void setDefaultDatabaseProperties() {
        System.setProperty("db.url", "jdbc:postgresql://localhost:5432/currency_db");
        System.setProperty("db.user", "postgres");
        System.setProperty("db.password", "postgres");
        System.out.println("Using default database configuration");
    }

    /**
     * Shows an error dialog when database connection fails.
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
                        "- Network connectivity\n\n" +
                        "Loaded configuration:\n" +
                        "URL: " + System.getProperty("db.url") + "\n" +
                        "User: " + System.getProperty("db.user")
        );
        alert.showAndWait();
    }

    /**
     * Shows a general error dialog.
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