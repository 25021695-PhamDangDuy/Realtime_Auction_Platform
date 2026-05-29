module Realtime.Auction.Platform {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    opens myWeb.view to javafx.fxml; // Thay đổi theo package của bạn
    exports myWeb.view;
    exports myWeb;          // Bắt buộc phải có để Java kích hoạt được file Main.java
    exports myWeb.database; // Cho phép các nơi khác gọi DatabaseCreator
    exports myWeb.models;
}