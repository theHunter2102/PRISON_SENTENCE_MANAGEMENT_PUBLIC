package com.example.psmsystem.model.prisoner;

public class Prisoner {
    private String prisonerCode;
    private String prisonerName;
    private String DOB;
    private int Gender;
    private String identityCard;
    private String contactName;
    private String contactPhone;
    private String imagePath;
    private boolean status;
    private int user_id;


    public Prisoner(){}

    public Prisoner(String prisonerCode, String prisonerName, String DOB, int gender, String identityCard, String contactName, String contactPhone, String imagePath, boolean status, int user_id) {
        this.prisonerCode = prisonerCode;
        this.prisonerName = prisonerName;
        this.DOB = DOB;
        Gender = gender;
        this.identityCard = identityCard;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.imagePath = imagePath;
        this.status = status;
        this.user_id = user_id;
    }

    public String getPrisonerCode() {
        return prisonerCode;
    }

    public void setPrisonerCode(String prisonerCode) {
        this.prisonerCode = prisonerCode;
    }

    public String getPrisonerName() {
        return prisonerName;
    }

    public void setPrisonerName(String prisonerName) {
        this.prisonerName = prisonerName;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public int getGender() {
        return Gender;
    }

    public void setGender(int gender) {
        Gender = gender;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
