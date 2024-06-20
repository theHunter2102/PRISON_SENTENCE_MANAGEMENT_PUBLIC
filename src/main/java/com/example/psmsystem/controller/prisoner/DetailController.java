package com.example.psmsystem.controller.prisoner;

import com.example.psmsystem.controller.DataStorage;
import com.example.psmsystem.controller.MainPanelController;
import com.example.psmsystem.model.assess.Assess;
import com.example.psmsystem.model.health.Health;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.model.sentence.Sentence;
import com.example.psmsystem.service.assessDao.AssessDao;
import com.example.psmsystem.service.healthDao.HealthDao;
import com.example.psmsystem.service.prisonerDAO.PrisonerDAO;
import com.example.psmsystem.service.sentenceDao.SentenceDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetailController implements Initializable {

    @FXML
    private ImageView imgPrisonerAdd;

    @FXML
    private Label lbContactName;

    @FXML
    private Label lbContactPhone;

    @FXML
    private Label lbDOB;

    @FXML
    private Label lbFullName;

    @FXML
    private Label lbGender;

    @FXML
    private Label lbIdentityCard;

    @FXML
    private Label lbPrisonerId;


    @FXML
    private Label lbNumberOfSentence;

    @FXML
    private Label txtAssessCode;

    @FXML
    private Label txtCrimes;

    @FXML
    private Label txtCrimes2;

    @FXML
    private Label txtEndDate;

    @FXML
    private Label txtEventDate;

    @FXML
    private Label txtEventType;

    @FXML
    private Label txtHealthCode;

    @FXML
    private Label txtHealthy;

    @FXML
    private Label txtHeight;

    @FXML
    private Label txtLevelAssess;

    @FXML
    private Label txtLevelHealth;

    @FXML
    private Label txtNoteAssess;

    @FXML
    private Label txtSentenceType;

    @FXML
    private Label txtStartDate;

    @FXML
    private Label txtStatusServing;

    @FXML
    private Label txtUpdateDate;

    @FXML
    private Label txtWeight;


    private int prisonerId;
    private int sentenceCode;

    public void setPrisoner(int prisonerId) {
        this.prisonerId = prisonerId;
        getPrisonerInformation(prisonerId);
    }

    public  void getSentence()
    {
        try {
            SentenceDao sentenceDao = new SentenceDao();
            List<Sentence> sentenceList = sentenceDao.getSentence();
            int numberOfSentence = 0;

            String sentenceType = "";
            Date startDate = null;
            Date endDate = null;
            String crimeCode = "";
            int sentenceCodeGet = 0;
            for (Sentence sentence : sentenceList) {
                if (sentence.getPrisonerId() == this.prisonerId) {
                    numberOfSentence++;

                    sentenceCodeGet = sentence.getSentenceCode();
                    sentenceType = sentence.getSentenceType();
                    startDate = sentence.getStartDate();
                    endDate = sentence.getEndDate();
                    crimeCode = sentence.getCrimesCode();
                }
            }

            this.sentenceCode = sentenceCodeGet;
            txtStatusServing.setText("Are Serving");
            lbNumberOfSentence.setText(String.valueOf(numberOfSentence));
            txtSentenceType.setText(sentenceType);
            txtStartDate.setText(String.valueOf(startDate));
            txtEndDate.setText(String.valueOf(endDate));
            txtCrimes.setText(crimeCode);
            System.out.println("Prisoner id : " + this.prisonerId);
            System.out.println("number of sentence: " + numberOfSentence);
        }catch (Exception e){
            System.out.println("getSentence - DetailController :" + e.getMessage());
        }
    }


    public void getHealth()
    {
        HealthDao healthDao = new HealthDao();
        List<Health> healthList = healthDao.getHealth();

        try {
            String healthCode = "";
            String update = null;
            double weight = 0.0;
            double height = 0.0;
            boolean status = false;  // (false: khỏe, true: bệnh )
            int level = 0;


            for (Health health : healthList) {
                if (health.getSentenceCode() == this.sentenceCode) {

                    healthCode = health.getHealthCode();
                    update = health.getCheckupDate();
                    weight = health.getWeight();
                    height = health.getHeight();
                    status = health.getStatus();
                    level = health.getLevel();
                    break;
                }
            }

            if (healthCode != null)
            {
                txtHealthCode.setText(healthCode);
            }
            else
            {
                txtHealthCode.setText("null");
            }
            if (update != null)
            {
                txtUpdateDate.setText(update);
            }else
            {
                txtUpdateDate.setText("null");
            }
            if (weight != 0.0)
            {
                txtWeight.setText(String.valueOf(weight + " kg"));
            }else
            {
                txtWeight.setText("null");
            }
            if (height != 0.0)
            {
                txtHeight.setText(String.valueOf(height + " meter"));
            }
            else
            {
                txtHeight.setText("null");
            }

            if (!status) {
                txtHealthy.setText("Healthy");
            } else {
                txtHealthy.setText("Sick");
            }

//        ,-- 0: không bệnh, 1: nhẹ, 2: nặng, 3: cần can thiệp

            if (level == 0) {
                txtLevelHealth.setText("Strong");
            } else if (level == 1) {
                txtLevelHealth.setText("Mild");
            } else if (level == 2) {
                txtLevelHealth.setText("Severe");
            } else if (level == 3) {
                txtLevelHealth.setText("Requires intervention");
            } else {
                txtLevelHealth.setText("null");
            }
        }catch (Exception e) {
            System.out.println("getHealth - DetailController :  " + e.getMessage());
        }
    }
    public void getAssess()
    {
        AssessDao assessDao = new AssessDao();
        List<Assess> assessList = assessDao.getAssess();

        try {
            String processCode = "";
            String eventDate = "";
            String eventType = "";
            int level = 0;
            String note = "";

            for (Assess assess : assessList) {
                if (assess.getSentencesCode() == this.sentenceCode) {
                    processCode = assess.getProcessCode();
                    eventDate = assess.getDateOfOccurrence();
                    eventType = assess.getEventType();
                    level = assess.getLevel();
                    note = assess.getNote();
                }
            }

            if (processCode != null)
            {
                txtAssessCode.setText(processCode);
            }
            else
            {
                txtAssessCode.setText("null");
            }
            if (eventDate != null)
            {
                txtEventDate.setText(eventDate);
            }else
            {
                txtEventDate.setText("null");
            }
            if (eventType != null)
            {
                txtEventType.setText(eventType);
            }else
            {
                txtEventType.setText("null");
            }
            if (level == 1) {
                txtLevelAssess.setText("Mild");
            } else if (level == 2) {
                txtLevelAssess.setText("Moderate");
            } else if (level == 3) {
                txtLevelAssess.setText("Good");
            } else if (level == 4) {
                txtLevelAssess.setText("Very good");
            } else {
                txtLevelAssess.setText("null");
            }
            if (note != null)
            {
                txtNoteAssess.setText(note);
            }else
            {
                txtNoteAssess.setText("null");
            }
        }catch (Exception e)
        {
            System.out.println("Get Assess - DetailController : " + e.getMessage());
        }

    }
    public void getPrisonerInformation(int prisonerId) {
        try {
            String defaultPath = "/com/example/psmsystem/assets/imagesPrisoner/default.png";
            PrisonerDAO prisonerDAO = new PrisonerDAO();
            List<Prisoner> prisonerList = prisonerDAO.getAllPrisoner();
            String swPrisonerId = String.valueOf(prisonerId);
            Prisoner prisonerShow = new Prisoner();
            for (Prisoner prisoner : prisonerList) {
                if (prisoner.getPrisonerCode().equals(swPrisonerId))
                {
                    prisonerShow = prisoner;
                    break;
                }
            }
             String id = prisonerShow.getPrisonerCode();
             String prisonerName = prisonerShow.getPrisonerName();
             String DOB = prisonerShow.getDOB();
             int gender = prisonerShow.getGender();
             String identityCard = prisonerShow.getIdentityCard();
             String contactName = prisonerShow.getContactName();
             String contactPhone = prisonerShow.getContactPhone();
             String imagePath = prisonerShow.getImagePath();

            SimpleDateFormat formatDOBfromString = new SimpleDateFormat("yyyy-MM-dd");
            Date dateOfBirth = formatDOBfromString.parse(DOB);

            // Sử dụng SimpleDateFormat với định dạng "dd/MM/yyyy" để hiển thị DOB
            SimpleDateFormat formatDOB = new SimpleDateFormat("dd/MM/yyyy");
            String DOBShow = formatDOB.format(dateOfBirth);
            if (gender == 1) {
                lbGender.setText("Male");
            } else if (gender == 2) {
                lbGender.setText("FeMale");
            } else {
                lbGender.setText("Other");
            }
            File imageFile;
            if (imagePath != null && !imagePath.isEmpty()) {
                imageFile = new File(imagePath);
            } else {
                imageFile = new File(defaultPath);
            }
            Image image = new Image(imageFile.toURI().toString());

            imgPrisonerAdd.setImage(image);
//             lbPrisonerId.setText(id);
             lbFullName.setText(prisonerName);
             lbContactName.setText(contactName);
             lbContactPhone.setText(contactPhone);

             lbIdentityCard.setText(identityCard);
            lbDOB.setText(DOBShow);
            getSentence();
            getHealth();
            getAssess();
        }catch (Exception e){
            System.out.println("Error in getPrisonerInformation" + e.getMessage());
        }
    }

    public void moreInfo(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Coming soon!");
        alert.showAndWait();
    }
    public void back(ActionEvent event) {
        Stage stage = (Stage) imgPrisonerAdd.getScene().getWindow();
        stage.close();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
