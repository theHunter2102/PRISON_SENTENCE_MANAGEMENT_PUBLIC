package com.example.psmsystem.service.sentenceDao;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.database.DbConnection;
import com.example.psmsystem.model.sentence.ISentenceDao;
import com.example.psmsystem.model.sentence.Sentence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SentenceDao implements ISentenceDao<Sentence> {
    Logger LOGGER = Logger.getLogger(SentenceDao.class.getName());
    private static final String INSERT_QUERY = "INSERT INTO sentences (prisoner_id, sentences_code, sentence_type, crimes_code, start_date, end_date, release_date, status, parole_eligibility) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SENTENCE_QUERY = "UPDATE sentences SET prisoner_id = ?, sentences_code = ?, sentence_type = ?, crimes_code = ?, start_date = ?, end_date = ?, release_date = ?, status = ?, parole_eligibility = ? WHERE sentence_id = ?";
    private static final String DELETE_SENTENCE_QUERY = "DELETE FROM sentences WHERE sentence_id = ?";
    private static final String SELECT_BY_SENTENCE_QUERY = "SELECT s.sentence_id, s.prisoner_id, p.prisoner_name, s.sentence_type, s.sentences_code, s.crimes_code, s.start_date, s.end_date, s.release_date, s.status, s.parole_eligibility FROM sentences s JOIN prisoners p ON p.prisoner_id = s.prisoner_id";
    private static final String SELECT_BY_CODE_SENTENCE_QUERY = "SELECT * FROM sentences WHERE sentences_code = ?";
    private static final String COUNT_PRISONERS_BY_SENTENCE_TYPE_QUERY = "SELECT sentence_type, COUNT(*) AS prisoner_count FROM sentences GROUP BY sentence_type";
    private static final String SELECT_SENTENCE_ID_MAX = "SELECT MAX(sentences_code) AS max_sentence_code FROM sentences";

    private static final String INSERT_SENTENCE_CRIME = "INSERT INTO sentence_crimes VALUE (?, ?, ?)";
    @Override
    public boolean addSentence(Sentence sentence) {
        //check status role
        boolean isRole = false;
        //check list role
        for (ApplicationState.RoleName r : ApplicationState.getInstance().getRoleName()) {
            if (r.equals(ApplicationState.RoleName.PRISONER_MANAGEMENT) || r.equals(ApplicationState.RoleName.ULTIMATE_AUTHORITY)) {
                isRole = true;
                break;
            }
        }
        //runtime if role not equal
        if(!isRole) throw new RuntimeException("You do not have permission to perform this operation.");

        try(Connection connection = DbConnection.getDatabaseConnection().getConnection())
        {

            //check start > 18years(dob)
            try (PreparedStatement checkDOBPrisonerPs = connection.prepareStatement("SELECT date_birth FROM prisoners WHERE prisoner_id = ?")){
                checkDOBPrisonerPs.setInt(1,sentence.getPrisonerId());
                ResultSet checkDOBPrisonerRs = checkDOBPrisonerPs.executeQuery();
                if(!checkDOBPrisonerRs.next()) throw  new RuntimeException("Prisoner not found.");
                if(sentence.getStartDate().toLocalDate().isBefore(checkDOBPrisonerRs.getDate("date_birth").toLocalDate().plusYears(18)))
                    throw new RuntimeException("The sentence can only start after the prisoner's 18th birthday.");
            }

            // The status cannot be updated to false if the prisoner already has one false sentence.
            try (PreparedStatement isStatusSentencesPs = connection.prepareStatement("SELECT COUNT(*) FROM sentences WHERE prisoner_id = ? AND status = false")){
                isStatusSentencesPs.setInt(1,sentence.getPrisonerId());
                ResultSet isStatusSentencesRs = isStatusSentencesPs.executeQuery();
                if(isStatusSentencesRs.next()) {
                    int count = isStatusSentencesRs.getInt(1);
                    if(count > 1) throw new RuntimeException("This prisoner is currently undergoing rehabilitation under another sentence and cannot have their release date removed.");
                }
            }
            //create Sentence
            try (PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);){
                ps.setInt(1,sentence.getPrisonerId());
                ps.setInt(2,sentence.getSentenceCode());
                ps.setString(3,sentence.getSentenceType());
                ps.setString(4,sentence.getCrimesCode());
                ps.setDate(5, sentence.getStartDate());
                if(sentence.getEndDate() == null) {
                    ps.setNull(6, Types.DATE);
                } else {
                    ps.setDate(6, sentence.getEndDate());
                }
                if(sentence.getReleaseDate() == null){
                    ps.setNull(7,Types.DATE);
                    ps.setBoolean(8,false);
                } else {
                    ps.setDate(7, sentence.getReleaseDate());
                    ps.setBoolean(8,true);
                }
                ps.setString(9,sentence.getParole());
                ps. executeUpdate();
            }
            //update status prisoner
            try (PreparedStatement setStatusPrisonerPs = connection.prepareStatement("UPDATE `prisoners` SET `status` = ? WHERE `prisoner_id` = ?")){
                setStatusPrisonerPs.setBoolean(1,sentence.getReleaseDate() != null);
                setStatusPrisonerPs.setInt(2,sentence.getPrisonerId());
                setStatusPrisonerPs.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"add sentence failed: ",e);
            throw new RuntimeException("Add failed: an error in system. Please try again in a few minutes.");
        }
    }

    @Override
    public List<Sentence> getSentence() {
        List<Sentence> sentenceList = new ArrayList<>();
        try {

            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement(SELECT_BY_SENTENCE_QUERY);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Sentence sentence = new Sentence();
                sentence.setSentenceId(rs.getInt("sentence_id"));
                sentence.setPrisonerId(rs.getInt("prisoner_id"));
                sentence.setPrisonerName(rs.getString("prisoner_name"));
                sentence.setSentenceCode(rs.getInt("sentences_code"));
                sentence.setSentenceType(rs.getString("sentence_type"));
                sentence.setCrimesCode(rs.getString("crimes_code"));
                sentence.setStartDate(rs.getDate("start_date"));
                sentence.setEndDate(rs.getDate("end_date"));
                sentence.setReleaseDate(rs.getDate("release_date"));
                sentence.setStatus(rs.getBoolean("status"));
                sentence.setParole(rs.getString("parole_eligibility"));
                sentenceList.add(sentence);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return sentenceList;
    }

    @Override
    public int getMaxIdSentence()
    {
        try {
            Connection connection = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_SENTENCE_ID_MAX);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("max_sentence_code")+1;
            } else {
                return -1;
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }
    @Override
    public void updateSentence(Sentence sentence, int id) {
        //check status role
        boolean isRole = false;
        //check list role
        for (ApplicationState.RoleName r : ApplicationState.getInstance().getRoleName()) {
            if (r.equals(ApplicationState.RoleName.PRISONER_MANAGEMENT) || r.equals(ApplicationState.RoleName.ULTIMATE_AUTHORITY)) {
                isRole = true;
                break;
            }
        }
        //runtime if role not equal
        if(!isRole) throw new RuntimeException("You do not have permission to perform this operation.");

        //check crime
        if(sentence.getCrimesCode() == null) throw new RuntimeException("Please select crimes for sentence");
        try(Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            //check start > 18years(dob)
            try (PreparedStatement checkDOBPrisonerPs = connection.prepareStatement("SELECT date_birth FROM prisoners WHERE prisoner_id = ?")){
                checkDOBPrisonerPs.setInt(1,sentence.getPrisonerId());
                ResultSet checkDOBPrisonerRs = checkDOBPrisonerPs.executeQuery();
                if(!checkDOBPrisonerRs.next()) throw  new RuntimeException("Prisoner not found.");
                if(sentence.getStartDate().toLocalDate().isBefore(checkDOBPrisonerRs.getDate("date_birth").toLocalDate().plusYears(18)))
                    throw new RuntimeException("The sentence can only start after the prisoner's 18th birthday.");
            }

            // The status cannot be updated to false if the prisoner already has one false sentence.
            try (PreparedStatement isStatusSentencesPs = connection.prepareStatement("SELECT COUNT(*) FROM sentences WHERE prisoner_id = ? AND status = false")){
                isStatusSentencesPs.setInt(1,sentence.getPrisonerId());
                ResultSet isStatusSentencesRs = isStatusSentencesPs.executeQuery();
                if(isStatusSentencesRs.next()) {
                    int count = isStatusSentencesRs.getInt(1);
                    if(count > 1) throw new RuntimeException("This prisoner is currently undergoing rehabilitation under another sentence and cannot have their release date removed.");

                }
            }
            //update sentence
            try(PreparedStatement ps = connection.prepareStatement(UPDATE_SENTENCE_QUERY)) {
                ps.setInt(1, sentence.getSentenceId());
                ps.setInt(1,sentence.getPrisonerId());
                ps.setInt(2,sentence.getSentenceCode());
                ps.setString(3,sentence.getSentenceType());
                ps.setString(4,sentence.getCrimesCode());
                ps.setDate(5, (Date) sentence.getStartDate());
                ps.setDate(6, (Date) sentence.getEndDate());
                if(sentence.getReleaseDate() != null) {
                    ps.setDate(7, (Date) sentence.getReleaseDate());
                    ps.setBoolean(8,true);
                } else {
                    ps.setNull(7,Types.DATE);
                    ps.setBoolean(8,false);
                }
                ps.setString(9,sentence.getParole());
                ps.setInt(10, id);
                ps.executeUpdate();
            }
            //update status prisoner
            try (PreparedStatement setStatusPrisonerPs = connection.prepareStatement("UPDATE `prisoners` SET `status` = ? WHERE `prisoner_id` = ?")){
                setStatusPrisonerPs.setBoolean(1,sentence.getReleaseDate() != null);
                setStatusPrisonerPs.setInt(2,sentence.getPrisonerId());
                setStatusPrisonerPs.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Update sentence failed: ",e);
            throw new RuntimeException("Update sentence failed: Please try again in a few minutes.");
        }
    }

    @Override
    public void deleteSentence(int id) {
        //check status role
        boolean isRole = false;
        //check list role
        for (ApplicationState.RoleName r : ApplicationState.getInstance().getRoleName()) {
            if (r.equals(ApplicationState.RoleName.PRISONER_MANAGEMENT) || r.equals(ApplicationState.RoleName.ULTIMATE_AUTHORITY)) {
                isRole = true;
                break;
            }
        }
        //runtime if role not equal
        if(!isRole) throw new RuntimeException("You do not have permission to perform this operation.");


        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            //delete commendations
            try(PreparedStatement deleteCommendationsPs = connection.prepareStatement("DELETE FROM commendations WHERE sentence_id = ?"))  {
                deleteCommendationsPs.setInt(1,id);
                deleteCommendationsPs.executeUpdate();
            }
            //delete disciplinary
            try(PreparedStatement deleteDisciplinaryPs = connection.prepareStatement("DELETE FROM disciplinary_measures WHERE sentence_id = ?"))  {
                deleteDisciplinaryPs.setInt(1,id);
                deleteDisciplinaryPs.executeUpdate();
            }
            //delete health
            try(PreparedStatement delHealthsPs = connection.prepareStatement("DELETE FROM healths WHERE sentence_id = ?"))  {
                delHealthsPs.setInt(1,id);
                delHealthsPs.executeUpdate();
            }
            //delete ignored
            try(PreparedStatement delIgoreePs = connection.prepareStatement("DELETE FROM incareration_process WHERE sentence_id = ?"))  {
                delIgoreePs.setInt(1,id);
                delIgoreePs.executeUpdate();
            }
            //delete vitsit
            try(PreparedStatement delVisitPs = connection.prepareStatement("DELETE FROM visit_log WHERE sentence_id = ?"))  {
                delVisitPs.setInt(1,id);
                delVisitPs.executeUpdate();
            }
            try(PreparedStatement ps = connection.prepareStatement(DELETE_SENTENCE_QUERY)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"delete sentence failed: ",e);
            throw new RuntimeException("delete failed!");
        }
    }

    @Override
    public int getSentenceId(int sentenceCode) {
        int sentenceId = -1;

        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_CODE_SENTENCE_QUERY)) {
                ps.setInt(1, sentenceCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        sentenceId = rs.getInt("sentence_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sentenceId;
    }

    public void insertSentenceCrimes(int sentenceId, int crimeId, int year)
    {
        try{
            Connection connection = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement ps = connection.prepareStatement(INSERT_SENTENCE_CRIME);
            ps.setInt(1,sentenceId);
            ps.setInt(2,crimeId);
            ps.setInt(3,year);
            ps.executeQuery();
        }catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public Map<String, Integer> countPrisonersBySentenceType() {
        Map<String, Integer> prisonersBySentenceType = new HashMap<>();

        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(COUNT_PRISONERS_BY_SENTENCE_TYPE_QUERY);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String sentenceType = rs.getString("sentence_type");
                int prisonerCount = rs.getInt("prisoner_count");
                prisonersBySentenceType.put(sentenceType, prisonerCount);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return prisonersBySentenceType;
    }

    @Override
    public ObservableList<Sentence> getPrisonerName() {
        List<Sentence> sentence = getSentence();
        ObservableList<Sentence> prisonerList = FXCollections.observableArrayList(sentence);
        return prisonerList;
    }
}
