package view;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuctionHomeScreen extends Application {

    private String username;
    private long userBalance ;
    private Label lblSystemTime;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final List<AuctionSession> sessionList = new ArrayList<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void start(Stage primaryStage) {

        // ROOT LAYOUT
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // [HEADER] - Hiển thị Tên user, Số dư tài khoản và Thời gian hệ thống
        HBox header = createHeader();
        root.setTop(header);

        // [CENTER] - Danh sách các phiên đấu giá
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(25));

        Label lblTitle = new Label("Danh sách các phiên đấu giá tài sản");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Lưới hiển thị danh sách các phiên (GridPane)
        GridPane sessionGrid = new GridPane();
        sessionGrid.setHgap(20);
        sessionGrid.setVgap(20);

        for (int i = 0; i < sessionList.size(); i++) {
            VBox card = createSessionCard(sessionList.get(i));
            sessionGrid.add(card, i % 3, i / 3); // Tự động xuống dòng sau mỗi 3 thẻ
        }

        mainContainer.getChildren().addAll(lblTitle, sessionGrid);
        scrollPane.setContent(mainContainer);
        root.setCenter(scrollPane);

        // [BOTTOM] - Taskbar (Thanh điều hướng) theo yêu cầu
        HBox taskbar = createTaskbar();
        root.setBottom(taskbar);

        // Kích hoạt luồng cập nhật thời gian thực
        startRealtimeClock();

        // Khởi tạo Cửa sổ ứng dụng
        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setTitle("Trang Chủ Hệ Thống Đấu Giá - Màn Hình Chính");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> executorService.shutdownNow()); // Tắt thread khi thoát app
        primaryStage.show();
    }

    // --- TOÀN BỘ THÀNH PHẦN GIAO DIỆN THEO ĐÚNG ĐỀ BÀI ---

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setStyle("-fx-background-color: #2c3e50;");
        header.setAlignment(Pos.CENTER_LEFT);

        // Thành phần: Tên user & Số dư tài khoản
        VBox userBox = new VBox(4);
        Label lblUser = new Label("👤 Người dùng: " + username);
        lblUser.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label lblBalance = new Label("💰 Số dư: " + currencyFormat.format(userBalance));
        lblBalance.setStyle("-fx-font-size: 13px; -fx-text-fill: #f1c40f; -fx-font-weight: bold;");
        userBox.getChildren().addAll(lblUser, lblBalance);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Thành phần: Thời gian hệ thống
        lblSystemTime = new Label();
        lblSystemTime.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-background-color: rgba(255,255,255,0.15); -fx-padding: 6 12 6 12; -fx-background-radius: 5;");

        header.getChildren().addAll(userBox, spacer, lblSystemTime);
        return header;
    }

    private VBox createSessionCard(AuctionSession session) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 0);");
        card.setPrefWidth(300);

        // 1. Label hiển thị Trạng thái (Bám sát thiết kế: Chưa bắt đầu, Đang diễn ra, Đã kết thúc)
        Label lblStatus = new Label(session.status);
        if (session.status.equals("Đang diễn ra")) {
            lblStatus.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 15; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else if (session.status.equals("Chưa bắt đầu")) {
            lblStatus.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 15; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            lblStatus.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 15; -fx-font-size: 11px; -fx-font-weight: bold;");
        }

        // HBox chứa Trạng thái ở góc trái và Ngày tháng ở góc phải (dd/mm/yyyy)
        HBox topRow = new HBox();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label lblDate = new Label(session.dateStr);
        lblDate.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-font-size: 12px;");
        topRow.getChildren().addAll(lblStatus, spacer, lblDate);

        // 2. Ô vuông chứa ảnh tài sản (Bám sát hình vẽ của bạn)
        StackPane imgBox = new StackPane(new Label("🖼️ ẢNH TÀI SẢN"));
        imgBox.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-background-color: #f8f9fa; -fx-border-radius: 4;");
        imgBox.setPrefSize(120, 120); // Tạo khung vuông ô ảnh

        // 3. Thông tin chi tiết sản phẩm
        Label lblName = new Label(session.itemName);
        lblName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblName.setWrapText(true);

        Label lblPrice = new Label("Giá khởi điểm: " + currencyFormat.format(session.price));
        lblPrice.setStyle("-fx-font-size: 13px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        // 4. Nút hành động: Chỉ dẫn tới phòng đấu chứ không đấu trực tiếp tại đây
        Button btnView = new Button();
        if (session.status.equals("Đang diễn ra")) {
            btnView.setText("Vào phòng đấu ngay 🔨");
            btnView.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        } else if (session.status.equals("Chưa bắt đầu")) {
            btnView.setText("Đăng ký đặt chỗ trước");
            btnView.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-cursor: hand;");
        } else {
            btnView.setText("Xem kết quả phiên");
            btnView.setStyle("-fx-background-color: #eaeded; -fx-text-fill: #7f8c8d; -fx-cursor: hand;");
        }
        btnView.setMaxWidth(Double.MAX_VALUE);

        // Sự kiện khi nhấn nút (Sẽ code chuyển Scene sang phòng đấu ở đây)
        btnView.setOnAction(e -> {
            System.out.println("Chuyển hướng người dùng sang phòng đấu của: " + session.itemName);
            AuctionRoom room = new AuctionRoom();
        });

        card.getChildren().addAll(topRow, imgBox, lblName, lblPrice, btnView);
        return card;
    }

    private HBox createTaskbar() {
        // Thanh điều hướng (Taskbar) dưới đáy màn hình
        HBox taskbar = new HBox();
        taskbar.setPadding(new Insets(12, 0, 12, 0));
        taskbar.setStyle("-fx-background-color: #2c3e50;");
        taskbar.setAlignment(Pos.CENTER);

        Button btnHome = createNavButton("🏠 Trang chủ", true);
        Button btnRoom = createNavButton("🔨 Phòng đấu giá", false);
        Button btnNoti = createNavButton("🔔 Thông báo", false);
        Button btnProfile = createNavButton("👤 Tài khoản", false);
        btnRoom.setOnAction( event ->  {
            try {
                Stage currentStage = (Stage) btnRoom.getScene().getWindow();
                AuctionRoom room = new AuctionRoom();
                Stage roomStage = new Stage();
                room.start(roomStage);
                currentStage.close();
                System.out.println("[LOG NAVIGATION]: Đã chuyển từ Trang chủ sang Phòng đấu giá thành công.");
            } catch (Exception e) {
                System.err.println("[LOG ERROR]: Không thể chuyển cảnh sang phòng đấu giá: " + e.getMessage());
                e.printStackTrace();
            }
        });
        btnProfile.setOnAction(event -> {
            try{
                Stage currentStage = (Stage) btnProfile.getScene().getWindow();
                Account account = new Account();
                Stage accountStage = new Stage();
                account.start(accountStage);
                currentStage.close();
                System.out.println("[LOG NAVIGATION]: Chuyển cửa sổ sang Account thành công.");
            } catch (Exception e) {
                System.err.println("[LOG ERROR]: Không thể chuyển cảnh tài khoản: " + e.getMessage());
                e.printStackTrace();
            }
        });
        taskbar.getChildren().addAll(btnHome, btnRoom, btnNoti, btnProfile);
        return taskbar;
    }
    private Button createNavButton(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setPrefWidth(140);
        if (isActive) {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-font-size: 13px; -fx-cursor: hand;");
        }
        return btn;
    }
    // --- LOGIC ĐỒNG HỒ REALTIME ---
    private void startRealtimeClock() {
        executorService.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            lblSystemTime.setText("⏱ Hệ thống: " + now.format(formatter));
        }), 0, 1, TimeUnit.SECONDS);
    }


    public static void main(String[] args) {
        launch(args);
    }

    // --- LỚP QUẢN LÝ THÔNG TIN PHIÊN ĐẤU GIÁ ---
    static class AuctionSession {
        String status;   // Chưa bắt đầu, Đang diễn ra, Đã kết thúc
        String dateStr;  // dd/mm/yyyy
        String itemName; // Tên tài sản đấu giá
        long price;      // Giá khởi điểm

        public AuctionSession(String status, String dateStr, String itemName, long price) {
            this.status = status;
            this.dateStr = dateStr;
            this.itemName = itemName;
            this.price = price;
        }
    }
}