module Realtime.Auction.Platform {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    opens java.view to javafx.fxml; // Thay đổi theo package của bạn
    exports java.view;
    exports java;          // Bắt buộc phải có để Java kích hoạt được file Main.java
    exports java.database; // Cho phép các nơi khác gọi DatabaseCreator
    exports java.models;
}