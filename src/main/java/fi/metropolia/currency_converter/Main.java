package fi.metropolia.currency_converter;

import fi.metropolia.currency_converter.controller.CurrencyController;
import fi.metropolia.currency_converter.model.CurrencyModel;
import fi.metropolia.currency_converter.view.CurrencyView;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            CurrencyModel model = new CurrencyModel();
            CurrencyView view = new CurrencyView();
            CurrencyController controller = new CurrencyController(model, view);

            // Set controller first, then start the view
            view.setController(controller);
            view.start(stage);

        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Application failed to start: " + e.getMessage());
        }
    }

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