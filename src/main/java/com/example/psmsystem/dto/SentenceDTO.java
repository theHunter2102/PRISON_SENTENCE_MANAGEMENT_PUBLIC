package com.example.psmsystem.dto;

import com.example.psmsystem.model.sentence.Sentence;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SentenceDTO {
    private StringProperty prisonerName = new SimpleStringProperty();
    private String identityCard;
    private int prisonerId;
    private Sentence sentence;
    public SentenceDTO() {
    }


    public SentenceDTO(String prisonerName,int prisonerId, String identityCard, Sentence sentence) {
        this.prisonerName.set(prisonerName);
        this.prisonerId = prisonerId;
        this.identityCard = identityCard;
        this.sentence = sentence;
    }

    public StringProperty prisonerNameProperty() {
        return prisonerName;
    }

    public String getPrisonerName() {
        return prisonerName.get();
    }

    public void setPrisonerName(String prisonerName) {
        this.prisonerName.set(prisonerName);
    }

    public Sentence getSentence() {
        return sentence;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public int getPrisonerId() {
        return prisonerId;
    }

    public void setPrisonerId(int prisonerId) {
        this.prisonerId = prisonerId;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }
    public List<String> getPropertyNames() {
        List<String> propertyNames = new ArrayList<>();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(Sentence.class)) {
                // Nếu trường là một đối tượng của lớp Sentence, thêm các thuộc tính của nó vào danh sách
                Field[] sentenceFields = field.getType().getDeclaredFields();
                for (Field sentenceField : sentenceFields) {
                    propertyNames.add(field.getName() + "." + sentenceField.getName());
                }
            } else {
                // Nếu không phải là một đối tượng của lớp Sentence, chỉ thêm tên của trường vào danh sách
                propertyNames.add(field.getName());
            }
        }
        return propertyNames;
    }


    public String getPropertyValue(String propertyName) {
        try {
            Field field = getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            Object value = field.get(this);
            return value != null ? value.toString() : "";
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return "";
        }
    }
}
