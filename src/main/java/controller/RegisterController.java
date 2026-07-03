package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController {
    @FXML
    private TextField usernameTextField;
    private PasswordField passwordTextField;
    private PasswordField confirmpasswordTextField;
    private Button registerButton;
    private Label usernameLabel;
    private Label passwordLabel;
    private Label registerLabel;

    public void initialize(URL url, ResourceBundle resourceBundle){
        usernameLabel.setText("");
        passwordLabel.setText("");
        registerLabel.setText("");
    }

    public void handleRegisterButton(ActionEvent e){
        //Lấy dữ liệu
        String name = usernameTextField.getText().strip();
        String pw = passwordTextField.getText();
        String cpw = confirmpasswordTextField.getText();

        //Kiểm tra logic (sau này sẽ cần đẩy logic nghiệp vụ ra chỗ khác)
        if(!cpw.equals(pw)){
            passwordLabel.setText("Password is not match");
            passwordLabel.setText("");
            registerLabel.setText("Register is'nt successful");
        }
        if(name.length() < 3){
            usernameLabel.setText("Username is not enough length");
            usernameLabel.setText("");
            registerLabel.setText("Register is'nt successful");
        }

        usernameTextField.clear();
        passwordTextField.clear();
        confirmpasswordTextField.clear();
        registerLabel.setText("Register is successfull");




    }
}
