package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/SaladList.fxml"));
        Scene scene = new Scene(loader.load(), 700, 500);

        // Читаємо CSS як текст і передаємо напряму
        try (var stream = getClass().getResourceAsStream("/css/style.css")) {
            if (stream != null) {
                String cssContent = new String(stream.readAllBytes());
                scene.getStylesheets().add(
                        "data:text/css," + java.net.URLEncoder.encode(cssContent, "UTF-8")
                                .replace("+", "%20"));
            }
        } catch (Exception ignored) {}

        stage.setTitle("Шеф-кухар ");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}