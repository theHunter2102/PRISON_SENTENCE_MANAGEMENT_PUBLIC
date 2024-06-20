package com.example.psmsystem.model.health;

public class Health {
    private int id;
    private String information;

    public String getInformation() {
        return information;
    }

    public void setInformation(int level) {
        switch (level) {
            case 0 -> this.information = "Strong";
            case 1 -> this.information = "Minor illness";
            case 2 -> this.information = "Severe illness";
            case 3 -> this.information = "disease requiring intervention";
            default -> this.information = null;
        }
    }

    public Health(int id, String healthCode, int prisonerId, String sentenceId, int sentenceCode, String prisonerName, Double weight, Double height, String checkupDate, Boolean status, int level) {
        this.id = id;
        this.healthCode = healthCode;
        this.prisonerId = prisonerId;
        this.sentenceId = sentenceId;
        this.sentenceCode = sentenceCode;
        this.prisonerName = prisonerName;
        this.weight = weight;
        this.height = height;
        this.checkupDate = checkupDate;
        this.status = status;
        this.level = level;
    }

    private String healthCode;
    private int prisonerId;
    private String sentenceId ;
    private int sentenceCode;
    private String prisonerName;
    private Double weight;
    private Double height;
    private String checkupDate;
    private Boolean status;
    private int level;

    public Health() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHealthCode() {
        return healthCode;
    }

    public void setHealthCode(String healthCode) {
        this.healthCode = healthCode;
    }

    public int getPrisonerId() {
        return prisonerId;
    }

    public void setPrisonerId(int prisonerId) {
        this.prisonerId = prisonerId;
    }

    public String getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(String sentenceId) {
        this.sentenceId = sentenceId;
    }

    public int getSentenceCode() {
        return sentenceCode;
    }

    public void setSentenceCode(int sentenceCode) {
        this.sentenceCode = sentenceCode;
    }

    public String getPrisonerName() {
        return prisonerName;
    }

    public void setPrisonerName(String prisonerName) {
        this.prisonerName = prisonerName;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getCheckupDate() {
        return checkupDate;
    }

    public void setCheckupDate(String checkupDate) {
        this.checkupDate = checkupDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
