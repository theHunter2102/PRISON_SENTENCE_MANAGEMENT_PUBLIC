package com.example.psmsystem.controller;

import com.example.psmsystem.controller.prisoner.AddPrisonerController;
import com.example.psmsystem.helper.AlertHelper;
import com.example.psmsystem.model.user.IUserDao;
import com.example.psmsystem.model.user.User;
import com.example.psmsystem.service.userDao.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;

public class LoginController implements Initializable{
    private static IUserDao<User> userDao;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    Window window;

    String fxmlPath = "/com/example/psmsystem/";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public LoginController() {
        userDao = new UserDao();
    }

    @FXML
    void login(ActionEvent event) throws IOException {
        if (this.isValidated()) {
            String usernameText = username.getText();
            String passwordText = password.getText();
            try {
                User user = userDao.checkLogin(usernameText, passwordText);
                if (user != null) {
                    loginButton.getScene().getWindow().hide();

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath + "view/MainPanelView.fxml"));
                        Parent root = loader.load();
                        Stage stage = new Stage();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.setMaximized(true);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setTitle("Admin Panel");
                        stage.getIcons().add(new Image("file: " + fxmlPath + "assets/icon.png"));
                        stage.show();
                        MainPanelController controller = loader.getController();
                        controller.initData(user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                            "Invalid username and password.");
                    username.requestFocus();
                }
            } catch (RuntimeException e) {

                    AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                            "Invalid username and password.");
                    username.requestFocus();

            }


        }
    }


    private boolean isValidated() {
        window = loginButton.getScene().getWindow();
        if (username.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Username text field cannot be blank.");
            username.requestFocus();
        } else if (username.getText().length() < 5 || username.getText().length() > 25) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Username text field cannot be less than 5 and greator than 25 characters.");
            username.requestFocus();
        } else if (password.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Password text field cannot be blank.");
            password.requestFocus();
        } else if (password.getText().length() < 5 || password.getText().length() > 25) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Password text field cannot be less than 5 and greator than 25 characters.");
            password.requestFocus();
        } else {
            return true;
        }
        return false;
    }


    @FXML
    void showRegisterStage(MouseEvent event) throws IOException{
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath + "view/RegisterView.fxml")));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("User Registration");
        stage.getIcons().add(new Image("file: " + fxmlPath + "assets/icon.png"));
        stage.show();
    }

    public void onBtnCloseClick(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

//    private String hashPassword(String password) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash = digest.digest(password.getBytes());
//            StringBuilder hexString = new StringBuilder();
//
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) hexString.append('0');
//                hexString.append(hex);
//            }
//
//            return hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//            return null;
//
//        }
//    }
}
