package com.example.psmsystem.controller;

import com.example.psmsystem.controller.userlog.UserLogController;
import com.example.psmsystem.model.crime.Crime;
import com.example.psmsystem.model.crime.ICrimeDao;
import com.example.psmsystem.model.managementvisit.IManagementVisitDao;
import com.example.psmsystem.model.managementvisit.ManagementVisit;
import com.example.psmsystem.model.prisoner.IPrisonerDao;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.model.sentence.ISentenceDao;
import com.example.psmsystem.model.sentence.Sentence;
import com.example.psmsystem.model.userlog.IUserLogDao;
import com.example.psmsystem.model.userlog.UserLog;
import com.example.psmsystem.service.assessDao.AssessDao;
import com.example.psmsystem.service.crimeDao.CrimeDao;
import com.example.psmsystem.service.healthDao.HealthDao;
import com.example.psmsystem.service.managementvisitDao.ManagementVisitDao;
import com.example.psmsystem.service.prisonerDAO.PrisonerDAO;
import com.example.psmsystem.service.sentenceDao.SentenceDao;
import com.example.psmsystem.service.userLogDao.UserLogDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private IPrisonerDao<Prisoner> prisonerDao;
    private IManagementVisitDao<ManagementVisit> managementVisitDao;
    private ICrimeDao<Crime> crimeDao;
    private ISentenceDao<Sentence> sentenceDao;
    private IUserLogDao userLogDao = new UserLogDao();

    @FXML
    private AreaChart<String, Number> areaChart;

    @FXML
    private BarChart<String, Number> barChartDiscipline;

    @FXML
    private BarChart<String, Number> barChartBonus;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private PieChart pieChart;

    @FXML
    private StackedAreaChart<Number, Number> stackedAreaChart;

    @FXML
    private Label txtCrime;

    @FXML
    private Label txtPrisoner;

    @FXML
    private Label txtVisitor;

    private static final int ITEMS_PER_PAGE = 10;

    private List<UserLog> userLogs;
    private int currentOffset = 0;

    @FXML
    private ScrollPane scrollPane;

    private VBox vBox;
    @FXML
    private ComboBox<Integer> fillCombobox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        prisonerDao = new PrisonerDAO();
        managementVisitDao = new ManagementVisitDao();
        sentenceDao = new SentenceDao();
        crimeDao = new CrimeDao();

        txtPrisoner.setText(String.valueOf(prisonerDao.getCountPrisoner()));
        txtVisitor.setText(String.valueOf(managementVisitDao.getCountManagementVisit()));
        txtCrime.setText(String.valueOf(crimeDao.getCountCrime()));

        areaChartController();
        barChartController();
        pieChartController();
        HealthDao healthDao = new HealthDao();
        List<Integer> years = healthDao.getListYear();

        fillCombobox.setItems(FXCollections.observableArrayList(years));
        fillCombobox.getSelectionModel().selectFirst(); // Select the first year by default

        fillCombobox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                stackedAreaChartController(newValue);
            }
        });

        // Call the stackedAreaChartController with the initially selected year
        stackedAreaChartController(fillCombobox.getValue());
//        userLogDao.selectAllUserLogs();
        loadDataUserLog();

//        List<UserLog> userLogs = userLogDao.selectAllUserLogs();
//        try {
//            loadUserLogs(userLogs);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void loadDataUserLog() {
        vBox = new VBox();
        scrollPane.setContent(vBox);
        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);


        // Initialize userLogs
        userLogs = userLogDao.selectAllUserLogs();; // You need to replace this with your actual method to load user logs

        loadInitialUserLogs();

        // Add scroll listener
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == scrollPane.getVmax()) {
                // Load more data when scrolled to the bottom
                Platform.runLater(this::loadMoreUserLogs);
            }
        });
    }

    private void loadInitialUserLogs() {
        // Load the first set of user logs
        List<UserLog> initialLogs = getNextUserLogs();
        try {
            loadUserLogs(initialLogs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMoreUserLogs() {
        // Load the next set of user logs
        List<UserLog> moreLogs = getNextUserLogs();
        if (!moreLogs.isEmpty()) {
            try {
                loadUserLogs(moreLogs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<UserLog> getNextUserLogs() {
        // This method should return the next set of user logs from your data source
        int nextOffset = currentOffset + ITEMS_PER_PAGE;
        List<UserLog> nextLogs = new ArrayList<>();
        if (currentOffset < userLogs.size()) { // Check if currentOffset is less than userLogs size
            if (nextOffset <= userLogs.size()) {
                nextLogs = userLogs.subList(currentOffset, nextOffset);
            } else {
                nextLogs = userLogs.subList(currentOffset, userLogs.size());
            }
            currentOffset = nextOffset;
        }

        return nextLogs;
    }

    private void loadUserLogs(List<UserLog> userLogs) throws IOException {
        for (UserLog userLog : userLogs) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/psmsystem/view/userlog/UserLogView.fxml"));
            AnchorPane itemUserLog = loader.load();
            UserLogController controller = loader.getController();
            controller.setUserLogData(userLog.getUserName(), userLog.getDateUpdate().toString(), userLog.getNote());
            vBox.getChildren().add(itemUserLog);
//            vBox.getStyleClass().add("scroll-vbox");

        }
    }

    public void areaChartController() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        barChartBonus.setTitle("Prisoner Bonus Chart");
        barChartBonus.getXAxis().setLabel("Prisoner");
        barChartBonus.getYAxis().setLabel("Count");

        AssessDao assessDao = new AssessDao();
        Map<String, Integer> bonusData = assessDao.getBonusData();

        XYChart.Series<String, Number> bonusSeries = new XYChart.Series<>();
        bonusSeries.setName("Bonus");
        bonusData.forEach((prisonerName, bonusCount) -> bonusSeries.getData().add(new XYChart.Data<>(prisonerName, bonusCount)));

        barChartBonus.getData().add(bonusSeries);
    }

    public void lineChartController() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        yAxis.setLabel("Number of Visits");

        lineChart.setTitle("Number of Visits per Month");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Visits");

        Map<String, Integer> visitsByMonth = managementVisitDao.countVisitsByMonth();
        for (Map.Entry<String, Integer> entry : visitsByMonth.entrySet()) {
            series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
        }
        lineChart.getData().add(series);
    }

    public void barChartController() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        barChartDiscipline.setTitle("Prisoner Discipline Chart");
        barChartDiscipline.getXAxis().setLabel("Prisoner");
        barChartDiscipline.getYAxis().setLabel("Count");

        AssessDao assessDao = new AssessDao();
        Map<String, Integer> breachData = assessDao.getBreachOfDisciplineData();

        XYChart.Series<String, Number> breachSeries = new XYChart.Series<>();
        breachSeries.setName("Breach of Discipline");
        breachData.forEach((prisonerName, breachCount) -> breachSeries.getData().add(new XYChart.Data<>(prisonerName, breachCount)));

        barChartDiscipline.getData().add(breachSeries);
    }

    public void pieChartController() {
        Map<String, Integer> genderCount = prisonerDao.countGender();

        pieChart.setTitle("Gender Prisoner Chart");
        for (Map.Entry<String, Integer> entry : genderCount.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
            pieChart.getData().add(slice);
        }
    }

    public void stackedAreaChartController(Integer year) {
        HealthDao healthDao = new HealthDao();
//        int year = LocalDate.now().getYear();
        Map<Integer, Integer> strongHealthData = healthDao.getStrongHealthDataByMonthYear(year);
        Map<Integer, Integer> weakHealthData = healthDao.getWeakHealthDataByMonthYear(year);

        NumberAxis xAxis = new NumberAxis(1, 12, 1);
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Count");
        stackedAreaChart.setTitle("Health Status by Month (" + year + ")");

        XYChart.Series<Number, Number> strongSeries = new XYChart.Series<>();
        strongSeries.setName("Strong");
        for (Map.Entry<Integer, Integer> entry : strongHealthData.entrySet()) {
            strongSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        XYChart.Series<Number, Number> weakSeries = new XYChart.Series<>();
        weakSeries.setName("Weak");
        for (Map.Entry<Integer, Integer> entry : weakHealthData.entrySet()) {
            weakSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        stackedAreaChart.getData().clear();
        stackedAreaChart.getData().addAll(strongSeries, weakSeries);
    }
}
