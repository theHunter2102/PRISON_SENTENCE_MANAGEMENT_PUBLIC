package com.example.psmsystem.model.userlog;

import java.util.List;

public interface IUserLogDao<T> {
    void insertUserLog(T t);
    List<T> selectAllUserLogs();
    void deleteUserLogUpdateDate(T t);
}
