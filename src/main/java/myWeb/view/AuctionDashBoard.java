package myWeb.view;

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


public class AuctionDashBoard extends Application {
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sàn đấu giá trực tuyến");
        //Tạo thanh tiêu đề
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setSpacing(20);
        header.setStyle("-fx-background-color: #2c3e50;");

        Label lblLogo = new Label("AUCTION SYSTEM");
        lblLogo.setTextFill(Color.WHITE);
        lblLogo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Đăng xuất");
        header.getChildren().addAll(lblLogo, spacer, btnLogout);
        //Tạo danh sách các sản phẩm
        FlowPane productContainer = new FlowPane();
        productContainer.setPadding(new Insets(20));
        productContainer.setHgap(20);
        productContainer.setVgap(20);
        productContainer.setStyle("-fx-background-color: #ecf0f1;");
        ScrollPane scrollPane = new ScrollPane(productContainer);
        scrollPane.setFitToWidth(true);

        // Layout chính
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    //Tạo 1 ô sản phẩm(Product Card)
    private VBox createProductCard(String name, String price) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setPrefWidth(250);

        Label lblName = new Label(name);
        lblName.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Giả lập ảnh (Thay bằng ImageView nếu bạn có file ảnh)
        Rectangle placeholder = new Rectangle(200, 120, Color.LIGHTGRAY);
        // Lưu ý: Import javafx.scene.shape.Rectangle nếu dùng placeholder này

        Label lblPrice = new Label("Giá hiện tại: " + price);
        lblPrice.setTextFill(Color.FIREBRICK);
        lblPrice.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));

        Button btnBid = new Button("Đặt giá / Chi tiết");
        btnBid.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        card.getChildren().addAll(lblName, lblPrice, btnBid);

        // Hiệu ứng hover
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #3498db; -fx-border-radius: 10; -fx-background-radius: 10;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 10; -fx-background-radius: 10;"));

        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

