package Controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = loader.load();
        SimulationController simulationController = loader.getController();
        primaryStage.setTitle("Traffic Simulation");
        primaryStage.setScene(new Scene(root, 1000, 1000));
        primaryStage.show();
        primaryStage.setOnHidden(e -> {
            simulationController.shutdown();
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
