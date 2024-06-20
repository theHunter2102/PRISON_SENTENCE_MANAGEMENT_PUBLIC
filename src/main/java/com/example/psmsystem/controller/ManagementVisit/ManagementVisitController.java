package com.example.psmsystem.controller.ManagementVisit;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.dto.SentenceDTO;
import com.example.psmsystem.helper.AlertHelper;
import com.example.psmsystem.model.managementvisit.IManagementVisitDao;
import com.example.psmsystem.model.managementvisit.ManagementVisit;
import com.example.psmsystem.model.prisoner.IPrisonerDao;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.model.sentence.ISentenceDao;
import com.example.psmsystem.model.sentence.Sentence;
import com.example.psmsystem.model.sentence.SentenceServiceImpl;
import com.example.psmsystem.model.userlog.IUserLogDao;
import com.example.psmsystem.model.userlog.UserLog;
import com.example.psmsystem.service.managementvisitDao.ManagementVisitDao;
import com.example.psmsystem.service.prisonerDAO.PrisonerDAO;
import com.example.psmsystem.service.sentenceDao.SentenceDao;
import com.example.psmsystem.service.sentenceDao.SentenceService;
import com.example.psmsystem.service.userLogDao.UserLogDao;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.StringUtils;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.stage.Window;
import org.controlsfx.control.SearchableComboBox;

public class ManagementVisitController implements Initializable {
    private static IPrisonerDao<Prisoner> prisonerDao;
    private static IManagementVisitDao<ManagementVisit> managementVisitDao;
    private ISentenceDao<Sentence> sentenceDao;
    private SentenceServiceImpl<SentenceDTO> sentenceService = new SentenceService();
    private IUserLogDao userlogDao = new UserLogDao();

    @FXML
    private TableView<ManagementVisit> dataTable;

    @FXML
    private DatePicker dateVisitDate;

    @FXML
    private SearchableComboBox<SentenceDTO> filterCombo;

    @FXML
    private TableColumn<ManagementVisit, String> noteColumn;

    @FXML
    private Pagination pagination;

    @FXML
    private TableColumn<ManagementVisit, Integer> sentenceCodeColumn;

    @FXML
    private TableColumn<ManagementVisit, String> prisonernameColumn;

    @FXML
    private TableColumn<ManagementVisit, String> relationshipColumn;

    @FXML
    private TableColumn<ManagementVisit, String> cccdColumn;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtcccd;

    @FXML
    private TextField txtRelationship;

    @FXML
    private TextField txtVisitorName;

    @FXML
    private TextArea txtaNote;

    @FXML
    private TableColumn<ManagementVisit, String> visitdateColumn;

    @FXML
    private TableColumn<ManagementVisit, String> startTimeColumn;

    @FXML
    private TableColumn<ManagementVisit, String> endTimeColumn;

    @FXML
    private TableColumn<ManagementVisit, String> visitnameColumn;

    @FXML
    private ComboBox<String> endTimeHour;

    @FXML
    private ComboBox<String> endTimeMinute;

    @FXML
    private ComboBox<String> startTimeHour;

    @FXML
    private ComboBox<String> startTimeMinute;

    private final int itemsPerPage = 20;

    @FXML
    private Button createId;

    Integer index;
    Window window;
    int visitationId;

    ObservableList<ManagementVisit> listTable = FXCollections.observableArrayList();
    ObservableList<String> hours = FXCollections.observableArrayList();
    ObservableList<String> minutes = FXCollections.observableArrayList();

    String fxmlPath = "/com/example/psmsystem/";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prisonerDao = new PrisonerDAO();
        managementVisitDao = new ManagementVisitDao();
        sentenceDao = new SentenceDao();

        listTable.addAll(managementVisitDao.getManagementVisits());
        dataTable.setFixedCellSize(37);

        StringConverter<SentenceDTO> converter = FunctionalStringConverter.to(sentence -> (sentence == null) ? "" : sentence.getSentence().getSentenceCode() + ": " + sentence.getPrisonerName());
        filterCombo.setItems(sentenceService.getPrisonerName());
        filterCombo.setConverter(converter);

        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }

        for (int i = 0; i < 60; i++) {
            minutes.add(String.format("%02d", i));
        }

        // Đặt các giá trị cho các ComboBox
        startTimeHour.setItems(hours);
        startTimeMinute.setItems(minutes);
        endTimeHour.setItems(hours);
        endTimeMinute.setItems(minutes);

        // Thiết lập giá trị mặc định nếu cần
        startTimeHour.getSelectionModel().selectFirst();
        startTimeMinute.getSelectionModel().selectFirst();
        endTimeHour.getSelectionModel().selectFirst();
        endTimeMinute.getSelectionModel().selectFirst();

        createId.disableProperty().bind(
                dataTable.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0)
        );

        loadDataTable();
        setupPagination();
        initUI();
        setupSearch();
    }

    private void initUI() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        dateVisitDate.setConverter(converter);
    }

    private void loadDataTable() {
        sentenceCodeColumn.setCellValueFactory(new PropertyValueFactory<>("sentenceCode"));
        prisonernameColumn.setCellValueFactory(new PropertyValueFactory<>("prisonerName"));
        visitnameColumn.setCellValueFactory(new PropertyValueFactory<>("visitorName"));
        cccdColumn.setCellValueFactory(new PropertyValueFactory<>("identityCard"));
        relationshipColumn.setCellValueFactory(new PropertyValueFactory<>("relationship"));
        visitdateColumn.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

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

    @FXML
    void onClean(ActionEvent event) {
        filterCombo.setValue(null);
        filterCombo.setPromptText("Select Sentence Code");
        txtVisitorName.clear();
        txtcccd.clear();
        txtRelationship.clear();
        dateVisitDate.setValue(null);
        dateVisitDate.setPromptText("YYYY-MM-DD");
        txtaNote.clear();
        startTimeHour.getSelectionModel().selectFirst();
        startTimeMinute.getSelectionModel().selectFirst();
        endTimeHour.getSelectionModel().selectFirst();
        endTimeMinute.getSelectionModel().selectFirst();

        dataTable.getSelectionModel().clearSelection();
    }

    @FXML
    void onCreate(ActionEvent event) {
//        LocalDate selectedDate = dateVisitDate.getValue();
//        Sentence selectedPrisoner = filterCombo.getValue();

        if (!isValidate()) {
            return;
        }

        SentenceDTO selectedValue = filterCombo.getValue();
        int prisonerId = selectedValue.getPrisonerId();
        int sentenceCode = selectedValue.getSentence().getSentenceCode();
        String sentenceId = String.valueOf(sentenceDao.getSentenceId(sentenceCode));

        String prisonerName = selectedValue.getPrisonerName();
        String visitorName = txtVisitorName.getText();
        String cccd = txtcccd.getText();
        String relationship = txtRelationship.getText();
        String note = txtaNote.getText();
        LocalDate selectedDate = dateVisitDate.getValue();
        String date = selectedDate.toString();


        if (hasVisitorThisMonth(selectedValue.getSentence().getSentenceCode(), selectedDate)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Sentence code number " + selectedValue.getSentence().getSentenceCode() + " has had visitors this month.");
            filterCombo.requestFocus();
            return;
        }

        String startHour = startTimeHour.getSelectionModel().getSelectedItem();
        String startMinute = startTimeMinute.getSelectionModel().getSelectedItem();
        String endHour = endTimeHour.getSelectionModel().getSelectedItem();
        String endMinute = endTimeMinute.getSelectionModel().getSelectedItem();

        String startTimeString = startHour + ":" + startMinute + ":00";
        String endTimeString = endHour + ":" + endMinute + ":00";

        LocalTime startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime endTime = LocalTime.parse(endTimeString, DateTimeFormatter.ofPattern("HH:mm:ss"));

        String formattedStartTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formattedEndTime = endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        ManagementVisit mv = new ManagementVisit(sentenceId, sentenceCode, prisonerId, prisonerName, visitorName, cccd, relationship, date, formattedStartTime, formattedEndTime, note);
        try {
            managementVisitDao.addManagementVisit(mv);
        }  catch (RuntimeException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    e.getMessage());
            return;
        }
        listTable.add(mv);
        dataTable.setItems(listTable);

        AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success", "Visit created successfully.");
        userlogDao.insertUserLog(new UserLog(ApplicationState.getInstance().getId(), ApplicationState.getInstance().getUsername(), LocalDateTime.now(), "Created Visit name " + visitorName));

        onClean(event);
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

            LocalDate selectedDate = dateVisitDate.getValue();
            if (selectedDate == null) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                        "Please select a visit date.");
                return;
            }

            if (visitationId == -1) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                        "No visit found.");
                return;
            }

            // Hiển thị hộp thoại xác nhận
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirmation");
            confirmationDialog.setHeaderText("Delete visit!");
            confirmationDialog.setContentText("Are you sure you want to delete this visit?");

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationDialog.getButtonTypes().setAll(okButton, cancelButton);

            Optional<ButtonType> result = confirmationDialog.showAndWait();

            // Xử lý phản hồi của người dùng
            if (result.isPresent() && result.get() == okButton) {
                try {
                    managementVisitDao.deleteManagementVisit(visitationId);
                    ManagementVisit selected = dataTable.getSelectionModel().getSelectedItem();
                    listTable.remove(selected);
                    dataTable.setItems(listTable);
                    resetValue();
                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success",
                            "Visit deleted successfully.");

                    ApplicationState appState = ApplicationState.getInstance();
                    UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Deleted Visit name " + selected.getVisitorName());
                    userlogDao.insertUserLog(userLog);
                } catch (RuntimeException e) {
                    AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                            e.getMessage());
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "An error occurred while deleting the visit.");
        }
    }

    @FXML
    void onEdit(ActionEvent event) {
        if (!isValidate()) {
            return;
        }

        SentenceDTO selectedValue = filterCombo.getValue();
        int prisonerId = selectedValue.getPrisonerId();
        int sentenceCode = selectedValue.getSentence().getSentenceCode();
        String sentenceId = String.valueOf(sentenceDao.getSentenceId(sentenceCode));

        String prisonerName = selectedValue.getPrisonerName();
        String visitorName = txtVisitorName.getText();
        String cccd = txtcccd.getText();
        String relationship = txtRelationship.getText();
        String note = txtaNote.getText();
        LocalDate selectedDate = dateVisitDate.getValue();
        String date = selectedDate.toString();

        String startHour = startTimeHour.getSelectionModel().getSelectedItem();
        String startMinute = startTimeMinute.getSelectionModel().getSelectedItem();
        String endHour = endTimeHour.getSelectionModel().getSelectedItem();
        String endMinute = endTimeMinute.getSelectionModel().getSelectedItem();

        String startTimeString = startHour + ":" + startMinute + ":00";
        String endTimeString = endHour + ":" + endMinute + ":00";

        LocalTime startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime endTime = LocalTime.parse(endTimeString, DateTimeFormatter.ofPattern("HH:mm:ss"));

        String formattedStartTime = startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formattedEndTime = endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        if (visitationId <1) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "No visit found for the selected prisoner and date.");
            return;
        }

        ManagementVisit mv = new ManagementVisit(sentenceId, sentenceCode, prisonerId, prisonerName, visitorName, cccd, relationship, date, formattedStartTime, formattedEndTime, note);
        try {
            managementVisitDao.updateManagementVisit(mv, visitationId);
        }  catch (RuntimeException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    e.getMessage());
            return;
        }

        index = dataTable.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            ManagementVisit visit = listTable.get(index);
            visit.setSentenceCode(sentenceCode);
            visit.setPrisonerName(prisonerName);
            visit.setVisitorName(visitorName);
            visit.setIdentityCard(cccd);
            visit.setRelationship(relationship);
            visit.setVisitDate(date);
            visit.setStartTime(formattedStartTime);
            visit.setEndTime(formattedEndTime);
            visit.setNotes(note);

            dataTable.setItems(listTable);
            dataTable.refresh();
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success",
                    "Visit updated successfully.");

            ApplicationState appState = ApplicationState.getInstance();
            UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Updated Visit name " + visitorName);
            userlogDao.insertUserLog(userLog);
            onClean(event);
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "No visit selected.");
        }
    }

    @FXML
    void getItem(MouseEvent event) {
        index = dataTable.getSelectionModel().getSelectedIndex();
        if(index <= -1){
            return;
        }
        for (SentenceDTO sentence : filterCombo.getItems()) {
            if (sentence.getSentence().getSentenceCode() == (sentenceCodeColumn.getCellData(index))) {
                filterCombo.setValue(sentence);
                break;
            }
        }

        SentenceDTO selectedValue = filterCombo.getValue();
        int prisonerId = selectedValue.getPrisonerId();
        txtVisitorName.setText(visitnameColumn.getCellData(index).toString());
        txtcccd.setText(cccdColumn.getCellData(index).toString());
        txtRelationship.setText(relationshipColumn.getCellData(index).toString());

        LocalDate date = LocalDate.parse(visitdateColumn.getCellData(index).toString());
        dateVisitDate.setValue(date);
        LocalDate selectedDate = dateVisitDate.getValue();
        String dateString = selectedDate.toString();
        txtaNote.setText(noteColumn.getCellData(index).toString());

        List<String> startTime = setComboBoxValues(startTimeColumn.getCellData(index).toString());
        List<String> endTime = setComboBoxValues(endTimeColumn.getCellData(index).toString());

        startTimeHour.getSelectionModel().select(startTime.get(0));
        startTimeMinute.getSelectionModel().select(startTime.get(1));
        endTimeHour.getSelectionModel().select(endTime.get(0));
        endTimeMinute.getSelectionModel().select(endTime.get(1));

        visitationId = managementVisitDao.getVisitationId(prisonerId, dateString);

    }
    private void resetValue(){
        filterCombo.setValue(null);
        filterCombo.setPromptText("Select Sentence Code");
        txtVisitorName.clear();
        txtcccd.clear();
        txtRelationship.clear();
        dateVisitDate.setValue(null);
        dateVisitDate.setPromptText("YYYY-MM-DD");
        startTimeHour.getSelectionModel().selectFirst();
        startTimeMinute.getSelectionModel().selectFirst();
        endTimeHour.getSelectionModel().selectFirst();
        endTimeMinute.getSelectionModel().selectFirst();
        txtaNote.clear();
    }

    private void setupSearch() {
        FilteredList<ManagementVisit> filteredData = new FilteredList<>(listTable, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(managementVisit -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                List<String> startTime = setComboBoxValues(managementVisit.getStartTime());
                List<String> endTime = setComboBoxValues(managementVisit.getEndTime());

                String startTimeString = startTime.get(0) + ":" + startTime.get(1);
                String endTimeString = endTime.get(0) + ":" + endTime.get(1);

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(managementVisit.getSentenceCode()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (managementVisit.getPrisonerName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (managementVisit.getVisitorName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (managementVisit.getIdentityCard().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (managementVisit.getRelationship().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (managementVisit.getVisitDate().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (managementVisit.getNotes().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (startTimeString.contains(lowerCaseFilter)) {
                    return true;
                }
                else if (endTimeString.contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
            updatePagination(filteredData);
        });

        dataTable.setItems(filteredData);
    }

    private void updatePagination(FilteredList<ManagementVisit> filteredData) {
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

        if (filterCombo.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Please select a prisoner.");
            filterCombo.requestFocus();
            return false;
        }
        if (txtVisitorName.getText() == null || txtVisitorName.getText().trim().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Visitor Name is required.");
            txtVisitorName.requestFocus();
            return false;
        }
        if (!txtcccd.getText().matches("^(?!0{12})\\d{12}$")) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Identity card must be 12character number and not 000000000000.");
            txtcccd.requestFocus();
            return false;
        }
        if (txtRelationship.getText() == null || txtRelationship.getText().trim().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Relationship is required.");
            txtRelationship.requestFocus();
            return false;
        }
        if (dateVisitDate.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Visit Date is required.");
            dateVisitDate.requestFocus();
            return false;
        } else if (dateVisitDate.getValue().isAfter(LocalDate.now())) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Visit Date cannot be in the future.");
            dateVisitDate.requestFocus();
            return false;
        }

        String startHour = startTimeHour.getSelectionModel().getSelectedItem();
        String startMinute = startTimeMinute.getSelectionModel().getSelectedItem();
        String endHour = endTimeHour.getSelectionModel().getSelectedItem();
        String endMinute = endTimeMinute.getSelectionModel().getSelectedItem();

        LocalTime startTime = LocalTime.of(Integer.parseInt(startHour), Integer.parseInt(startMinute));
        LocalTime endTime = LocalTime.of(Integer.parseInt(endHour), Integer.parseInt(endMinute));

        LocalTime morningStart = LocalTime.of(7, 30);
        LocalTime morningEnd = LocalTime.of(11, 0);
        LocalTime afternoonStart = LocalTime.of(14, 0);
        LocalTime afternoonEnd = LocalTime.of(17, 0);

        if (!((startTime.isAfter(morningStart.minusSeconds(1)) && endTime.isBefore(morningEnd.plusSeconds(1))) ||
                (startTime.isAfter(afternoonStart.minusSeconds(1)) && endTime.isBefore(afternoonEnd.plusSeconds(1))))) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Visit time must be within 07:30-11:00 or 14:00-17:00.");
            startTimeHour.requestFocus();
            return false;
        }

        if (endTime.isBefore(startTime) || startTime.until(endTime, ChronoUnit.MINUTES) > 60) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Visit duration cannot be more than 60 minutes.");
            endTimeHour.requestFocus();
            return false;
        }

        return true;
    }

    private boolean hasVisitorThisMonth(int prisonerCode, LocalDate visitDate) {
        LocalDate startOfMonth = visitDate.withDayOfMonth(1);
        LocalDate endOfMonth = visitDate.withDayOfMonth(visitDate.lengthOfMonth());

        for (ManagementVisit visit : managementVisitDao.getManagementVisits()) {
            if (visit.getSentenceCode() == prisonerCode) {
                LocalDate visitLocalDate = LocalDate.parse(visit.getVisitDate());
                if ((visitLocalDate.isEqual(startOfMonth) || visitLocalDate.isAfter(startOfMonth)) &&
                        (visitLocalDate.isEqual(endOfMonth) || visitLocalDate.isBefore(endOfMonth))) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<String> setComboBoxValues(String timeString) {
        // Split the time string into parts
        String[] timeParts = timeString.split(":");
        String hour = timeParts[0];
        String minute = timeParts[1];

        // Create a list and add the hour and minute
        List<String> timeResult = new ArrayList<>();
        timeResult.add(hour);
        timeResult.add(minute);

        // Return the list
        return timeResult;
    }
}
