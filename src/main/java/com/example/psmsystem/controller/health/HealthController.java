package com.example.psmsystem.controller.health;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.dto.SentenceDTO;
import com.example.psmsystem.helper.AlertHelper;
import com.example.psmsystem.model.health.Health;
import com.example.psmsystem.model.health.IHealthDao;
import com.example.psmsystem.model.prisoner.IPrisonerDao;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.model.sentence.ISentenceDao;
import com.example.psmsystem.model.sentence.Sentence;
import com.example.psmsystem.model.sentence.SentenceServiceImpl;
import com.example.psmsystem.model.userlog.IUserLogDao;
import com.example.psmsystem.model.userlog.UserLog;
import com.example.psmsystem.service.healthDao.HealthDao;
import com.example.psmsystem.service.prisonerDAO.PrisonerDAO;
import com.example.psmsystem.service.sentenceDao.SentenceDao;
import com.example.psmsystem.service.sentenceDao.SentenceService;
import com.example.psmsystem.service.userLogDao.UserLogDao;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class HealthController implements Initializable {
    private static IPrisonerDao<Prisoner> prisonerDao;
    private static IHealthDao<Health> healthDao;
    private ISentenceDao<Sentence> sentenceDao;
    private SentenceServiceImpl<SentenceDTO> sentenceService = new SentenceService();
    private IUserLogDao userlogDao = new UserLogDao();

    @FXML
    private TableColumn<Health, String> checkupDateColumn;

    @FXML
    private TableView<Health> dataTable;

    @FXML
    private DatePicker dateCheckupDate;

    @FXML
    private ComboBox<String> cbLevel;

    @FXML
    private SearchableComboBox<SentenceDTO> filterCombo;

    @FXML
    private TableColumn<Health, Double> heightColumn;

    @FXML
    private Pagination pagination;

    @FXML
    private TableColumn<Health, Integer> levelColumn;

    @FXML
    private TableColumn<Health, Integer> prisonercodeColumn;

    @FXML
    private TableColumn<Health, String> healthcodeColumn;

    @FXML
    private TableColumn<Health, String> prisonernameColumn;

    @FXML
    private TableColumn<Health, Boolean> statusColumn;
    @FXML
    private TableColumn<Health, Integer> idColumn;

    @FXML
    private TextField txtHeight;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtWeight;

    @FXML
    private TableColumn<Health, Double> weightColumn;

    private final int itemsPerPage = 20;

    @FXML
    private Button createId;

    Integer index;
    private int healthId;
    Window window;
    private final Map<Integer, String> levelMap = new HashMap<>();
    ObservableList<Health> listTable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prisonerDao = new PrisonerDAO();
        healthDao = new HealthDao();
        sentenceDao = new SentenceDao();

        listTable.addAll(healthDao.getHealth());
        dataTable.setFixedCellSize(37);

        StringConverter<SentenceDTO> converter = FunctionalStringConverter.to(sentence -> (sentence == null) ? "" : sentence.getSentence().getSentenceCode() + ": " + sentence.getPrisonerName());
        filterCombo.setItems(sentenceService.getPrisonerName());
        filterCombo.setConverter(converter);

        cbLevel.setItems(FXCollections.observableArrayList("Strong", "mild", "severe", "requires intervention"));

        cbLevel.getSelectionModel().select("Strong");

        levelMap.put(0, "Strong");
        levelMap.put(1, "mild");
        levelMap.put(2, "severe");
        levelMap.put(3, "requires intervention");

        createId.disableProperty().bind(
                dataTable.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(0)
        );

        loadDataTable();
        setupPagination();
        initUI();
        setupSearch();
    }

    @FXML
    void getItem(MouseEvent event) {
        index = dataTable.getSelectionModel().getSelectedIndex();
        if(index <= -1){
            return;
        }
        // Lấy ID từ cột ẩn
        healthId = Integer.parseInt(idColumn.getCellData(index).toString());

        for (SentenceDTO sentence : filterCombo.getItems()) {
            if (sentence.getSentence().getSentenceCode() == (prisonercodeColumn.getCellData(index))) {
                filterCombo.setValue(sentence);
                break;
            }
        }
        SentenceDTO selectedValue = filterCombo.getValue();
//        String prisonerId = selectedValue.getPrisonerId();
        txtWeight.setText(weightColumn.getCellData(index).toString());
        txtHeight.setText(heightColumn.getCellData(index).toString());
        LocalDate date = LocalDate.parse(checkupDateColumn.getCellData(index).toString());
        dateCheckupDate.setValue(date);
        LocalDate selectedDate = dateCheckupDate.getValue();
        String dateString = selectedDate.toString();
        cbLevel.setValue(levelColumn.getCellData(index).toString());
        String healthCode = healthcodeColumn.getCellData(index).toString();

        Integer levelDescription = Integer.parseInt(levelColumn.getCellData(index).toString());

        String levelValue = levelMap.get(levelDescription);
        if (levelValue != null) {
            cbLevel.setValue(levelValue);
        } else {
            System.out.println("Level description not found in map.");
        }

    }

    private void resetValue(){
        filterCombo.setValue(null);
        filterCombo.setPromptText("Select Sentence Code");
        txtWeight.clear();
        txtHeight.clear();
        dateCheckupDate.setValue(null);
        dateCheckupDate.setPromptText("YYYY-MM-DD");
        cbLevel.getSelectionModel().select("Strong");
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
        dateCheckupDate.setConverter(converter);
    }

    private void loadDataTable() {
        idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setVisible(false);
        dataTable.getColumns().add(idColumn);
        healthcodeColumn.setCellValueFactory(new PropertyValueFactory<>("healthCode"));
        prisonercodeColumn.setCellValueFactory(new PropertyValueFactory<>("sentenceCode"));
        prisonernameColumn.setCellValueFactory(new PropertyValueFactory<>("prisonerName"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        checkupDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkupDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        statusColumn.setCellFactory(col -> new TableCell<Health, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "sick" : "healthy");
                }
            }
        });
        levelColumn.setCellFactory(col -> new TableCell<Health, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    switch (item) {
                        case 0:
                            setText("Strong");
                            break;
                        case 1:
                            setText("mild");
                            break;
                        case 2:
                            setText("severe");
                            break;
                        default:
                            setText("requires intervention");
                            break;
                    }
                }
            }
        });

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
        FilteredList<Health> filteredData = new FilteredList<>(listTable, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(health -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (health.getHealthCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(health.getSentenceCode()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (health.getPrisonerName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(health.getWeight()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (String.valueOf(health.getHeight()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (health.getCheckupDate().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                else if (lowerCaseFilter.contains("healthy") && !health.getStatus()) {
                    return true; // Tìm kiếm theo status: healthy
                }
                else if (lowerCaseFilter.contains("sick") && health.getStatus()) {
                    return true; // Tìm kiếm theo status: sick
                }
                else {
                    Integer levelValue = health.getLevel();
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

    private void updatePagination(FilteredList<Health> filteredData) {
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
        try {
            Double weight = Double.parseDouble(txtWeight.getText());
            Double height = Double.parseDouble(txtHeight.getText())/100;
            if (weight < 20 || weight > 250) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Minimum weight 20kg and maximum 250kg");
                txtWeight.requestFocus();
                return false;
            }
            if(height < 0.5 || height > 2.5) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Minimum height is 50cm and maximum is 250cm");
                txtHeight.requestFocus();
                return false;
            }
        } catch (Exception e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Please enter number for full weight and height");
            txtWeight.requestFocus();
            return false;
        }
        if (dateCheckupDate.getValue() == null || dateCheckupDate.getValue().isAfter(LocalDate.now())) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", "Check up Date can't after to day");
            dateCheckupDate.requestFocus();
            return false;
        }
        return true;
    }

    @FXML
    void onClean(ActionEvent event) {
        filterCombo.setValue(null);
        filterCombo.setPromptText("Select Sentence Code");
        txtWeight.clear();
        txtHeight.clear();
        dateCheckupDate.setValue(null);
        dateCheckupDate.setPromptText("YYYY-MM-DD");
        cbLevel.getSelectionModel().select("Strong");;

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
        Double weight = Double.valueOf(txtWeight.getText());
        Double height = Double.parseDouble(txtHeight.getText())/100;
        boolean status = cbLevel.getSelectionModel().getSelectedIndex() == 0 ? false : true;
        String level = cbLevel.getValue();

        LocalDate selectedDate = dateCheckupDate.getValue();
        String date = selectedDate.toString();

        Integer levelValue = null;
        for (Map.Entry<Integer, String> entry : levelMap.entrySet()) {
            if (entry.getValue().equals(level)) {
                levelValue = entry.getKey();
            }
        }
        String healthCode = getHealthCode();

        Health health = new Health(0,healthCode, prisonerId, sentenceId, sentenceCode, prisonerName, weight, height, date, status, levelValue);
        try {
            healthDao.addHealth(health);
        } catch (RuntimeException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    e.getMessage());
            return;
        }
        listTable.add(health);
        dataTable.setItems(listTable);

        AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success", "Health created successfully.");

        ApplicationState appState = ApplicationState.getInstance();
        UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Created Health code " + healthCode);
        userlogDao.insertUserLog(userLog);

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

            LocalDate selectedDate = dateCheckupDate.getValue();
            if (selectedDate == null) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                        "Please select a Check-up date.");
                return;
            }

            if (healthId < 1) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                        "Health not found.");
                return;
            }

            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirmation");
            confirmationDialog.setHeaderText("Delete health!");
            confirmationDialog.setContentText("Are you sure you want to delete it?");

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationDialog.getButtonTypes().setAll(okButton, cancelButton);

            Optional<ButtonType> result = confirmationDialog.showAndWait();

            if (result.isPresent() && result.get() == okButton) {
                try {
                    healthDao.deleteHealth(healthId);
                    Health selected = dataTable.getSelectionModel().getSelectedItem();
                    listTable.remove(selected);
                    dataTable.setItems(listTable);
                    resetValue();
                    AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success",
                            "Health deleted successfully.");
                    ApplicationState appState = ApplicationState.getInstance();
                    UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Deleted Health code " + selected.getHealthCode());
                    userlogDao.insertUserLog(userLog);
                } catch (RuntimeException e) {
                    AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                            "An error occurred while deleting the health.");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "An error occurred while deleting the health.");
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
        Double weight = Double.valueOf(txtWeight.getText());
        Double height = Double.valueOf(txtHeight.getText());
        String level = cbLevel.getValue();

        LocalDate selectedDate = dateCheckupDate.getValue();
        String date = selectedDate.toString();

        Integer levelValue = null;
        for (Map.Entry<Integer, String> entry : levelMap.entrySet()) {
            if (entry.getValue().equals(level)) {
                levelValue = entry.getKey();
            }
        }

        if (healthId < 1) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "No visit found.");
            return;
        }
        index = dataTable.getSelectionModel().getSelectedIndex();

        Health selectedHealth = dataTable.getItems().get(index);
        String hearthCode = healthcodeColumn.getCellData(selectedHealth);
        boolean status = levelValue > 0 ? true : false;
        Health mv = new Health(healthId,hearthCode, prisonerId, sentenceId, sentenceCode, prisonerName, weight, height, date, status, levelValue);
        try {
            healthDao.updateHealth(mv, healthId);
        } catch (RuntimeException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    e.getMessage());
            return;
        }


        if (index >= 0) {
            Health health = listTable.get(index);
            health.setPrisonerId(prisonerId);
            health.setSentenceId(sentenceId);
            health.setSentenceCode(sentenceCode);
            health.setPrisonerName(prisonerName);
            health.setWeight(weight);
            health.setHeight(height);
            health.setCheckupDate(date);
            health.setStatus(status);
            health.setLevel(levelValue);

            dataTable.setItems(listTable);
            dataTable.refresh();
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, window, "Success",
                    "Health updated successfully.");

            ApplicationState appState = ApplicationState.getInstance();
            UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Updated Health code " + hearthCode);
            userlogDao.insertUserLog(userLog);

            onClean(event);
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "No health selected.");
        }
    }

    private String getHealthCode(){
        int code = healthDao.getCountHealth();
        code ++;
        return "H"+code;
    }
}
