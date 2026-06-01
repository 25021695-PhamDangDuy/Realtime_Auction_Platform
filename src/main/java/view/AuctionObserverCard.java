package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class AuctionObserverCard extends HBox {
    public AuctionObserverCard(AuctionDashBoard.AuctionItem item) {
        this.setSpacing(20);
        this.setPadding(new Insets(10, 15, 10, 15));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 6;");

        Label lblTitle = new Label(item.name);
        lblTitle.setStyle("-fx-font-weight: bold;");
        HBox.setHgrow(lblTitle, Priority.ALWAYS); // Chiếm trọn khoảng trống bên trái

        Label lblPrice = new Label(item.price);
        lblPrice.setStyle("-fx-text-fill: #e53935; -fx-font-weight: bold;");

        Label lblTime = new Label("⏳ " + item.timeLeft);

        Button btnUnwatch = new Button("Bỏ theo dõi");
        btnUnwatch.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px;");

        this.getChildren().addAll(lblTitle, lblPrice, lblTime, btnUnwatch);
    }
}
