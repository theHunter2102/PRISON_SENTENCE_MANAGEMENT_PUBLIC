package com.example.psmsystem.model.assess;

public class Assess {
    private String processCode;
    private String sentencesId;
    private int sentencesCode;
    private int prisonerId;
    private String prisonerName;
    private String dateOfOccurrence;
    private String eventType;
    private int level;
    private String note;

    public Assess() {
    }

    public Assess(String processCode, String sentencesId, int sentencesCode, int prisonerId, String prisonerName, String dateOfOccurrence, String eventType, int level, String note) {
        this.processCode = processCode;
        this.sentencesId = sentencesId;
        this.sentencesCode = sentencesCode;
        this.prisonerId = prisonerId;
        this.prisonerName = prisonerName;
        this.dateOfOccurrence = dateOfOccurrence;
        this.eventType = eventType;
        this.level = level;
        this.note = note;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getSentencesId() {
        return sentencesId;
    }

    public void setSentencesId(String sentencesId) {
        this.sentencesId = sentencesId;
    }

    public int getSentencesCode() {
        return sentencesCode;
    }

    public void setSentencesCode(int sentencesCode) {
        this.sentencesCode = sentencesCode;
    }

    public int getPrisonerId() {
        return prisonerId;
    }

    public void setPrisonerId(int prisonerId) {
        this.prisonerId = prisonerId;
    }

    public String getPrisonerName() {
        return prisonerName;
    }

    public void setPrisonerName(String prisonerName) {
        this.prisonerName = prisonerName;
    }

    public String getDateOfOccurrence() {
        return dateOfOccurrence;
    }

    public void setDateOfOccurrence(String dateOfOccurrence) {
        this.dateOfOccurrence = dateOfOccurrence;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
