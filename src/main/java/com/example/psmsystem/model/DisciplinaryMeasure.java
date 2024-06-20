package com.example.psmsystem.model;


import java.util.Date;

public class DisciplinaryMeasure {
    private  int id;
    private int sentenceId;
    private Date dateOfOccurrence;
    private int level;
    private String note;

    public DisciplinaryMeasure() {
    }

    public DisciplinaryMeasure(int id, int sentence_id, Date dateOfOccurrence, int level, String note) {
        this.id = id;
        this.sentenceId = sentence_id;
        this.dateOfOccurrence = dateOfOccurrence;
        this.level = level;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSentence_id() {
        return sentenceId;
    }

    public void setSentenceId(int sentence_id) {
        this.sentenceId = sentence_id;
    }

    public Date getDateOfOccurrence() {
        return dateOfOccurrence;
    }

    public void setDateOfOccurrence(Date dateOfOccurrence) {

        this.dateOfOccurrence = dateOfOccurrence;
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
