package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.List;



public class AuctionMarketplace extends BorderPane {
    private final ObservableList<AuctionDashBoard.AuctionItem> masterData = FXCollections.observableArrayList();
    private final GridPane gridPane = new GridPane();

    public AuctionMarketplace(List<AuctionDashBoard.AuctionItem> mockData) {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f8f9fa;");

        // Khởi tạo dữ liệu mẫu hệ thống
        loadSystemAuctions();


        // Sử dụng FilteredList của JavaFX để tự động lọc dữ liệu mà không cần truy vấn lại
        FilteredList<AuctionDashBoard.AuctionItem> filteredData = new FilteredList<>(masterData, p -> true);

        // 1. THANH TÌM KIẾM & BỘ LỌC (TOP)
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(0, 0, 20, 0));

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Tìm kiếm vật phẩm đấu giá...");
        txtSearch.setPrefWidth(300);
        txtSearch.setStyle("-fx-background-radius: 4;");

        ComboBox<String> cbFilterStatus = new ComboBox<>();
        cbFilterStatus.getItems().addAll("Tất cả trạng thái", "Đang diễn ra", "Chưa bắt đầu", "Đã kết thúc");
        cbFilterStatus.getSelectionModel().selectFirst();

        topBar.getChildren().addAll(txtSearch, cbFilterStatus);
        this.setTop(topBar);

        // 2. KHU VỰC HIỂN THỊ LƯỚI SẢN PHẨM (CENTER)
        gridPane.setHgap(20);
        gridPane.setVgap(20);

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        this.setCenter(scrollPane);

        // 3. LOGIC KẾT HỢP BỘ LỌC & TÌM KIẾM
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> matchFilter(item, newValue, cbFilterStatus.getValue()));
        });

        cbFilterStatus.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> matchFilter(item, txtSearch.getText(), newValue));
        });

        // Mỗi khi danh sách sau khi lọc thay đổi -> Vẽ lại lưới UI
        filteredData.addListener((javafx.collections.ListChangeListener<? super AuctionDashBoard.AuctionItem>) c -> {
            renderGrid(filteredData);
        });

        // Vẽ lưới lần đầu tiên
        renderGrid(filteredData);
    }




    // Hàm kiểm tra xem vật phẩm có khớp với từ khóa tìm kiếm và bộ lọc không
    private boolean matchFilter(AuctionDashBoard.AuctionItem item, String searchText, String statusFilter) {
        boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                (item.name != null && item.name.toLowerCase().contains(searchText.toLowerCase()));
        boolean matchesStatus = statusFilter == null || statusFilter.equals("Tất cả trạng thái");

        return matchesSearch && matchesStatus;
    }

    // Hàm phụ trách xếp các Card vào Toạ độ (Cột, Hàng) của GridPane
    private void renderGrid(ObservableList<AuctionDashBoard.AuctionItem> list) {
        gridPane.getChildren().clear();
        int columns = 4; // Định dạng cố định hiển thị 4 cột sản phẩm một hàng

        for (int i = 0; i < list.size(); i++) {
            AuctionGridCard card = new AuctionGridCard(list.get(i));
            int column = i % columns;
            int row = i / columns;
            gridPane.add(card, column, row);
        }
    }
    private void loadSystemAuctions() {
        masterData.add(new AuctionDashBoard.AuctionItem("Tranh Sơn Dầu Phố Cổ 1990", "15000000", "02:00","Đang diễn ra"));
        masterData.add(new AuctionDashBoard.AuctionItem("Đồng hồ cổ Rolex Submariner", "320000000", "00:45","Đang diễn ra"));
        masterData.add(new AuctionDashBoard.AuctionItem("Bình gốm Chu Đậu thế kỷ XV", "45000000", "00:00","Đã đóng"));
        masterData.add(new AuctionDashBoard.AuctionItem("Tượng Phật ngọc Nephrite", "89000000", "00:00","Đã đóng"));
        masterData.add(new AuctionDashBoard.AuctionItem("Sách cổ mạ vàng quý hiếm", "6500000", "00:00","Đã đóng"));
        masterData.add(new AuctionDashBoard.AuctionItem("Đồng xu bạc thời Nhà Nguyễn", "2300000", "01:30","Đã đóng"));
    }
}
