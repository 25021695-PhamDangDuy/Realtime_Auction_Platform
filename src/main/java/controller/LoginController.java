package controller;

import controller.network.MessageListener;
import controller.network.ServerConnection;
import function.SystemLogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.User;
import server.GsonUtil;

import java.io.IOException;

public class LoginController implements MessageListener {
    private ServerConnection connection;
    private Stage primaryStage;
    /*
    Khởi tạo ID
     */
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label loginLabel;

    //Setter
    public void setConnection(ServerConnection connection) {
        this.connection = connection;
        connection.setMessageListener(this);
    }
    public void setPrimaryStage(Stage stage){
        primaryStage = stage;
    }
    //Init
    @FXML
    private void initialize(){
        loginLabel.setText("");
    }

    /*
    =====================[Xử lí sự kiện]================================
     */
    @FXML
    public void handleLoginButton(ActionEvent e){
        String name = usernameTextField.getText().strip();
        String pw = passwordTextField.getText();



        String command = "LOGIN|" + name + "|" + pw;
        connection.sendCommand(command);
    }

    @FXML
    public void nagivToRegister(ActionEvent e) throws IOException {
        Stage primaryStage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        Scene newScene = getScene("/view/RegisterView.fxml");

        primaryStage.setScene(newScene);
        primaryStage.show();
    }

    /*
    ================[Xử lí thông tin từ server]===========================
     */
    @Override
    public void onMessageReceived(String message) {
        String[] parts = message.split("\\|");
        String command = parts[0];

        javafx.application.Platform.runLater(() -> {
            //Xử lí logic thông tin từ server
            switch (command){
                case "SUCCESS_LOGIN":
                    //Nagiv to role of User
                    SystemLogger.getInstance().info("Login Success");
                    loginLabel.setText("Đăng nhập thành công!");

                    User user = GsonUtil.gson.fromJson(parts[1], User.class);
                    String balance = String.valueOf(user.getWallet().getBalance());
                    nagivToDashboard(user.getName(),balance);

                    connection.sendCommand("GET_SESSIONS|ACTIVE");
                    connection.sendCommand("GET_SESSION|UPCOMING");

                    break;
                case "ERROR":
                    String displayLabel = handleException(parts[1],usernameTextField.getText(),passwordTextField.getText());
                    loginLabel.setText(displayLabel);
                    break;
            }
        });

    }


    /*
    ===================[Helper Methods]==================================
     */

    public Scene getScene(String url) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));

        Parent root = loader.load();

        RegisterController registerController =  loader.getController();
        registerController.setConnection(this.connection);

        return new Scene(root);
    }

    public String handleException(String e,String name,String pw){
        if(e.equals("Thiếu tham số đăng nhập.")){
            if(name.equals("")){
                return "Vui lòng điền tên đăng nhập";
            }else {
                return "Vui lòng điền mật khẩu";
            }
        }
        return e;
    }

    private void nagivToDashboard(String name, String balance){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));

            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setConnection(connection);
            dashboardController.setPrimaryStage(primaryStage);

            dashboardController.setUsernameText(name);
            dashboardController.setBalanceText(balance,"VND");

            Scene newScene = new Scene(root);

            primaryStage.setScene(newScene);

            primaryStage.centerOnScreen();

            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
