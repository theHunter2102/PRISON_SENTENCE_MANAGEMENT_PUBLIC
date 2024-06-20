package com.example.psmsystem.controller.crime;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.helper.AlertHelper;
import com.example.psmsystem.model.crime.Crime;
import com.example.psmsystem.model.crime.ICrimeDao;
import com.example.psmsystem.model.health.Health;
import com.example.psmsystem.model.prisoner.IPrisonerDao;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.model.sentence.Sentence;
import com.example.psmsystem.model.userlog.IUserLogDao;
import com.example.psmsystem.model.userlog.UserLog;
import com.example.psmsystem.service.crimeDao.CrimeDao;
import com.example.psmsystem.service.userLogDao.UserLogDao;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class CrimeController implements Initializable {
    private static ICrimeDao<Crime> crimeDao;
    private IUserLogDao userlogDao = new UserLogDao();
    @FXML
    private TableView<Crime> dataTable;

    @FXML
    private TableColumn<Crime, String> idColumn;

    @FXML
    private TableColumn<Crime, String> nameColumn;

    @FXML
    private Pagination pagination;

    @FXML
    private TextField txtCrime;

    @FXML
    private TextField txtSearch;

    private final int itemsPerPage = 6;

    Integer index;
    Window window;
    int visitationId;

    ObservableList<Crime> listTable = FXCollections.observableArrayList();

    @FXML
    void getItem(MouseEvent event) {
        index = dataTable.getSelectionModel().getSelectedIndex();
        if(index <= -1){
            return;
        }
        txtCrime.setText(nameColumn.getCellData(index).toString());

        visitationId = crimeDao.getCrimeId(txtCrime.getText());
    }

    @FXML
    void onClean(ActionEvent event) {
        txtCrime.clear();
        dataTable.getSelectionModel().clearSelection();
    }

    @FXML
    void onCreate(ActionEvent event) {
        if (!isValidate()) {
            return;
        }

        String crimeName = txtCrime.getText();
        if(crimeName.isBlank()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Please enter crime name");
            return;
        }
        Crime crime = new Crime(crimeName);

        try {
            crimeDao.addCrime(crime);
        } catch (RuntimeException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", e.getMessage());
            return;
        }

        listTable.add(crime);
        dataTable.setItems(listTable);

        AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success", "Crime created successfully.");
        ApplicationState appState = ApplicationState.getInstance();
        UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Created Crime name " + crimeName);
        userlogDao.insertUserLog(userLog);

        onClean(event);
    }

    @FXML
    void onDelete(ActionEvent event) {
        try {
            if (visitationId == -1) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                        "No sentence found for the selected prisoner.");
                return;
            }

            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirmation");
            confirmationDialog.setHeaderText("Delete sentence!");
            confirmationDialog.setContentText("Are you sure you want to delete it?");

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationDialog.getButtonTypes().setAll(okButton, cancelButton);

            Optional<ButtonType> result = confirmationDialog.showAndWait();

            if (result.isPresent() && result.get() == okButton) {
                Crime crime = dataTable.getSelectionModel().getSelectedItem();

                if (crime != null) {
                    try {
                        crimeDao.deleteCrime(visitationId);
                        listTable.remove(crime);
                        dataTable.setItems(listTable);
                        resetValue();
                        AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success",
                                "Sentence deleted successfully.");

                        ApplicationState appState = ApplicationState.getInstance();
                        UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Deleted Crime id " + visitationId);
                        userlogDao.insertUserLog(userLog);
                    } catch (RuntimeException e) {
                        AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                                e.getMessage());
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "An error occurred while deleting the sentence.");
        }
    }

    @FXML
    void onEdit(ActionEvent event) {
        if (!isValidate()) {
            return;
        }

        String nameCrime = txtCrime.getText();

        if (visitationId == -1) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "No sentence found for the selected prisoner.");
            return;
        }

        Crime crime = new Crime(nameCrime);
        try {
            crimeDao.updateCrime(crime, visitationId);
        } catch (RuntimeException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "No Crime selected.");
            return;
        }


        index = dataTable.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            Crime c = listTable.get(index);
            c.setSentenceCode(nameCrime);

            dataTable.setItems(listTable);
            dataTable.refresh();
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success",
                    "Crime updated successfully.");

            ApplicationState appState = ApplicationState.getInstance();
            UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Updated Crime id " + visitationId);
            userlogDao.insertUserLog(userLog);

            onClean(event);
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "No Crime selected.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        crimeDao = new CrimeDao();

        listTable.addAll(crimeDao.getCrime());
        dataTable.setFixedCellSize(40);

        loadDataTable();
        setupPagination();
        setupSearch();
    }

    private void resetValue(){
        txtCrime.clear();
    }

    private void loadDataTable() {
        idColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Crime, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Crime, String> param) {
                int index = dataTable.getItems().indexOf(param.getValue());
                return new javafx.beans.property.SimpleStringProperty(String.valueOf(index + 1));
            }
        });
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("sentenceCode"));

        dataTable.setItems(listTable);
        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        FilteredList<Crime> filteredData = new FilteredList<>(listTable, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(crime -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (crime.getSentenceCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
            updatePagination(filteredData);
        });

        dataTable.setItems(filteredData);
    }

    private void updatePagination(FilteredList<Crime> filteredData) {
        int pageCount = (int) Math.ceil((double) filteredData.size() / itemsPerPage);
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, filteredData.size());
            dataTable.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
            return new BorderPane(dataTable);
        });
    }

    private boolean isValidate() {
        int existingCrimeId = crimeDao.getCrimeId(txtCrime.getText());
        if (existingCrimeId != -1) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Crime already exists in the database.");
            return false;
        }

        return true;
    }

}
