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

    private javafx.scene.control.TableView<models.Item> tableInventory = new javafx.scene.control.TableView<>();
    private String currentFilter = "AVAILABLE";
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
    // xây dựng thanh menu bên trái
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new javafx.geometry.Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPrefWidth(220);

        javafx.scene.control.Label lblMenu = new javafx.scene.control.Label("BẢNG ĐIỀU KHIỂN");
        lblMenu.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // --- 1. NHÓM NÚT CƠ BẢN (AI CŨNG CÓ) ---
        javafx.scene.control.Button btnHome = createMenuButton(" 🏠 Về Trang Chủ");
        btnHome.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");
        btnHome.setOnAction(e -> {
            System.out.println("Quay về Sảnh...");
            // Gọi logic chuyển về màn hình chính của bạn ở đây
        });

        javafx.scene.control.Button btnProfile = createMenuButton(" 👤 Hồ sơ cá nhân");
        btnProfile.setOnAction(e -> triggerLoadProfile());

        javafx.scene.control.Button btnWallet = createMenuButton(" 💳 Quản lý ví tiền");
        btnWallet.setOnAction(e -> triggerLoadWallet());

        // Đổ nhóm cơ bản vào Sidebar
        sidebar.getChildren().addAll(lblMenu, btnHome, new javafx.scene.control.Label(""), btnProfile, btnWallet);


        // --- 2. NHÓM NÚT ĐẶC THÙ (PHÂN QUYỀN BIDDER / SELLER) ---

        // Khối 1: Bất kỳ ai mang dòng máu Bidder (Bidder gốc hoặc Seller kế thừa) đều có nút đi mua hàng
        if (currentUser instanceof models.Bidder) {
            javafx.scene.control.Button btnHistory = createMenuButton(" 📜 Lịch Sử Đấu Giá");
            // btnHistory.setOnAction(e -> ...);

            javafx.scene.control.Button btnWonItems = createMenuButton(" 🏆 Tài Sản Đã Mua");
            btnWonItems.setOnAction(e -> {
                // Dọn dẹp màn hình giữa và hiện chữ Loading
                centerContent.getChildren().clear();
                centerContent.getChildren().add(new javafx.scene.control.Label("Đang lấy danh sách từ kho đồ..."));

                // Gửi lệnh lên Server
                if (connection != null) {
                    connection.sendCommand("GET_MY_ITEMS|SOLD");
                }
            });

            sidebar.getChildren().addAll(new javafx.scene.control.Label(""), btnHistory, btnWonItems);
        }

        // Khối 2: ĐÃ XÓA CHỮ "else". Giờ nó là một cổng kiểm tra độc lập.
        // Chỉ những ai thực sự có nhãn mác Seller mới lọt được qua cổng này để lấy thêm nút bán hàng!
        if (currentUser instanceof models.Seller) {

            // Đường kẻ mờ phân tách khu vực
            javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
            separator.setStyle("-fx-background-color: #ecf0f1; -fx-opacity: 0.2;");

            javafx.scene.control.Button btnInventory = createMenuButton(" 📦 Kho Đồ Của Tôi");

            // GẮN SỰ KIỆN NÚT BẤM VÀO ĐÂY:
            btnInventory.setOnAction(e -> {
                // Đặt lại filter mặc định là AVAILABLE mỗi khi bấm từ menu vào
                currentFilter = "AVAILABLE";

                // Gọi hàm vẽ giao diện và tự động bắn lệnh lên Server
                initInventoryUI();
            });

            javafx.scene.control.Button btnMyRooms = createMenuButton(" ⚖ Phòng Đấu Giá");
            // btnMyRooms.setOnAction(e -> ...);

            // Bơm thêm vào thanh Sidebar hiện tại
            sidebar.getChildren().addAll(new javafx.scene.control.Label(""), separator, btnInventory, btnMyRooms);
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
        centerContent.getChildren().clear();

        // 2. Tạo các Label mới và lấy dữ liệu TRỰC TIẾP từ biến currentUser
        Label lblTitle = new Label("HỒ SƠ CÁ NHÂN");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Label khoảng trắng để tạo khoảng cách cho đẹp
        Label spacer = new Label("");

        Label lblId = new Label("ID: " + currentUser.getID().toString());
        lblId.setFont(Font.font("Arial", 16));

        // Đã sửa lại thành getUsername() cho chuẩn xác
        Label lblUsername = new Label("Tài khoản: " + currentUser.getName());
        lblUsername.setFont(Font.font("Arial", 16));

        Label lblName = new Label("Tên hiển thị: " + currentUser.getName());
        lblName.setFont(Font.font("Arial", 16));

        // Dùng reflection lấy thẳng tên Class (Bidder hoặc Seller) để hiển thị
        String roleName = currentUser.getClass().getSimpleName();
        String displayRole = roleName.equals("Bidder") ? "Người Mua (Bidder)" : "Người Bán (Seller)";

        Label lblRole = new Label("Vai trò: " + displayRole);
        lblRole.setFont(Font.font("Arial", 16));

        // =========================================================
        // KHU VỰC CHỨA VAI TRÒ VÀ NÚT NÂNG CẤP (Dùng HBox để xếp ngang)
        // =========================================================
        javafx.scene.layout.HBox roleBox = new javafx.scene.layout.HBox(15);
        roleBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        roleBox.getChildren().add(lblRole);

        // Chỉ "mọc" ra nút nâng cấp nếu người này đang là Bidder
        if (roleName.equals("Bidder")) {
            Button btnUpgrade = new Button("Nâng cấp lên Seller");
            btnUpgrade.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

            btnUpgrade.setOnAction(e -> {
                // Đổi text và khóa nút lại để tránh user bấm spam 2, 3 lần
                btnUpgrade.setText("Đang xử lý...");
                btnUpgrade.setDisable(true);

                // Gửi lệnh lên Server (Lưu ý: biến connection có thể khác tùy file của bạn,
                // ví dụ: App.connection.sendCommand(...) nếu connection là biến static)
                if (connection != null) {
                    connection.sendCommand("UPGRADE_SELLER");
                }
            });

            // Nhét nút vào HBox bên cạnh cái Label Vai trò
            roleBox.getChildren().add(btnUpgrade);
        }
        // =========================================================

        // 3. Bơm tất cả vào centerContent để hiển thị ngay lập tức
        // CHÚ Ý: Thay lblRole cũ bằng cái roleBox mới
        centerContent.getChildren().addAll(lblTitle, spacer, lblId, lblUsername, lblName, roleBox);
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
    // HÀM PHỤ TRỢ: SETUP CÁC CỘT CHO BẢNG KHO ĐỒ
    // ==========================================================
    private void setupTableColumns(javafx.scene.control.TableView<models.Item> table) {
        // 1. Xóa sạch cột cũ đi để tránh lỗi nhân đôi cột khi bấm load lại nhiều lần
        table.getColumns().clear();

        // 2. Cột Tên vật phẩm (Lưu ý: Chữ "name" phải khớp với tên biến trong class Item)
        javafx.scene.control.TableColumn<models.Item, String> colName = new javafx.scene.control.TableColumn<>("Tên vật phẩm");
        colName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));

        // 3. Cột Giá (Thay "currentPrice" bằng đúng tên biến lưu giá trong class Item )
        javafx.scene.control.TableColumn<models.Item, Double> colPrice = new javafx.scene.control.TableColumn<>("Giá (VNĐ)");
        colPrice.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("currentPrice"));

        // 4. Cột Mô tả (Thay "description" bằng đúng tên biến mô tả trong class Item)
        javafx.scene.control.TableColumn<models.Item, String> colDesc = new javafx.scene.control.TableColumn<>("Mô tả");
        colDesc.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));

        // Nhét tất cả cột vào bảng
        table.getColumns().addAll(colName, colPrice, colDesc);

        // Cài đặt cho bảng tự động giãn cột cho vừa khít chiều rộng
        table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
        table.setRowFactory(tv -> {
            javafx.scene.control.TableRow<models.Item> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                // Nếu click 2 lần liên tiếp và dòng đó có chứa vật phẩm
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    models.Item selectedItem = row.getItem();
                    // Gọi hàm hiện cửa sổ chi tiết
                    showItemDetailPopup(selectedItem);
                }
            });
            return row;
        });

    }
    // ==========================================================
    // HÀM HIỂN THỊ HỘP THOẠI CHI TIẾT VẬT PHẨM
    // ==========================================================
    private void showItemDetailPopup(models.Item item) {
        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Chi Tiết Vật Phẩm");

        // Tên vật phẩm in to ở trên cùng
        dialog.setHeaderText("Tên sản phẩm: " + item.getName());

        // Nút Đóng mặc định
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);

        // Khung lưới xếp chữ cho ngay ngắn
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new javafx.geometry.Insets(20, 40, 10, 10));

        // Hàng 1: ID
        grid.add(new javafx.scene.control.Label("Mã vật phẩm (ID):"), 0, 0);
        javafx.scene.control.Label lblId = new javafx.scene.control.Label(item.getID() != null ? item.getID().toString() : "Chưa có ID");
        lblId.setStyle("-fx-font-weight: bold;");
        grid.add(lblId, 1, 0);

        // Hàng 2: Giá
        grid.add(new javafx.scene.control.Label("Giá hiện tại:"), 0, 1);
        javafx.scene.control.Label lblPrice = new javafx.scene.control.Label(item.getPrice() + " VNĐ");
        lblPrice.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(lblPrice, 1, 1);

        // Hàng 3: Mô tả (Dùng TextArea để nội dung dài tự xuống dòng)
        grid.add(new javafx.scene.control.Label("Mô tả chi tiết:"), 0, 2);
        String descText = item.getCondition() != null ? item.getCondition() : "Không có mô tả.";
        javafx.scene.control.TextArea txtDesc = new javafx.scene.control.TextArea(descText);

        txtDesc.setEditable(false); // Khóa không cho sửa
        txtDesc.setWrapText(true);  // Ép tự xuống dòng
        txtDesc.setPrefRowCount(4);
        txtDesc.setPrefWidth(300);
        txtDesc.setStyle("-fx-control-inner-background: #f4f4f4; -fx-background-color: transparent;");
        grid.add(txtDesc, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Hiển thị hộp thoại và chờ người dùng bấm đóng
        dialog.showAndWait();
    }
    private void showAddProductForm() {
        centerContent.getChildren().clear();

        javafx.scene.layout.VBox formBox = new javafx.scene.layout.VBox(15);
        formBox.setPadding(new javafx.geometry.Insets(30));
        formBox.setMaxWidth(500);

        javafx.scene.control.Label lblTitle = new javafx.scene.control.Label("THÊM VẬT PHẨM MỚI");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Các trường nhập liệu
        javafx.scene.control.TextField txtName = new javafx.scene.control.TextField();
        txtName.setPromptText("Nhập tên vật phẩm...");

        javafx.scene.control.TextField txtPrice = new javafx.scene.control.TextField();
        txtPrice.setPromptText("Nhập giá khởi điểm (VNĐ)...");

        javafx.scene.control.TextArea txtDesc = new javafx.scene.control.TextArea();
        txtDesc.setPromptText("Nhập mô tả chi tiết...");
        txtDesc.setPrefRowCount(4);

        // Nút Lưu và Hủy
        javafx.scene.control.Button btnSave = new javafx.scene.control.Button("Lưu Vật Phẩm");
        btnSave.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        javafx.scene.control.Button btnCancel = new javafx.scene.control.Button("Hủy");

        javafx.scene.layout.HBox btnBox = new javafx.scene.layout.HBox(10, btnSave, btnCancel);

        // Sự kiện
        btnCancel.setOnAction(e -> initInventoryUI()); // Quay lại kho đồ

        btnSave.setOnAction(e -> {
            String name = txtName.getText();
            String price = txtPrice.getText();
            String desc = txtDesc.getText();

            if(name.isEmpty() || price.isEmpty()) {
                // Hiện cảnh báo thiếu thông tin... (Bạn tự thêm hàm Alert nhé)
                return;
            }

            if (connection != null) {
                // Gửi lệnh tạo lên Server (Bạn sẽ cần viết AddItemCommand trên Server để hứng)
                connection.sendCommand("ADD_ITEM|" + name + "|" + price + "|" + desc);
                btnSave.setText("Đang lưu...");
                btnSave.setDisable(true);
            }
        });

        formBox.getChildren().addAll(lblTitle, new javafx.scene.control.Label("Tên vật phẩm:"), txtName,
                new javafx.scene.control.Label("Giá khởi điểm:"), txtPrice,
                new javafx.scene.control.Label("Mô tả:"), txtDesc, btnBox);
        centerContent.getChildren().add(formBox);
    }
    private void initInventoryUI() {
        centerContent.getChildren().clear();

        // 1. KHU VỰC HEADER CHỨA TIÊU ĐỀ, THANH CHỌN VÀ NÚT THÊM
        javafx.scene.layout.HBox headerBox = new javafx.scene.layout.HBox(15); // Khoảng cách các thành phần là 15px
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        javafx.scene.control.Label lblTitle = new javafx.scene.control.Label("📦 KHO ĐỒ CỦA TÔI");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // --- TẠO THANH CHỌN (ComboBox) ---
        javafx.scene.control.ComboBox<String> cbStatusFilter = new javafx.scene.control.ComboBox<>();
        cbStatusFilter.getItems().addAll("Chưa bán (AVAILABLE)", "Đang đấu giá (AUCTION)");

        // Đặt giá trị hiển thị mặc định dựa trên currentFilter
        cbStatusFilter.setValue(currentFilter.equals("AVAILABLE") ? "Chưa bán (AVAILABLE)" : "Đang đấu giá (AUCTION)");
        cbStatusFilter.setStyle("-fx-font-size: 14px; -fx-cursor: hand;");

        // Lò xo đẩy nút Thêm sang sát lề phải
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        javafx.scene.control.Button btnAddItem = new javafx.scene.control.Button("➕ Thêm vật phẩm");
        btnAddItem.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnAddItem.setOnAction(e -> showAddProductForm());

        headerBox.getChildren().addAll(lblTitle, cbStatusFilter, spacer, btnAddItem);

        // 2. CÀI ĐẶT BẢNG DUY NHẤT
        setupTableColumns(tableInventory);

        // 3. SỰ KIỆN KHI BẤM CHỌN THANH TRẠNG THÁI KHÁC
        cbStatusFilter.setOnAction(e -> {
            String selected = cbStatusFilter.getValue();

            // Cập nhật lại biến trạng thái hiện tại
            if (selected.contains("AVAILABLE")) {
                currentFilter = "AVAILABLE";
            } else {
                currentFilter = "AUCTION";
            }

            // Xóa dữ liệu cũ trong bảng và hiện chữ Đang tải
            tableInventory.getItems().clear();
            tableInventory.setPlaceholder(new javafx.scene.control.Label("Đang tải dữ liệu từ Server..."));

            // Bắn lệnh MỚI lên Server
            if (connection != null) {
                connection.sendCommand("GET_MY_ITEMS|" + currentFilter);
            }
        });

        // 4. BƠM TẤT CẢ RA MÀN HÌNH
        javafx.scene.layout.VBox mainBox = new javafx.scene.layout.VBox(15);
        javafx.scene.layout.VBox.setVgrow(tableInventory, javafx.scene.layout.Priority.ALWAYS);
        mainBox.getChildren().addAll(headerBox, tableInventory);

        centerContent.getChildren().add(mainBox);

        // 5. CHẠY LỆNH LẦN ĐẦU TIÊN KHI VỪA MỞ MÀN HÌNH NÀY LÊN
        if (connection != null) {
            tableInventory.setPlaceholder(new javafx.scene.control.Label("Đang tải dữ liệu từ Server..."));
            connection.sendCommand("GET_MY_ITEMS|" + currentFilter);
        }
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
                case "SUCCESS_UPGRADE":
                    String newProfileJson = parts[1];

                    // 1. Dùng GSON đúc lại đối tượng User mới (Lúc này GSON sẽ tự hiểu nó là Seller nhờ cái nhãn "type")
                    models.User upgradedUser = GsonUtil.gson.fromJson(newProfileJson, models.User.class);

                    javafx.application.Platform.runLater(() -> {
                        // 2. Báo tin vui
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        alert.setTitle("Thành công");
                        alert.setHeaderText(null);
                        alert.setContentText("Chúc mừng! Bạn đã được thăng cấp thành Người Bán (Seller)!");
                        alert.showAndWait();

                        // 3. CẬP NHẬT LẠI BIẾN CURRENT USER CHO TOÀN BỘ GIAO DIỆN
                        this.currentUser = upgradedUser; // (Thay 'this' bằng tên class chứa biến currentUser của bạn)

                        // 4. Gọi hàm vẽ lại toàn bộ thanh Menu bên trái (Sidebar)
                        // Vì currentUser giờ đã là Seller, cái code instanceof Seller của bạn sẽ chạy!
                        createSidebar();

                        // 5. Load lại chính diện mạo Hồ sơ
                        triggerLoadProfile();
                    });
                    break;
                case "SUCCESS_GET_ITEMS":
                    // Lấy cục data phía sau (ví dụ: "SOLD|[]")
                    String payload = parts[1];

                    // Cắt nó ra làm 2 mảnh bằng dấu "|"
                    String[] subParts = payload.split("\\|", 2);
                    String itemStatus = subParts[0]; // Mảnh 1: "SOLD"
                    String jsonList = subParts[1];   // Mảnh 2: "[]"

                    // Đúc JSON thành danh sách (Đoạn này trở đi giữ nguyên)
                    java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<models.Item>>(){}.getType();
                    List<models.Item> itemList = GsonUtil.gson.fromJson(jsonList, listType);

                    javafx.application.Platform.runLater(() -> {
                        // ==========================================
                        // TỔNG ĐÀI PHÂN LUỒNG GIAO DIỆN
                        // ==========================================
                        if (itemStatus.equals("SOLD")) {
                            showBoughtItemsUI(itemList);
                        }
                        else if (itemStatus.equals("AVAILABLE") || itemStatus.equals("AUCTION")) {
                            if (itemStatus.equals(currentFilter)) {
                                tableInventory.getItems().setAll(itemList);
                                if (itemList.isEmpty()) {
                                    tableInventory.setPlaceholder(new javafx.scene.control.Label("Chưa có vật phẩm nào ở trạng thái này."));
                                }
                            }
                        }
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







