module Realtime.Auction.Platform {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    requires org.junit.jupiter.engine;
    requires org.junit.jupiter.api;
    requires org.mockito.junit.jupiter;
    requires javafx.fxml;
    opens view to javafx.fxml,com.google.gson; // Thay đổi theo package của bạn
    exports view;
    exports database; // Cho phép các nơi khác gọi DatabaseCreator
    exports function;
//    exports server;
    exports service;
    exports models;

    // THÊM: Mở package cho JUnit
    opens server to com.google.gson;
    opens function to org.junit.platform.commons, com.google.gson;
    opens database to org.junit.platform.commons,com.google.gson;
    opens service to org.junit.platform.commons;
    opens models to org.junit.platform.commons,com.google.gson;
    exports service.brain;
    opens service.brain to org.junit.platform.commons;
    exports database.items;
    opens database.items to org.junit.platform.commons;
//    opens server to org.junit.platform.commons;

}