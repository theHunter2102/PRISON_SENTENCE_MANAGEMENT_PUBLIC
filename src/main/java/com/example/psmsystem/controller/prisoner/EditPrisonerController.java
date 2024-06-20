package com.example.psmsystem.controller.prisoner;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.controller.DataStorage;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.model.userlog.IUserLogDao;
import com.example.psmsystem.model.userlog.UserLog;
import com.example.psmsystem.service.prisonerDAO.PrisonerDAO;
import com.example.psmsystem.service.userLogDao.UserLogDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class EditPrisonerController  implements Initializable {
    private IUserLogDao userlogDao = new UserLogDao();
    @FXML
    private Button btnAddImage;

    @FXML
    private Button btnUpdate;

    @FXML
    private DatePicker datePrisonerDOBAdd;

    @FXML
    private ImageView imgPrisonerAdd;

    @FXML
    private Label printTest;

    @FXML
    private TextField txtContactName;

    @FXML
    private TextField txtContactPhone;

    @FXML
    private TextField txtCrimes;

    @FXML
    private TextField txtPhysicalCondition;

    @FXML
    private TextField txtPrisonerFNAdd;

    @FXML
    private TextField txtIdentity;
    @FXML
    private Label lbPrisonerId;

    @FXML
    private Prisoner prisonerEdit;

    @FXML
    private CheckComboBox<String> ccbCrimes;

    @FXML
    private ToggleGroup tgGender;

    @FXML
    private RadioButton rbtnFemale;

    @FXML
    private RadioButton rbtnMale;

    @FXML
    private RadioButton rbtnOther;
    @FXML
    private ToggleGroup tgSentenceType;

    @FXML
    private RadioButton rbtnUnlimited;

    @FXML
    private RadioButton rbtnLimited;
    private boolean imageSelected;
    private String getRelativePath;
    private Prisoner prisoner;
    private PrisonerController prisonerController;

    public void setPrisonerEdit(Prisoner prisoner) {
        this.prisonerEdit = prisoner;
        setInformation();
    }

    public static boolean isPositiveInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        for (char ch : str.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return false;
            }
        }

        return true;
    }
    public void checkIdentityCard()
    {
        try {
            List<Prisoner> prisonerList;
            PrisonerDAO prisonerDAO = new PrisonerDAO();
            prisonerList = prisonerDAO.getAllPrisoner();
            boolean prisonerFound = false;
            String identityCard = txtIdentity.getText();
            if (identityCard.length() != 12 || !isPositiveInteger(identityCard)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Add Prisoner");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid identity card (12 integer only)!");
                alert.showAndWait();
                return;
            }

            for (Prisoner prisoner : prisonerList) {
                if (prisoner.getIdentityCard().equals(txtIdentity.getText())) {
                    prisonerFound = true;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Add Prisoner");
                    alert.setHeaderText("INFORMATION");
                    alert.setContentText("Prisoner already exists");
                    alert.showAndWait();
                    String defaultPath = "/com/example/psmsystem/assets/imagesPrisoner/default.png";
                    String id = prisoner.getPrisonerCode();
                    String name = prisoner.getPrisonerName();
                    String DOB = prisoner.getDOB();
                    int gender = prisoner.getGender();
                    String contactName = prisoner.getContactName();
                    String contactPhone = prisoner.getContactPhone();
                    String imagePath = prisoner.getImagePath();
                    lbPrisonerId.setText(id);
                    txtPrisonerFNAdd.setText(name);
                    datePrisonerDOBAdd.setValue(LocalDate.parse(DOB));
                    if (gender == 1) {
                        rbtnMale.setSelected(true);
                    }
                    else if (gender == 2) {
                        rbtnFemale.setSelected(true);
                    }else
                    {
                        rbtnOther.setSelected(true);
                    }
                    txtContactName.setText(contactName);
                    txtContactPhone.setText(contactPhone);
                    File imageFile;
                    if (imagePath != null && !imagePath.isEmpty()) {
                        imageFile = new File(imagePath);
                    } else {
                        imageFile = new File(defaultPath);
                    }
                    Image image = new Image(imageFile.toURI().toString());
                    imgPrisonerAdd.setImage(image);
                    break;
                }

            }
            if (!prisonerFound) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Add Prisoner");
                alert.setHeaderText("INFORMATION");
                alert.setContentText("Prisoner not already exists");
                alert.showAndWait();
            }
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }


    public void setInformation() {
        try {
            String defaultPath = "/com/example/psmsystem/assets/imagesPrisoner/default.png";
            if (prisonerEdit != null) {
                String id = prisonerEdit.getPrisonerCode();
                List<Prisoner> prisonerList;
                PrisonerDAO prisonerDAO = new PrisonerDAO();
                prisonerList = prisonerDAO.getAllPrisoner();
                for (Prisoner prisoner : prisonerList) {
                        if (prisoner.getPrisonerCode().equals(id)) {
                            String identity = prisoner.getIdentityCard();
                            String name = prisoner.getPrisonerName();
                            String DOB = prisoner.getDOB();
                            int gender = prisoner.getGender();
                            String contactName = prisoner.getContactName();
                            String contactPhone = prisoner.getContactPhone();
                            String imagePath = prisoner.getImagePath();
                            txtIdentity.setText(identity);
                            lbPrisonerId.setText(id);
                            txtPrisonerFNAdd.setText(name);
                            datePrisonerDOBAdd.setValue(LocalDate.parse(DOB));
                            if (gender == 1) {
                                rbtnMale.setSelected(true);
                            } else if (gender == 2) {
                                rbtnFemale.setSelected(true);
                            } else {
                                rbtnOther.setSelected(true);
                            }
                            txtContactName.setText(contactName);
                            txtContactPhone.setText(contactPhone);
                            File imageFile;
                            if (imagePath != null && !imagePath.isEmpty()) {
                                imageFile = new File(imagePath);
                            } else {
                                imageFile = new File(defaultPath);
                            }
                            Image image = new Image(imageFile.toURI().toString());
                            imgPrisonerAdd.setImage(image);
                            lbPrisonerId.setVisible(false);
                            this.getRelativePath = imagePath;
                        }
                }
            }
        }catch (Exception e)
        {
            System.out.println("Edit prisoner - setInformation: " + e.getMessage() );
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

    public interface Callback {
        void execute();
    }

    public void back(ActionEvent event, AddPrisonerController.Callback callback)  {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
        System.out.println("Cửa sổ đã được đóng");
        if (callback != null) {
            System.out.println("Thực hiện callback");
            callback.execute();
        }
    }

    public String selectImageFile() {
        if (!imageSelected) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image", "*.png", "*.jpg", "*.gif"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());

            if (selectedFile != null) {
                // Lấy tên tệp từ đường dẫn ban đầu
                String fileName = selectedFile.getName();

                String destinationFolderPath = "src/main/resources/com/example/psmsystem/imagesPrisoner/";

                String relativePath = destinationFolderPath + fileName;
                File destFile = new File(relativePath);

                try (InputStream inputStream = new FileInputStream(selectedFile)) {
                    Files.copy(inputStream, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("Tệp đã chọn: " + relativePath);
                Image image = new Image(selectedFile.toURI().toString());
                imgPrisonerAdd.setImage(image);
                imageSelected = true;
                this.getRelativePath = relativePath;
                return relativePath;
            } else {
                System.out.println("Không có tệp nào được chọn.");
            }
        }
        return null;
    }


    public boolean getPrisoner()
    {
//         public Prisoner(String prisonerCode, String prisonerName, String DOB, int gender, String identityCard, String contactName, String contactPhone, String imagePath, boolean status, int user_id) {

        String identityCard = txtIdentity.getText();
        String id = prisonerEdit.getPrisonerCode();
        String name = txtPrisonerFNAdd.getText();
        RadioButton genderRadio = (RadioButton) tgGender.getSelectedToggle();
        String gender = genderRadio.getText();
        String contactName = txtContactName.getText();
        String contactPhone = txtContactPhone.getText();
        LocalDate DOB = datePrisonerDOBAdd.getValue();
        Date dob = Date.valueOf(DOB);
        int genderInputDb;
        if (gender.equals("Male")) {
            genderInputDb = 1;
        } else if (gender.equals("Female")) {
            genderInputDb = 2;
        } else {
            genderInputDb = 3;
        }
        boolean allZeros = true;
        for (char ch : identityCard.toCharArray()) {
            if (ch != '0') {
                allZeros = false;
                break;
            }
        }
        if (!(identityCard.matches("\\d{12}")) || allZeros ) {
            showAlert("Invalid identity card");
            return false;
        }

        if (!name.matches("[\\p{L}]+(\\s+[\\p{L}]+)*"
        )) {
            showAlert("Invalid prisoner name");
            return false;
        }

        if (!contactName.matches("[\\p{L}]+(\\s+[\\p{L}]+)*"
        )) {
            showAlert("Invalid contact name");
            return false;
        }
        if (!contactPhone.matches("^0[0-9]{9}$")) {
            showAlert("Invalid contact phone");
            return false;
        }
        Prisoner prisoner = new Prisoner();
        prisoner.setPrisonerCode(id);
        prisoner.setPrisonerName(name);
        prisoner.setDOB(String.valueOf(dob));
        prisoner.setContactName(contactName);
        prisoner.setContactPhone(contactPhone);
        prisoner.setGender(genderInputDb);
        prisoner.setImagePath(getRelativePath);
        prisoner.setIdentityCard(identityCard);
        this.prisoner = prisoner;
        return true;
    }

    public void updatePrisoner(ActionEvent actionEvent) {
        if (getPrisoner()) {
            PrisonerDAO prisonerDAO = new PrisonerDAO();
            try {
                prisonerDAO.updatePrisoner(this.prisoner);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Update successfully!");

                ApplicationState appState = ApplicationState.getInstance();
                UserLog userLog = new UserLog(appState.getId(), appState.getUsername(), LocalDateTime.now(), "Updated Prisoner code " + this.prisoner.getPrisonerCode());
                userlogDao.insertUserLog(userLog);

                alert.showAndWait();
                List<Prisoner> prisonerList = prisonerDAO.getPrisonerInItem();
                back(actionEvent, () -> prisonerController.refreshPrisonerList(prisonerList));
            } catch (RuntimeException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Update fail!");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.prisonerController = DataStorage.getPrisonerController();
        System.out.println("prisoner controller: " + prisonerController);
        tgGender = new ToggleGroup();
        tgSentenceType = new ToggleGroup();
        rbtnMale.setToggleGroup(tgGender);
        rbtnFemale.setToggleGroup(tgGender);
        rbtnOther.setToggleGroup(tgGender);
        setInformation();
    }
    public void back(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
}
