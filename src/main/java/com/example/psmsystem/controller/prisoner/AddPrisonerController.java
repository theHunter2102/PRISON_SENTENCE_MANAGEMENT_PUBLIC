package com.example.psmsystem.controller.prisoner;
import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.controller.DataStorage;
import com.example.psmsystem.model.crime.Crime;
import com.example.psmsystem.model.prisoner.Prisoner;
import com.example.psmsystem.model.sentence.Sentence;
import com.example.psmsystem.model.userlog.IUserLogDao;
import com.example.psmsystem.model.userlog.UserLog;
import com.example.psmsystem.service.crimeDao.CrimeDao;
import com.example.psmsystem.service.prisonerDAO.PrisonerDAO;
import com.example.psmsystem.service.sentenceDao.SentenceDao;
import com.example.psmsystem.service.userLogDao.UserLogDao;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.CheckComboBox;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;

public class AddPrisonerController implements Initializable {
    private IUserLogDao userlogDao = new UserLogDao();
    @FXML
    private TextField txtIdentityCard;
    @FXML
    private TextField txtContactName;

    @FXML
    private AnchorPane anchorPaneAddPrisoner;

    @FXML
    private Button btnAddImage;

    @FXML
    private Button btnAddPrisonerFinal;

    @FXML
    private ImageView imgPrisonerAdd;

    @FXML
    private TextField txtContactPhone;

    @FXML
    private DatePicker datePrisonerDOBAdd;

    @FXML
    private TextField txtPrisonerFNAdd;

    @FXML
    private ToggleGroup tgGender;

    @FXML
    private RadioButton rbtnFemale;

    @FXML
    private RadioButton rbtnMale;

    @FXML
    private RadioButton rbtnOther;

    @FXML
    private Label lbPrisonerId;

    @FXML
    private CheckComboBox<String> ccbCrimes;

    @FXML
    private ToggleGroup tgSentenceType;

    @FXML
    private RadioButton rbtnUnlimited;

    @FXML
    private RadioButton rbtnLimited;
    @FXML
    private Label lbSentenceId;

    @FXML
    private Button btnShowYearInput;
    @FXML
    private DatePicker dateIn;
    @FXML
    private DatePicker dateOut;
    @FXML
    private Button btnCheckIdentity;

    @FXML
    private Button btnEndDate;

    private String getRelativePath;
    private int userId;
    private PrisonerController prisonerController;
    private List<Integer> selectedCrimesId;
    private boolean imageSelected = false;
    private int sentenceId;
    private Prisoner prisoner;
    private Sentence sentence;
    private int prisonerId;
    private boolean checkIdentity = false;


public void setBtnAddPrisonerFinal(ActionEvent event) {
    if (imgPrisonerAdd.getImage() == null) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Prisoner");
        alert.setHeaderText(null);
        alert.setContentText("Please select a prisoner image");
        alert.showAndWait();
        return;
    }
    if (getPrisoner()) {
        if (getSentence()) {
            //get date startDate and dOB
            LocalDate startDate = this.sentence.getStartDate().toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dOB = LocalDate.parse(this.prisoner.getDOB(),formatter);
            //if start Date before 18year dOb -> error message
            if(startDate.isBefore(dOB.plusYears(18))) {
                showAlert("Court sentence start date cannot be set before the prisoner turns 18 years old.");
                return;
            }
            SentenceDao sentenceDao = new SentenceDao();
            PrisonerDAO prisonerDao = new PrisonerDAO();
            if (!checkIdentity) {
                System.out.print("insert All");

                //add prisoner
                try {
                    prisonerDao.insertPrisonerDB(prisoner);
                    //add history
                    userlogDao.insertUserLog(new UserLog(ApplicationState.getInstance().getId(), ApplicationState.getInstance().getUsername(), LocalDateTime.now(), "Created Prisoner code: " + prisoner.getPrisonerCode()));
                } catch (RuntimeException e) {
                    showAlert(e.getMessage());
                }
                //add sentence
                try {
                    sentenceDao.addSentence(this.sentence);
                    userlogDao.insertUserLog(new UserLog(ApplicationState.getInstance().getId(), ApplicationState.getInstance().getUsername(), LocalDateTime.now(), "Created Sentence code " + this.sentence.getSentenceCode()));
                    Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                    alert1.setHeaderText("Add new information");
                    alert1.setContentText("Add prisoner success!");
                    alert1.showAndWait();
                    List<Prisoner> prisonerList = prisonerDao.getPrisonerInItem();
                    back(event, () -> prisonerController.refreshPrisonerList(prisonerList));
                } catch (RuntimeException e) {
                    showAlert(e.getMessage());
                }
            }
            else
            {
                try {
                    sentenceDao.addSentence(this.sentence);
                    System.out.print("insert Sentence");
                    Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                    alert1.setHeaderText("Add new information");
                    alert1.setContentText("Add Sentence success!");
                    alert1.showAndWait();
                } catch (RuntimeException e) {
                    showAlert(e.getMessage());
                }

                List<Prisoner> prisonerList = prisonerDao.getPrisonerInItem();
                back(event, () -> prisonerController.refreshPrisonerList(prisonerList));
            }

        }
    }
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
            String identityCard = txtIdentityCard.getText();
            if (identityCard.length() != 12 || !isPositiveInteger(identityCard)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Add Prisoner");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid identity card (12 integer only)!");
                alert.showAndWait();
                return;
            }

            for (Prisoner prisoner : prisonerList) {
                if (prisoner.getIdentityCard().equals(txtIdentityCard.getText())) {
                    prisonerFound = true;
                    this.checkIdentity = true;
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
                    this.prisonerId = Integer.parseInt(id);
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
//                    lbPrisonerId.setVisible(false);
                    getRelativePath=imagePath;
                     break;
                }
            }
            if (!prisonerFound) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Add Prisoner");
                alert.setHeaderText("INFORMATION");
                alert.setContentText("Prisoner not already exists");
                alert.showAndWait();
                setPrisonerId();
            }
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    public void setPrisonerController(PrisonerController prisonerController) {
        this.prisonerController = prisonerController;
    }

    public static Date convertToDate(LocalDate localDate) {
        return Date.valueOf(localDate);
    }
    public Map<Integer,Integer> getTimesOfCrimes()
    {
        return DataStorage.getCrimesTime();
    }

    public int calculateTotal(Map<Integer, Integer> map) {
        int total = 0;
        for (int value : map.values()) {
            total += value;
        }
        return total;
    }

    public void setEndDate()
    {
        try {
            RadioButton selectedSentenceType = (RadioButton) tgSentenceType.getSelectedToggle();
            String sentenceTypeText = selectedSentenceType.getText();
            // Lấy giá trị ban đầu của DatePicker
            int totalTime = 0;
            if (sentenceTypeText.equals("Life imprisonment")) {
                LocalDate startDay = dateIn.getValue();
                LocalDate unlimitedDate = startDay.plusYears(130);
                dateOut.setValue(unlimitedDate);
            } else if (sentenceTypeText.equals("limited time")) {
                if (getTimesOfCrimes() != null) {
                    totalTime = calculateTotal(getTimesOfCrimes());
                }
                LocalDate startDay = dateIn.getValue();
                LocalDate unlimitedDate = startDay.plusMonths(totalTime);
                dateOut.setValue(unlimitedDate);
                System.out.println("Set end date : " + dateOut.getValue());
            }
        }catch (Exception e)
        {
            System.out.println("AddPrisonerController - setEndDate: " + e.getMessage());
        }
}


    public String getCrimeCode() {

            CrimeDao crimeDao = new CrimeDao();
            List<Crime> allCrimes = crimeDao.getCrime();

            Map<Integer, Integer> crimesTimeMap = DataStorage.getCrimesTime();

            // Tạo đối tượng StringBuilder để xây dựng chuỗi kết quả
            StringBuilder resultBuilder = new StringBuilder();

            for (Map.Entry<Integer, Integer> entry : crimesTimeMap.entrySet()) {
                int crimeId = entry.getKey();
                for (Crime crime : allCrimes) {
                    if (crime.getCrimeId() == crimeId) {
                        // Nếu đây không phải là tội phạm đầu tiên trong chuỗi kết quả
                        if (resultBuilder.length() > 0) {
                            resultBuilder.append(", "); // Thêm dấu phẩy và khoảng trắng
                        }
                        resultBuilder.append(crime.getCrimeName()); // Thêm tên tội phạm vào chuỗi kết quả
                        break;
                    }
                }
            }
            return resultBuilder.toString();
        }

    public boolean getSentence() {
    try {
        RadioButton selectedSentenceType = (RadioButton) tgSentenceType.getSelectedToggle();
        if (selectedSentenceType == null) {
            showAlert("Sentence type must be selected");
            return false;
        }
        String sentenceTypeText = selectedSentenceType.getText();
        System.out.println("Sentence type: " + sentenceTypeText);
        LocalDate dateInput = dateIn.getValue();

        // Chọn "life"
        if (sentenceTypeText.equals("Life imprisonment")) {
            if (dateInput == null || dateInput.isAfter(LocalDate.now())) {
                showAlert("Invalid start date");
                return false;
            }

            // Thiết lập giá trị cho dateOut
            Date startDate = convertToDate(dateInput);
            this.sentence = new Sentence(prisonerId, txtPrisonerFNAdd.getText(), Integer.parseInt(lbSentenceId.getText()), sentenceTypeText, "", startDate, null, null, false, "");
        }
        else if (sentenceTypeText.equals("limited time")) {
            if (dateInput == null || dateInput.isAfter(LocalDate.now())) {
                showAlert("Invalid start date");
                return false;
            }
            String crimeCode = getCrimeCode();
            if(crimeCode.isBlank())  {
                showAlert("Select crime and input times");
                return false;
            }
            if (getTimesOfCrimes().isEmpty()) {
                showAlert("Select crime and input times");
                return false;
            }
            Date startDate = convertToDate(dateInput);
            Date endDate = Date.valueOf(dateOut.getValue());
            this.sentence = new Sentence(prisonerId, txtPrisonerFNAdd.getText(), Integer.parseInt(lbSentenceId.getText()), sentenceTypeText, crimeCode, startDate, endDate, null, false, "");
        }
        return true;
    } catch (Exception e) {
        System.out.println("getSentence - addPrisonerController : " + e.getMessage());
        showAlert("An error in system. Please try again in a few minutes.");
    }
    return false;
}


    public void setIdSentence()
    {
        SentenceDao sentenceDao = new SentenceDao();
        int sentenceIdShow = sentenceDao.getMaxIdSentence();
        this.sentenceId = sentenceIdShow;
        lbSentenceId.setText(String.valueOf(sentenceIdShow));
    }
    public void setPrisonerId()
    {
        PrisonerDAO prisonerDAO = new PrisonerDAO();
        int prisonerIdDB = prisonerDAO.getIdEmpty();
        this.prisonerId = prisonerIdDB;
        if ( prisonerIdDB < 10) {
            lbPrisonerId.setText("0000"+this.prisonerId);
        }
        else if (prisonerIdDB < 100 && prisonerIdDB > 10) {
            lbPrisonerId.setText("000"+this.prisonerId);
        }
        else if (prisonerIdDB > 100 && prisonerIdDB < 1000 )
        {
            lbPrisonerId.setText("00"+this.prisonerId);
        }
        else if (prisonerIdDB > 1000 && prisonerIdDB < 10000 )
        {
            lbPrisonerId.setText("0"+this.prisonerId);
        }
    }

    public boolean getPrisoner() {
        try {
            RadioButton selectedGender = (RadioButton) tgGender.getSelectedToggle();
            if (selectedGender == null) {
                showAlert("Invalid Gender");
                return false;
            }

            String selectedRadioButtonText = selectedGender.getText();
            int genderInputDb;
            if (selectedRadioButtonText.equals("Male")) {
                genderInputDb = 1;
            } else if (selectedRadioButtonText.equals("Female")) {
                genderInputDb = 2;
            } else {
                genderInputDb = 3;
            }
            String fullName = txtPrisonerFNAdd.getText();
            LocalDate dob = datePrisonerDOBAdd.getValue();
            String contactName = txtContactName.getText();
            String contactPhone = txtContactPhone.getText();
            String identityCard = txtIdentityCard.getText();
            String code = lbPrisonerId.getText();
            boolean status = false;
            int userIdDb = this.userId;
            System.out.println("User Id Add: " + userIdDb);
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

            if (!fullName.matches("[\\p{L}]+(\\s+[\\p{L}]+)*"
            )) {
                showAlert("Invalid prisoner name");
                return false;
            }

            if (!contactName.matches("[\\p{L}]+(\\s+[\\p{L}]+)*"
            )) {
                showAlert("Invalid contact name");
                return false;
            }
            // Kiểm tra ngày sinh
            LocalDate currentDate = LocalDate.now();
            LocalDate eighteenYearsAgo = currentDate.minusYears(18);
            if (!dob.isBefore(eighteenYearsAgo)) {
                showAlert("Prisoner must be at least 18 years old");
                return false;
            }

            // Kiểm tra số điện thoại
            if (!contactPhone.matches("^0[0-9]{9}$")) {
                showAlert("Invalid contact phone");
                return false;
            }
            Prisoner prisoner = new Prisoner();
            prisoner.setPrisonerCode(String.valueOf(code));
            prisoner.setPrisonerName(fullName);
            prisoner.setDOB(String.valueOf(dob));
            prisoner.setContactName(contactName);
            prisoner.setContactPhone(contactPhone);
            prisoner.setGender(genderInputDb);
            prisoner.setImagePath(getRelativePath);
            prisoner.setStatus(status);
            prisoner.setUser_id(userIdDb);
            prisoner.setIdentityCard(identityCard);
            this.prisoner = prisoner;
            return true;
        } catch (Exception e) {
            showAlert("An error occurred while adding prisoner information");
            System.out.println("getPrisoner - AddPrisonerController ; " + e.getMessage());
        }
        return false;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void setUserIdAdd(int userId)
    {
        this.userId=userId;
    }

    public String selectImageFile() {
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
            getRelativePath = relativePath;
            return relativePath;
        } else {
            System.out.println("Không có tệp nào được chọn.");
        }
        return null;
    }



    public interface Callback {
        void execute();
    }

    public void back(ActionEvent event, Callback callback)  {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        if (callback != null) {
            callback.execute();
        }
    }

    public void backPrisoner(ActionEvent event)
    {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
    public void setCbCrimes()
    {
        CrimeDao crimeDao = new CrimeDao();
        List<Crime> crimeList = crimeDao.getCrime();
        List<String> crimes = new ArrayList<>();
        for (Crime crime : crimeList)
        {
            crimes.add(crime.getCrimeName());
        }
        ccbCrimes.getItems().addAll(crimes);
    }

public void getSelectedCrimes() {
    CrimeDao crimeDao = new CrimeDao();
    List<Crime> crimeList = crimeDao.getCrime();
    ObservableList<String> selectedCrimes = ccbCrimes.getCheckModel().getCheckedItems();
    System.out.println("Selected Crimes: " + selectedCrimes);
    List<Integer> idList = new ArrayList<>();
    for (String selectedCrimeName : selectedCrimes) {
        for (Crime crime : crimeList) {
            if (selectedCrimeName.equals(crime.getCrimeName())) {
                idList.add(crime.getCrimeId());
                System.out.println("Crime Id: " + crime.getCrimeId());
                break; // Đã tìm thấy tên tương ứng, thoát khỏi vòng lặp trong.
            }
        }
    }
    this.selectedCrimesId = idList;
}

    public void openInputYearWindow() {
        try {
            getSelectedCrimes();
            System.out.println("List id add window: " + this.selectedCrimesId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/psmsystem/view/prisoner/InputYearCrimesView.fxml"));
            AnchorPane newWindowContent = loader.load();

            InputYearCrimes controller = loader.getController();
            controller.getIdCrimes(this.selectedCrimesId, this.sentenceId);

            Stage newStage = new Stage();
            Scene scene = new Scene(newWindowContent);
            newStage.setScene(scene);
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle("Edit Prisoner");
            newStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }



    @FXML
    void loadInputYearCrimeView(ActionEvent event) {
        openInputYearWindow();

    }
    public void setImgDefault()
    {
        String defaultPath = "src/main/resources/com/example/psmsystem/imagesPrisoner/defaultImage.png";
        try {
            File imageFile = new File(defaultPath);
            Image image = new Image(imageFile.toURI().toString());
            imgPrisonerAdd.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error setting image: " + e.getMessage());
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    setImgDefault();
        setPrisonerId();
        setIdSentence();
        setCbCrimes();
        LocalDate currentDate = LocalDate.now();
        LocalDate eighteenYearsAgo = currentDate.minusYears(20);
        datePrisonerDOBAdd.setValue(eighteenYearsAgo);
        lbPrisonerId.setVisible(false);
        tgGender = new ToggleGroup();
        tgSentenceType = new ToggleGroup();
        rbtnMale.setToggleGroup(tgGender);
        rbtnFemale.setToggleGroup(tgGender);
        rbtnOther.setToggleGroup(tgGender);
        rbtnLimited.setToggleGroup(tgSentenceType);
        rbtnUnlimited.setToggleGroup(tgSentenceType);

        tgSentenceType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton selectedRadioButton = (RadioButton) newValue;
//            if (selectedRadioButton == rbtnUnlimited)
//            {
//                dateOut.setDisable(true);
//                ccbCrimes.setDisable(true);
//                btnShowYearInput.setDisable(true);
//            }else
//            {
//                dateOut.setDisable(false);
//                ccbCrimes.setDisable(false);
//                btnShowYearInput.setDisable(false);
//            }
        });
        dateIn.valueProperty().addListener((observable, oldValue, newValue) -> {
            setEndDate();
        });
        LocalDate initialDate = dateOut.getValue();
        dateOut.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(initialDate)) {
                setEndDate();
            }
        });


    }
}
