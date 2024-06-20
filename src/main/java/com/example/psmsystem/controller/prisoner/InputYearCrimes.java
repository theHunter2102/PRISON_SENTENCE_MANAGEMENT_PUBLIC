package com.example.psmsystem.controller.prisoner;

import com.example.psmsystem.controller.DataStorage;
import com.example.psmsystem.model.crime.Crime;
import com.example.psmsystem.service.crimeDao.CrimeDao;
import com.example.psmsystem.service.sentenceDao.SentenceDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class InputYearCrimes implements Initializable {

    @FXML
    private VBox vbYOC;
    @FXML
    private VBox vbNameOC;
    @FXML
    private VBox vbNameCrime;

    @FXML
    private HBox hbYear;

    private Map<Integer, TextField> textFieldMap = new HashMap<>(); // Lưu trữ ID tội phạm và TextField tương ứng
    private Map<Integer, Integer> monthCrimeMap = new HashMap<>(); // Lưu trữ ID tội phạm và số tháng
    private List<Integer> idList;
    private int sentenceId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (this.idList != null) {
            getYearOfCrimes(this.idList);
        }
    }

    public void getIdCrimes(List<Integer> idList, int sentenceId) {
        this.idList = idList;
        this.sentenceId = sentenceId;
        // Gọi phương thức này để cập nhật giao diện nếu cần
        if (idList != null) {
            getYearOfCrimes(idList);
            System.out.println("id list" + this.idList);
            System.out.println("sentenceID YOC : " + this.sentenceId);
        } else {
            System.out.println("id list is null ");
        }
    }

//    public void acceptMonthOfCrime(ActionEvent event) {
//        try {
//            SentenceDao sentenceDao = new SentenceDao();
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            VBox vBox = new VBox(10);
//            int totalMonth = 0;
//            int checkEmpty = 0;
////            int year = 0;
//            for (Map.Entry<Integer, TextField> entry : textFieldMap.entrySet()) {
//                int crimeId = entry.getKey(); // Lấy ID tội phạm từ Map
//                TextField textField = entry.getValue(); // Lấy TextField tương ứng
//                Label labelName = new Label();
//                String name = textField.getPromptText();
//
//                if (!textField.getText().isBlank() && !textField.getText().isEmpty()) {
//                    int year = Integer.parseInt(textField.getText());
//                    totalMonth += year;
//                    monthCrimeMap.put(crimeId, year);
//                    labelName.setText(name + ": " + year + " tháng");
//                    vBox.getChildren().addAll(labelName);
//                    checkEmpty++;
//                }
//            }
//
//
//            if (checkEmpty != textFieldMap.size() || totalMonth == 0 ) {
//                alert.setTitle("Error");
//                alert.setHeaderText(null);
//                alert.setContentText("Enter all fields correctly");
//                return;
//            }
//            System.out.println("Total month: " + totalMonth);
//            DataStorage.setCrimesTime(monthCrimeMap);
//            alert.getDialogPane().setContent(vBox);
//            alert.setOnCloseRequest(event1 -> {
//                back();
//            });
//            alert.showAndWait();
//        }catch (Exception e) {
//            System.out.println("InputYearCrime - AcceptMonthOfCrime  : " + e.getMessage());
//        }
//    }
public void acceptMonthOfCrime(ActionEvent event) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    try {
        SentenceDao sentenceDao = new SentenceDao();
        VBox vBox = new VBox(10);
        int totalMonth = 0;
        boolean anyEmpty = false;
        for (Map.Entry<Integer, TextField> entry : textFieldMap.entrySet()) {
            int crimeId = entry.getKey();
            TextField textField = entry.getValue();
            String name = textField.getPromptText();
            int year = Integer.parseInt(textField.getText());
            if (year < 1) {
//                showAlert("Please enter a value for " + name);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter positive integer.");
                alert.showAndWait();
                return;
            }

            totalMonth += year;
            monthCrimeMap.put(crimeId, year);
            Label labelName = new Label(name + ": " + year + " tháng");
            vBox.getChildren().addAll(labelName);
        }
        if (totalMonth <= 3)
        {
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Total month must be greater than 3");
            alert.showAndWait();
            return;
        }

        System.out.println("Total month: " + totalMonth);
        DataStorage.setCrimesTime(monthCrimeMap);
        alert.getDialogPane().setContent(vBox);
        alert.setOnCloseRequest(event1 -> {
            back();
        });
        alert.showAndWait();
    } catch (Exception e) {
        System.out.println("InputYearCrime - AcceptMonthOfCrime  : " + e.getMessage());
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Enter all fields correctly and positive integer.");
        alert.showAndWait();
    }
}

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


//
//    public Map<Integer, Integer> getMonthOfCrimes() {
//        return this.monthCrimeMap;
//    }

    public void getYearOfCrimes(List<Integer> selectedCrimeIdList) {
        try {
            if (selectedCrimeIdList != null) {
                CrimeDao crimeDao = new CrimeDao();
                List<Crime> crimeList = crimeDao.getCrime();
                for (Crime crime : crimeList) {
                    for (int id : selectedCrimeIdList) {
                        if (id == crime.getCrimeId()) {
                            HBox hbYear = new HBox();
                            HBox hbName = new HBox();
                            TextField add = new TextField();
                            Label label = new Label();
                            String name = crime.getCrimeName();
                            label.setText(name);
                            add.setPromptText(name);
                            label.setPrefHeight(25);
                            label.setPrefWidth(120);
                            add.setPrefHeight(25);
                            add.setPrefWidth(120);
                            label.setMinHeight(25);
                            label.setMinWidth(120);
                            add.setMinHeight(25);
                            add.setMinWidth(120);
                            hbName.getChildren().add(label);
                            hbYear.getChildren().add(add);
                            vbNameCrime.getChildren().add(hbName);
                            vbYOC.getChildren().addAll(hbYear);
                            textFieldMap.put(id, add); // Lưu trữ ID tội phạm và TextField
                        }
                    }
                }
            } else {
                System.out.println("selectedCrimeIdList is null. Make sure it is initialized before calling getYearOfCrimes().");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void back() {
        Stage currentStage = (Stage) vbYOC.getScene().getWindow();
        currentStage.close();
    }
}

