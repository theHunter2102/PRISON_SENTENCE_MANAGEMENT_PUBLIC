package com.example.psmsystem.model.prisoner;

import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;

public interface IPrisonerDao<T> {
    List<T> getAllPrisoner();
    List<T> getItemComboboxPrisoner();
    List<Prisoner>getPrisonerInItem();

    ObservableList<T> getPrisonerName();

    int getCountPrisoner();
    Map<String, Integer> countGender();
    boolean updatePrisoner(T prisoner);
}
