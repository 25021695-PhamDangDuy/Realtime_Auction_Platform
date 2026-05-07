module Realtime.Auction.Platform {
    requires javafx.controls;
    requires javafx.fxml;

    opens myWeb.view to javafx.fxml; // Thay đổi theo package của bạn
    exports myWeb.view;

}