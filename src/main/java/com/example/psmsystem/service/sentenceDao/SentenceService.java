package com.example.psmsystem.service.sentenceDao;

import com.example.psmsystem.database.DbConnection;
import com.example.psmsystem.dto.Consider;
import com.example.psmsystem.dto.SentenceDTO;
import com.example.psmsystem.model.sentence.SentenceServiceImpl;
import com.example.psmsystem.model.sentence.Sentence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SentenceService implements SentenceServiceImpl<SentenceDTO> {
    Logger LOGGER = Logger.getLogger(SentenceService.class.getName());
    private static final String SELECT_BY_SENTENCE_QUERY = "SELECT s.prisoner_id, p.prisoner_name, p.identity_card, s.sentence_type, s.sentences_code, s.crimes_code, s.start_date, s.end_date, s.release_date, s.status, s.parole_eligibility FROM sentences s JOIN prisoners p ON p.prisoner_id = s.prisoner_id";

    @Override
    public List<SentenceDTO> getSentence() {


        List<SentenceDTO> sentenceList = new ArrayList<>();
        try {

            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement(SELECT_BY_SENTENCE_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Sentence sentence = new Sentence();
                sentence.setSentenceCode(rs.getInt("sentences_code"));
                sentence.setSentenceType(rs.getString("sentence_type"));
                sentence.setCrimesCode(rs.getString("crimes_code"));
                sentence.setStartDate(rs.getDate("start_date"));
                sentence.setEndDate(rs.getDate("end_date"));
                sentence.setReleaseDate(rs.getDate("release_date"));
                sentence.setStatus(rs.getBoolean("status"));
                sentence.setParole(rs.getString("parole_eligibility"));

                SentenceDTO sentenceDTO = new SentenceDTO(
                        rs.getString("prisoner_name"),
                        rs.getInt("prisoner_id"),
                        rs.getString("identity_card"),
                        sentence
                );
                sentenceList.add(sentenceDTO);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return sentenceList;
    }

    @Override
    public ObservableList<SentenceDTO> getPrisonerName() {
        List<SentenceDTO> sentence = getSentence();
        ObservableList<SentenceDTO> prisonerList = FXCollections.observableArrayList(sentence);
        return prisonerList;
    }
    //consider
    public HashMap<String,List<Consider>> classify() {

        List<Consider> considerList = new ArrayList<>();
        // get year(now)
        int currentYear = LocalDate.now().getYear();
        // get firstday of year
        LocalDate firstDayOfYear = LocalDate.of(currentYear, 1, 1);
        // get lastday of year
        LocalDate lastDayOfYear = LocalDate.of(currentYear, 12, 31);
        List<Consider> considers1 = new ArrayList<>();
        List<Consider> considers2 = new ArrayList<>();
        List<Consider> considers3 = new ArrayList<>();
        List<Consider> considers4 = new ArrayList<>();
        List<Consider> considers5 = new ArrayList<>();
        List<Consider> considers6 = new ArrayList<>();
        List<Consider> considers7 = new ArrayList<>();
        List<Consider> considers8 = new ArrayList<>();
        List<Consider> considers9 = new ArrayList<>();
        List<Consider> considers10 = new ArrayList<>();
        List<Consider> considers11 = new ArrayList<>();
        List<Consider> considers12 = new ArrayList<>();

        HashMap<String,List<Consider>> result = new HashMap<>();
        //get All sentence have type:limited_time and status: false(in)
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()){
            try (PreparedStatement considerPs = connection.prepareStatement(
                    "SELECT s.*, p.prisoner_name, p.identity_card,p.image " +
                            "FROM sentences s " +
                            "JOIN prisoners p ON s.prisoner_id = p.prisoner_id  " +
                            "WHERE s.sentence_type LIKE 'limited_time' AND s.status = false")) {
                ResultSet  considerRs= considerPs.executeQuery();
                if(!considerRs.next()) return null;
                while (considerRs.next()) {
                    Consider consider = new Consider();
                    consider.setImage(considerRs.getString("image"));
                    consider.setPrisonerName(considerRs.getString("prisoner_name"));
                    consider.setIdentityCard(considerRs.getString("identity_card"));
                    consider.setSentenceCode(considerRs.getInt("sentences_code"));
                    consider.setSentenceId(considerRs.getInt("sentence_id"));
                    considerList.add(consider);
                }

            }
            for (Consider consider : considerList) {
                int healthCheck = 0;
                int commendationSum = 0;
                int disciplinarySum = 0;
                int commendationLevel4Count = 0;
                int commendationLevel3Count = 0;
                int commendationLevel2Count = 0;
                int commendationLevel1Count = 0;
                int disciplinaryLevel1Count = 0;
                int disciplinaryLevel2Count = 0;
                int disciplinaryLevel3Count = 0;
                int disciplinaryLevel4Count = 0;
                boolean isHaveDisciplinary = false;


                // Get health levels
                try (PreparedStatement getHealthLevelPs = connection.prepareStatement(
                        "SELECT level FROM healths WHERE (checkup_date BETWEEN ? AND ?) AND sentence_id = ?")) {
                    getHealthLevelPs.setDate(1, Date.valueOf(firstDayOfYear));
                    getHealthLevelPs.setDate(2, Date.valueOf(lastDayOfYear));
                    getHealthLevelPs.setInt(3, consider.getSentenceId());
                    ResultSet getHealthLevelRs = getHealthLevelPs.executeQuery();

                    while (getHealthLevelRs.next()) {
                        int level = getHealthLevelRs.getInt("level");
                        //check health
                        healthCheck = Math.max(healthCheck, level);
                    }
                    consider.setHealth(healthCheck);
                }
                //check health lv3
                if (healthCheck == 3) { // Special care
                    considers1.add(consider);
                    continue;
                }
                //set health
                consider.setHealth(healthCheck);

                //get ignored
                try(PreparedStatement ignoredPs = connection.prepareStatement("SELECT event_type, level FROM incareration_process " +
                        "WHERE sentence_id = ? AND date_of_occurrence BETWEEN ? AND ?")) {
                    ignoredPs.setInt(1,consider.getSentenceId());
                    ignoredPs.setDate(2,Date.valueOf(firstDayOfYear));
                    ignoredPs.setDate(3,Date.valueOf(lastDayOfYear));
                    ResultSet ignoreRs = ignoredPs.executeQuery();
                    while (ignoreRs.next()) {
                        if(ignoreRs.getString("event_type").equals("Bonus")) {
                            switch (ignoreRs.getInt("level")) {
                            case 1->commendationLevel1Count++;
                            case 2->commendationLevel2Count++;
                            case 3->commendationLevel3Count++;
                            case 4->commendationLevel4Count++;
                            }
                        } else {
                            switch (ignoreRs.getInt("level")) {
                                case 1->disciplinaryLevel1Count++;
                                case 2->disciplinaryLevel2Count++;
                                case 3->disciplinaryLevel3Count++;
                                case 4->disciplinaryLevel4Count++;
                            }
                        }
                    }
                    // total
                    commendationSum = commendationLevel1Count + commendationLevel2Count + commendationLevel3Count + commendationLevel4Count;
                    disciplinarySum = disciplinaryLevel1Count + disciplinaryLevel2Count + disciplinaryLevel3Count + disciplinaryLevel4Count;
                    //set
                    consider.setCommendationSum(commendationSum);
                    consider.setDisciplinaryMeasureSum(disciplinarySum);
                    //check disciplinary
                    isHaveDisciplinary = disciplinarySum > 0;
                }

                // Get commendation levels
//                try (PreparedStatement getCommendationLevelPs = connection.prepareStatement(
//                        "SELECT level FROM commendations WHERE (date_of_occurrence BETWEEN ? AND ?) AND sentence_id = ?")) {
//                    getCommendationLevelPs.setDate(1, Date.valueOf(firstDayOfYear));
//                    getCommendationLevelPs.setDate(2, Date.valueOf(lastDayOfYear));
//                    getCommendationLevelPs.setInt(3, consider.getSentenceId());
//                    ResultSet getCommendationLevelRs = getCommendationLevelPs.executeQuery();
//
//                    // sum
//                    int commendationSum = 0;
//                    while (getCommendationLevelRs.next()) {
//                        int level = getCommendationLevelRs.getInt("level");
//
//                        //sum
//                        commendationSum++;
//
//                        //count by level
//                        switch (level) {
//                            case 1->commendationLevel1Count++;
//                            case 2->commendationLevel2Count++;
//                            case 3->commendationLevel3Count++;
//                            case 4->commendationLevel4Count++;
//                        }
//                    }
//                    //set sum
//                    consider.setCommendationSum(commendationSum);
//                }
//
//
//                // Get disciplinary measures
//                try (PreparedStatement getDisciplinaryLevelPs = connection.prepareStatement(
//                        "SELECT level FROM disciplinary_measures WHERE (date_of_occurrence BETWEEN ? AND ?) AND sentence_id = ?")) {
//                    getDisciplinaryLevelPs.setDate(1, Date.valueOf(firstDayOfYear));
//                    getDisciplinaryLevelPs.setDate(2, Date.valueOf(lastDayOfYear));
//                    getDisciplinaryLevelPs.setInt(3, consider.getSentenceId());
//                    ResultSet getDisciplinaryLevelRs = getDisciplinaryLevelPs.executeQuery();
//
//                    //sum disciplinary
//                    int disciplinarySum = 0;
//                    while (getDisciplinaryLevelRs.next()) {
//                        //sum
//                        disciplinarySum++;
//                        int level = getDisciplinaryLevelRs.getInt("level");
//                        //count by level
//                        switch (level) {
//                            case 1->disciplinaryLevel1Count++;
//                            case 2->disciplinaryLevel2Count++;
//                            case 3->disciplinaryLevel3Count++;
//                            case 4->disciplinaryLevel4Count++;
//                        }
//                        isHaveDisciplinary = true;
//                    }
//                    //set sum
//                    consider.setDisciplinaryMeasureSum(disciplinarySum);
//
 //               }
                // Evaluate the prisoner based on criteria
                //Sentence reduction by one year
                if (commendationLevel4Count >= 20 && healthCheck <= 1 && !isHaveDisciplinary) {
                    considers2.add(consider);
                }
                //Sentence reduction by six months
                else if (commendationLevel3Count + commendationLevel4Count >= 25 && healthCheck <= 1 && !isHaveDisciplinary) {
                    considers3.add(consider);
                }
                //Sentence reduction by one month
                else if (commendationLevel2Count + commendationLevel3Count + commendationLevel4Count >= 30 && healthCheck <= 1 && !isHaveDisciplinary) {
                    considers4.add(consider);
                }
                //Gift and an additional health check-up
                else if (commendationSum >= 20 && healthCheck == 2 && disciplinaryLevel1Count <= 2){
                    considers6.add(consider);
                }
                //Gift
                else if (commendationSum >= 20 && healthCheck <= 1 && disciplinaryLevel1Count <= 2) {
                    considers5.add(consider);
                }
                //Solitary confinement
                else if (disciplinarySum >= 25 || disciplinaryLevel4Count >= 5) {
                    considers7.add(consider);
                }
                //Enforce
                else if (disciplinarySum >= 20 || disciplinaryLevel4Count >= 3) {
                    considers8.add(consider);
                }
                //Punishment of no family visits and labor for 48 hours
                else if (disciplinaryLevel1Count + disciplinaryLevel2Count + disciplinaryLevel3Count >= 16  || disciplinaryLevel3Count >= 3) {
                    considers9.add(consider);
                }
                //labor for 36 hours
                else if (disciplinaryLevel1Count + disciplinaryLevel2Count >= 11 || disciplinaryLevel2Count >= 3) {
                    considers10.add(consider);
                }
                //labor for 12 hours
                else if (disciplinaryLevel1Count >= 5) {
                    considers11.add(consider);
                }
                //else
                else {
                    considers12.add(consider);
                }
            }
            result.put("Special care",considers1);
            result.put("Sentence reduction by one year",considers2);
            result.put("Sentence reduction by six months",considers3);
            result.put("Sentence reduction by one month",considers4);
            result.put("Gift of blankets, mats, clothes, fans",considers5);
            result.put("Gift of blankets, mats, clothes, fans and an additional health check-up",considers6);
            result.put("Solitary confinement",considers7);
            result.put("Enforce",considers8);
            result.put("Punishment of no family visits and labor for 48 hours",considers9);
            result.put("labor for 36 hours",considers10);
            result.put("labor for 12 hours",considers11);
            result.put("normal",considers12);

        } catch (SQLException  e) {
            LOGGER.log(Level.SEVERE,"Evaluate failed: ",e.getMessage());
            throw new RuntimeException("An Error in system. Please try again");
        }
        return result;
    }
}
