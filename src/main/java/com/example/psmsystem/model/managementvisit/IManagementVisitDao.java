package com.example.psmsystem.model.managementvisit;

import java.util.List;
import java.util.Map;

public interface IManagementVisitDao<T> {
    void addManagementVisit(T t);
    List<T> getManagementVisits();
    void updateManagementVisit(T t, int id);
    void deleteManagementVisit(int id);
    int getVisitationId(int prisonerCode, String visitDate);
    int getCountManagementVisit();
    Map<String, Integer> countVisitsByMonth();
}
