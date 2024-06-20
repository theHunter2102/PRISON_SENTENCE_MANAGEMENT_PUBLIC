/**
 * @author inforkgodara
 */
package com.example.psmsystem.database;

import com.example.psmsystem.config.AppConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

public class DbConnection {

    private Connection con;
    private static DbConnection dbc;

    private DbConnection() {

        try {

            con = DriverManager.getConnection(AppConfig.getInstance().getUrlData(), AppConfig.getInstance().getUsernameData(), AppConfig.getInstance().getPasswordData());
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static DbConnection getDatabaseConnection() throws SQLException {
        if (dbc == null) {
            dbc = new DbConnection();
        } else if (dbc.getConnection().isClosed()) {
            dbc = new DbConnection();
        }
        return dbc;
    }

    public Connection getConnection() {
        return con;
    }

}
