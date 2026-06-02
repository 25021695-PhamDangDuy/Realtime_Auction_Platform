package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AuctionNotificationView extends Application {

    // Khai báo biến ở phạm vi Class để hàm onMessageReceived sau này có thể thay đổi được nội dung
    private Text txtStatusMessage;
    private Text txtSubDetail;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Thông Báo Phiên Đấu Giá");

        // 1. Dùng VBox xếp chồng từ trên xuống dưới, căn giữa tuyệt đối
        VBox rootBox = new VBox(25); // Khoảng cách giữa các phần tử là 25px
        rootBox.setAlignment(Pos.CENTER);
        rootBox.setPadding(new Insets(40));
        rootBox.setStyle("-fx-background-color: #ffffff;"); // Nền trắng sạch sẽ

        // 2. Icon hoặc Ký tự trạng thái lớn làm điểm nhấn trực quan
        Text txtIcon = new Text("📢");
        txtIcon.setFont(Font.font("Arial", 48));

        // 3. Tiêu đề chính của thông báo
        Text txtHeader = new Text("THÔNG BÁO CUỘC ĐẤU GIÁ");
        txtHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        txtHeader.setFill(Color.web("#7f8c8d"));



        // 6. Nút "Quay lại đăng nhập" đặt ở dưới cùng
        Button btnBackToLogin = new Button("← Quay lại đăng nhập");
        btnBackToLogin.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9; -fx-underline: true; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Xử lý sự kiện click để quay lại màn hình AuctionLogin của bạn
        btnBackToLogin.setOnAction(event -> {
            try {
                AuctionLogin loginView = new AuctionLogin();
                Stage currentStage = (Stage) btnBackToLogin.getScene().getWindow();
                loginView.start(currentStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 7. Gom tất cả các phần tử vào VBox
        rootBox.getChildren().addAll(txtIcon, txtHeader, btnBackToLogin);

        // 8. Tạo Scene và hiển thị lên Stage
        Scene scene = new Scene(rootBox, 450, 400);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Cố định kích thước giao diện thông báo cho đẹp
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    public void onMessageReceived(String serverMessage) {
        javafx.application.Platform.runLater(() -> {

            if (serverMessage.equals("AUCTION_READY")) {
                txtStatusMessage.setText("🟢 Phiên đấu giá ĐÃ SẴN SÀNG!");
                txtStatusMessage.setFill(Color.web("#27ae60")); // Đổi chữ sang màu xanh lá
                txtSubDetail.setText("Hệ thống sẽ tự động đưa bạn vào phòng trong vài giây...");

            } else if (serverMessage.equals("AUCTION_ENDED")) {
                txtStatusMessage.setText("🏁 Phiên đấu giá ĐÃ KẾT THÚC!");
                txtStatusMessage.setFill(Color.web("#2c3e50")); // Đổi sang màu tối
                txtSubDetail.setText("Cảm ơn bạn đã tham gia cuộc đấu giá ngày hôm nay.");
            }

        });
    }
}
