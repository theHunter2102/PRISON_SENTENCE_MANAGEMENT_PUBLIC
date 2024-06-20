package com.example.psmsystem.model.report;

import java.util.Date;

public class Report {
    private String sentenceId;
    private String sentenceCode;
    private String prisonerId;
    private String prisonerName;
    private boolean status;
    private int level;
    private int totalReward;
    private int totalDiscipline;
    private Date releaseDate;
    private Date endDate; // Thêm trường endDate
    private String paroleEligibility;

    public Report() {}

    public Report(String sentenceId, String sentenceCode, String prisonerId, String prisonerName, boolean status, int level, int totalReward, int totalDiscipline, Date releaseDate, Date endDate, String paroleEligibility) {
        this.sentenceId = sentenceId;
        this.sentenceCode = sentenceCode;
        this.prisonerId = prisonerId;
        this.prisonerName = prisonerName;
        this.status = status;
        this.level = level;
        this.totalReward = totalReward;
        this.totalDiscipline = totalDiscipline;
        this.releaseDate = releaseDate;
        this.endDate = endDate; // Khởi tạo trường endDate
        this.paroleEligibility = paroleEligibility;
    }

    // Getters and setters
    public String getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(String sentenceId) {
        this.sentenceId = sentenceId;
    }

    public String getSentenceCode() {
        return sentenceCode;
    }

    public void setSentenceCode(String sentenceCode) {
        this.sentenceCode = sentenceCode;
    }

    public String getPrisonerId() {
        return prisonerId;
    }

    public void setPrisonerId(String prisonerId) {
        this.prisonerId = prisonerId;
    }

    public String getPrisonerName() {
        return prisonerName;
    }

    public void setPrisonerName(String prisonerName) {
        this.prisonerName = prisonerName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(int totalReward) {
        this.totalReward = totalReward;
    }

    public int getTotalDiscipline() {
        return totalDiscipline;
    }

    public void setTotalDiscipline(int totalDiscipline) {
        this.totalDiscipline = totalDiscipline;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getParoleEligibility() {
        return paroleEligibility;
    }

    public void setParoleEligibility(String paroleEligibility) {
        this.paroleEligibility = paroleEligibility;
    }
}
