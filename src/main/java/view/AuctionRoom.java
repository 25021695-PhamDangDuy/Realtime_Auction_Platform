package view;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import view.network.MessageListener;
import view.network.ServerConnection;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuctionRoom extends Application implements MessageListener {
    private String roomId = "";

    // Thông tin phiên đấu giá hiện tại
    private String itemName;
    private String sellerName;
    private String dateStr;
    private long initPrice ;
    private long currentPrice;
    private javafx.scene.image.ImageView imgProductView;
    private int timeLeft;
    private Label lblCountdown;
    private Label lblMoneyToWords;
    private javafx.scene.control.TextField txtBidInput;

    private Button btnSubmitBid;
    private VBox historyLogBox;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ServerConnection connection = new ServerConnection();
    private Stage primaryStage;
    // Giả sử bạn có một Label hoặc Text để hiển thị mức giá cao nhất hiện tại
    private javafx.scene.control.Label lblCurrentPrice;
    private javafx.scene.control.Label lblName;
    private javafx.scene.control.Label lblMessage;
    public AuctionRoom(ServerConnection connection, Stage primaryStage) {
        this.connection = connection;
        this.primaryStage = primaryStage;
    }

    public AuctionRoom() {
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        connection.setMessageListener(this);

        // 2. Khởi tạo ImageView hiển thị ảnh sản phẩm (Kích thước tùy chọn)
        imgProductView = new javafx.scene.image.ImageView();
        imgProductView.setFitWidth(250);
        imgProductView.setFitHeight(250);
        imgProductView.setPreserveRatio(true);


        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // 1. TOP BAR (Thanh quay lại và Tiêu đề phiên)
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // 2. MAIN LAYOUT: CHIA THÀNH 2 CỘT CHÍNH (HBox)
        HBox mainContent = new HBox(30);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);

        // --- CỘT TRÁI: THÔNG TIN VẬT PHẨM (45% Chiều rộng) ---
        VBox leftColumn = createLeftColumn();
        leftColumn.setPrefWidth(420);

        // --- CỘT PHẢI: BẢNG ĐIỀU KHIỂN ĐẤU GIÁ TRỰC TIẾP (55% Chiều rộng) ---
        VBox rightColumn = createRightColumn();
        rightColumn.setPrefWidth(500);

        mainContent.getChildren().addAll(leftColumn, rightColumn);
        root.setCenter(mainContent);

        // 3. TASKBAR (Chân trang cố định)
        HBox taskbar = createTaskbar();
        root.setBottom(taskbar);

        // Kích hoạt bộ đếm ngược thời gian thực
        startCountdown();

        // Thiết lập Scene
        Scene scene = new Scene(root, 1000, 680);
        primaryStage.setTitle("Phòng Đấu Giá Trực Tuyến - " + itemName);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> executorService.shutdownNow());
        primaryStage.show();
    }

    // --- XÂY DỰNG CÁC VÙNG GIAO DIỆN CỤ THỂ ---

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button btnBack = new Button("⬅ Quay lại");
        btnBack.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-cursor: hand;");
        btnBack.setOnAction(e -> System.out.println("Quay lại màn hình chính Dashboard..."));

        Label lblRoomTitle = new Label("PHÒNG ĐẤU GIÁ TÀI SẢN TRỰC TUYẾN");
        lblRoomTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        topBar.getChildren().addAll(btnBack, lblRoomTitle);
        return topBar;
    }

    private VBox createLeftColumn() {
        VBox container = new VBox(15);

        // Khung chứa ảnh vuông định hình viền xanh theo mẫu thiết kế
        StackPane imgFrame = new StackPane(new Label(" HÌNH ẢNH SẢN PHẨM"));
        imgFrame.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-background-color: #eaeded; -fx-border-radius: 5;");
        imgFrame.setPrefSize(420, 300);

        // Khối thông tin chi tiết
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10, 0, 10, 0));

        Label lblName = new Label(itemName);
        lblName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblName.setWrapText(true);

        Label lblSeller = new Label("Người bán: " + sellerName);
        lblSeller.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label lblDate = new Label("Ngày đăng phiên: " + dateStr);
        lblDate.setStyle("-fx-font-size: 13px; -fx-text-fill: #95a5a6; -fx-font-style: italic;");

        Label lblDetailTitle = new Label("Mô tả tài sản:");
        lblDetailTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");


        infoBox.getChildren().addAll(lblName, lblSeller, lblDate, lblDetailTitle);
        container.getChildren().addAll(imgFrame, infoBox);
        return container;
    }

    private VBox createRightColumn() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(10));

        // 1. Trạng thái & Bộ đếm ngược thời gian
        HBox statusRow = new HBox(15);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        Label lblStatus = new Label("ĐANG DIỄN RA");
        lblStatus.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 5 12; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 12px;");

        lblCountdown = new Label("⏱️ Còn lại:  ");
        lblCountdown.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        statusRow.getChildren().addAll(lblStatus, lblCountdown);

        // 2. Hiển thị Giá hiện tại & Giá khởi điểm
        VBox priceDisplayBox = new VBox(8);
        priceDisplayBox.setPadding(new Insets(15));
        priceDisplayBox.setStyle("-fx-background-color: #fff5f5; -fx-background-radius: 8; -fx-border-color: #ffe3e3;");

        Label lblInit = new Label("Giá khởi điểm: " + currencyFormat.format(initPrice));
        lblInit.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        lblCurrentPrice = new Label("GIÁ HIỆN TẠI: " + currencyFormat.format(currentPrice));
        lblCurrentPrice.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 20px; -fx-font-weight: bold;");


        priceDisplayBox.getChildren().addAll(lblInit, lblCurrentPrice);

        // 3. KHU VỰC ĐẶT GIÁ THÔNG MINH (Ô nhập tự do + Dịch chữ + Nút gợi ý nhảy nhanh)
        VBox bidActionBox = new VBox(12);
        bidActionBox.setPadding(new Insets(15));
        bidActionBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 0);");

        Label lblInputTitle = new Label("Nhập mức giá muốn trả:");
        lblInputTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Hàng chứa ô nhập tự do và nút xác nhận
        HBox inputRow = new HBox(10);
        txtBidInput = new TextField();
        txtBidInput.setPrefHeight(40);
        txtBidInput.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        HBox.setHgrow(txtBidInput, Priority.ALWAYS);
        btnSubmitBid = new Button("ĐẶT GIÁ NGAY");
        btnSubmitBid.setPrefHeight(40);
        btnSubmitBid.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        inputRow.getChildren().addAll(txtBidInput, btnSubmitBid);

        // Nhãn phụ: Tự động dịch số tiền sang CHỮ tiếng Việt để tránh gõ nhầm hàng triệu/hàng tỷ
        lblMoneyToWords = new Label("Chưa nhập số tiền");
        lblMoneyToWords.setStyle("-fx-text-fill: #2980b9; -fx-font-style: italic; -fx-font-size: 12px;");
        lblMoneyToWords.setWrapText(true);

        // Bộ lọc chặn nhập chữ, chỉ cho nhập số và cập nhật bộ dịch chữ Realtime
        txtBidInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtBidInput.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            if (!newValue.isEmpty()) {
                try {
                    long amount = Long.parseLong(newValue);
                    lblMoneyToWords.setText("Bằng chữ: " + convertNumberToWords(amount) + " đồng.");
                } catch (NumberFormatException ex) {
                    lblMoneyToWords.setText("Số tiền vượt quá giới hạn tính toán.");
                }
            } else {
                lblMoneyToWords.setText("Chưa nhập số tiền");
            }
        });


        // Sự kiện xử lý nút xác nhận Đặt Giá
        btnSubmitBid.setOnAction(e -> {
            String bidAmountStr = txtBidInput.getText().trim();
            if (bidAmountStr.isEmpty()) {
                System.out.println("Vui lòng nhập số tiền muốn đặt!");
                System.out.println("Vui lòng nhập số tiền muốn đặt!");
                lblMessage.setText("Vui lòng nhập số tiền muốn đặt!");
                return;
            }

            try {
                // Kiểm tra xem số tiền nhập vào có hợp lệ hay không (phải là số lớn hơn 0)
                long bidAmount = Long.parseLong(bidAmountStr);
                if (bidAmount <= 0) {
                    System.out.println("Số tiền đặt giá phải lớn hơn 0!");
                    System.out.println("Số tiền đặt giá phải lớn hơn 0!");
                    lblMessage.setText("Số tiền đặt giá phải lớn hơn 0!");
                    return;
                }


                String command = "BID|" + bidAmount;

                // ---- BƯỚC 3: Gọi hàm gửi lệnh đi tới server ----
                connection.sendCommand(command);
                System.out.println("[LOG SENT]: Đã gửi yêu cầu đặt giá -> " + command);
                // Xóa trống ô nhập sau khi bấm đặt để tiện cho lần nhập sau
                txtBidInput.clear();
                lblMessage.setText("Đang gửi yêu cầu đặt giá...");
                lblMessage.setTextFill(javafx.scene.paint.Color.BLUE);


            } catch (NumberFormatException ex) {
                System.err.println("[LOG ERROR]: Số tiền nhập vào không hợp lệ!");
            } catch (Exception ex) {
                System.err.println("[LOG ERROR]: Lỗi kết nối mạng khi đặt giá: " + ex.getMessage());
            }
        });
        // 4. KHỐI LỊCH SỬ TRẢ GIÁ TRỰC TIẾP (Tạo không khí kịch tính)
        VBox historyBox = new VBox(10);
        Label lblHistTitle = new Label("📜 Lịch sử đặt giá phòng đấu (Realtime Log):");
        lblHistTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        ScrollPane scrollHistory = new ScrollPane();
        scrollHistory.setPrefHeight(120);
        scrollHistory.setFitToWidth(true);

        historyLogBox = new VBox(6);
        historyLogBox.setPadding(new Insets(8));
        historyLogBox.getChildren().add(new Label("• [Hệ thống] Phiên đấu giá đã kích hoạt. Mức giá hiện tại là " + currencyFormat.format(currentPrice)));
        scrollHistory.setContent(historyLogBox);

        historyBox.getChildren().addAll(lblHistTitle, scrollHistory);

        container.getChildren().addAll(statusRow, priceDisplayBox, bidActionBox, historyBox);
        return container;
    }

    private Button createQuickBidButton(String text, long increment) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btn, Priority.ALWAYS);
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");

        // Khi click nút nhanh: Tự điền vào ô nhập mức giá mới = Giá hiện tại + Khoảng nhảy
        btn.setOnAction(e -> {
            long targetBid = currentPrice;
            txtBidInput.setText(String.valueOf(targetBid));
        });
        return btn;
    }

    private HBox createTaskbar() {
        HBox taskbar = new HBox();
        taskbar.setPadding(new Insets(10, 0, 10, 0));
        taskbar.setStyle("-fx-background-color: #2c3e50;");
        taskbar.setAlignment(Pos.CENTER);

        Button btnHome = new Button("🏠 Trang chủ");
        Button btnRoom = new Button("🔨 Phòng đấu");
        Button btnNoti = new Button("🔔 Thông báo");

        btnHome.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7;");
        btnRoom.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
        btnNoti.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7;");
        btnHome.setOnAction(event -> {
            try {
                // 1. Khởi tạo thực thể của màn hình HomeScreen
                AuctionHomeScreen homeScreen = new AuctionHomeScreen(connection,new Stage());

                // 2. Gọi hàm start và truyền cửa sổ chính primaryStage vào
                homeScreen.start(primaryStage);

                System.out.println("[NAVIGATION]: Đã chuyển về giao diện HomeScreen.");
            } catch (Exception ex) {
                System.err.println("[ERROR]: Không thể chuyển sang HomeScreen: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        taskbar.getChildren().addAll(btnHome, btnRoom, btnNoti);
        return taskbar;
    }

    // --- LOGIC XỬ LÝ NGHIỆP VỤ ĐẤU GIÁ & ĐẾM NGƯỢC ---

    private void handlePlaceBid() {
        String rawInput = txtBidInput.getText().trim();
        if (rawInput.isEmpty()) {
            showAlert("Vui lòng điền số tiền hợp lệ trước khi nhấn đặt giá!");
            return;
        }

        long userBid = Long.parseLong(rawInput);

        // KIỂM TRA LUẬT: Giá đưa ra phải lớn hơn hoặc bằng Giá hiện tại + Bước giá tối thiểu
        long requiredAmount = currentPrice;
        if (userBid < requiredAmount) {
            showAlert("Đặt giá thất bại! Mức giá bạn đưa ra phải tối thiểu đạt: " + currencyFormat.format(requiredAmount));
            return;
        }

        // Chấp nhận đặt giá thành công
        currentPrice = userBid;
        lblCurrentPrice.setText("GIÁ HIỆN TẠI: " + currencyFormat.format(currentPrice));
        txtBidInput.clear();

        // Đẩy dòng log mới vào lịch sử phòng đấu
        Label newLog = new Label("• [Bạn] vừa trả giá thành công mức: " + currencyFormat.format(currentPrice));
        newLog.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        historyLogBox.getChildren().add(0, newLog); // Chèn lên đầu danh sách log
    }

    private void startCountdown() {
        executorService.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            if (timeLeft > 0) {
                timeLeft--;
                int mins = timeLeft / 60;
                int secs = timeLeft % 60;
                lblCountdown.setText(String.format("⏱️ Còn lại: %02d:%02d", mins, secs));
            } else {
                lblCountdown.setText("❌ Phiên đã đóng!");
                btnSubmitBid.setDisable(true);
                txtBidInput.setDisable(true);
                executorService.shutdown();
            }
        }), 0, 1, TimeUnit.SECONDS);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // --- HÀM TRỢ GIÚP DỊCH SỐ THÀNH CHỮ TIẾNG VIỆT ĐƠN GIẢN (CONCEPT) ---
    private String convertNumberToWords(long number) {
        if (number == 0) return "Không";
        // Hàm rút gọn phục vụ việc demo nhanh hiển thị hàng Triệu và Tỷ cho bài tập lớn
        String[] units = {"", "nghìn", "triệu", "tỷ"};
        StringBuilder res = new StringBuilder();
        int unitIdx = 0;

        long temp = number;
        while (temp > 0) {
            long part = temp % 1000;
            if (part > 0) {
                res.insert(0, part + " " + units[unitIdx] + " ");
            }
            temp /= 1000;
            unitIdx++;
        }
        return res.toString().replaceAll("\\s+", " ").trim();
    }
    public void onMessageReceived(String serverMessage) {
        javafx.application.Platform.runLater(() -> {
            System.out.println("Nhận được dữ liệu phòng đấu giá: " + serverMessage);

            // Tách chuỗi dữ liệu nhận được bằng dấu gạch đứng
            String[] tokens = serverMessage.split("\\|");
            String header = tokens[0];
            if ("ROOM_INFO".equals(header)) {
                String targetRoomId = tokens[1];
                String productName = tokens[2];
                String currentPrice = tokens[3];
                String imageBase64 = tokens[4]; // Chuỗi ảnh Base64 lấy từ DB của server

                this.roomId = targetRoomId;
                lblName.setText("Sản phẩm: " + productName);
                lblCurrentPrice.setText("Giá hiện tại: " + currentPrice + " VND");

                // Xử lý nạp ảnh Base64 lên ImageView sản phẩm
                if (!"NO_IMAGE".equals(imageBase64) && !imageBase64.isEmpty()) {
                    try {
                        byte[] imageBytes = java.util.Base64.getDecoder().decode(imageBase64);
                        java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(imageBytes);
                        javafx.scene.image.Image image = new javafx.scene.image.Image(bis);
                        imgProductView.setImage(image);
                    } catch (Exception ex) {
                        System.out.println("Lỗi nạp ảnh sản phẩm từ Server!");
                        ex.printStackTrace();
                    }
                }
            }



            if ("BID_UPDATE".equals(header)) {
                // // tokens là roomId, tokens là giá mới, tokens là tên người đặt

                // 3. SỬA CHUẨN: Lấy index 1 và index 2 (Đã xóa bỏ hoàn toàn dấu phẩy thừa)
                String targetRoomId = tokens[1];
                String newPrice = tokens[2];
                // Kiểm tra xem có đúng là gói tin của phòng hiện tại không
                if (this.roomId.equals(targetRoomId)) {
                    lblCurrentPrice.setText("Giá hiện tại: " + newPrice + " VND");
                    lblMessage.setText("Có người vừa đặt giá mới!");
                    lblMessage.setTextFill(javafx.scene.paint.Color.GREEN);
                }
            } else if ("BID_ERR_LOW".equals(header)) {
                // Trường hợp bạn đặt giá thấp hơn giá hiện tại của phòng và bị server từ chối
                lblMessage.setText("Giá bạn đặt thấp hơn giá hiện tại của phòng!");
                lblMessage.setTextFill(javafx.scene.paint.Color.FIREBRICK);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}






