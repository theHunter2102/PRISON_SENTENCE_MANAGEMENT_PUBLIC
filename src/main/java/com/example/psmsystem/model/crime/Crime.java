package com.example.psmsystem.model.crime;

public class Crime {

    private String sentenceCode;
    private int crimeId;
    private String crimeName;

    public Crime(){}


    public Crime(String sentenceCode) {
        this.sentenceCode = sentenceCode;
    }

    public Crime(int crimeId, String crimeName) {
        this.crimeId = crimeId;
        this.crimeName = crimeName;
    }

    public int getCrimeId() {
        return crimeId;
    }

    public void setCrimeId(int crimeId) {
        this.crimeId = crimeId;
    }

    public String getCrimeName() {
        return crimeName;
    }

    public void setCrimeName(String crimeName) {
        this.crimeName = crimeName;
    }


    public String getSentenceCode() {
        return sentenceCode;
    }

    public void setSentenceCode(String sentenceCode) {
        this.sentenceCode = sentenceCode;
    }
}
