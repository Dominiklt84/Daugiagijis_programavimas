package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage)  {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/app/program/main.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setTitle("Words Counter");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }catch (Exception e){
            throw new RuntimeException("Failed to start application", e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
