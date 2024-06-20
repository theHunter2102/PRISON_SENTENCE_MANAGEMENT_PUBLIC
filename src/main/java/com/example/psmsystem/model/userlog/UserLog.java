package com.example.psmsystem.model.userlog;

import java.time.*;

public class UserLog {
    private int userId;
    private String userName;
    private LocalDateTime dateUpdate;
    private String note;

    public UserLog() {
    }

    public UserLog(int userId, String userName, LocalDateTime dateUpdate, String note) {
        this.userId = userId;
        this.userName = userName;
        this.dateUpdate = dateUpdate;
        this.note = note;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(LocalDateTime dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
