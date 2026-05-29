package myWeb.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class AuctionObserverView extends VBox {

    private final ObservableList<AuctionDashBoard.AuctionItem> watchData;
    private final VBox listContainer = new VBox(10); // Vùng chứa động
    private boolean isGridMode = false; // Mặc định hiển thị dạng hàng dọc (Cột) cho người quan sát

    // Constructor nhận vào danh sách dữ liệu từ Dashboard truyền sang
    public AuctionObserverView(List<AuctionDashBoard.AuctionItem> data) {
        this.watchData = FXCollections.observableArrayList(data);

        this.setPadding(new Insets(15));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #f4f6f9;");

        // 1. Tiêu đề và nút bấm chuyển đổi cấu trúc (Grid/List)
        HBox headerBar = new HBox(15);
        Label lblHeader = new Label("DANH SÁCH THEO DÕI LIVE (OBSERVER MODE)");
        lblHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        Button btnSwitchView = new Button("Đổi giao diện (Lưới / Cột)");
        btnSwitchView.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 11px;");
        btnSwitchView.setOnAction(e -> {
            isGridMode = !isGridMode;
            renderWatchList();
        });

        headerBar.getChildren().addAll(lblHeader, btnSwitchView);
        this.getChildren().add(headerBar);

        // 2. Vùng hiển thị có cuộn tự động
        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        this.getChildren().add(scrollPane);

        // Vẽ danh sách lần đầu
        renderWatchList();
    }

    private void renderWatchList() {
        listContainer.getChildren().clear();

        if (isGridMode) {
            // Hiển thị dạng LƯỚI (Dùng lại thẻ ô vuông của bạn)
            GridPane gridPane = new GridPane();
            gridPane.setHgap(15);
            gridPane.setVgap(15);
            int columns = 3; // 3 cột một hàng khi chèn khung nhỏ

            for (int i = 0; i < watchData.size(); i++) {
                AuctionGridCard card = new AuctionGridCard(watchData.get(i));
                gridPane.add(card, i % columns, i / columns);
            }
            listContainer.getChildren().add(gridPane);
        } else {
            // Hiển thị dạng CỘT (Hàng ngang nối tiếp) dành riêng cho người quan sát
            for (AuctionDashBoard.AuctionItem item : watchData) {
                AuctionObserverCard rowCard = new AuctionObserverCard(item);
                listContainer.getChildren().add(rowCard);
            }
        }
    }
}
