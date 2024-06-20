package com.example.psmsystem.model.assess;

import java.util.List;

public interface IAssessDao<T> {
    void addAssess(T t);
    List<T> getAssess();
    void updateAssess(T t, int id);
    void deleteAssess(int id);
    int getAssessId(String prisonerCode, String eventDate);
    int getCountAssess();
}
