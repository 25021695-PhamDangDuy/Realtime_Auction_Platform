package controller;

import controller.network.MessageListener;
import function.SystemLogger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.AuctionSession;

import java.io.IOException;

public class SessionCardController implements MessageListener {
    /*
    Thuộc tính
     */
    private Stage primaryStage;
    private int longTime;
    private Timeline timeline;
    private Parent root;
    private AuctionSession session;

    @FXML
    private Text itemTypeText;
    @FXML
    private Text sellerNameText;
    @FXML
    private Text currencyPriceText;
    @FXML
    private Text timeText;
    @FXML
    private ImageView imgItem;
    @FXML
    private Button joinRoomButton;

    public void setItemTypeText(String itemTypeText) {
        this.itemTypeText.setText(itemTypeText);
    }

    public void setSellerNameText(String sellerNameText){
        this.sellerNameText.setText(sellerNameText);
    }

    public void setCurrencyPriceText(String s){
        currencyPriceText.setText(s);
    }
    public void setlongTime(int time){
        longTime = time;
        timeline.play();
    }
    public void setPrimaryStage(Stage stage){
        primaryStage = stage;
    }
    public void setRoot(Parent parent){
        root = parent;
    }
    public void setSession(AuctionSession auctionSession){
        session = auctionSession;
    }

    //Hàm khởi tạo
    @FXML
    public void initialize(){
        timelineHandle();
    }

    /*
            Xử lí sự kiện
             */
    @FXML
    public void handlejoinButton(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AuctionRoom.fxml"));
        SystemLogger.getInstance().info("Load Sucess");
        Parent parent = loader.load();

        if(root instanceof HBox){
            ((HBox) root).getChildren().set(1,parent);
        }

        primaryStage.centerOnScreen();
        primaryStage.show();
        SystemLogger.getInstance().info("Set Primary Sucess");

    }

    @Override
    public void onMessageReceived(String message) {}

    public void timelineHandle(){
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            if(longTime >= 0){
                int seconds = longTime % 60;
                int partminutes = longTime / 60;
                int minutes = partminutes % 60;
                int hours = partminutes / 60;

                String time = hours + "h:" + minutes + "m:" + seconds + "s";
                timeText.setText(time);
            }
            else {
                timeline.stop();
                timeText.setText("Hết Giờ!");
            }
            longTime--;
        });

        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);


    }
}
