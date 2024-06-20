package com.example.psmsystem.controller.prisoner;

import com.example.psmsystem.controller.DataStorage;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.service.prisonerDAO.PrisonerDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PrisonerController implements Initializable {

    private final int itemsPerRow = 4;
    private final int rowsPerPage = 3;
    PrisonerDAO prisonerDAO = new PrisonerDAO();
    List<Prisoner> prisonerList;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnExport;

    @FXML
    private Button btnSearch;

    @FXML
    private Pagination pgPagination;

    @FXML
    private TextField txtSearchInput;

    private final String fxmlPath = "/com/example/psmsystem/";

    private int userId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DataStorage.setPrisonerController(this);
        prisonerList = prisonerDAO.getPrisonerInItem(); // default initialization
        setupPagination();
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) prisonerList.size() / (itemsPerRow * rowsPerPage));
        pgPagination.setPageCount(pageCount);
        pgPagination.setPageFactory(this::createPage);
//        String appDir = System.getProperty("user.dir");
//
//        System.out.println("Đường dẫn : " + appDir);
    }

    private VBox createPage(int pageIndex) {
        VBox pageBox = new VBox(30);
        int startIndex = pageIndex * itemsPerRow * rowsPerPage;
        int endIndex = Math.min(startIndex + itemsPerRow * rowsPerPage, prisonerList.size());

        for (int i = startIndex; i < endIndex; i += itemsPerRow) {
            HBox rowBox = new HBox(30);
            int rowEndIndex = Math.min(i + itemsPerRow, prisonerList.size());
            for (int j = i; j < rowEndIndex; j++) {
                Prisoner prisoner = prisonerList.get(j);
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath + "view/prisoner/" + "ItemPrisonerView.fxml"));
                    AnchorPane prisonerItem = fxmlLoader.load();
                    ItemPrisonerController controller = fxmlLoader.getController();
                    controller.setPrisonerController(this);
                    controller.setPrisonerItem(prisoner);
                    rowBox.getChildren().add(prisonerItem);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            pageBox.getChildren().add(rowBox);
        }
        return pageBox;
    }

    public void refreshPrisonerList(List<Prisoner> prisonerList) {
        this.prisonerList = prisonerList;
        setupPagination();
    }

    @FXML
    public void onSearch(MouseEvent event) {
        String searchText = txtSearchInput.getText();
        if (searchText.isEmpty()) {
            showAlert("Please enter a search term.");
            return;
        }

        if (isNumeric(searchText)) {
            if (Integer.parseInt(searchText) > prisonerList.size()) {
                showAlert("Prisoner with ID " + searchText + " not found.");
                return;
            }
            Prisoner prisoner = prisonerDAO.searchPrisonerById(searchText);
            if (prisoner != null) {
                prisonerList = List.of(prisoner);
            } else {
                showAlert("Prisoner with ID " + searchText + " not found.");
            }
        } else {
            prisonerList = prisonerDAO.searchPrisonersByName(searchText);
            if (prisonerList.isEmpty()) {
                showAlert("No prisoners matching " + searchText + " found.");
            }
        }
        setupPagination();
        txtSearchInput.clear();
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("[0-9]+");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void openAddWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath + "view/prisoner/AddPrisonerView.fxml"));
            AnchorPane newWindowContent = loader.load();

            AddPrisonerController addPrisonerController = loader.getController();
            addPrisonerController.setUserIdAdd(this.userId);
            addPrisonerController.setPrisonerController(this);
            System.out.println("User id add new : " + this.userId);

            Stage newStage = new Stage();
            Scene scene = new Scene(newWindowContent);
            newStage.setScene(scene);
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle("New Window");

            newStage.show();
        } catch (IOException e) {
            System.out.println("PrisonerController: " + e.getMessage());
        }
    }

    @FXML
    public  void getAllPrisoners(MouseEvent event) {
        PrisonerDAO prisonerDAO = new PrisonerDAO();
        this.prisonerList = prisonerDAO.getAllPrisonerInDb();
        refreshPrisonerList(this.prisonerList);
    }


    public void openFilterWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath + "view/prisoner/FilterView.fxml"));
            AnchorPane newWindowContent = loader.load();
            FilterController filterController = loader.getController();
            filterController.setPrisonerController(this);
            Stage newStage = new Stage();
            Scene scene = new Scene(newWindowContent);
            newStage.setScene(scene);
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle("New Window");

            newStage.show();
        } catch (IOException e) {
            System.out.println("PrisonerController - openFilterWindow: " + e.getMessage());
        }
    }

    public void openExportWindow() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Export");
        alert.setHeaderText(null);
        alert.setContentText("COMING SOON!");
        alert.showAndWait();
    }

    @FXML
    void loadAddPrisonerView(MouseEvent event) {
        openAddWindow();
    }

    @FXML
    void loadFilterView(MouseEvent event) {
        openFilterWindow();
    }

    @FXML
    void loadExportView(MouseEvent event) {
        openExportWindow();
    }
}
