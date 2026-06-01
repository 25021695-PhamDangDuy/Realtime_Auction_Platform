package view;

import javafx.application.Application;
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
//import view.network.ServerConnection;


public class AuctionLogin extends Application {
//    private ServerConnection connection;
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

        btn.setOnAction(event -> {
            String username = userTextField.getText().trim();
            String password = pwBox.getText().trim();


            if (username.isEmpty() || password.isEmpty()) {
                actiontarget.setFill(javafx.scene.paint.Color.FIREBRICK);
                actiontarget.setText("Vui lòng nhập tên tài khoản,mật khẩu !");
                actiontarget.setStyle("-fx-fill: #e53935;");
                return;
            } else {
                actiontarget.setText("Tài khoản hoặc mật khẩu không chính xác!");
                actiontarget.setStyle("-fx-fill: #e53935;");
            }


            // Bước 2: Ghép lại thành cú pháp (Chữ đầu tiên viết hoa là lệnh khởi động)
            // Ví dụ lệnh đăng nhập viết hoa là: LOGIN
            // Cú pháp mẫu: LOGIN <username>
            String command = "LOGIN|" + username;
            try {
//                connection.sendCommand(command);
//                AuctionHomeScreen homeScreen = new AuctionHomeScreen();
//                homeScreen.start(primaryStage);

                // Hiển thị trạng thái tạm thời trên giao diện
                actiontarget.setFill(javafx.scene.paint.Color.GREEN);
                actiontarget.setText("Đang kết nối server và gửi yêu cầu...");
                System.out.println("Đã gửi lên server: " + command);
            } catch (Exception e) {
                actiontarget.setFill(javafx.scene.paint.Color.FIREBRICK);
                actiontarget.setText("Lỗi kết nối tới server!");
                e.printStackTrace();
            }


        });
        Button btnGoToRegister = new Button("Chưa có tài khoản? Đăng ký ngay");
        btnGoToRegister.setStyle("-fx-background-color: transparent; -fx-text-fill: #1e88e5; -fx-underline: true; -fx-cursor: hand;");
        hbBtn.getChildren().add(btnGoToRegister);
        btnGoToRegister.setOnAction(e -> {
            try {
                // Khởi tạo màn hình đăng ký tài khoản
                AuctionRegister registerApp = new AuctionRegister();

                // Truyền chính primaryStage hiện tại sang để đổi giao diện trên cùng 1 cửa sổ
                registerApp.start(primaryStage);

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

    public static void main(String[] args) {
        launch(args);
    }
}
