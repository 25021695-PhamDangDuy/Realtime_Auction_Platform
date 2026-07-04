package Application;
// Điều chỉnh package theo đúng cấu trúc của bạn

import controller.RegisterController;
import controller.network.MessageListener;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import controller.network.ServerConnection;
import javafx.stage.StageStyle;

import java.net.URL;


public class App extends Application implements MessageListener {

    // Khởi tạo một đối tượng kết nối duy nhất (static)
    // để các Controller khác (như AuctionLogin) có thể gọi: App.connection.sendCommand(...)
    public static final ServerConnection connection = new ServerConnection();

    @Override
    public void start(Stage primaryStage) throws Exception {

        try{
            // 1. Mở ống Socket kết nối thẳng tới Server ngay khi App vừa chạy
            boolean isConnected = connection.connect("localhost", 8080);
            connection.setMessageListener(this);

            URL url = getClass().getResource("/view/RegisterView.fxml");
            if(url == null){
                System.out.println("Url is not found!");
            }

            FXMLLoader loader = new FXMLLoader(url);

            Parent root = loader.load();
            RegisterController registerController = loader.getController();
            registerController.setConnection(connection);

            Scene scene = new Scene(root);

            primaryStage.setTitle("Register");
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.UNDECORATED);


            primaryStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }

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

    @Override
    public void onMessageReceived(String message) {

    }
}







