package com.example.psmsystem.model.crime;

import java.util.List;

public interface ICrimeDao<T> {
    void addCrime(T t);
    List<T> getCrime();
    void updateCrime(T t, int id);
    void deleteCrime(int id);
    int getCrimeId(String crimeName);
    int getCountCrime();
}
