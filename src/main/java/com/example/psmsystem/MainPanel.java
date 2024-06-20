/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.psmsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author Ramesh Godara
 */
public class MainPanel extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("view/LoginView.fxml"));
        Scene scene = new Scene(root);
        stage.setMaximized(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("User Login");
        stage.setScene(scene);
        stage.getIcons().add(new Image("file: /com/example/psmsystem/assets/icon.png"));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String sql = "SELECT ul.user_id, ul.date_update, ul.note FROM update_log ul " +
                "JOIN user_role ur ON ur.user_id = ul.user_id " +
                "JOIN roles r ON r.role_id = ur.role_id " +
                "WHERE ";
        String name = "value";

        for (int i = 0 ;i < 3; i ++) {
            sql += "r.name LIKE '"+name+"'";
            if(i < 2) {
                sql += " OR ";
            }
        }
        System.out.println(sql);
        launch(args);
    }
}
