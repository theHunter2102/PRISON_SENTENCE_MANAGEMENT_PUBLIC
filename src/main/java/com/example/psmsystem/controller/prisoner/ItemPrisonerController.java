package com.example.psmsystem.controller.prisoner;


import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.model.sentence.Sentence;
import com.example.psmsystem.model.userlog.IUserLogDao;
import com.example.psmsystem.model.userlog.UserLog;
import com.example.psmsystem.service.prisonerDAO.PrisonerDAO;
import com.example.psmsystem.service.sentenceDao.SentenceDao;
import com.example.psmsystem.service.userLogDao.UserLogDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ItemPrisonerController implements Initializable {
    private IUserLogDao userlogDao = new UserLogDao();
    @FXML
    private Label lblYearSentence;

    @FXML
    private Label lblYearSentence1;

    @FXML
    private AnchorPane ItemPrisoner;
    @FXML
    private Button btnDelete;

    @FXML
    private Button btnDetail;

    @FXML
    private Button btnEdit;

    @FXML
    private ImageView imagePrisoner;
    @FXML
    private Label prisonerId;

    @FXML
    private Label prisonerName;

    private final String fxmlPath = "/com/example/psmsystem/";
    private Prisoner prisonerShowEdit;
    private PrisonerController prisonerController;
    private int prisonerIdDetail;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void btnEditOnAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath + "view/prisoner/EditPrisonerView.fxml"));
            AnchorPane newWindowContent = loader.load();

            EditPrisonerController editPrisonerController = loader.getController();
            editPrisonerController.setPrisonerEdit(prisonerShowEdit); // Truyền thông tin về tù nhân cần chỉnh sửa
            Stage newStage = new Stage();
            Scene scene = new Scene(newWindowContent);
            newStage.setScene(scene);
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle("Edit Prisoner");
            newStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void loadEditPrisonerView(ActionEvent event) {
        btnEditOnAction();
    }

    public void setPrisonerItem(Prisoner prisoner) {
        prisonerShowEdit = prisoner;
        SentenceDao sentenceDao = new SentenceDao();
        int prisonerIdDb = Integer.parseInt(prisoner.getPrisonerCode());

        List<Sentence> sentenceList = sentenceDao.getSentence();
        try
        {
            for (Sentence sentence : sentenceList) {
                if (sentence.getPrisonerId() == prisonerIdDb) {
                    String sentenceType = sentence.getSentenceType();
                    if (sentenceType.equals("limited time")) {
                        String start = String.valueOf(sentence.getStartDate());
                        String end = String.valueOf(sentence.getEndDate());
                        int years = calYearSentence(start, end);
                        lblYearSentence.setText("Time: " + years + " year");
                        break;
                    }else if (sentenceType.equals("life imprisonment")) {
                        lblYearSentence.setVisible(false);
                        lblYearSentence1.setVisible(true);
                    }
                }
            }
        }catch (Exception e)
        {
            System.out.println("year od sentence: " + e.getMessage());
        }

        String defaultPath = "/com/example/psmsystem/assets/imagesPrisoner/default.png";
        String id = prisoner.getPrisonerCode();
        String name = prisoner.getPrisonerName();
        String imagePath = prisoner.getImagePath();
        int idShow = Integer.parseInt(id);
        this.prisonerIdDetail = idShow;
        try {
            File imageFile;
            if (imagePath != null && !imagePath.isEmpty()) {
                imageFile = new File(imagePath);
            } else {
                imageFile = new File(defaultPath);
            }

            Image image = new Image(imageFile.toURI().toString());
            prisonerId.setText(formatPrisonerId(idShow));
            prisonerName.setText(name);
            imagePrisoner.setImage(image);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String formatPrisonerId(int idShow) {
        if (idShow < 10) {
            return "0000" + idShow;
        } else if (idShow < 100) {
            return "000" + idShow;
        } else if (idShow < 1000) {
            return "00" + idShow;
        } else if (idShow < 10000) {
            return "0" + idShow;
        } else {
            return String.valueOf(idShow);
        }
    }

    public static int calYearSentence(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, dateFormatter);
        LocalDate end = LocalDate.parse(endDate, dateFormatter);
        return Period.between(start, end).getYears();
    }
    public void setPrisonerController(PrisonerController prisonerController) {
        this.prisonerController = prisonerController;
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        if (prisonerShowEdit != null) {
            String prisonerCode = prisonerShowEdit.getPrisonerCode();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete prisoner with code: " + prisonerCode + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    PrisonerDAO prisonerDAO = new PrisonerDAO();
                    try {
                        prisonerDAO.deletePrisoner(prisonerCode);
                        // Hiển thị thông báo thành công
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Deletion Successful");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Prisoner with code: " + prisonerCode + " has been deleted.");
                        successAlert.showAndWait();

                        ApplicationState appState = ApplicationState.getInstance();
                        UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Deleted Prisoner code " + prisonerCode);
                        userlogDao.insertUserLog(userLog);

                        List<Prisoner> prisonerList = prisonerDAO.getPrisonerInItem();
                        prisonerController.refreshPrisonerList(prisonerList);
                    } catch (RuntimeException e) {
                        // Hiển thị thông báo lỗi nếu không thể xóa tù nhân
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Deletion Failed");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText(e.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });
        } else {
            // Hiển thị thông báo lỗi nếu không có tù nhân được chọn
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Prisoner Selected");
            alert.setHeaderText(null);
            alert.setContentText("No prisoner selected for deletion.");
            alert.showAndWait();
        }
    }

    public void openDetailWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath + "view/prisoner/DetailView.fxml"));
            AnchorPane newWindowContent = loader.load();

            DetailController detailController = loader.getController();
            detailController.setPrisoner(this.prisonerIdDetail);

            Stage newStage = new Stage();
            Scene scene = new Scene(newWindowContent);
            newStage.setScene(scene);
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle("New Window");

            newStage.show();
        } catch (IOException e) {
            System.out.println( "PrisonerController - openDetailWindow: " + e.getMessage());
        }
    }

    @FXML
    void loadDetailView(ActionEvent event) {
        openDetailWindow();
    }


}
