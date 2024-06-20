package com.example.psmsystem.controller;

import com.example.psmsystem.helper.AlertHelper;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.psmsystem.model.user.IUserDao;
import com.example.psmsystem.model.user.User;
import com.example.psmsystem.service.userDao.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

public class RegisterController implements Initializable{

    private static IUserDao<User> userDao;

    @FXML
    private Button registerButton;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private TextField txtFullName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    @FXML
    private CheckBox checkBoxHealthExaminer;

    @FXML
    private CheckBox checkBoxPrisonerManagement;

    @FXML
    private CheckBox checkBoxVisitControl;

    Window window;

    String fxmlPath = "/com/example/psmsystem/";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public RegisterController() {
        userDao = new UserDao();
    }

    @FXML
    private void register() {
        window = registerButton.getScene().getWindow();
        if (this.isValidated()) {
            String password = txtPassword.getText();
            User user = new User(txtFullName.getText(), txtUsername.getText(), password);
            // get list role
            List<String> roleNames = new ArrayList<>();
            if (checkBoxHealthExaminer.isSelected()) {
                roleNames.add("HEALTH_EXAMINER");
            }
            if (checkBoxPrisonerManagement.isSelected()) {
                roleNames.add("PRISONER_MANAGEMENT");
            }
            if (checkBoxVisitControl.isSelected()) {
                roleNames.add("VISIT_CONTROL");
            }

            // check size
            if (roleNames.size() < 1 || roleNames.size() > 2) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                        "Please select at least 1 and at most 2 roles.");
                return;
            }
            try {
                userDao.addUser(user,roleNames);
                this.clearForm();
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Information",
                        "You have registered successfully.");
            } catch (RuntimeException e) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                        e.getMessage());
                return;
            }

        }
    }

    private boolean isAlreadyRegistered() {
        return userDao.checkUsername(txtUsername.getText());
    }

    private boolean isValidated() {

        window = registerButton.getScene().getWindow();
        if (txtFullName.getText().equals("")) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Full name text field cannot be blank.");
            txtFullName.requestFocus();
        } else if (txtFullName.getText().length() < 2 || txtFullName.getText().length() > 25) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "First name text field cannot be less than 2 and greator than 25 characters.");
            txtFullName.requestFocus();
        } else if (txtUsername.getText().equals("")) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Last name text field cannot be blank.");
            txtUsername.requestFocus();
        } else if (txtUsername.getText().length() < 2 || txtUsername.getText().length() > 25) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Last name text field cannot be less than 2 and greator than 25 characters.");
            txtUsername.requestFocus();
        } else if (txtPassword.getText().equals("")) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Email text field cannot be blank.");
            txtPassword.requestFocus();
        } else if (txtPassword.getText().length() < 5 || txtPassword.getText().length() > 45) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Email text field cannot be less than 5 and greator than 45 characters.");
            txtPassword.requestFocus();
        } else if (txtConfirmPassword.getText().equals("")) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Username text field cannot be blank.");
            txtConfirmPassword.requestFocus();
        } else if (txtConfirmPassword.getText().length() < 5 || txtConfirmPassword.getText().length() > 25) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Username text field cannot be less than 5 and greator than 25 characters.");
            txtConfirmPassword.requestFocus();
        } else if (isAlreadyRegistered()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "The username is already taken by someone else.");
            txtConfirmPassword.requestFocus();
        } else {
            return true;
        }
        return false;
    }

    private boolean clearForm() {
        txtFullName.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
        return true;
    }

    @FXML
    private void showLoginStage() throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.close();

        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath + "view/LoginView.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("User Login");
        stage.getIcons().add(new Image("file: " + fxmlPath + "assets/icon.png"));
        stage.show();
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

    public void onBtnCloseClick(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
}
