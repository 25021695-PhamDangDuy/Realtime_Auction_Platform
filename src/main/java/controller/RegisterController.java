package controller;

import controller.network.MessageListener;
import controller.network.ServerConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import service.brain.AccountController;
import java.net.URL;

public class RegisterController implements MessageListener {
    private ServerConnection connection;


    /*
    ===============[Hàm khởi tạo]=====================
     */
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private PasswordField confirmpasswordTextField;
    @FXML
    private Button registerButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label registerLabel;
    @FXML
    private Hyperlink loginLink;

    public void setConnection(ServerConnection conn){
        connection = conn;
        connection.setMessageListener(this);
    }

    //Hàm khởi tạo
    @FXML
    private void initialize(){
        usernameLabel.setText("");
        passwordLabel.setText("");
        registerLabel.setText("");

    }

    /*
    ===============[Xử lí sự kiện]=====================
     */
    @FXML
    public void handleRegisterButton(ActionEvent e) throws IOException {
        //Lấy dữ liệu
        String name = usernameTextField.getText().strip();
        String pw = passwordTextField.getText();
        String cpw = confirmpasswordTextField.getText();

        //Gửi yêu cầu
        String command = "REGISTER|" + name + "|" + pw + "|" + cpw;
        connection.sendCommand(command);


    }

    @FXML
    public void nagivToLogin(ActionEvent e) throws IOException {
        Scene loginScene = getNewScene("/view/LoginView.fxml");

        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();

        stage.setScene(loginScene);
        stage.setTitle("LoginView");

        stage.show();
    }


    /*
    ===============[Xử lí thông tin từ server]=====================
     */
    @Override
    public void onMessageReceived(String message) {
        String[] parts = message.trim().split("\\|");
        String command = parts[0];

        javafx.application.Platform.runLater(() -> {
            if(command.equals("SUCCESS")){
                usernameTextField.clear();
                passwordTextField.clear();
                confirmpasswordTextField.clear();
                registerLabel.setText(parts[1]);
            }
            else {
                String name = usernameTextField.getText().strip();
                String pw = passwordTextField.getText();
                String cpw = confirmpasswordTextField.getText();

                String s = getFailString(parts[1],name,pw,cpw);
                registerLabel.setText(s);
            }

        });
    }
    /*
    ===================[Helper Methods]==============================
     */
    private String getFailString(String failString, String name, String pw, String cpw){
        if(failString.equals("Thiếu tham số đăng ký.")) {
            if (name.equals("")) {
                return "Điền tên đăng kí!";
            }
            if (pw.equals("")) {
                return "Điền mật khẩu của bạn!";
            }
            if (cpw.equals("")) {
                return "Điền mật khẩu xác thực của bạn!";
            }
        }
        return failString;
    }

    private Scene getNewScene(String url) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        Parent loginRoot = loader.load();

        LoginController loginController = loader.getController();
        loginController.setConnection(connection);

        return new Scene(loginRoot);
    }
}
