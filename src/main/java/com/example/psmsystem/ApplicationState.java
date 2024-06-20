package com.example.psmsystem;

import java.util.List;

public class ApplicationState {
    private static ApplicationState instance;
    private String username;
    private int id;
    private List<RoleName> roleName;

    // Private constructor to prevent instantiation
    private ApplicationState() {}

    // Static method to get the single instance of the class
    public static ApplicationState getInstance() {
        if (instance == null) {
            instance = new ApplicationState();
        }
        return instance;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static void setInstance(ApplicationState instance) {
        ApplicationState.instance = instance;
    }

    public enum  RoleName {
        PRISONER_MANAGEMENT,HEALTH_EXAMINER,VISIT_CONTROL,ULTIMATE_AUTHORITY
    }

    public List<RoleName> getRoleName() {
        return roleName;
    }

    public void setRoleName(List<RoleName> roleName) {
        this.roleName = roleName;
    }
}

