package com.example.psmsystem.config;

import javafx.application.Application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private final Properties properties;
    private static AppConfig instance;
    private String usernameData;
    private String passwordData;
    private String urlData;
    private String imagePathDefault;
    private String imagePathSave;

    public String getImagePathDefault() {
        return imagePathDefault;
    }

    public String getImagePathSave() {
        return imagePathSave;
    }

    private AppConfig() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("failed get properties");
                return;
            }
            // Load a properties file from class path, inside static method
            properties.load(input);
            // Get the property value and print it out
            this.usernameData = properties.getProperty("db.username");
            this.passwordData = properties.getProperty("db.password");
            this.urlData = properties.getProperty("db.url");
            this.imagePathDefault = properties.getProperty("imagePathDefault");
            this.imagePathSave = properties.getProperty("imagePathSave");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public String getUsernameData() {
        return usernameData;
    }

    public String getPasswordData() {
        return passwordData;
    }

    public String getUrlData() {
        return urlData;
    }
}
