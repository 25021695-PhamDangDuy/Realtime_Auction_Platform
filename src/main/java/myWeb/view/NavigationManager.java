package myWeb.view;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.collections.ObservableList;

public class NavigationManager {
    private static StackPane contentArea;
    private static ObservableList<AuctionDashBoard.AuctionItem> globalMockData;

    // Khởi tạo vùng chứa chính của Dashboard
    public static void initialize(StackPane area, ObservableList<AuctionDashBoard.AuctionItem> mockData) {
        contentArea = area;
        globalMockData = mockData;
    }

    // Hàm gọi để chuyển màn hình động sang bất kỳ Component nào
    public static void switchView(Pane newView) {
        if (contentArea != null) {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(newView);
        } else {
            System.err.println("NavigationManager chưa được khởi tạo contentArea!");
        }
    }

    // Trả về mockData chung cho các màn hình dùng chung dữ liệu
    public static ObservableList<AuctionDashBoard.AuctionItem> getMockData() {
        return globalMockData;
    }
}
