module Realtime.Auction.Platform {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    requires org.junit.jupiter.engine;
    requires org.junit.jupiter.api;
    requires org.mockito.junit.jupiter;
    opens view to javafx.fxml; // Thay đổi theo package của bạn
    exports view;
    exports database; // Cho phép các nơi khác gọi DatabaseCreator
    exports function;
//    exports server;
    exports controller;
    exports models;

    // THÊM: Mở package cho JUnit
    opens function to org.junit.platform.commons;
    opens database to org.junit.platform.commons;
    opens controller to org.junit.platform.commons;
    opens models to org.junit.platform.commons;
//    opens server to org.junit.platform.commons;

}