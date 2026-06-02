package view;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import view.network.MessageListener;
import view.network.ServerConnection;

public class Account extends Application implements MessageListener {
    private double soDu ; // 5 triệu VND
    private Stage primaryStage;
    private javafx.scene.control.Label lblSoDu;
    private ServerConnection connection;

    public Account(Stage primaryStage, ServerConnection connection) {
        this.primaryStage = primaryStage;
        this.connection = connection;
    }

    public Account() {
    }


    @Override
    public void start(Stage stage) {
        if (this.primaryStage == null) {
            this.primaryStage = stage;
        }

        // 2. Đồng bộ lại connection lấy trực tiếp từ lớp App (Sử dụng tính năng static từ App.java)
        if (this.connection == null) {
            this.connection = view.App.connection;
        }

        // 3. Bây giờ connection chắc chắn đã có dữ liệu, gọi an toàn không lo crash
        this.connection.setMessageListener(this);

        this.primaryStage.setTitle("Hệ thống Đấu giá - Tài khoản Người dùng");

        // 1. Tiêu đề giao diện
        Label lblTitle = new Label("QUẢN LÝ TÀI KHOẢN ĐẤU GIÁ");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblTitle.setTextFill(Color.web("#2C3E50"));

        // 2. Khu vực thông tin người dùng
        GridPane gridInfo = new GridPane();
        gridInfo.setHgap(10);
        gridInfo.setVgap(10);
        gridInfo.setPadding(new Insets(15));
        gridInfo.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5;");

        gridInfo.add(new Label("Tên người dùng:"), 0, 0);
        Label lblUsername = new Label("NguyenVanA123");
        lblUsername.setStyle("-fx-font-weight: bold;");
        gridInfo.add(lblUsername, 1, 0);

        gridInfo.add(new Label("Email:"), 0, 1);
        gridInfo.add(new Label("nguyenvana@gmail.com"), 1, 1);

        gridInfo.add(new Label("Số dư hiện tại:"), 0, 2);
        lblSoDu = new Label(formatCurrency(soDu));
        lblSoDu.setStyle("-fx-font-weight: bold; -fx-text-fill: #27AE60; -fx-font-size: 16px;");
        gridInfo.add(lblSoDu, 1, 2);

        // 3. Khu vực chức năng Nạp / Rút tiền
        VBox vboxActions = new VBox(15);
        vboxActions.setPadding(new Insets(15));
        vboxActions.setStyle("-fx-border-color: #BDC3C7; -fx-border-radius: 5;");

        Label lblActionTitle = new Label("Giao dịch tài chính");
        lblActionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Nhập số tiền (VND)...");

        // Chỉ cho phép nhập số vào ô tiền
        txtAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtAmount.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        HBox hboxButtons = new HBox(15);
        hboxButtons.setAlignment(Pos.CENTER);

        Button btnNapTien = new Button("Nạp tiền");
        btnNapTien.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnNapTien.setPrefWidth(100);

        Button btnRutTien = new Button("Rút tiền");
        btnRutTien.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnRutTien.setPrefWidth(100);

        hboxButtons.getChildren().addAll(btnNapTien, btnRutTien);
        vboxActions.getChildren().addAll(lblActionTitle, txtAmount, hboxButtons);

        // 4. Xử lý sự kiện nút Nạp tiền
        btnNapTien.setOnAction(e -> {
            String input = txtAmount.getText().trim();
            if (input.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập số tiền cần nạp.");
                return;
            }
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Lỗi", "Số tiền nạp phải lớn hơn 0.");
                    return;
                }

                String command = "RECHARGE|" + amount;
                connection.sendCommand(command);

                System.out.println("[LOG SENT]: Đã gửi yêu cầu nạp tiền -> " + command);
                txtAmount.clear();


            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ.");
            }




        });

        // 5. Xử lý sự kiện nút Rút tiền
        btnRutTien.setOnAction(e -> {
            String input = txtAmount.getText();
            if (input.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập số tiền cần rút.");
                return;
            }
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Lỗi", "Số tiền rút phải lớn hơn 0.");
                return;
            }
            try {
                double amount1 = Double.parseDouble(input);
                if (amount1 <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Lỗi", "Số tiền rút phải lớn hơn 0.");
                    return;
                }

                // CHUẨN HÓA LỆNH: WITHDRAW|Số_tiền
                String command = "WITHDRAW|" + amount;
                connection.sendCommand(command);

                System.out.println("[LOG SENT]: Đã gửi yêu cầu rút tiền -> " + command);
                txtAmount.clear();

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ.");
            }


        });

        // Layout chính
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(lblTitle, gridInfo, vboxActions);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Hàm cập nhật nhãn hiển thị số dư
    private void capNhatSoDu() {
        lblSoDu.setText(formatCurrency(soDu));
    }

    // Hàm định dạng tiền tệ VND
    private String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    // Hàm hiển thị thông báo nhanh
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void onMessageReceived(String serverMessage) {
        javafx.application.Platform.runLater(() -> {
            System.out.println("Nhận được phản hồi tài chính: " + serverMessage);

            String[] tokens = serverMessage.split("\\|");
            String header = tokens[0];

            if ("BALANCE_UPDATE".equals(header)) {
                // Lấy số dư mới do Server tính toán và trả về
                String newBalanceStr = tokens[1];


                lblSoDu.setText("Số dư: " + formatCurrency(Double.parseDouble(newBalanceStr)) + " VND");

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Tài khoản của bạn đã được cập nhật số dư mới!");
            }
            else if ("WITHDRAW_ERR_INSUFFICIENT".equals(header)) {

                showAlert(Alert.AlertType.ERROR, "Thất bại", "Số dư tài khoản không đủ để thực hiện giao dịch này!");
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}


