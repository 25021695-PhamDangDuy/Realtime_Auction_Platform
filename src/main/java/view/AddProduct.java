package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
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
import java.time.LocalDate;

public class AddProduct extends Application implements MessageListener {
    private Stage primaryStage;
    private javafx.scene.control.Label lblMessage;
    private ServerConnection connection;
    private GridPane grid;
    private File selectedImageFile; // Lưu file ảnh người dùng chọn
    private ImageView imgPreview;

    public AddProduct() {
    }

    public AddProduct(ServerConnection connection) {
        this.connection = connection;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.grid = new GridPane();
        this.grid.setAlignment(javafx.geometry.Pos.CENTER);
        this.grid.setHgap(10);
        this.grid.setVgap(10);
        this.grid.setPadding(new javafx.geometry.Insets(25, 25, 25, 25));


        this.connection = new ServerConnection();
        this.primaryStage = stage;

        this.connection.setMessageListener(this);

        imgPreview = new ImageView();
        imgPreview.setFitWidth(150);
        imgPreview.setFitHeight(150);
        imgPreview.setPreserveRatio(true);

        try {
            var inputStream = getClass().getResourceAsStream("/images/default-product.png");

            if (inputStream != null) {
                Image defaultImg = new Image(inputStream);
                imgPreview.setImage(defaultImg);
            } else {
                System.out.println("⚠️ Không tìm thấy file tại: resources/images/default-product.png");
                // Bạn có thể không làm gì cả, hoặc set một ảnh trống/màu nền cho imgPreview ở đây
            }
        } catch (Exception e) {
            System.out.println("Lỗi xử lý ảnh: " + e.getMessage());
        }


        Button btnChooseImage = new Button("Chọn ảnh sản phẩm");
        btnChooseImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn ảnh sản phẩm đấu giá");

            // Chỉ lọc các định dạng file ảnh phổ biến
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


        grid.add(new Label("Hình ảnh:"), 0, 4);
        grid.add(btnChooseImage, 1, 4);
        grid.add(imgPreview, 1, 5);
        TextField txtName = new TextField();
        txtName.setPromptText("Nhập tên sản phẩm...");


        TextArea txtDesc = new TextArea();
        txtDesc.setPromptText("Mô tả sản phẩm...");
        txtDesc.setPrefRowCount(3);

        TextField txtStartPrice = new TextField();
        txtStartPrice.setPromptText("Giá khởi điểm (VNĐ)...");

        TextField txtStepPrice = new TextField();
        txtStepPrice.setPromptText("Bước giá (VNĐ)...");

        DatePicker datePicker = new DatePicker(LocalDate.now());
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));


        grid.add(new Label("Tên SP:"), 0, 0);
        grid.add(txtName, 1, 0);

        grid.add(new Label("Giá sàn:"), 0, 1);
        grid.add(txtStartPrice, 1, 1);

        grid.add(new Label("Ngày kết thúc:"), 0, 2);
        grid.add(datePicker, 1, 2);

        Button btnSave = new Button("ĐĂNG SẢN PHẨM");
        grid.add(btnSave, 1, 3);
        btnSave.setOnAction(e -> {
            String productName = txtName.getText().trim();
            String startPriceStr = txtStartPrice.getText().trim();

            // Lấy ngày chọn từ DatePicker (nếu trống thì báo lỗi)
            if (productName.isEmpty() || startPriceStr.isEmpty() || datePicker.getValue() == null) {
                lblMessage.setText("Vui lòng nhập đầy đủ thông tin sản phẩm!");
                lblMessage.setTextFill(javafx.scene.paint.Color.FIREBRICK);
                return;
            }

            String endDate = datePicker.getValue().toString(); // Trả về định dạng YYYY-MM-DD

            try {
                long startPrice = Long.parseLong(startPriceStr);
                if (startPrice < 0) {
                    lblMessage.setText("Giá khởi điểm không được âm!");
                    lblMessage.setTextFill(javafx.scene.paint.Color.FIREBRICK);
                    return;
                }

                // CHUẨN HÓA CHUỖI LỆNH GỬI ĐI
                String command = "ADD_PRODUCT|" + productName + "|" + startPrice + "|" + endDate;

                connection.sendCommand(command);
                System.out.println("[LOG SENT]: Đã gửi yêu cầu đăng sản phẩm -> " + command);

                lblMessage.setText("Đang xử lý đăng sản phẩm...");
                lblMessage.setTextFill(javafx.scene.paint.Color.BLUE);

            } catch (NumberFormatException ex) {
                lblMessage.setText("Giá tiền nhập vào phải là số nguyên hợp lệ!");
                lblMessage.setTextFill(javafx.scene.paint.Color.FIREBRICK);
            } catch (Exception ex) {
                lblMessage.setText("Lỗi kết nối đến Server!");
                lblMessage.setTextFill(javafx.scene.paint.Color.FIREBRICK);
                ex.printStackTrace();
            }
        });
    }
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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if ("ADD_PRODUCT_FAILED".equals(serverMessage)) {
                lblMessage.setText("Đăng sản phẩm thất bại từ hệ thống!");
                lblMessage.setTextFill(Color.FIREBRICK);
            }

        });
        Scene scene = new Scene(grid,400, 350);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    public static void main(String[] args){
        launch(args);
    }
}

