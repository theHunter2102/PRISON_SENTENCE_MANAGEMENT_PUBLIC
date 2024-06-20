package com.example.psmsystem.model.managementvisit;

public class ManagementVisit {
    private String sentenceId;
    private int sentenceCode;
    private int prisonerId;
    private String prisonerName;
    private String visitorName;
    private String identityCard;
    private String relationship;
    private String visitDate;
    private String startTime;
    private String endTime;
    private String notes;

    public ManagementVisit() {}

    public ManagementVisit(String sentenceId,int sentenceCode,int prisonerId, String prisonerName, String visitorName, String identityCard, String relationship, String visitDate, String startTime, String endTime, String notes) {
        this.sentenceId = sentenceId;
        this.sentenceCode = sentenceCode;
        this.prisonerId = prisonerId;
        this.prisonerName = prisonerName;
        this.visitorName = visitorName;
        this.identityCard = identityCard;
        this.relationship = relationship;
        this.visitDate = visitDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
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

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
