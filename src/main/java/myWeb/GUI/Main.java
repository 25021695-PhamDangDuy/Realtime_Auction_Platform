package myWeb.GUI; // Dòng này phải khớp với thư mục chứa nó

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("JavaFX đã sẵn sàng!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("Test JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Lệnh này là bắt buộc để chạy JavaFX
    }
}