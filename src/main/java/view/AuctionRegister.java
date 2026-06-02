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
import view.network.MessageListener;
import view.network.ServerConnection;



public class AuctionRegister extends Application implements MessageListener {
    private ServerConnection connection;
    private Stage primaryStage;
    private javafx.scene.text.Text message;
    

    public AuctionRegister(ServerConnection connection, Stage primaryStage) {
        this.connection = connection;
        this.primaryStage = primaryStage;
    }

    public void start(Stage stage){
        this.primaryStage = stage;
        connection.setMessageListener(this);
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
        message = new Text();
        grid.add(message, 1, 6);
        btnRegister.setOnAction(event -> {
            String username = userField.getText().trim();
            String password = pwField.getText().trim();
            String confirmpw = confirmPwField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || confirmpw.isEmpty()) {
                System.out.println("Vui lòng điền đầy đủ thông tin!");
                message.setText("Vui lòng điền đầy đủ thông tin!");
                message.setFill(javafx.scene.paint.Color.FIREBRICK);
                return;
            }
            if (!password.equals(confirmpw)) {
                System.out.println("Mật khẩu nhập lại không khớp!");
                message.setText("Mật khẩu nhập lại không trùng khớp!");
                message.setFill(javafx.scene.paint.Color.FIREBRICK);
                return;
            }
            String command = "REGISTER|" + username.trim() + "|" + password.trim() + "|" + confirmpw.trim();
            try {
                connection.sendCommand(command);
                System.out.println("[LOG SENT]: Đã gửi yêu cầu đăng ký -> " + command);

                message.setFill(javafx.scene.paint.Color.BLUE);
                message.setText("Đang gửi yêu cầu đăng ký...");
            } catch (Exception e) {
                System.err.println("[LOG ERROR]: Lỗi: " + e.getMessage());
                message.setFill(javafx.scene.paint.Color.FIREBRICK);
                message.setText("Lỗi kết nối mạng!");
            }
            try{
                AuctionLogin loginApp = new AuctionLogin(connection);
                loginApp.start(primaryStage);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
        //NÚT QUAY LẠI ĐĂNG NHẬP
        Button btnBackToLogin = new Button("← Quay lại đăng nhập");
        btnBackToLogin.setStyle("-fx-background-color: transparent; -fx-text-fill: #1a88e5; -fx-underline: true; -fx-cursor: hand; -fx-font-weight: bold;");
        grid.add(btnBackToLogin, 1, 8);
        btnBackToLogin.setOnAction(event -> {
            try {
                AuctionLogin loginView = new AuctionLogin();
                // 2. Lấy Stage hiện tại một cách an toàn từ chính nút bấm này
                Stage currentStage = (Stage) btnBackToLogin.getScene().getWindow();

                // 3. Gọi hàm start và truyền stage hiện tại vào để quay về màn hình cũ
                loginView.start(currentStage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Scene scene = new Scene(grid, 450, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void onMessageReceived(String serverMessage) {
        // Luôn cập nhật UI từ luồng Server bằng Platform.runLater
        javafx.application.Platform.runLater(() -> {
            System.out.println("Nhận được từ server (Register): " + serverMessage);

            // Giả sử server của bạn kia trả về "REGISTER_SUCCESS" khi tạo tk thành công
            if ("REGISTER_SUCCESS".equals(serverMessage)) {
                message.setText("Đăng ký thành công! Đang chuyển sang Đăng nhập...");
                message.setFill(javafx.scene.paint.Color.GREEN);

                try {
                    // Đăng ký thành công thì chuyển về màn hình Login cho user nhập lại
                    AuctionLogin loginApp = new AuctionLogin(connection);
                    loginApp.start(primaryStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            // Giả sử server trả về "REGISTER_EXISTS" nếu tên tài khoản bị trùng dưới database
            else if ("REGISTER_EXISTS".equals(serverMessage)) {
                message.setText("Tên tài khoản đã tồn tại trên hệ thống!");
                message.setFill(javafx.scene.paint.Color.FIREBRICK);
            }
            else {
                message.setText("Đăng ký thất bại! Vui lòng thử lại.");
                message.setFill(javafx.scene.paint.Color.FIREBRICK);
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }




}