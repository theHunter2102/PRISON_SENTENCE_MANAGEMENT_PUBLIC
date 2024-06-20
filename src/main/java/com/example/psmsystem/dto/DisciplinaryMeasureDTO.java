package com.example.psmsystem.dto;

import com.example.psmsystem.model.Commendation;
import com.example.psmsystem.model.DisciplinaryMeasure;

public class DisciplinaryMeasureDTO {
    private int sentenceCode;
    private String informationLevel;
    private DisciplinaryMeasure disciplinaryMeasure;

    public DisciplinaryMeasureDTO() {
    }

    public DisciplinaryMeasureDTO(int sentenceCode, String informationLevel, DisciplinaryMeasure disciplinaryMeasure) {
        this.sentenceCode = sentenceCode;
        this.informationLevel = informationLevel;
        this.disciplinaryMeasure = disciplinaryMeasure;
    }

    public int getSentenceCode() {
        return sentenceCode;
    }

    public void setSentenceCode(int sentenceCode) {
        this.sentenceCode = sentenceCode;
    }

    public String getInformationLevel() {
        return informationLevel;
    }

    public void setInformationLevel(int  level) {
        switch (level) {
            case 1 -> this.informationLevel = "MILD";
            case 2 -> this.informationLevel = "MODERATE";
            case 3 -> this.informationLevel = "GOOD";
            case 4 -> this.informationLevel = "VERY GOOD";
            default -> this.informationLevel = null;
        }
    }

    public DisciplinaryMeasure getDisciplinaryMeasure() {
        return disciplinaryMeasure;
    }

    public void setDisciplinaryMeasure(DisciplinaryMeasure disciplinaryMeasure) {
        this.disciplinaryMeasure = disciplinaryMeasure;
    }
}
