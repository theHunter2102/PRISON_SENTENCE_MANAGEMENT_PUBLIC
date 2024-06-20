package com.example.psmsystem.controller.report;


import com.example.psmsystem.config.AppConfig;

import com.example.psmsystem.ApplicationState;

import com.example.psmsystem.dto.Consider;
import com.example.psmsystem.helper.AlertHelper;

import com.example.psmsystem.model.userlog.IUserLogDao;
import com.example.psmsystem.model.userlog.UserLog;

import com.example.psmsystem.service.sentenceDao.SentenceService;
import com.example.psmsystem.service.userLogDao.UserLogDao;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.io.File;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ReportController implements Initializable {
    private UserLogDao reportDao = new UserLogDao();
    private IUserLogDao userlogDao = new UserLogDao();
    @FXML
    private TableView<Consider> dataTable;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private Label selectedKeyLabel;

    @FXML
    private TableColumn<Consider, String> imageColumn;
    @FXML
    private TableColumn<Consider, String> prisonerNameColumn;
    @FXML
    private TableColumn<Consider, String> identityCardColumn;
    @FXML
    private TableColumn<Consider, Integer> sentenceCodeColumn;
    @FXML
    private TableColumn<Consider, Integer> sentenceIdColumn;
    @FXML
    private TableColumn<Consider, String> healthColumn;
    @FXML
    private TableColumn<Consider, Integer> commendationSumColumn;
    @FXML
    private TableColumn<Consider, Integer> disciplinarySumColumn;

    @FXML
    private Pagination pagination;

    @FXML
    private Label txtUpdate;

    @FXML
    private TextField txtSearch;

    Window window;

    private final int itemsPerPage = 10;

    private SentenceService sentenceService = new SentenceService();
    private HashMap<String, List<Consider>> result;
    ObservableList<Consider> listTable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        dataTable.setFixedCellSize(37);
        ApplicationState appState = ApplicationState.getInstance();
        txtUpdate.setText("Data updated on: " + reportDao.timeUpdate(appState.getId()));

        result = sentenceService.classify();

        ObservableList<String> keyList = FXCollections.observableArrayList();
        for (Map.Entry<String, List<Consider>> entry : result.entrySet()) {
            String key = entry.getKey();
            int size = entry.getValue().size();
            String label = key + ": " + size;
            keyList.add(label);
        }
        choiceBox.setItems(keyList);

        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedKey = newValue.split(":")[0].trim();
                updateTableView(selectedKey);
            }
        });

        setUpTableView();
    }

    private void setUpTableView() {
        imageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImage()));
        prisonerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrisonerName()));
        identityCardColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdentityCard()));
        sentenceCodeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSentenceCode()).asObject());
//        sentenceIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSentenceId()).asObject());
        healthColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHealth()));
        commendationSumColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCommendationSum()).asObject());
        disciplinarySumColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDisciplinaryMeasureSum()).asObject());

        imageColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
//                        Image image = new Image(item);

                        File imageFile;
                        imageFile = new File(item);

                        Image image = new Image(imageFile.toURI().toString());
                        System.out.println("image : " + image);
//                        image.errorProperty().addListener((obs, oldError, newError) -> {
//                            if (newError) {
//                                imageView.setImage(new Image(AppConfig.getInstance().getImagePathDefault()));
//                            }
//                        });

                        imageView.setImage(image);
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(100);
                        setGraphic(imageView);
                    } catch ( Exception e) {
                        // Nếu URL không hợp lệ, sử dụng hình ảnh mặc định
                        System.out.println("Item : " +item);
                        imageView.setImage(new Image(AppConfig.getInstance().getImagePathDefault()));
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(100);
                        setGraphic(imageView);
                    }
                }
            }
        });
    }


    @FXML
    private void handleChoiceBoxAction() {
        String selectedKey = choiceBox.getSelectionModel().getSelectedItem().split(":")[0].trim();
        if (selectedKey != null) {
            updateTableView(selectedKey);
            setupSearch();
        }
    }

    private void updateTableView(String key) {
//        selectedKeyLabel.setText("Selected Key: " + key);
//        dataTable.getItems().clear();
        listTable.clear();

        List<Consider> considers = result.get(key);
        if (considers != null && !considers.isEmpty()) {
//            dataTable.getItems().addAll(considers);
            listTable.addAll(considers);

        }
        setupPagination();
    }

    @FXML
    void onUpdate(ActionEvent event) {
//        List<Report> reports = reportDao.getAll();
        Date currentDate = new Date();

        // Định dạng ngày theo chuẩn "YYYY-MM-DD hh:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(currentDate);

        txtUpdate.setText("Data updated on: " + formattedDate);
//        reportDao.updateUpdateLog(currentDate);

        ApplicationState appState = ApplicationState.getInstance();
        UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Updated the latest data: " + formattedDate);
        userlogDao.deleteUserLogUpdateDate(userLog);
        userlogDao.insertUserLog(userLog);
    }

    @FXML
    private void onExport(ActionEvent event) {
        //check status role
        boolean isRole = false;
        //check list role
        for (ApplicationState.RoleName r : ApplicationState.getInstance().getRoleName()) {
            if ( r.equals(ApplicationState.RoleName.ULTIMATE_AUTHORITY)) {
                isRole = true;
                break;
            }
        }
        //runtime if role not equal
        if(!isRole) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Warning", "You do not have permission to perform this operation.");
            return;
        }

        String selectedKey = choiceBox.getValue();
        if (selectedKey == null || selectedKey.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, window, "Warning", "Please select a key from the ChoiceBox.");
            return;
        }

        String fileName = selectedKey.trim().split(":")[0] + ".xlsx";
        // Đường dẫn thư mục
        String folderPath = "excel";
        File directory = new File(folderPath);

        // Kiểm tra và tạo thư mục nếu chưa tồn tại
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Report Data");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Prisoner Name", "Identity Card", "Sentence Code", "Health", "Commendation Sum", "Disciplinary Measure Sum"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            ObservableList<Consider> data = dataTable.getItems();
            if (data.isEmpty()) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "No Data", "No data to export.");
                return;
            }
            int rowNum = 1;
            for (Consider consider : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(consider.getPrisonerName());
                row.createCell(1).setCellValue(consider.getIdentityCard());
                row.createCell(2).setCellValue(consider.getSentenceCode());
                row.createCell(3).setCellValue(consider.getHealth());
                row.createCell(4).setCellValue(consider.getCommendationSum());
                row.createCell(5).setCellValue(consider.getDisciplinaryMeasureSum());
            }

            try (FileOutputStream outputStream = new FileOutputStream(new File(directory, fileName))) {
                workbook.write(outputStream);
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success", "Exported successfully. Please check: " + System.getProperty("user.dir") + "\\" + directory + "\\" + fileName);

                ApplicationState appState = ApplicationState.getInstance();
                UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Exported file name " + fileName);
                userlogDao.insertUserLog(userLog);
            } catch (IOException e) {
                e.printStackTrace();
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "An error occurred while exporting data.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "An error occurred while exporting data.");
        }
    }

    private void updatePagination(FilteredList<Consider> filteredData) {
        int pageCount = (int) Math.ceil((double) filteredData.size() / itemsPerPage);
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, filteredData.size());
            dataTable.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
            return new BorderPane(dataTable);
        });
    }
    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) listTable.size() / itemsPerPage);
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(this::createPage);
    }
    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, listTable.size());
        dataTable.setItems(FXCollections.observableArrayList(listTable.subList(fromIndex, toIndex)));
        return new BorderPane(dataTable);
    }

    private void setupSearch() {
        FilteredList<Consider> filteredData = new FilteredList<>(listTable, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(sentence -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(sentence.getSentenceCode()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (sentence.getPrisonerName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (sentence.getIdentityCard().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(sentence.getSentenceCode()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(sentence.getHealth()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(sentence.getCommendationSum()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(sentence.getDisciplinaryMeasureSum()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
//                else if (sentence.getParole().toLowerCase().contains(lowerCaseFilter)) {
//                    return true;
//                }

                return false;
            });
            updatePagination(filteredData);
        });

        dataTable.setItems(filteredData);
    }
}
