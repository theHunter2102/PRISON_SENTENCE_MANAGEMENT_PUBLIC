package com.example.psmsystem.model.user;

import java.util.List;

public interface IUserDao<T> {
    void addUser(T t,List<String> roles);
    T checkLogin(String username, String password);
    boolean checkUsername(String username);
    List<T> getAllUsers();
}
