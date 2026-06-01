package view;

import view.AuctionDashBoard.AuctionItem;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class AuctionGridCard extends VBox {
    public AuctionGridCard(AuctionItem item) {
        // Cấu hình container cho mỗi thẻ sản phẩm
        this.setSpacing(8);
        this.setPadding(new Insets(10));
        this.setPrefWidth(220);
        this.setStyle("-fx-background-color: #ffffff; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        // 1. Ảnh giả lập sản phẩm (Placeholder)
        ImageView imageView = new ImageView();
        try {
            // Bạn có thể thay bằng đường dẫn ảnh thật hoặc ảnh online
            imageView.setImage(new Image("https://via.placeholder.com/200x130"));
        } catch (Exception e) {
            // Xử lý nếu không load được ảnh
        }
        imageView.setFitWidth(200);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);
        // 2. Tiêu đề vật phẩm (Lấy giá trị chuỗi tĩnh từ getName() hoặc item.name)
        Label lblTitle = new Label(item.getName());
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #212121;");
        lblTitle.setWrapText(true);
        lblTitle.setMaxHeight(40);

        // 3. Trạng thái phiên (Gán trực tiếp giá trị chuỗi từ trường status)
        Label lblStatus = new Label(item.status); // Sử dụng trực tiếp thuộc tính chuỗi hoặc hàm getter nếu có
        configureStatusStyle(lblStatus, item.status);

        // 4. Khu vực hiển thị Giá
        Label lblPriceTag = new Label("Giá hiện tại:");
        lblPriceTag.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");

        // Vì item.getPrice() trả về chuỗi đã định dạng sẵn (Ví dụ: "$1,200") nên truyền thẳng vào Label
        Label lblPriceValue = new Label(item.getPrice());
        lblPriceValue.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #e53935;");



        // 5. Nút bấm hành động (Chỉ xem chi tiết)
        Button btnViewDetail = new Button("Xem chi tiết");
        btnViewDetail.setMaxWidth(Double.MAX_VALUE); // Cho nút rộng bằng card
        btnViewDetail.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4;");
        btnViewDetail.setOnAction(e -> {
            System.out.println("Chuyển hướng đến chi tiết phiên: " + item.getName());
            // Logic mở màn hình chi tiết viết ở đây
        });

        // Thêm tất cả các thành phần vào Card
        this.getChildren().addAll(imageView, lblStatus, lblTitle, lblPriceTag, lblPriceValue, btnViewDetail);
    }

    // Hàm phụ để đổi màu badge tùy theo trạng thái phiên đấu giá
    private void configureStatusStyle(Label label, String status) {
        label.setPadding(new Insets(2, 6, 2, 6));
        label.setStyle("-fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;");
        if ("Đang diễn ra".equals(status)) {
            label.setStyle(label.getStyle() + "-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32;");
        } else if ("Chưa bắt đầu".equals(status)) {
            label.setStyle(label.getStyle() + "-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00;");
        } else {
            label.setStyle(label.getStyle() + "-fx-background-color: #eceff1; -fx-text-fill: #455a64;");
        }
    }
}
