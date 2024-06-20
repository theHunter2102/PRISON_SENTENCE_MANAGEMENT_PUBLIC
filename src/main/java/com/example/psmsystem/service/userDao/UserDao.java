package com.example.psmsystem.service.userDao;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.database.DbConnection;
import com.example.psmsystem.model.Role;
import com.example.psmsystem.model.user.IUserDao;
import com.example.psmsystem.model.user.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDao implements IUserDao<User> {
    Logger LOGGER = Logger.getLogger(UserDao.class.getName());
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/prisonerms";
//    private static final String DB_USER = "root";
////    private static final String DB_PASSWORD = "12345678";
//    private static final String DB_PASSWORD = "";
    private static final String INSERT_QUERY = "INSERT INTO users (full_name, username, password) VALUES (?, ?, ?)";
    private static final String SELECT_BY_USERNAME_PASSWORD_QUERY = "SELECT * FROM users WHERE username = ? and password = ?";
    private static final String SELECT_BY_USERNAME_QUERY = "SELECT * FROM users WHERE username = ?";
    private static final String SELECT_BY_USER_QUERY = "SELECT * FROM users";

    @Override
    public void addUser(User user,List<String> roleNames) {
        String hashPassword = hashPassword(user.getPassword());
        try (Connection conn = DbConnection.getDatabaseConnection().getConnection()){
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY)){

                System.out.println(hashPassword);
                stmt.setString(1, user.getFullName());
                stmt.setString(2, user.getUsername());
                stmt.setString(3, hashPassword);
                stmt.executeUpdate();
            }
            //get user_id
            int id = 0;
            try (PreparedStatement getUserPs = conn.prepareStatement("SELECT user_id FROM users WHERE username = ? AND password = ?")){
                getUserPs.setString(1, user.getUsername());
                getUserPs.setString(2, hashPassword);
                ResultSet getUserRS = getUserPs.executeQuery();
                if(!getUserRS.next()) throw new RuntimeException("add Role failed!");
                id = getUserRS.getInt("user_id");
            }
            if(id < 1) throw new RuntimeException("User not found.");
            //get Roles
            List<Role> rolesData = new ArrayList<>();
            try (PreparedStatement getRoleIdPs = conn.prepareStatement("SELECT * FROM roles")){
                ResultSet getRoleRs = getRoleIdPs.executeQuery();
                while (getRoleRs.next()) {
                    Role r = new Role();
                    r.setId(getRoleRs.getInt("role_id"));
                    r.setName(getRoleRs.getString("name"));
                    rolesData.add(r);
                }
            }
            //add role
            String sql = "INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (?,?)";
            for (Role r : rolesData) {
                if(roleNames.contains(r.getName())) {
                    try (PreparedStatement addRolePs = conn.prepareStatement(sql)){
                        addRolePs.setInt(1,id);
                        addRolePs.setInt(2,r.getId());
                        addRolePs.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Register failed: ",e);
            throw new RuntimeException("Register failed!");

        }
    }

    @Override
    public User checkLogin(String username, String password) {
        User user = new User();
        try ( Connection conn = DbConnection.getDatabaseConnection().getConnection()){
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USERNAME_PASSWORD_QUERY)){
                String hashPassword = hashPassword(password);

                stmt.setString(1, username);
                stmt.setString(2, hashPassword);
                ResultSet rs = stmt.executeQuery();
                if(!rs.next())  throw  new RuntimeException("Invalid account");
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
            }
            //get roles
            List<ApplicationState.RoleName> roles = new ArrayList<>();
            try (PreparedStatement getRolesPs = conn.prepareStatement("SELECT r.name FROM roles r " +
                    "JOIN user_role ur ON ur.role_id = r.role_id " +
                    "JOIN users u ON u.user_id = ur.user_id " +
                    "WHERE u.user_id = ?")){
                getRolesPs.setInt(1,user.getUserId());
                ResultSet getRolesRs = getRolesPs.executeQuery();
                while (getRolesRs.next()) {
                    roles.add(ApplicationState.RoleName.valueOf(getRolesRs.getString("name")));
                }
                ApplicationState.getInstance().setRoleName(roles);
                for (ApplicationState.RoleName r: ApplicationState.getInstance().getRoleName()) {
                    System.out.println(r);
                }
            }
            ApplicationState.getInstance().setUsername(username);
            ApplicationState.getInstance().setId(user.getUserId());
            return user;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"login failed: ",e);
            throw new RuntimeException("Login failed!");
        }

    }

    @Override
    public boolean checkUsername(String username) {
        try{
            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USERNAME_QUERY);
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,"check failed: ",ex);
            System.out.println(username);
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        try {
            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement(SELECT_BY_USER_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setFullName(rs.getString("name"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("hash_password"));
                userList.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userList;
    }
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString(); // Trả về mật khẩu đã được mã hóa
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return password; // Trả về mật khẩu gốc trong trường hợp xảy ra lỗi
    }


}
