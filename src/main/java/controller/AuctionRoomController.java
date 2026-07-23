package controller;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import controller.network.MessageListener;
import controller.network.ServerConnection;
import function.SystemLogger;
import javafx.animation.PauseTransition;
import javafx.css.CssParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.AuctionSession;
import models.User;
import net.bytebuddy.description.method.MethodDescription;
import server.GsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

public class AuctionRoomController implements MessageListener {
    //Tham số
    private Stage primaryStage;
    private ServerConnection connection;
    private AuctionSession session;
    private User user;
    private DashboardController mainController;
    @FXML
    private Button bidButton;

    @FXML
    private Label conditionText;

    @FXML
    private Text currencyPriceText;

    @FXML
    private Text idSessionText;

    @FXML
    private Text topUserText;

    @FXML
    private TextField inputTextField;

    @FXML
    private Text itemNameText;

    @FXML
    private Text itemTypeText;

    @FXML
    private Button leaveButton;

    @FXML
    private Text miniumStepText;

    @FXML
    private Text sellernameText;

    @FXML
    private Text timeText;

    @FXML
    private Text notifyText;

    public void setConnection(ServerConnection connection) {
        this.connection = connection;
        connection.setMessageListener(this);
    }

    public void setMainController(DashboardController mainController) {
        this.mainController = mainController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public void setSession(AuctionSession session) throws SQLException {
        this.session = session;

        conditionText.setText(session.getItem().getCondition());

        setCurrencyPriceText(String.valueOf(session.getCurrentPrice()));
        idSessionText.setText(session.getID().toString());
        itemNameText.setText(session.getItem().getName());
        itemTypeText.setText(session.getItem().getClass().getName());
        setMiniumStepText(String.valueOf(session.getMinIncrement()));
        sellernameText.setText(session.getSeller().getName());
        timeText.setText(session.getStartTime().toString());

        if(session.getTopBid() == null){
            setTopUserText("Chưa có");
        }
        else {
            setTopUserText(session.getTopBidder().getName());
        }
    }
    public void setUser(User u){user = u;}

    public void setCurrencyPriceText(String price) {
        String prompt = "Giá hiện tại:" + price + "VNĐ";
        this.currencyPriceText.setText(prompt);
    }
    public void setMiniumStepText(String price){
        String prompt = "Bước giá:" + session.getMinIncrement() + "VNĐ";
        this.miniumStepText.setText(prompt);
    }
    public void setTopUserText(String user){
        String prompt = "Người thắng hiện tại:" + user;
        this.topUserText.setText(prompt);
    }


    /*
        Xử lí sự kiện
         */
    @FXML
    public void handleBidButton(ActionEvent e){
        String amount = inputTextField.getText();

        String command = "BID|" + session.getID() + "|" + amount;

        connection.sendCommand(command);
        System.out.println(command);
    }
    @FXML
    public void handleLeaveButton(ActionEvent e) throws IOException {
        String command = "LEAVE_ROOM|" + session.getID();
        connection.sendCommand(command);
    }

    @Override
    public void onMessageReceived(String message) {
        String[] parts = message.trim().split("\\|");
        String command = parts[0];

        javafx.application.Platform.runLater(() -> {
            switch (command){
                case "SUCCESS_INFORMATION":
                    SystemLogger.getInstance().warning("SUCCESS INFOR 1");
                    User user = GsonUtil.gson.fromJson(parts[1],User.class);
                    this.user = user;

                    SystemLogger.getInstance().warning("SUCCESS INFOR 2" + this.user.getName());

                    mainController.setBalanceText(String.valueOf(user.getWallet().getBalance()),"VNĐ");

                    SystemLogger.getInstance().info("Set Primary Sucess");
                    break;

                case "SUCCESS_BID":

                    notifyText.setText("Đặt giá thành công");
                    connection.sendCommand("GET_INFORMATION");

                    javafx.util.Duration duration = Duration.millis(700);
                    PauseTransition pauseTransition = new PauseTransition(duration);
                    pauseTransition.setOnFinished(event -> {

                        setTopUserText(this.user.getName());
                        setCurrencyPriceText(inputTextField.getText());

                        inputTextField.clear();
                        notifyText.setText("");
                    });

                    pauseTransition.play();
                    break;

                case "SUCCESS_LEAVE_ROOM":
                    mainController.reSessionCard();
                    mainController.showSessionCard();
                    mainController.setUpController(this.user,primaryStage,connection);
                    break;
                case "ERROR":

                    try {
                        notifyText.setText(parts[1]);
                        wait(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }finally {
                        notifyText.setText("");
                    }
                    break;

            }
        });
    }
}
