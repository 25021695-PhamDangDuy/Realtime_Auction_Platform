package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.Item;
import models.User;
import server.GsonUtil;
import view.network.MessageListener;
import view.network.ServerConnection;

import java.util.List;
import java.util.UUID;


public class UserDashboardScreen extends Application implements MessageListener {
    private User currentUser;
    private ServerConnection connection;
    private Stage primaryStage;

    // Các thành phần UI cốt lõi
    private BorderPane root;
    private VBox centerContent;

    // Các thành phần UI cần cập nhật dữ liệu sau khi Server trả về
    private Label lblProfileName;
    private Label lblProfileRole;

    private Label lblWalletBalance;
    private Label lblLockedBalance;
    private VBox actionFormBox;
    //contructor
    public UserDashboardScreen(ServerConnection connection, Stage primaryStage, User currentUser) {
        this.connection = connection;
        this.primaryStage = primaryStage;
        this.currentUser=currentUser;
    }
    public UserDashboardScreen(){};

    //hàm start(khởi taạo UI)
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        //Chuyển tai nghe về màn hình này
        connection.setMessageListener(this);
        primaryStage.setTitle("Bảng Điều Khiển Cá Nhân");

        root = new BorderPane();
        root.setPrefSize(900, 600);

        // Khởi tạo vùng hiển thị ở giữa (Hiện lời chào mặc định)
        centerContent = new VBox(20);
        centerContent.setPadding(new Insets(30));
        centerContent.setAlignment(Pos.TOP_LEFT);
        showWelcomeScreen();

        root.setCenter(centerContent);

        // Khởi tạo thanh Menu bên trái
        root.setLeft(createSidebar());

        // Gắn vào Scene và hiển thị
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //xây dựng thanh menu bên trái
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPrefWidth(220);

        Label lblMenu = new Label("BẢNG ĐIỀU KHIỂN");
        lblMenu.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // --- 1. NHÓM NÚT CƠ BẢN (AI CŨNG CÓ) ---
        Button btnHome = createMenuButton("🏠 Về Trang Chủ");
        btnHome.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");
        btnHome.setOnAction(e -> {
            System.out.println("Quay về Sảnh...");
            // view.AuctionHomeScreen home = new view.AuctionHomeScreen(connection, primaryStage);
            // try { home.start(primaryStage); } catch (Exception ex) { ex.printStackTrace(); }
        });

        Button btnProfile = createMenuButton("👤 Hồ sơ cá nhân");
        btnProfile.setOnAction(e -> triggerLoadProfile());

        Button btnWallet = createMenuButton("💳 Quản lý ví tiền");
        btnWallet.setOnAction(e -> triggerLoadWallet());

        // Đổ nhóm cơ bản vào Sidebar
        sidebar.getChildren().addAll(lblMenu, btnHome, new Label(""), btnProfile, btnWallet);

        // --- 2. NHÓM NÚT ĐẶC THÙ (PHÂN QUYỀN BIDDER / SELLER) ---
        // Lưu ý: Biến currentUser phải được truyền vào từ Constructor
        if (currentUser instanceof models.Bidder) {
            Button btnHistory = createMenuButton("📜 Lịch Sử Đấu Giá");
            // btnHistory.setOnAction(e -> ...);

            Button btnWonItems = createMenuButton("🏆 Tài Sản Đã Mua");
            btnWonItems.setOnAction(e -> {
                // Dọn dẹp màn hình giữa và hiện chữ Loading
                centerContent.getChildren().clear();
                centerContent.getChildren().add(new Label("Đang lấy danh sách từ kho đồ..."));

                // Gửi lệnh lên Server (Gọi đúng cái case "SOLD" của bạn)
                if (connection != null) {
                    connection.sendCommand("GET_MY_ITEMS|SOLD");
                }
            });

            sidebar.getChildren().addAll(new Label(""), btnHistory, btnWonItems);

        } else if (currentUser instanceof models.Seller) {
            Button btnInventory = createMenuButton("📦 Kho Đồ Của Tôi");
            // btnInventory.setOnAction(e -> ...);

            Button btnMyRooms = createMenuButton("⚖️ Phòng Đấu Giá");
            // btnMyRooms.setOnAction(e -> ...);

            sidebar.getChildren().addAll(new Label(""), btnInventory, btnMyRooms);
        }

        return sidebar;
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-alignment: BASELINE_LEFT; -fx-font-size: 14px;");
        return btn;
    }

    // ==========================================================
    // 4. KÍCH HOẠT LỆNH & VẼ KHUNG CHỜ
    // ==========================================================
    private void showWelcomeScreen() {
        centerContent.getChildren().clear();
        Label lblWelcome = new Label("Chào mừng đến với hệ thống quản lý!");
        lblWelcome.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        centerContent.getChildren().add(lblWelcome);
    }

    private void triggerLoadProfile() {
        // 1. Xóa nội dung cũ đang hiển thị ở phần giữa màn hình
        centerContent.getChildren().clear();

        // 2. Tạo các Label mới và lấy dữ liệu TRỰC TIẾP từ biến currentUser
        Label lblTitle = new Label("HỒ SƠ CÁ NHÂN");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Label khoảng trắng để tạo khoảng cách cho đẹp
        Label spacer = new Label("");

        Label lblId = new Label("ID: " + currentUser.getID().toString());
        lblId.setFont(Font.font("Arial", 16));

        Label lblUsername = new Label("Tài khoản: " + currentUser.getName());
        lblUsername.setFont(Font.font("Arial", 16));

        Label lblName = new Label("Tên hiển thị: " + currentUser.getName());
        lblName.setFont(Font.font("Arial", 16));

        // Dùng reflection lấy thẳng tên Class (Bidder hoặc Seller) để hiển thị
        String roleName = currentUser.getClass().getSimpleName();
        String displayRole = roleName.equals("Bidder") ? "Người Mua (Bidder)" : "Người Bán (Seller)";

        Label lblRole = new Label("Vai trò: " + displayRole);
        lblRole.setFont(Font.font("Arial", 16));

        // 3. Bơm tất cả vào centerContent để hiển thị ngay lập tức
        centerContent.getChildren().addAll(lblTitle, spacer, lblId, lblUsername, lblName, lblRole);
    }

    private void triggerLoadWallet() {
        centerContent.getChildren().clear();

        Label title = new Label("QUẢN LÝ TÀI CHÍNH");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        lblWalletBalance = new Label("Số dư khả dụng: Đang kết nối...");
        lblWalletBalance.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblWalletBalance.setStyle("-fx-text-fill: #27ae60;");

        lblLockedBalance = new Label("Số dư đóng băng: Đang tải...");
        lblLockedBalance.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        lblLockedBalance.setStyle("-fx-text-fill: #e67e22;");

        // Hộp chứa 3 nút thao tác
        HBox buttonBox = new HBox(15);

        Button btnDeposit = new Button("Nạp tiền");
        btnDeposit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnWithdraw = new Button("Rút tiền");
        btnWithdraw.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnHistory = new Button("Biến động");
        btnHistory.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");

        buttonBox.getChildren().addAll(btnDeposit, btnWithdraw, btnHistory);

        // Hộp chứa form nhập liệu (ban đầu rỗng)
        actionFormBox = new VBox(10);
        actionFormBox.setPadding(new Insets(15, 0, 0, 0));

        // --- GẮN SỰ KIỆN CHO CÁC NÚT ---
        btnDeposit.setOnAction(e -> {
            showTransactionForm(actionFormBox, "Nạp tiền", "DEPOSIT", "Hãy nhập số tiền cần nạp:");
        });

        btnWithdraw.setOnAction(e -> {
            showTransactionForm(actionFormBox, "Rút tiền", "WITHDRAW", "Hãy nhập số tiền cần rút:");
        });

        btnHistory.setOnAction(e -> {
            // Logic tạm để bạn bổ sung sau
            actionFormBox.getChildren().clear();
            actionFormBox.getChildren().add(new Label("Chức năng xem biến động ví đang được xây dựng..."));
            // connection.sendCommand("GET_WALLET_HISTORY");
        });

        // Đổ toàn bộ vào khung giữa
        centerContent.getChildren().addAll(title, lblWalletBalance, lblLockedBalance, buttonBox, actionFormBox);

        // Gửi lệnh lấy số dư hiện tại
        if (connection != null) {
            connection.sendCommand("VIEW_WALLET");
        }
    }

    // HÀM TẠO FORM NHẬP TIỀN BÊN DƯỚI CÁC NÚT
    private void showTransactionForm(VBox container, String actionName, String commandType, String instructionText) {
        container.getChildren().clear(); // Dọn dẹp form cũ nếu có

        Label lblInstruct = new Label(instructionText);
        lblInstruct.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Nhập số tiền (VD: 50000)");
        txtAmount.setMaxWidth(250);

        Label lblStatus = new Label(); // Dùng để báo lỗi định dạng hoặc báo đang gửi
        lblStatus.setStyle("-fx-text-fill: red;");

        Button btnConfirm = new Button("Xác nhận");
        btnConfirm.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");

        Button btnCancel = new Button("Hủy");

        HBox formButtons = new HBox(10, btnConfirm, btnCancel);

        // Bấm hủy thì ẩn form đi
        btnCancel.setOnAction(e -> container.getChildren().clear());

        // Bấm Xác nhận thì kiểm tra và gửi mạng
        btnConfirm.setOnAction(e -> {
            String amountStr = txtAmount.getText().trim();
            try {
                long amount = Long.parseLong(amountStr);
                if (amount <= 0) {
                    lblStatus.setText("Số tiền phải lớn hơn 0!");
                    return;
                }

                // Gửi lệnh lên Server (VD: DEPOSIT|500000)
                if (connection != null) {
                    connection.sendCommand(commandType + "|" + amount);
                }

                // Khóa nút để không bấm 2 lần, đổi chữ thông báo
                btnConfirm.setDisable(true);
                lblStatus.setStyle("-fx-text-fill: #e67e22;");
                lblStatus.setText("⏳ Đang xử lý giao dịch, vui lòng chờ...");

            } catch (NumberFormatException ex) {
                lblStatus.setText("Lỗi: Bạn phải nhập con số hợp lệ!");
            }
        });

        container.getChildren().addAll(lblInstruct, txtAmount, lblStatus, formButtons);
    }
    private void showBoughtItemsUI(List<models.Item> items) {
        centerContent.getChildren().clear();

        javafx.scene.control.Label lblTitle = new javafx.scene.control.Label("TÀI SẢN ĐÃ MUA");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Nếu list trống, báo cho người ta biết
        if (items == null || items.isEmpty()) {
            centerContent.getChildren().addAll(lblTitle, new javafx.scene.control.Label("Kho đồ trống trơn. Hãy ra chợ chốt đơn đi bạn!"));
            return;
        }

        // Khởi tạo cái Bảng
        javafx.scene.control.TableView<models.Item> table = new javafx.scene.control.TableView<>();

        // Cột 1: Tên vật phẩm (Chữ "name" phải khớp với tên biến trong class Item của bạn)
        javafx.scene.control.TableColumn<models.Item, String> colName = new javafx.scene.control.TableColumn<>("Tên vật phẩm");
        colName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));

        // Cột 2: Giá trị (Thay "currentPrice" bằng tên biến lưu giá trị món đồ)
        javafx.scene.control.TableColumn<models.Item, Double> colPrice = new javafx.scene.control.TableColumn<>("Giá chốt");
        colPrice.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("currentPrice"));

        // Cột 3: Mô tả
        javafx.scene.control.TableColumn<models.Item, String> colDesc = new javafx.scene.control.TableColumn<>("Mô tả");
        colDesc.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));

        // Nhét các cột vào bảng, nhét dữ liệu vào bảng
        table.getColumns().addAll(colName, colPrice, colDesc);
        table.getItems().addAll(items);

        // Ép bảng tự động giãn vừa khung hình
        table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);

        // Bơm tất cả ra màn hình
        centerContent.getChildren().addAll(lblTitle, table);
    }

    // ==========================================================
    // 5. TRẠM THU PHÁT: GHI ĐÈ HÀM TỪ MessageListener
    // ==========================================================
    @Override
    public void onMessageReceived(String message) {
        String[] parts = message.split("\\|", 2);
        String command = parts[0];
        String data = (parts.length > 1) ? parts[1] : "";

        // TẤT CẢ TƯƠNG TÁC GIAO DIỆN PHẢI NẰM TRONG Platform.runLater
        Platform.runLater(() -> {
            switch (command) {
                case "SUCCESS_GET_ITEMS":
                    String itemsJson = parts[1];

                    // Dùng bùa chú GSON để đúc chuỗi JSON trở lại thành List<Item>
                    java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<models.Item>>(){}.getType();
                    List<Item> boughtItems = GsonUtil.gson.fromJson(itemsJson, listType);

                    // Bắt buộc dùng Platform.runLater để vẽ lên Giao diện (vì đang ở luồng mạng)
                    javafx.application.Platform.runLater(() -> {
                        showBoughtItemsUI(boughtItems);
                    });
                    break;

                case "SUCCESS_WALLET_BALANCE":
                    String[] balances = data.split("\\|");
                    if (balances.length == 2 && lblWalletBalance != null) {
                        lblWalletBalance.setText("Số dư khả dụng: " + balances[0] + " VNĐ");
                        lblLockedBalance.setText("Số dư đóng băng: " + balances[1] + " VNĐ");

                    }
                    break;
                case "SUCCESS_DEPOSIT":
                case "SUCCESS_WITHDRAW":
                    // Hiện Popup báo thành công
                    javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thành công");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText(data);
                    successAlert.show();

                    // Dọn dẹp cái form nhập tiền (ẩn nó đi)
                    if (actionFormBox != null) {
                        actionFormBox.getChildren().clear();
                    }

                    // Bắn lệnh xin lại số dư mới nhất để chữ nảy số tự động
                    if (connection != null) {
                        connection.sendCommand("VIEW_WALLET");
                    }
                    break;
                case "ERROR":
                    System.out.println("Lỗi từ Server: " + data);
                    // Cập nhật lên một Label thông báo lỗi nào đó nếu cần
                    break;

                default:
                    System.out.println("Lệnh không xác định: " + command);
                    break;
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}







