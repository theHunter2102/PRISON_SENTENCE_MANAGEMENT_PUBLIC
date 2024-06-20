package com.example.psmsystem.model.sentence;

import com.example.psmsystem.dto.SentenceDTO;
import javafx.collections.ObservableList;

import java.util.List;

public interface SentenceServiceImpl<T> {
    List<T> getSentence();
    ObservableList<T> getPrisonerName();
}
