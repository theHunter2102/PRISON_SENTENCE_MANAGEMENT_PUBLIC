package com.example.psmsystem.controller.userlog;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserLogController {
    @FXML
    private Label txtDateTime;

    @FXML
    private Label txtNote;

    @FXML
    private Label txtUserName;

    public void setUserLogData(String username, String date, String note) {
        txtUserName.setText(username);
        LocalDateTime logDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        String formattedDateTime = formatDateTime(logDateTime);
        txtDateTime.setText(formattedDateTime);
        txtNote.setText(note);
    }
    private String formatDateTime(LocalDateTime logDateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(logDateTime, now);

        if (duration.toMinutes() < 60) {
            return duration.toMinutes() + " minutes ago";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + " hours ago";
        } else {
            return logDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}
