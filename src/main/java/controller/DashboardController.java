package controller;

import com.google.gson.reflect.TypeToken;
import controller.network.MessageListener;
import controller.network.ServerConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.AuctionSession;
import org.junit.platform.engine.SelectorResolutionResult;
import server.GsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DashboardController implements MessageListener {
    /*
    Thuộc tính
     */
    private ServerConnection connection;
    private Stage primaryStage;

    @FXML
    private Text balanceText;
    @FXML
    private Text joinsText;
    @FXML
    private Text usernameText;
    @FXML
    private GridPane sessionsGridCard;
    @FXML
    private HBox dashboardHbox;

    /*
    Hàm khởi tạo
     */
    @FXML
    private void initialize(){
    }

    public void setConnection(ServerConnection connection) {
        this.connection = connection;
        connection.setMessageListener(this);


    }
    public void setPrimaryStage(Stage stage){
        primaryStage = stage;
    }

    public void setBalanceText(String balance, String dv){
        balanceText.setText(balance + dv);
    }
    public void setJoinsText(String sessions){
        joinsText.setText(sessions);
    }
    public void setUsernameText(String name){
        usernameText.setText(name);
    }

    /*
        Xử lí sự kiện
         */
    @FXML
    public void nagivUserView(ActionEvent e){}
    public void nagivWalletView(ActionEvent e){}
    public void nagivItemView(ActionEvent e){}

    public void nagivLogoutView(MouseEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));

        Parent root = loader.load();
        LoginController loginController = loader.getController();

        loginController.setPrimaryStage(primaryStage);
        loginController.setConnection(connection);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }


    /*
    Xử lí server
     */
    @Override
    public void onMessageReceived(String message) {
        String[] parts = message.trim().split("\\|");
        String command = parts[0];

        javafx.application.Platform.runLater(() -> {
            switch (command){
                case "SUCCESS_SESSIONS":
                    // 1. Định nghĩa chính xác kiểu dữ liệu kèm cả Generic

                // 2. Truyền listType vào thay vì List.class
                    List<AuctionSession> list = new ArrayList<>();
                    if(parts[1].equals("EMPTY")){

                    }
                    else {
                        Type listType = new TypeToken<List<AuctionSession>>(){}.getType();
                        list = GsonUtil.gson.fromJson(parts[1], listType);

                        try {
                            int index = 0;
                            for(int i = 0; i < 3; i ++){
                                for(int j = 0; j < 2; j++){
                                    if(index >= list.size()){
                                        break;
                                    }
                                    AuctionSession auctionSession = list.get(index);
                                    Node node = setupSessionCard(auctionSession);
                                    sessionsGridCard.add(node,j,i);
                                    index++;
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    System.out.println(list);;
                case "ERROR":
                    System.out.println(parts[1]);
            }
        });
    }


    /*
    Helper Methods
     */
    public Node setupSessionCard(AuctionSession auctionSession) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SessionCard.fxml"));

        Parent root = loader.load();
        SessionCardController sessionCardController = loader.getController();

        long duration = Duration.between(auctionSession.getStartTime(),auctionSession.getEndTime()).toSeconds();

        sessionCardController.setCurrencyPriceText(String.valueOf(auctionSession.getCurrentPrice()));
        sessionCardController.setItemTypeText(auctionSession.getItem().getName());
        sessionCardController.setSellerNameText(auctionSession.getSeller().getName());
        sessionCardController.setlongTime((int)duration);
        sessionCardController.setRoot(dashboardHbox);
        sessionCardController.setSession(auctionSession);
        sessionCardController.setPrimaryStage(primaryStage);

        return root;

    }


}
