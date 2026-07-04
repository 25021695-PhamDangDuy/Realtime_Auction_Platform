package controller;

import controller.network.MessageListener;
import controller.network.ServerConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController implements MessageListener {
    private ServerConnection connection;

    /*
    Khởi tạo ID
     */
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    //Setter
    public void setConnection(ServerConnection connection) {
        this.connection = connection;
        connection.setMessageListener(this);
    }

    //Init
    @FXML
    private void initialize(){
    }

    @FXML
    public void handleLoginButton(ActionEvent e){
        String name = usernameTextField.getText().strip();
        String pw = passwordField.getText().strip();

        String command = "LOGIN|" + name + "\\|" + pw;
        connection.sendCommand(command);
    }

    @Override
    public void onMessageReceived(String message) {
        String[] parts = message.split("\\|");
        String command = parts[0];


    }
}
