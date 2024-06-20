package com.example.psmsystem.controller.sentence;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.dto.SentenceDTO;
import com.example.psmsystem.helper.AlertHelper;
import com.example.psmsystem.model.crime.Crime;
import com.example.psmsystem.model.crime.ICrimeDao;
import com.example.psmsystem.model.prisoner.IPrisonerDao;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.model.sentence.ISentenceDao;
import com.example.psmsystem.model.sentence.Sentence;
import com.example.psmsystem.model.sentence.SentenceServiceImpl;
import com.example.psmsystem.model.userlog.IUserLogDao;
import com.example.psmsystem.model.userlog.UserLog;
import com.example.psmsystem.service.crimeDao.CrimeDao;
import com.example.psmsystem.service.prisonerDAO.PrisonerDAO;
import com.example.psmsystem.service.sentenceDao.SentenceDao;
import com.example.psmsystem.service.sentenceDao.SentenceService;
import com.example.psmsystem.service.userLogDao.UserLogDao;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SearchableComboBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class SentenceController implements Initializable {
    private IPrisonerDao<Prisoner> prisonerDao;
    private ICrimeDao<Crime> crimeDao;
    private ISentenceDao<Sentence> sentenceDao;
    private SentenceServiceImpl<SentenceDTO> sentenceService = new SentenceService();
    private IUserLogDao userlogDao = new UserLogDao();

    @FXML
    private ComboBox<String> cbSentenceType;

    @FXML
    private CheckComboBox<Crime> ccbSentenceCode;

    @FXML
    private DatePicker dateEndDate;

    @FXML
    private DatePicker dateStartDate;

    @FXML
    private TableColumn<Sentence, Date> endDateColumn;

    @FXML
    private SearchableComboBox<SentenceDTO> filterCombo;

    @FXML
    private TableColumn<Sentence, String> paroleEligibilityColumn;

    @FXML
    private TableColumn<Sentence, Integer> prisonerCodeColumn;

    @FXML
    private TableColumn<Sentence, String> prisonerNameColumn;

    @FXML
    private TableColumn<Sentence, String> crimesColumn;

    @FXML
    private TableColumn<Sentence, String> sentenceTypeColumn;

    @FXML
    private TableColumn<Sentence, Date> startDateColumn;

    @FXML
    private TableColumn<Sentence, Date> releaseDateColumn;

    @FXML
    private TableColumn<Sentence, Boolean> statusColumn;

    @FXML
    private TableView<Sentence> dataTable;

    @FXML
    private TextField txtMonth;

    @FXML
    private TextField txtParoleEligibility;

    @FXML
    private TextField txtSearch;

    @FXML
    private DatePicker dateReleaseDate;

//    @FXML
//    private TextField txtStatus;

    @FXML
    private TextField txtYear;

    @FXML
    private Pagination pagination;

    private final int itemsPerPage = 10;

    Integer index;
    Window window;
    int visitationId;

    ObservableList<Sentence> listTable = FXCollections.observableArrayList();
    ObservableList<Crime> listCrime = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prisonerDao = new PrisonerDAO();
        crimeDao = new CrimeDao();
        sentenceDao = new SentenceDao();

        listTable.addAll(sentenceDao.getSentence());
        listCrime.addAll(crimeDao.getCrime());

        ccbSentenceCode.getItems().addAll(listCrime);

        dataTable.setFixedCellSize(40);

        StringConverter<SentenceDTO> converter = FunctionalStringConverter.to(sentence -> (sentence == null) ? "" : sentence.getSentence().getSentenceCode() + ": " + sentence.getPrisonerName());
        filterCombo.setItems(sentenceService.getPrisonerName());
        filterCombo.setConverter(converter);

        cbSentenceType.setItems(FXCollections.observableArrayList("life imprisonment", "limited time"));

        cbSentenceType.getSelectionModel().select("limited time");

        cbSentenceType.setOnAction(event -> {
            String selectedType = cbSentenceType.getSelectionModel().getSelectedItem();
            if (selectedType != null) {
                switch (selectedType) {
                    case "life imprisonment":
                        dateEndDate.setDisable(true);
                        dateReleaseDate.setDisable(true);
//                        txtMonth.clear();
//                        txtYear.clear();
                        break;
                    case "limited time":
                        dateEndDate.setDisable(false);
                        dateReleaseDate.setDisable(false);
                        break;
                    default:
//                        txtMonth.setDisable(false);
//                        txtYear.setDisable(false);
                        break;
                }
            }
        });

        ccbSentenceCode.setConverter(new StringConverter<Crime>() {
            @Override
            public String toString(Crime crime) {
                return crime != null ? crime.getSentenceCode() : "";
            }

            @Override
            public Crime fromString(String string) {
                return listCrime.stream().filter(crime -> crime.getSentenceCode().equals(string)).findFirst().orElse(null);
            }
        });

//        setNumericTextField(txtYear);
//        setNumericTextField(txtMonth);

//        txtYear.textProperty().addListener((observable, oldValue, newValue) -> updateEndDate());
//        txtMonth.textProperty().addListener((observable, oldValue, newValue) -> updateEndDate());
//        dateStartDate.valueProperty().addListener((observable, oldValue, newValue) -> updateEndDate());

        loadDataTable();
        setupPagination();
        initUI();
        setupSearch();
    }

    private void loadDataTable() {
            prisonerCodeColumn.setCellValueFactory(new PropertyValueFactory<>("sentenceCode"));
        prisonerNameColumn.setCellValueFactory(new PropertyValueFactory<>("prisonerName"));
        sentenceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("sentenceType"));
        crimesColumn.setCellValueFactory(new PropertyValueFactory<>("crimesCode"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        releaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        paroleEligibilityColumn.setCellValueFactory(new PropertyValueFactory<>("parole"));

        statusColumn.setCellFactory(new Callback<TableColumn<Sentence, Boolean>, TableCell<Sentence, Boolean>>() {
            @Override
            public TableCell<Sentence, Boolean> call(TableColumn<Sentence, Boolean> param) {
                return new TableCell<Sentence, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item ? "Released" : "Are serving sentence");
                        }
                    }
                };
            }
        });

        dataTable.setItems(listTable);
        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupSearch() {
        FilteredList<Sentence> filteredData = new FilteredList<>(listTable, p -> true);

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
                else if (sentence.getSentenceType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(sentence.getSentenceCode()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(sentence.getStartDate()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(sentence.getEndDate()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(sentence.getReleaseDate()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (sentence.getParole().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
            updatePagination(filteredData);
        });

        dataTable.setItems(filteredData);
    }

    private void updatePagination(FilteredList<Sentence> filteredData) {
        int pageCount = (int) Math.ceil((double) filteredData.size() / itemsPerPage);
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, filteredData.size());
            dataTable.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
            return new BorderPane(dataTable);
        });
    }

    private void initUI() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Tạo StringConverter tùy chỉnh để chuyển đổi giữa LocalDate và String
        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        dateStartDate.setConverter(converter);
        dateEndDate.setConverter(converter);
        dateReleaseDate.setConverter(converter);
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

    @FXML
    void getItem(MouseEvent event) {
        index = dataTable.getSelectionModel().getSelectedIndex();
        if(index <= -1){
            return;
        }

        for (SentenceDTO sentence : filterCombo.getItems()) {
            if (sentence.getSentence().getSentenceCode() == (prisonerCodeColumn.getCellData(index))) {
                filterCombo.setValue(sentence);
                break;
            }
        }
        SentenceDTO selectedValue = filterCombo.getValue();
        int prisonerCode = selectedValue.getSentence().getSentenceCode();
        cbSentenceType.setValue(sentenceTypeColumn.getCellData(index).toString());

        ccbSentenceCode.getCheckModel().clearChecks();
        List<Crime> crimes = createCrimesFromSentenceCode(crimesColumn.getCellData(index).toString());

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < ccbSentenceCode.getItems().size(); i++) {
            Crime currentItem = ccbSentenceCode.getItems().get(i);
            for (Crime crime : crimes) {
                if (currentItem.getSentenceCode().equals(crime.getSentenceCode())) {
                    indices.add(i);
                    break;
                }
            }
        }

        for (int idx : indices) {
            ccbSentenceCode.getCheckModel().check(idx);
        }


        try {
            LocalDate startDate = LocalDate.parse(startDateColumn.getCellData(index).toString());
            dateStartDate.setValue(startDate);

        } catch (Exception e) {
            dateStartDate.setValue(null);
        }


        String endDateCellValue = endDateColumn.getCellData(index) != null ? endDateColumn.getCellData(index).toString() : null;
        if (endDateCellValue != null && !endDateCellValue.isEmpty()) {
            LocalDate endDate = LocalDate.parse(endDateCellValue);
            dateEndDate.setValue(endDate);
        } else {
            dateEndDate.setValue(null);
        }

        String releaseDateCellValue = releaseDateColumn.getCellData(index) != null ? releaseDateColumn.getCellData(index).toString() : null;
        if (releaseDateCellValue != null && !releaseDateCellValue.isEmpty()) {
            LocalDate releaseDate = LocalDate.parse(releaseDateCellValue);
            dateReleaseDate.setValue(releaseDate);
        } else {
            dateReleaseDate.setValue(null);
        }

//        txtStatus.setText(statusColumn.getCellData(index).toString());
        txtParoleEligibility.setText(paroleEligibilityColumn.getCellData(index).toString());

        visitationId = sentenceDao.getSentenceId(prisonerCode);

//        if (startDate != null && LocalDate.parse(endDateCellValue) != null) {
//            long yearsBetween = ChronoUnit.YEARS.between(startDate, LocalDate.parse(endDateCellValue));
//            long monthsBetween = ChronoUnit.MONTHS.between(startDate.plusYears(yearsBetween), LocalDate.parse(endDateCellValue));
//
//            txtYear.setText(String.valueOf(yearsBetween));
//            txtMonth.setText(String.valueOf(monthsBetween));
//        } else {
//            txtYear.setText("");
//            txtMonth.setText("");
//        }
    }

    private List<Crime> createCrimesFromSentenceCode(String sentenceCode) {
        List<Crime> crimes = new ArrayList<>();

        String[] codes = sentenceCode.split(", ");

        for (String code : codes) {
            Crime crime = new Crime();
            crime.setSentenceCode(code);
            crimes.add(crime);
        }

        return crimes;
    }


    @FXML
    void onClean(ActionEvent event) {
        filterCombo.setValue(null);
        filterCombo.setPromptText("Select Sentence Code");
        cbSentenceType.getSelectionModel().select("limited time");
        ccbSentenceCode.getCheckModel().clearChecks();
//        txtYear.clear();
//        txtMonth.clear();
        dateStartDate.setValue(null);
        dateStartDate.setPromptText("YYYY-MM-DD");
        dateEndDate.setValue(null);
        dateEndDate.setPromptText("YYYY-MM-DD");
        dateReleaseDate.setValue(null);
        dateReleaseDate.setPromptText("YYYY-MM-DD");
//        txtStatus.clear();
        txtParoleEligibility.clear();

        dataTable.getSelectionModel().clearSelection();
    }

    private String listCrime(){
        StringBuilder selectedCrimeCodes = new StringBuilder();
        ObservableList<Crime> selectedCrimes = ccbSentenceCode.getCheckModel().getCheckedItems();

        for (Crime crime : selectedCrimes) {
            selectedCrimeCodes.append(crime.getSentenceCode()).append(", ");
        }

        String result = selectedCrimeCodes.toString().replaceAll(", $", "");
        return result;
    }

    @FXML
    void onCreate(ActionEvent event) {
        if (!isValidate()) {
            return;
        }
        SentenceDTO selectedValue = filterCombo.getValue();
        int prisonerId = selectedValue.getPrisonerId();
        int sentenceCode = selectedValue.getSentence().getSentenceCode();
        String prisonerName = selectedValue.getPrisonerName();
        String sentenceType = cbSentenceType.getValue();
        String crimeCode = listCrime();
        LocalDate selectedStartDate = dateStartDate.getValue();
        java.sql.Date startDate = java.sql.Date.valueOf(selectedStartDate);
        LocalDate selectedEndDate = dateEndDate.getValue();
        java.sql.Date endDate = java.sql.Date.valueOf(selectedEndDate);
//        String endDate = selectedEndDate.toString();
        Boolean status = false;
        String paroleeligibility = txtParoleEligibility.getText();
        LocalDate selectedReleaseDate = dateReleaseDate.getValue();
        java.sql.Date releaseDate = java.sql.Date.valueOf(selectedReleaseDate);

        Sentence sentence = new Sentence(prisonerId, prisonerName, sentenceCode, sentenceType, crimeCode, startDate, endDate, releaseDate, status, paroleeligibility);
        sentenceDao.addSentence(sentence);
        listTable.add(sentence);
        dataTable.setItems(listTable);

        AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success", "Sentence created successfully.");

        onClean(event);
    }

    @FXML
    void onCrimeSetting(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/psmsystem/view/crime/CrimeView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Crime");
        stage.show();
    }

    @FXML
    void onDelete(ActionEvent event) {
        try {
            SentenceDTO selectedValue = filterCombo.getValue();
            if (selectedValue == null) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                        "Please select a prisoner.");
                return;
            }

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
                Sentence selected = dataTable.getSelectionModel().getSelectedItem();

                if (selected != null) {
//                    LocalDate endDate = selected.getEndDate() != null ? selected.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
                    long releaseTimeMillis = selected.getReleaseDate() != null ? selected.getReleaseDate().getTime() : 0;

                    LocalDate releaseDate = Instant.ofEpochMilli(releaseTimeMillis).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate today = LocalDate.now();

//                    System.out.println(selected.isStatus());
//                    System.out.println((releaseDate != null) + ""+ releaseDate);
//                    System.out.println((releaseDate.plusYears(20).isBefore(today)));

//                    if (endDate == null) {
//                        AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Warning",
//                                "Cannot delete record because the prisoner is life imprisonment.");
//                    }
                     if (selected.isStatus() && releaseDate != null && releaseDate.plusYears(1).isBefore(today)) {
                         try {
                             sentenceDao.deleteSentence(visitationId);
                         } catch (RuntimeException e) {
                             AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                                     "An error occurred while deleting the sentence.");
                             return;
                         }

                        listTable.remove(selected);
                        dataTable.setItems(listTable);
                        resetValue();
                        AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success",
                                "Sentence deleted successfully.");

                         ApplicationState appState = ApplicationState.getInstance();
                         UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Updated Sentence code " + selected.getSentenceCode());
                         userlogDao.insertUserLog(userLog);
                    }
                    else {
                        AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Warning",
                                "Delete failed: This sentence is either ongoing or hasn't reached its full 20-year duration.");
                    }
                }
                else {
                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Warning",
                            "Record cannot be deleted due to an active sentence.");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "An error occurred while deleting the sentence.");
        }
    }

    private void resetValue(){
        filterCombo.setValue(null);
        filterCombo.setPromptText("Select Sentence Code");
        cbSentenceType.getSelectionModel().select("limited time");
        ccbSentenceCode.getCheckModel().clearChecks();
//        txtYear.clear();
//        txtMonth.clear();
        dateStartDate.setValue(null);
        dateStartDate.setPromptText("YYYY-MM-DD");
        dateEndDate.setValue(null);
        dateEndDate.setPromptText("YYYY-MM-DD");
        dateReleaseDate.setValue(null);
        dateReleaseDate.setPromptText("YYYY-MM-DD");
//        txtStatus.clear();
        txtParoleEligibility.clear();
    }


    @FXML
    void onEdit(ActionEvent event) {
        if (!isValidate()) {
            return;
        }

        SentenceDTO selectedValue = filterCombo.getValue();
        int prisonerId = selectedValue.getPrisonerId();
        int sentenceCode = selectedValue.getSentence().getSentenceCode();
        String sentenceType = cbSentenceType.getValue();
        String crimeCode = listCrime();

        String prisonerName = selectedValue.getPrisonerName();
        LocalDate selectedStartDate = dateStartDate.getValue();
        LocalDate selectedEndDate = dateEndDate.getValue();
        LocalDate selectedReleaseDate = dateReleaseDate.getValue();
        //check startDate must not null
        if(selectedStartDate == null || selectedStartDate.isAfter(LocalDate.now())) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "The sentence must have a start date, and it must be either today or a past date.");
            return;
        }
        boolean status = false;

        //check sentenceType
        if(sentenceType.equals("limited time")) {

            //end Date must not null
            if(selectedEndDate == null ) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "A fixed-term prison sentence must have an expected release date.");
                return;
            } else { //end date not null
                //end date must be < 30years(start)
                if(selectedEndDate.isAfter(selectedStartDate.plusYears(30))) {
                    AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "The sentence is a maximum of 30 years.");
                    return;
                }
                if(selectedReleaseDate != null) {  //release not null
                    status = true;
                    if(selectedReleaseDate.isAfter(selectedEndDate) || selectedReleaseDate.isBefore(selectedStartDate.plusMonths(3))) {
                        AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "The actual release date must be before the end date of the sentence and at least 3 months after the start date.");
                        return;
                    }
                }
            }

        } else { // life: endDate and releaseDate must null
            selectedEndDate = null;
            selectedReleaseDate = null;
        }

        String paroleEligibility = txtParoleEligibility.getText();

        if (visitationId == -1) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "No sentence found for the selected prisoner.");
            return;
        }

        java.sql.Date startDate = selectedStartDate != null ? java.sql.Date.valueOf(selectedStartDate) : null;
        java.sql.Date endDate = selectedEndDate != null ? java.sql.Date.valueOf(selectedEndDate) : null;
        java.sql.Date releaseDate = selectedReleaseDate != null ? java.sql.Date.valueOf(selectedReleaseDate) : null;

        Sentence sentence = new Sentence(prisonerId, prisonerName, sentenceCode, sentenceType, crimeCode, startDate, endDate, releaseDate, status, paroleEligibility);
        try {
            sentenceDao.updateSentence(sentence, visitationId);
        } catch (RuntimeException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Update sentence failed: " + e.getMessage());
            return;
        }

        index = dataTable.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            Sentence s = listTable.get(index);
            s.setPrisonerId(prisonerId);
            s.setPrisonerName(prisonerName);
            s.setSentenceType(sentenceType);
            s.setSentenceCode(sentenceCode);
            s.setCrimesCode(crimeCode);
            s.setStartDate(startDate);
            s.setEndDate(endDate);
            s.setStatus(status);
            s.setReleaseDate(releaseDate);
            s.setParole(paroleEligibility);

            dataTable.setItems(listTable);
            dataTable.refresh();
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success",
                    "Sentence updated successfully.");

            ApplicationState appState = ApplicationState.getInstance();
            UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Updated Sentence code " + sentenceCode);
            userlogDao.insertUserLog(userLog);
            onClean(event);

        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "No sentence selected.");
        }
    }

    private boolean isValidate() {
        LocalDate startDate = dateStartDate.getValue();
        LocalDate endDate = dateEndDate.getValue();
        LocalDate releaseDate = dateReleaseDate.getValue();

        if (filterCombo.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Please select a prisoner.");
            filterCombo.requestFocus();
            return false;
        }
//
//        int prisonerCode = filterCombo.getValue().getSentence().getSentenceCode();
//        int sentenceId = sentenceDao.getSentenceId(prisonerCode);
//        if (sentenceId != -1) {
//            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Selected prisoner already has a sentence.");
//            return false;
//        }

        if (startDate != null && endDate != null) {
            if (!isDateDifferenceValid(startDate, endDate, 3)) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Validation Error", "End date must be at least 3 months greater than start date.");
                return false;
            }
        }

        if (startDate != null && releaseDate != null) {
            if (!isDateDifferenceValid(startDate, releaseDate, 3)) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Validation Error", "Release date must be at least 3 months greater than start date.");
                return false;
            }
        }

        return true;
    }

    private boolean isDateDifferenceValid(LocalDate startDate, LocalDate endDate, int months) {
        return !endDate.isBefore(startDate.plusMonths(months));
    }

//    private void setNumericTextField(TextField textField) {
//        textField.textProperty().addListener((observable, oldValue, newValue) -> {
//            if (!newValue.matches("\\d*")) {
//                textField.setText(newValue.replaceAll("[^\\d]", ""));
//            }
//        });
//    }
//
//    private void updateEndDate() {
//        if (dateStartDate.getValue() != null) {
//            try {
//                String textYear = txtYear.getText();
//                String textMonth = txtMonth.getText();
//                if (textYear.isEmpty()){
//                    txtYear.setText("0");
//                }
//                if (textMonth.isEmpty()){
//                    txtMonth.setText("0");
//                }
//                int years = textYear.isEmpty() ? 0 : Integer.parseInt(textYear);
//                int months = textMonth.isEmpty() ? 0 :Integer.parseInt(textMonth);
//                LocalDate startDate = dateStartDate.getValue();
//                System.out.println(startDate);
//                LocalDate endDate = startDate.plusYears(years).plusMonths(months);
//
//                dateEndDate.setValue(endDate);
//            } catch (NumberFormatException e) {
////                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Invalid Input",
////                        "Please enter valid numbers for year and month.");
//                dateEndDate.setValue(null);
//                dateEndDate.setPromptText("YYYY-MM-DD");
//            }
//        }
//        else{
//            txtYear.clear();
//            txtMonth.clear();
//        }
//    }
}
