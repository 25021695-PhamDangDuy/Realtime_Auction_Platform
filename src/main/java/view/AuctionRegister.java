package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AuctionRegister extends Application {
    public void start(Stage primaryStage){
        primaryStage.setTitle("Đăng ký tài khoản đấu giá");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);//căn giữa màn hình;
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text title = new Text("TẠO TÀI KHOẢN MỚI");
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
        grid.add(title, 0, 0, 2, 1);

        // Họ tên
        grid.add(new Label("Họ và tên:"), 0, 1);
        TextField nameField = new TextField();
        grid.add(nameField, 1, 1);

        // Tên đăng nhập
        grid.add(new Label("Tên đăng nhập:"), 0, 2);
        TextField userField = new TextField();
        grid.add(userField, 1, 2);

        // Mật khẩu
        grid.add(new Label("Mật khẩu:"), 0, 3);
        PasswordField pwField = new PasswordField();
        grid.add(pwField, 1, 3);

        // Xác nhận mật khẩu
        grid.add(new Label("Xác nhận mật khẩu:"), 0, 4);
        PasswordField confirmPwField = new PasswordField();
        grid.add(confirmPwField, 1, 4);

        // Nút Đăng ký
        Button btnRegister = new Button("Đăng ký ");
        btnRegister.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnRegister.setPrefWidth(200);
        grid.add(btnRegister, 1, 5);

        Text message = new Text();
        grid.add(message, 1, 6);



        Scene scene = new Scene(grid, 450, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}