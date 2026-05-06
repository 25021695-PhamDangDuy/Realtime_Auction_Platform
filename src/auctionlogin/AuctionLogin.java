package auctionlogin;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AuctionLogin extends Application {
    public void start(Stage primaryStage){
        primaryStage.setTitle("Hệ thống đấu giá online");
        GridPane grid = new GridPane();// căn chỉnh các ô nhập
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(25,25,25,25));
        Text scenetitle = new Text("CHÀO MỪNG BẠN QUAY TRỞ LẠI");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.BOLD,20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("Tên đăng nhập:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        userTextField.setPromptText("Nhập tài khoản...");
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Mật khẩu:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Nhập mật khẩu...");
        grid.add(pwBox, 1, 2);

        // 4. Nút bấm và khu vực hiển thị thông báo
        Button btn = new Button("Đăng nhập");
        btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold;");
        btn.setPrefWidth(200);

        VBox hbBtn = new VBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text(); // Nơi hiện thông báo lỗi/thành công
        grid.add(actiontarget, 1, 6);



        // 6. Hiển thị cửa sổ
        Scene scene = new Scene(grid, 400, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }





}
