package com.example.psmsystem.model.health;

import java.util.List;

public interface IHealthDao<T> {
    void addHealth(T t);
    List<T> getHealth();
    void updateHealth(T t, int id);
    void deleteHealth(int id);
    int getVisitationId(String prisonerCode, String checkupDate);
    int getCountHealth();
}
