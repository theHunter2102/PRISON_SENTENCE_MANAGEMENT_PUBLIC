package com.example.psmsystem.controller;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.controller.prisoner.AddPrisonerController;
import com.example.psmsystem.controller.prisoner.PrisonerController;
import com.example.psmsystem.model.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.MouseEvent;


public class MainPanelController implements Initializable {
    @FXML
    private Button btnClose;
    @FXML
    private Label assess;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Label dashboard;

    @FXML
    private Label prisoner;

    @FXML
    private Label health;

    @FXML
    private Label idLogin;

    @FXML
    private Label logout;

    @FXML
    private Label manageVisits;

    @FXML
    private  Label nameView;

    @FXML
    private Label report;

    @FXML
    private Label sentence;

    @FXML
    private AnchorPane centerPane;
    String fxmlPath = "/com/example/psmsystem/";
    private int userId;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void initData(User user) {
        idLogin.setText(user.getFullName());
        this.userId = user.getUserId();
        ApplicationState appState = ApplicationState.getInstance();
        appState.setUsername(user.getFullName());
        appState.setId(user.getUserId());

        loadFXML("Dashboard");
    }
    private void loadFXML(String fileName) {
        try {
            nameView.setText(fileName);
            String pathFileName;
            if (fileName.equals("Dashboard")){
                pathFileName = fxmlPath + "view/" + fileName + "View.fxml";
            }else{
                pathFileName = fxmlPath + "view/" + fileName.toLowerCase() + "/" + fileName + "View.fxml";
            }
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(pathFileName)));
            Parent root = loader.load();

            if (loader.getController() instanceof PrisonerController) {
                PrisonerController prisonerController = loader.getController();
                prisonerController.setUserId(this.userId);
            }
            centerPane.getChildren().clear(); // Xóa các node con hiện có
            centerPane.getChildren().add(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            dashboard.getScene().getWindow();


        } catch (IOException ex) {
            Logger.getLogger(MainPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MainPanelController() {
    }

    @FXML
    void close(MouseEvent  event) throws IOException {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.close();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath + "view/LoginView.fxml")));

        Scene scene = new Scene(root);
        stage.setMaximized(false);
        stage.setScene(scene);
        stage.setTitle("User Login");
        stage.getIcons().add(new Image("file: " + fxmlPath + "assets/icon.png"));
        stage.show();
    }

    @FXML
    void loadDashboardView(MouseEvent  event) {
        loadFXML("Dashboard");
        setButtonStyle(dashboard);
    }

    @FXML
    void loadAssessView(MouseEvent event) {
        loadFXML("Assess");
        setButtonStyle(assess);
    }

    @FXML
    void loadHealthView(MouseEvent event) {
        loadFXML("Health");
        setButtonStyle(health);
    }

    @FXML
    void loadManageVisitsView(MouseEvent event) {
        loadFXML("ManagementVisit");
        setButtonStyle(manageVisits);
    }

    @FXML
    void loadReportView(MouseEvent event) {
        loadFXML("Report");
        setButtonStyle(report);
    }

    @FXML
    void loadPrisonerView(MouseEvent event) {
        loadFXML("Prisoner");
        setButtonStyle(prisoner);
    }

    @FXML
    void loadSentenceView(MouseEvent event) {
        loadFXML("Sentence");
        setButtonStyle(sentence);
    }

    private void setButtonStyle(Label label) {
        // Xóa kiểu đã chọn từ tất cả các nút
        dashboard.getStyleClass().remove("selected");
        prisoner.getStyleClass().remove("selected");
        assess.getStyleClass().remove("selected");
        manageVisits.getStyleClass().remove("selected");
        report.getStyleClass().remove("selected");
        health.getStyleClass().remove("selected");
        sentence.getStyleClass().remove("selected");
        label.getStyleClass().add("selected");
    }

    public void onBtnCloseClick(ActionEvent event) {
        try{
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        }catch(Exception e){
            System.out.println("MainPanel - close : " + e.getMessage());
        }

    }
}
