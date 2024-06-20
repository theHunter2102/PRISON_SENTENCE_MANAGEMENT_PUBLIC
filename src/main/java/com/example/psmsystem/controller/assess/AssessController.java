package com.example.psmsystem.controller.assess;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.dto.SentenceDTO;
import com.example.psmsystem.helper.AlertHelper;
import com.example.psmsystem.model.assess.Assess;
import com.example.psmsystem.model.assess.IAssessDao;
import com.example.psmsystem.model.health.Health;
import com.example.psmsystem.model.prisoner.IPrisonerDao;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.model.sentence.ISentenceDao;
import com.example.psmsystem.model.sentence.Sentence;
import com.example.psmsystem.model.sentence.SentenceServiceImpl;
import com.example.psmsystem.model.userlog.IUserLogDao;
import com.example.psmsystem.model.userlog.UserLog;
import com.example.psmsystem.service.assessDao.AssessDao;
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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssessController implements Initializable {
    private IPrisonerDao<Prisoner> prisonerDao;
    private IAssessDao<Assess> assessDao;
    private ISentenceDao<Sentence> sentenceDao;
    private SentenceServiceImpl<SentenceDTO> sentenceService;
    private IUserLogDao userlogDao = new UserLogDao();

    @FXML
    private ComboBox<String> cbEventType;

    @FXML
    private TableView<Assess> dataTable;

    @FXML
    private DatePicker dateEventDate;

    @FXML
    private TableColumn<Assess, Integer> desctiptionColumn;

    @FXML
    private TableColumn<Assess, String> eventDateColumn;

    @FXML
    private TableColumn<Assess, String> eventTypeColumn;

    @FXML
    private SearchableComboBox<SentenceDTO> filterCombo;

    @FXML
    private TableColumn<Assess, String> noteColumn;

    @FXML
    private Pagination pagination;

    @FXML
    private TableColumn<Assess, Integer> prisonerCodeColumn;

    @FXML
    private TableColumn<Assess, String> prisonerNameColumn;

    @FXML
    private TextField txtDesctiption;

    @FXML
    private TextField txtNote;

    @FXML
    private ComboBox<String> cbLevel;

    @FXML
    private TableColumn<Assess, String> assessColumn;

    @FXML
    private TextField txtSearch;
    private final int itemsPerPage = 12;
    @FXML
    private Button createId;


    Integer index;
    Window window;
    int visitationId;
    private final Map<Integer, String> levelMap = new HashMap<>();
    ObservableList<Assess> listTable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prisonerDao = new PrisonerDAO();
        assessDao = new AssessDao();
        sentenceDao = new SentenceDao();
        sentenceService = new SentenceService();

        listTable.addAll(assessDao.getAssess());
        dataTable.setFixedCellSize(42);

        StringConverter<SentenceDTO> converter = FunctionalStringConverter.to(sentence -> (sentence == null) ? "" : sentence.getSentence().getSentenceCode() + ": " + sentence.getPrisonerName());
        filterCombo.setItems(sentenceService.getPrisonerName());
        filterCombo.setConverter(converter);

        cbEventType.setItems(FXCollections.observableArrayList("Bonus",  "Breach of discipline"));
        cbEventType.getSelectionModel().select("Bonus");
        cbLevel.setItems(FXCollections.observableArrayList("MILD", "MODERATE", "GOOD", "VERY GOOD"));
        cbLevel.getSelectionModel().selectFirst();

        cbEventType.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateLevelMap(newValue);
            if (newValue.equals("Bonus")) {
                cbLevel.setItems(FXCollections.observableArrayList("MILD", "MODERATE", "GOOD", "VERY GOOD"));
            } else {
                cbLevel.setItems(FXCollections.observableArrayList("MILD", "MODERATE", "SEVERE", "EXTREMELY SEVERE"));
            }
            cbLevel.getSelectionModel().selectFirst();
        });
        updateLevelMap("Bonus");
        levelMap.put(1, "MILD");
        levelMap.put(2, "MODERATE");
        levelMap.put(3, "GOOD");
        levelMap.put(4, "VERY GOOD");

        createId.disableProperty().bind(
                dataTable.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0)
        );

        loadDataTable();
        setupPagination();
        initUI();
        setupSearch();
    }

    private void loadDataTable() {
        assessColumn.setCellValueFactory(new PropertyValueFactory<>("processCode"));
        prisonerCodeColumn.setCellValueFactory(new PropertyValueFactory<>("sentencesCode"));
        prisonerNameColumn.setCellValueFactory(new PropertyValueFactory<>("prisonerName"));
        eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfOccurrence"));
        eventTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        desctiptionColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));


        desctiptionColumn.setCellFactory(col -> new TableCell<Assess, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String eventType = getTableView().getItems().get(getIndex()).getEventType();
                    String description = getDescription(eventType, item);
                    setText(description);
                }
            }
        });

        dataTable.setItems(listTable);
        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupSearch() {
        FilteredList<Assess> filteredData = new FilteredList<>(listTable, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(assess -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (assess.getProcessCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(assess.getSentencesCode()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (assess.getPrisonerName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (assess.getDateOfOccurrence().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (assess.getEventType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (assess.getNote().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else {
                    Integer levelValue = Integer.valueOf(assess.getLevel());
                    String levelString = levelMap.get(levelValue);
                    if (levelString != null && levelString.toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                }

                return false;
            });
            updatePagination(filteredData);
        });

        dataTable.setItems(filteredData);
    }

    private void updatePagination(FilteredList<Assess> filteredData) {
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
        dateEventDate.setConverter(converter);
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

    private boolean isValidate() {
        if (filterCombo.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Please select a prisoner.");
            filterCombo.requestFocus();
            return false;
        }

        if (dateEventDate.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Event Date is required.");
            dateEventDate.requestFocus();
            return false;
        }
        return true;
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
//        String prisonerCode = selectedValue.getPrisonerCode();
        LocalDate date = LocalDate.parse(eventDateColumn.getCellData(index).toString());
        dateEventDate.setValue(date);
        cbEventType.setValue(eventTypeColumn.getCellData(index).toString());
//        txtDesctiption.setText(desctiptionColumn.getCellData(index).toString());
        txtNote.setText(noteColumn.getCellData(index).toString());

        String assessCode = assessColumn.getCellData(index).toString();

        LocalDate selectedDate = dateEventDate.getValue();
        String dateString = selectedDate.toString();

        Integer levelDescription = Integer.parseInt(desctiptionColumn.getCellData(index).toString());

        String levelValue = levelMap.get(levelDescription);
        if (levelValue != null) {
            cbLevel.setValue(levelValue);
        } else {
            System.out.println("Level description not found in map.");
        }

        visitationId = assessDao.getAssessId(assessCode, dateString);
    }

    @FXML
    void onClean(ActionEvent event) {
        filterCombo.setValue(null);
        filterCombo.setPromptText("Select Sentence Code");
        cbEventType.getSelectionModel().select("Bonus");
        cbLevel.getSelectionModel().selectFirst();
//        txtDesctiption.clear();
        txtNote.clear();
        dateEventDate.setValue(null);
        dateEventDate.setPromptText("YYYY-MM-DD");
//        updateLevelMap("Bonus");
        dataTable.getSelectionModel().clearSelection();
    }

    @FXML
    void onCreate(ActionEvent event) {
        if (!isValidate()) {
            return;
        }

        SentenceDTO selectedValue = filterCombo.getValue();
        int prisonerId = selectedValue.getPrisonerId();
        int sentenceCode = selectedValue.getSentence().getSentenceCode();
        String sentenceId = String.valueOf(sentenceDao.getSentenceId(sentenceCode));
        String prisonerName = selectedValue.getPrisonerName();
        String eventType = cbEventType.getValue();
        LocalDate selectedStartDate = dateEventDate.getValue();
        String eventDate = selectedStartDate.toString();
        String level = cbLevel.getValue();
        String note = txtNote.getText();

        Integer levelValue = null;
        for (Map.Entry<Integer, String> entry : levelMap.entrySet()) {
            if (entry.getValue().equals(level)) {
                levelValue = entry.getKey();
            }
        }

        String processCode = getProcessCode();

        Assess assess = new Assess(processCode, sentenceId, sentenceCode, prisonerId, prisonerName, eventDate, eventType, levelValue, note);
        try {
            assessDao.addAssess(assess);
        } catch (RuntimeException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",e.getMessage());
            return;
        }
        listTable.add(assess);
        dataTable.setItems(listTable);

        AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success", "Assess created successfully.");
        ApplicationState appState = ApplicationState.getInstance();
        UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Created Assess code " + processCode);
        userlogDao.insertUserLog(userLog);

        onClean(event);
    }

    private String getProcessCode() {
        int code = assessDao.getCountAssess();
        code ++;
        return "P"+code;
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
            confirmationDialog.setHeaderText("Delete assess!");
            confirmationDialog.setContentText("Are you sure you want to delete it?");

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationDialog.getButtonTypes().setAll(okButton, cancelButton);

            Optional<ButtonType> result = confirmationDialog.showAndWait();

            if (result.isPresent() && result.get() == okButton) {
                Assess selected = dataTable.getSelectionModel().getSelectedItem();

                if (selected != null) {
                    try {
                        assessDao.deleteAssess(visitationId);
                    } catch (RuntimeException e) {
                        AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                                e.getMessage());
                        return;
                    }

                    listTable.remove(selected);
                    dataTable.setItems(listTable);
                    resetValue();
                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success",
                            "Assess deleted successfully.");

                    ApplicationState appState = ApplicationState.getInstance();
                    UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Deleted Assess has code " + selected.getProcessCode());
                    userlogDao.insertUserLog(userLog);
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
        SentenceDTO selectedValue = filterCombo.getValue();
        int prisonerId = selectedValue.getPrisonerId();
        int sentenceCode = selectedValue.getSentence().getSentenceCode();
        String sentenceId = String.valueOf(sentenceDao.getSentenceId(sentenceCode));
        String prisonerName = selectedValue.getPrisonerName();
        String eventType = cbEventType.getValue();
        LocalDate selectedStartDate = dateEventDate.getValue();
        String eventDate = selectedStartDate.toString();
        String level = cbLevel.getValue();
        String note = txtNote.getText();

        Integer levelValue = null;
        for (Map.Entry<Integer, String> entry : levelMap.entrySet()) {
            if (entry.getValue().equals(level)) {
                levelValue = entry.getKey();
            }
        }

        String processCode = getProcessCode();

        Assess assess = new Assess(processCode, sentenceId, sentenceCode, prisonerId, prisonerName, eventDate, eventType, levelValue, note);
        try {
            assessDao.updateAssess(assess, visitationId);
        } catch (RuntimeException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    e.getMessage());
            return;
        }

        index = dataTable.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            Assess s = listTable.get(index);
            s.setProcessCode(processCode);
            s.setSentencesCode(sentenceCode);
            s.setPrisonerName(prisonerName);
            s.setDateOfOccurrence(eventDate);
            s.setEventType(eventType);
            s.setLevel(levelValue);
            s.setNote(note);

            dataTable.setItems(listTable);
            dataTable.refresh();
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success",
                    "Assess updated successfully.");

            ApplicationState appState = ApplicationState.getInstance();
            UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Updated Assess has code " + processCode);
            userlogDao.insertUserLog(userLog);

            onClean(event);
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "No assess selected.");
        }
    }

    private void resetValue(){
        filterCombo.setValue(null);
        filterCombo.setPromptText("Select Sentence Code");
        cbEventType.getSelectionModel().select("Bonus");
        cbLevel.getSelectionModel().selectFirst();
//        txtDesctiption.clear();
        txtNote.clear();
        dateEventDate.setValue(null);
        dateEventDate.setPromptText("YYYY-MM-DD");
//        updateLevelMap("Bonus");
    }

    private void updateLevelMap(String eventType) {
        levelMap.clear();
        if ("Bonus".equals(eventType)) {
            levelMap.put(1, "MILD");
            levelMap.put(2, "MODERATE");
            levelMap.put(3, "GOOD");
            levelMap.put(4, "VERY GOOD");
        } else {
            levelMap.put(1, "MILD");
            levelMap.put(2, "MODERATE");
            levelMap.put(3, "SEVERE");
            levelMap.put(4, "EXTREMELY SEVERE");
        }
    }
    private String getDescription(String eventType, int level) {
        switch (eventType) {
            case "Bonus":
                switch (level) {
                    case 1:
                        return "MILD";
                    case 2:
                        return "MODERATE";
                    case 3:
                        return "GOOD";
                    case 4:
                        return "VERY GOOD";
                    default:
                        return "UNKNOWN";
                }
            case "Breach of discipline":
                switch (level) {
                    case 1:
                        return "MILD";
                    case 2:
                        return "MODERATE";
                    case 3:
                        return "SEVERE";
                    case 4:
                        return "EXTREMELY SEVERE";
                    default:
                        return "UNKNOWN";
                }
            default:
                return "UNKNOWN";
        }
    }
}