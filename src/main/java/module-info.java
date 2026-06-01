module Realtime.Auction.Platform {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    requires org.junit.jupiter.api;

    opens view to javafx.fxml; // Thay đổi theo package của bạn
    exports view;
    exports database; // Cho phép các nơi khác gọi DatabaseCreator
    exports function;
    exports server;
    exports controller;
    exports models;

}