package com.example.psmsystem.dto;

public class Consider {
    private String prisonerName;
    private String identityCard;
    private String health;
    private int sentenceCode;
    private int sentenceId;
    private String image;
    private int commendationSum;
    private int disciplinaryMeasureSum;

    public Consider(String prisonerName, String identityCard, int healthLevel, int sentenceCode, int sentenceId, String image, int commendationSum, int disciplinaryMeasureSum) {
        this.prisonerName = prisonerName;
        this.identityCard = identityCard;
        switch (healthLevel) {
            case 0 -> this.health = "Strong";
            case 1 -> this.health = "Minor illness";
            case 2 -> this.health = "Severe illness";
            case 3 -> this.health = "disease requiring intervention";
            default -> this.health = null;
        }
        this.sentenceCode = sentenceCode;
        this.sentenceId = sentenceId;
        this.image = image;
        this.commendationSum = commendationSum;
        this.disciplinaryMeasureSum = disciplinaryMeasureSum;
    }

    public Consider() {
    }

    public String getPrisonerName() {
        return prisonerName;
    }

    public void setPrisonerName(String prisonerName) {
        this.prisonerName = prisonerName;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(int  level) {
        switch (level) {
            case 0 -> this.health = "Strong";
            case 1 -> this.health = "Minor illness";
            case 2 -> this.health = "Severe illness";
            case 3 -> this.health = "disease requiring intervention";
            default -> this.health = null;
        }
    }

    public int getSentenceCode() {
        return sentenceCode;
    }

    public void setSentenceCode(int sentenceCode) {
        this.sentenceCode = sentenceCode;
    }

    public int getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(int sentenceId) {
        this.sentenceId = sentenceId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCommendationSum() {
        return commendationSum;
    }

    public void setCommendationSum(int commendationSum) {
        this.commendationSum = commendationSum;
    }

    public int getDisciplinaryMeasureSum() {
        return disciplinaryMeasureSum;
    }

    public void setDisciplinaryMeasureSum(int disciplinaryMeasureSum) {
        this.disciplinaryMeasureSum = disciplinaryMeasureSum;
    }
}
