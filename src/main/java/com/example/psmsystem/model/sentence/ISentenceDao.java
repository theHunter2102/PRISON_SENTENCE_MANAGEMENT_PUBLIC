package com.example.psmsystem.model.sentence;

import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;

public interface ISentenceDao<T> {
    boolean addSentence(T t);
    List<T> getSentence();

    int getMaxIdSentence();

    void updateSentence(T t, int id);
    void deleteSentence(int id);
    int getSentenceId(int prisonerCode);
    Map<String, Integer> countPrisonersBySentenceType();
    ObservableList<T> getPrisonerName();
}
