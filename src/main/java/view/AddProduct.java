package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.network.MessageListener;
import view.network.ServerConnection;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;

public class AddProduct extends Application implements MessageListener {
    private Stage primaryStage;
    private ServerConnection connection;
    private GridPane grid;

    // Các UI components cần dùng chung hoặc xử lý logic
    private ImageView imgPreview;
    private File selectedImageFile;
    private Label lblMessage;
    private ComboBox<String> cbItemType;

    private TextField txtName;
    private TextArea txtDesc;
    private TextField txtStartPrice;
    private TextField txtStepPrice;
    private DatePicker datePicker;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // 1. Khởi tạo kết nối Server
        this.connection = new ServerConnection();
        this.connection.setMessageListener(this);

        // 2. Cấu hình layout chính (GridPane) - Chỉ khởi tạo DUY NHẤT một lần tại đây
        this.grid = new GridPane();
        this.grid.setAlignment(Pos.CENTER);
        this.grid.setHgap(10);
        this.grid.setVgap(15);
        this.grid.setPadding(new Insets(25, 25, 25, 25));

        // 3. Khởi tạo cấu hình ban đầu cho ImageView preview ảnh
        this.imgPreview = new ImageView();
        this.imgPreview.setFitWidth(150);
        this.imgPreview.setFitHeight(150);
        this.imgPreview.setPreserveRatio(true);

        // Tải ảnh mặc định ban đầu
        try {
            InputStream inputStream = getClass().getResourceAsStream("/assets/default-product.png");
            if (inputStream != null) {
                Image defaultImg = new Image(inputStream);
                imgPreview.setImage(defaultImg);
            } else {
                System.out.println("⚠️ Không tìm thấy file tại: resources/images/default-product.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Khởi tạo nút bấm chọn ảnh và sự kiện FileChooser
        Button btnChooseImage = new Button("Chọn ảnh sản phẩm");
        btnChooseImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn ảnh sản phẩm đấu giá");

            // Lọc định dạng file ảnh phổ biến
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );

            // Mở cửa sổ chọn file
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                this.selectedImageFile = file;
                // Nạp ảnh vừa chọn vào ImageView để người dùng xem trước
                Image img = new Image(file.toURI().toString());
                imgPreview.setImage(img);
            }
        });

        // 5. Khởi tạo các ô nhập thông tin sản phẩm khác
        cbItemType = new ComboBox<>();

        // Nạp các danh mục tương ứng 100% với các Key định nghĩa ở Server (Ảnh 1 của bạn)
        cbItemType.setItems(FXCollections.observableArrayList(
                "ELECTRONIC",
                "ART",
                "VEHICLE"
        ));

        // Gợi ý: Chọn sẵn mục đầu tiên làm mặc định để tránh bị null
        cbItemType.getSelectionModel().selectFirst();

        // Thiết lập thêm một text gợi ý (Prompt Text) nếu không chọn mặc định
        cbItemType.setPromptText("-- Chọn loại sản phẩm --");

        txtName = new TextField();
        txtName.setPromptText("Nhập tên sản phẩm...");

        txtDesc = new TextArea();
        txtDesc.setPromptText("Mô tả sản phẩm...");
        txtDesc.setPrefRowCount(3);

        txtStartPrice = new TextField();
        txtStartPrice.setPromptText("Giá khởi điểm (VNĐ)...");



        datePicker = new DatePicker(LocalDate.now());

        // Nhãn hiển thị trạng thái kết quả đăng sản phẩm thành công/thất bại
        lblMessage = new Label();

        // Nút bấm gửi thông tin (Bạn có thể thêm nút này vào cuối form)
        Button btnSubmit = new Button("Đăng sản phẩm");
        btnSubmit.setOnAction(e -> {
            String selectedType = cbItemType.getValue();
            String productName = txtName.getText().trim();
            String startPriceStr = txtStartPrice.getText().trim();
            if (selectedType == null) {
                // Xử lý thông báo nếu người dùng chưa chọn loại hàng
                lblMessage.setText("Vui lòng chọn loại sản phẩm!");
                return;
            }
            // Lấy ngày chọn từ DatePicker (nếu trống thì báo lỗi)
            if (productName.isEmpty() || startPriceStr.isEmpty() || datePicker.getValue() == null) {
                lblMessage.setText("Vui lòng nhập đầy đủ thông tin sản phẩm!");
                lblMessage.setTextFill(Color.FIREBRICK);
                return;
            }

            String endDate = datePicker.getValue().toString(); // Trả về định dạng YYYY-MM-DD

            try {
                long startPrice = Long.parseLong(startPriceStr);
                if (startPrice < 0) {
                    lblMessage.setText("Giá khởi điểm không được âm!");
                    lblMessage.setTextFill(Color.FIREBRICK);
                    return;
                }

                // CHUẨN HÓA CHUỖI LỆNH GỬI ĐI
                String command = "CREATE_ITEM|" + selectedType + "|" + productName + "|" + startPriceStr;

                connection.sendCommand(command);
                System.out.println("[LOG SENT]: Đã gửi yêu cầu đăng sản phẩm -> " + command);

                lblMessage.setText("Đang xử lý đăng sản phẩm...");
                lblMessage.setTextFill(Color.BLUE);

            } catch (NumberFormatException ex) {
                lblMessage.setText("Giá tiền nhập vào phải là số nguyên hợp lệ!");
                lblMessage.setTextFill(Color.FIREBRICK);
            } catch (Exception ex) {
                lblMessage.setText("Lỗi kết nối đến Server!");
                lblMessage.setTextFill(Color.FIREBRICK);
                ex.printStackTrace();
            }
        });
        grid.add(new Label("Loại sản phẩm"),0,1);
        grid.add(cbItemType,1,1);
        grid.add(new Label("Hình ảnh:"), 0, 0);
        grid.add(btnChooseImage, 1, 0);
        grid.add(imgPreview, 1, 1);

        // Hàng 2: Tên sản phẩm
        grid.add(new Label("Tên SP:"), 0, 2);
        grid.add(txtName, 1, 2);

        // Hàng 3: Mô tả sản phẩm
        grid.add(new Label("Mô tả:"), 0, 3);
        grid.add(txtDesc, 1, 3);

        // Hàng 4: Giá khởi điểm
        grid.add(new Label("Giá khởi điểm:"), 0, 4);
        grid.add(txtStartPrice, 1, 4);

        grid.add(new Label("Ngày kết thúc:"), 0, 6);
        grid.add(datePicker, 1, 6);

        // Hàng 7: Nút Đăng & Dòng thông báo kết quả từ Server
        grid.add(btnSubmit, 1, 7);
        grid.add(lblMessage, 1, 8);

        // 7. Tạo Scene và hiển thị giao diện lên Stage chính
        Scene scene = new Scene(grid, 500, 650); // Bạn có thể tinh chỉnh lại kích thước cho vừa vặn
        primaryStage.setTitle("Thêm Sản Phẩm Đấu Giá");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void onMessageReceived(String serverMessage) {
        Platform.runLater(() -> {
            System.out.println("Nhận được phản hồi đăng sản phẩm: " + serverMessage);


            if ("ADD_PRODUCT_SUCCESS".equals(serverMessage)) {
                lblMessage.setText("Đăng sản phẩm thành công!");
                lblMessage.setTextFill(Color.GREEN);

                // Tùy chọn: Sau khi đăng thành công, tự động chuyển về màn hình chính sau 1-2 giây
                try {
                    AuctionHomeScreen homeScreen = new AuctionHomeScreen();
                    homeScreen.start(primaryStage);
                    connection.sendCommand("LOAD_AUCTION_ROOMS");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if ("ADD_PRODUCT_FAILED".equals(serverMessage)) {
                lblMessage.setText("Đăng sản phẩm thất bại từ hệ thống!");
                lblMessage.setTextFill(Color.FIREBRICK);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}








