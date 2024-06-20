package com.example.psmsystem.dto;

import com.example.psmsystem.model.Commendation;

public class CommendationDTO {
    private int sentenceCode;
    private String informationLevel;
    private Commendation commendation;

    public CommendationDTO() {
    }

    public CommendationDTO(int sentenceCode, String informationLevel, Commendation commendation) {
        this.sentenceCode = sentenceCode;
        this.informationLevel = informationLevel;
        this.commendation = commendation;
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

    public Commendation getCommendation() {
        return commendation;
    }

    public void setCommendation(Commendation commendation) {
        this.commendation = commendation;
    }
}
