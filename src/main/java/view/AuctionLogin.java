package view;

import function.SessionStatus;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.*;
import models.*;
import server.GsonUtil;
import view.network.MessageListener;
import view.network.ServerConnection;

import java.time.LocalDateTime;


public class AuctionLogin extends Application implements MessageListener {

    private ServerConnection connection;
    private Stage primaryStage;
    private Text actiontarget;
    public AuctionLogin() {}
    public AuctionLogin(ServerConnection connection) {
        this.connection = connection;
    }
    public AuctionLogin(ServerConnection connection, Stage primaryStage) {
        this.connection = connection;
        this.primaryStage = primaryStage;
    }
    public void start(Stage primaryStage) {
        this.connection.setMessageListener(this);
        primaryStage.setTitle("Hệ thống đấu giá online");
        GridPane grid = new GridPane();// căn chỉnh các ô nhập
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("CHÀO MỪNG BẠN QUAY TRỞ LẠI");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
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
        actiontarget = new Text(); // Nơi hiện thông báo lỗi/thành công
        grid.add(actiontarget, 1, 6);
        connection.setMessageListener(this);
        btn.setOnAction(event -> {
            String username = userTextField.getText().trim();
            String password = pwBox.getText().trim();
            String command = "LOGIN|" + username.trim() + "|" + password.trim();
            try {
                connection.sendCommand(command);
                System.out.println("[LOG SENT]: Đã gửi yêu cầu đăng nhập -> " + command);
            } catch (Exception e) {
                actiontarget.setText("Không thể gửi lệnh đến Server!");
                e.printStackTrace();
            }
        });
        Button btnGoToRegister = new Button("Chưa có tài khoản? Đăng ký ngay");
        btnGoToRegister.setStyle("-fx-background-color: transparent; -fx-text-fill: #1e88e5; -fx-underline: true; -fx-cursor: hand;");
        hbBtn.getChildren().add(btnGoToRegister);
        btnGoToRegister.setOnAction(e -> {
            try {
                // Khởi tạo màn hình đăng ký tài khoản
                AuctionRegister registerApp = new AuctionRegister(this.connection,this.primaryStage);

                // Truyền chính primaryStage hiện tại sang để đổi giao diện trên cùng 1 cửa sổ
                registerApp.start(this.primaryStage);

            } catch (Exception ex) {
                actiontarget.setText("Không thể chuyển sang màn hình đăng ký!");
                actiontarget.setStyle("-fx-fill: #e53935;");
                ex.printStackTrace();
            }
        });
        // 6. Hiển thị cửa sổ
        Scene scene = new Scene(grid, 400, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void onMessageReceived(String message) {
        String[] parts = message.split("\\|", 2);
        String command = parts[0];
        String data = (parts.length > 1) ? parts[1] : "";

        javafx.application.Platform.runLater(() -> {
            switch (command) {
                case "SUCCESS_LOGIN":
                    try {
                        // MỞ GÓI: Ép kiểu ngược từ JSON thành User xịn (có đầy đủ thuộc tính của Bidder/Seller)
                        User user = GsonUtil.gson.fromJson(data, User.class);

                        System.out.println("Đăng nhập thành công! Vai trò: " + user.getClass().getSimpleName());

                        // Khởi tạo Dashboard và truyền cái user vừa dịch được vào Constructor
                        view.AuctionHomeScreen dashboard = new view.AuctionHomeScreen(connection, primaryStage,user);
                        dashboard.start(primaryStage);


                    } catch (Exception e) {
                        System.out.println("Lỗi mở gói JSON!");
                        e.printStackTrace();
                    }
                    break;

                case "ERROR":
                    // Hiện hộp thoại báo lỗi (Sai pass, tài khoản không tồn tại...)
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText(data);
                    alert.show();
                    break;
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}






