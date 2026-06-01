module Realtime.Auction.Platform {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;

    opens view to javafx.fxml; // Thay đổi theo package của bạn
    exports view;
    exports database; // Cho phép các nơi khác gọi DatabaseCreator
    exports models;
    exports controller;
    exports server;

}