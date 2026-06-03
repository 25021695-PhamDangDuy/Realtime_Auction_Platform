package view;
// Điều chỉnh package theo đúng cấu trúc của bạn

import javafx.application.Application;
import javafx.stage.Stage;
import view.network.ServerConnection;

public class App extends Application {

    // Khởi tạo một đối tượng kết nối duy nhất (static)
    // để các Controller khác (như AuctionLogin) có thể gọi: App.connection.sendCommand(...)
    public static final ServerConnection connection = new ServerConnection();

    @Override
    public void start(Stage primaryStage) throws Exception {

        // 1. Mở ống Socket kết nối thẳng tới Server ngay khi App vừa chạy
        boolean isConnected = connection.connect("localhost", 8080);

        if (isConnected) {
            System.out.println("[App] Đã kết nối thành công tới Server đấu giá!");
            try {
                // GỌI GIAO DIỆN AUCTION LOGIN LÊN MÀN HÌNH
                AuctionLogin loginScreen = new AuctionLogin(connection,primaryStage);

                // Truyền cái sân khấu (primaryStage) sang cho AuctionLogin tự vẽ đồ của nó lên
                loginScreen.start(primaryStage);

            } catch (Exception e) {
                System.err.println("Lỗi khi mở giao diện đăng nhập!");
                e.printStackTrace();
            }
        } else {
            System.err.println("[App] Không thể kết nối. Vui lòng kiểm tra lại Server.");
        }

        // ========================================================
        // 2. GỌI GIAO DIỆN CỦA BẠN LÊN TẠI ĐÂY
        // Đoạn này dùng lại nguyên vẹn code khởi chạy GUI cũ của bạn.
        // Ví dụ:
        // NavigationManager.getInstance().initialize(primaryStage);
        // NavigationManager.getInstance().showLoginScreen();
        // ========================================================
    }

    @Override
    public void stop() throws Exception {
        // Hàm này tự động chạy khi người dùng bấm dấu X màu đỏ để tắt cửa sổ App.
        // Rất quan trọng: Phải đóng Socket để Server không bị treo tài nguyên.
        System.out.println("[App] Đang đóng ứng dụng và ngắt kết nối mạng...");
        super.stop();
    }

    public static void main(String[] args) {
        // Lệnh tiêu chuẩn để khởi chạy JavaFX
        launch(args);
    }
}







