package controller;

import com.google.gson.reflect.TypeToken;
import controller.network.MessageListener;
import controller.network.ServerConnection;
import function.SystemLogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.AuctionSession;
import models.User;
import org.junit.platform.engine.SelectorResolutionResult;
import server.GsonUtil;
import server.Role;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardController implements MessageListener {
    /*
    Thuộc tính
     */
    private ServerConnection connection;
    private Stage primaryStage;
    private User user;
    private Map<String,AuctionSession> listRoom = new HashMap<>();

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
    @FXML
    private Text userRoleText;
    @FXML
    private AnchorPane SessionsPane;

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
    public void setUserRoleText(Role role){userRoleText.setText(role.name());}

    public void setUser(User user){this.user = user;}
    public AnchorPane getSessionPane(){return SessionsPane;}
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
                        addSessionsList(list);

                        reSessionCard();
                    }
                    System.out.println(list);

                case "ERROR":
                    System.out.println(parts[1]);
            }
        });
    }


    /*
    Helper Methods
     */
    public Node setupSessionCard(AuctionSession auctionSession) throws IOException {
        SystemLogger.getInstance().warning("setupSessionCard in here 1");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SessionCard.fxml"));

        Parent root = loader.load();
        SessionCardController sessionCardController = loader.getController();

        SystemLogger.getInstance().warning("setupSessionCard in here 2");
        long duration = Duration.between(auctionSession.getStartTime(),auctionSession.getEndTime()).toSeconds();

        SystemLogger.getInstance().warning("setupSessionCard in here 3");
        sessionCardController.setCurrencyPriceText(String.valueOf(auctionSession.getCurrentPrice()));
        sessionCardController.setItemTypeText(auctionSession.getItem().getName());
        sessionCardController.setSellerNameText(auctionSession.getSeller().getName());
        sessionCardController.setlongTime((int)duration);
        sessionCardController.setRoot(SessionsPane);
        sessionCardController.setSession(auctionSession);
        SystemLogger.getInstance().warning("setupSessionCard in here 4");
        sessionCardController.setPrimaryStage(primaryStage);
        sessionCardController.setConnection(connection);
        sessionCardController.setDashboardController(this);
        return root;

    }
    public void reSessionCard(){
        try {
            List<AuctionSession> list = new ArrayList<>(listRoom.values());
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
    public void showSessionCard(){
        SessionsPane.getChildren().set(0,sessionsGridCard);
    }

    public void setUpController(User user, Stage stage, ServerConnection connection){
        primaryStage = stage;
        this.connection = connection;
        this.connection.setMessageListener(this);

        this.user = user;

        balanceText.setText(user.getWallet().getBalance() + "");

        usernameText.setText(user.getName());

        userRoleText.setText(user.getRole().name());

        connection.sendCommand("GET_SESSIONS|ALL");

    }

    //Helper for listRoom;
    public void addSessionsList(List<AuctionSession> sessionList){
        sessionList.forEach(session -> listRoom.put(session.getID().toString(),session));
    }





}
