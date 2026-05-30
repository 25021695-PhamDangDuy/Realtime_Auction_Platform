package view;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class AuctionDashBoard extends Application {
     static class AuctionItem{
         String name;
         String price;
         String timeLeft;
         String status; // Thêm trường này để phục vụ bộ lọc "Chưa bắt đầu/Đang diễn ra" trong ảnh yêu cầu

         // 2. Constructor nhận vào đúng các tham số kiểu String
         public AuctionItem(String name, String price, String timeLeft, String status) {
             this.name = name;
             this.price = price;
             this.timeLeft = timeLeft;
             this.status = status;
         }
         // 3. Các hàm lấy giá trị chuỗi trực tiếp (Thay thế cho các Property bị lỗi)
         public String getName() {
             return name;
         }

         public String getPrice() {
             return price;
         }

         public String getTimeLeft() {
             return timeLeft;
         }

         public String getStatus() {
             return status;
         }
     }
    private final List<AuctionItem> mockData = new ArrayList<>();
    private StackPane contentArea;
    public void start(Stage primaryStage) {
        //<Dữ liệu mẫu>//
        mockData.add(new AuctionItem("iPhone 15 Pro Max", "$1,200", "00:02:15","Đang diễn ra"));
        mockData.add(new AuctionItem("MacBook Pro M3", "$2,400", "00:45:10","Đang diễn ra"));
        mockData.add(new AuctionItem("Rolex Submariner", "$9,500", "04:12:00","Đang diễn ra"));
        mockData.add(new AuctionItem("PlayStation 5 Pro", "$600", "01:30:25","Đang diễn ra"));

        //Tạo thanh điều khiển trên(Top Bar) chuyển chế độ xem
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label lblTitle = new Label("HỆ THỐNG ĐẤU GIÁ ONLINE");
        lblTitle.setTextFill(Color.WHITE);
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        //căn khoảng trống giữa tiêu đề và các nút điều khiển
        Region spacer = new Region();
        HBox.setHgrow(spacer,Priority.ALWAYS);
        Button btnGridView = new Button("Xem dạng lưới");
        Button btnListView = new Button("Xem dạng cột");
        Button btnMarketplace = new Button("Sàn Đấu Giá");
        String btnStyle = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-cursor: hand;";
        btnGridView.setStyle(btnStyle);
        btnListView.setStyle(btnStyle);
        topBar.getChildren().addAll(lblTitle, spacer, btnGridView, btnListView);
        // Vùng hiển thị chính (Có ScrollPane phòng khi danh sách quá dài)
        contentArea = new StackPane();
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        // Đăng ký sự kiện đổi Layout khi click nút
        btnGridView.setOnAction(e -> switchViewMode("GRID"));
        btnListView.setOnAction(e -> switchViewMode("COLUMN"));
        btnMarketplace.setOnAction(e -> {
            // Khởi tạo màn hình Marketplace và truyền danh sách dữ liệu gốc vào
            AuctionMarketplace marketplace = new AuctionMarketplace(mockData);

            // Dọn sạch giao diện cũ ở vùng trung tâm và chèn Marketplace vào hiển thị
            contentArea.getChildren().clear();
            contentArea.getChildren().add(marketplace);
        });

// 3. Thêm nút này vào thanh topBar của bạn (Thêm vào sau spacer)
        topBar.getChildren().add(btnMarketplace);

        // Mặc định ban đầu hiển thị dạng LƯỚI (Trang chủ)
        switchViewMode("GRID");
        //Layout chính
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(scrollPane);
        Scene scene = new Scene(root,900,600);
        primaryStage.setTitle("Danh sách phiên đấu giá ");
        primaryStage.setScene(scene);
        primaryStage.show();
        // Logic này đặt bên trong sự kiện click nút "Danh sách theo dõi" ở Dashboard của bạn:
    }
    private void switchViewMode(String mode){
        contentArea.getChildren().clear();
        if ("GRID".equalsIgnoreCase(mode)){
            TilePane gridLayout = new TilePane();
            gridLayout.setPadding(new Insets(20));
            gridLayout.setHgap(20);
            gridLayout.setVgap(20);
            gridLayout.setPrefColumns(3);
            for (AuctionItem item : mockData) {
                VBox card = createProductCard(item.name, item.price, item.timeLeft, false);
                gridLayout.getChildren().add(card);
            }
            contentArea.getChildren().add(gridLayout);
        } else {
            VBox columnLayout = new VBox(15);
            columnLayout.setPadding(new Insets(20));
            contentArea.getChildren().add(columnLayout);
            for (AuctionItem item : mockData) {
                // Đối với dạng cột, ta báo hiệu cho hàm tạo card biết để tối ưu không gian chiều ngang
                VBox card = createProductCard(item.name, item.price, item.timeLeft, true);
                columnLayout.getChildren().add(card);
            }
        }
    }
    private VBox createProductCard(String name, String price, String timeLeft, boolean isListView){
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-background-radius: 10;");
        //Tự động căn chỉnh chiều rộng theo kiểu hiển thị
        if (isListView) {
            card.setPrefWidth(Double.MAX_VALUE); // Dạng cột thì co giãn hết hàng ngang
        } else {
            card.setPrefWidth(250); // Dạng lưới thì cố định kích thước
        }
        // Tên sản phẩm
        Label lblName = new Label(name);
        lblName.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Ảnh / Rectangle Placeholder
        Rectangle placeholder = new Rectangle(200, 120, Color.LIGHTGRAY);

        // Giá hiện tại
        Label lblPrice = new Label("Giá hiện tại: " + price);
        lblPrice.setTextFill(Color.FIREBRICK);
        lblPrice.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        // Bổ sung: Thanh đếm ngược thời gian
        Label lblTime = new Label("⏱ " + timeLeft);
        lblTime.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lblTime.setTextFill(Color.web("#e67e22"));

        // Nút hành động
        Button btnBid = new Button("Đặt giá / Chi tiết");
        btnBid.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        // Gom các thành phần lại vào Card
        card.getChildren().addAll(lblName, placeholder, lblPrice, lblTime, btnBid);

        // Hiệu ứng Hover mượt mà thừa hưởng từ ý tưởng gốc của bạn
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #3498db; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-background-radius: 10;"));

        return card;

    }

    public static void main(String[] args) {
        launch(args);
    }
}

