package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class AuctionWatchList extends Application {
    public void start(Stage primaryStage){
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(15));
        gridPane.setHgap(20);
        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.setStyle("-fx-background-color: #ffffff; " +
                "-fx-border-color: #3b5998; " +
                "-fx-border-width: 1px;");
        //Cột 1: Tên chức năng, Danh sách sản phẩm đấu giá
        Label lblTitle = new Label("Danh sách theo dõi");
        lblTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        GridPane.setConstraints(lblTitle, 0, 0);
        // 3. Cột 2: Trạng thái (ComboBox giả lập nút "Chưa bắt đầu" màu đỏ bo góc)
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Chưa bắt đầu", "Đang tiến hành", "Hoàn thành");
        cbStatus.setValue("Chưa bắt đầu");
        // Custom CSS để giống cái nút màu đỏ trong ảnh
        cbStatus.setStyle("-fx-background-color: #b30000; " +
                "-fx-background-radius: 15px; " +
                "-fx-mark-color: white; " + // Màu của mũi tên dropdown
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 2 10 2 10;");

        // Cập nhật màu chữ hiển thị của item được chọn thành màu trắng
        cbStatus.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });
        cbStatus.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                }
            }
        });
        GridPane.setConstraints(cbStatus, 1, 0);

        // 4. Cột 3: Trình chọn ngày tháng (dd/mm/yyyy)
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("dd/mm/yyyy");
        datePicker.setPrefWidth(140);
        datePicker.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        GridPane.setConstraints(datePicker, 2, 0);

        // 5. Cột 4: Phần mô tả chức năng (Wrap text để tự động xuống dòng)
        Label lblDescription = new Label(
                "Xây dựng màn hình danh sách phiên đấu giá, vật phẩm đang theo dõi " +
                        "theo nhiều dạng lưới, cột v.v để dễ chèn vào nhiều Scene khác nhau"
        );
        lblDescription.setWrapText(true); // Tự động xuống dòng
        lblDescription.setMaxWidth(280);  // Giới hạn chiều rộng của cột mô tả
        lblDescription.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333; -fx-line-spacing: 3px;");
        GridPane.setConstraints(lblDescription, 3, 0);

        // 6. Cấu hình độ giãn cách giữa các cột
        // Cho phép khoảng trống giữa cột ngày tháng và cột mô tả tự động giãn ra
        GridPane.setHgrow(datePicker, Priority.ALWAYS);
        gridPane.getChildren().addAll(lblTitle, cbStatus, datePicker, lblDescription);

        // 7. Tạo Scene và Stage để chạy ứng dụng
        // Bọc vào một layout lớn hơn để dễ nhìn (hoặc bạn có thể dùng trực tiếp gridPane làm root)
        Scene scene = new Scene(gridPane, 850, 120);

        primaryStage.setTitle("Giao diện người theo dõi phiên đấu giá");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }





    }

