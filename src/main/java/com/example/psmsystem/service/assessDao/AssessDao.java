package com.example.psmsystem.service.assessDao;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.database.DbConnection;
import com.example.psmsystem.model.assess.Assess;
import com.example.psmsystem.model.assess.IAssessDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssessDao implements IAssessDao<Assess> {
    Logger LOGGER = Logger.getLogger(AssessDao.class.getName());

    private static final String INSERT_QUERY = "INSERT INTO incareration_process (process_code, sentence_id, prisoner_id, date_of_occurrence, event_type, level, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_ASSESS_QUERY = "UPDATE incareration_process SET process_code = ?, sentence_id = ?, prisoner_id = ?, date_of_occurrence = ?, event_type = ?, level = ?, note = ? WHERE process_id = ?";
    private static final String DELETE_ASSESS_QUERY = "DELETE FROM incareration_process WHERE process_id = ?";
    private static final String SELECT_BY_ASSESS_QUERY = "SELECT ip.process_code, ip.sentence_id, s.sentences_code, ip.prisoner_id, p.prisoner_name,ip.date_of_occurrence, ip.event_type, ip.level, ip.note FROM incareration_process ip JOIN sentences s ON s.sentence_id = ip.sentence_id JOIN prisoners p ON p.prisoner_id = ip.prisoner_id ORDER BY date_of_occurrence";
    private static final String SELECT_BY_CODE_DATE_ASSESS_QUERY = "SELECT * FROM incareration_process WHERE process_code = ? AND date_of_occurrence = ?";
    private static final String MAX_PROCESS_CODE_QUERY = "SELECT MAX(CAST(SUBSTRING(process_code, 2) AS UNSIGNED)) AS max_assess_code FROM incareration_process WHERE process_code REGEXP '^P[0-9]+$'";
    private static final String BREACH_QUERY  = "SELECT p.prisoner_name, COUNT(CASE WHEN ip.event_type = 'Breach of discipline' THEN 1 ELSE NULL END) AS total_breach FROM incareration_process ip JOIN prisoners p ON ip.prisoner_id = p.prisoner_id WHERE ip.event_type = 'Breach of discipline' GROUP BY p.prisoner_name ORDER BY total_breach DESC LIMIT 5";
    private static final String BONUS_QUERY = "SELECT p.prisoner_name, COUNT(CASE WHEN ip.event_type = 'Bonus' THEN 1 ELSE NULL END) AS total_bonus FROM incareration_process ip JOIN prisoners p ON ip.prisoner_id = p.prisoner_id WHERE ip.event_type = 'Bonus' GROUP BY p.prisoner_name ORDER BY total_bonus DESC LIMIT 5";

    @Override
    public void addAssess(Assess assess) {
        //check status role
        boolean isRole = false;
        //check list role
        for (ApplicationState.RoleName r : ApplicationState.getInstance().getRoleName()) {
            if(r.equals(ApplicationState.RoleName.PRISONER_MANAGEMENT) || r.equals(ApplicationState.RoleName.ULTIMATE_AUTHORITY)) {
                isRole = true;
                break;
            }
        }
        //runtime if role not equal
        if(!isRole) throw new RuntimeException("You do not have permission to perform this operation.");
        try(Connection connection = DbConnection.getDatabaseConnection().getConnection())
        {
            //check visit date ith start end release,start,end of sentence
            //get sentence
            try (PreparedStatement getSentencePs = connection.prepareStatement("SELECT start_date , release_date , end_date FROM sentences WHERE sentence_id = ?")){
                getSentencePs.setString(1, assess.getSentencesId());
                ResultSet getSentenceRs = getSentencePs.executeQuery();
                if(!getSentenceRs.next()) throw new RuntimeException("Add assess failed: Sentence not found.");
                //set date
                LocalDate startDate = getSentenceRs.getDate("start_date").toLocalDate();
                //set date for visit date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate checkUpDate = LocalDate.parse(assess.getDateOfOccurrence(),formatter);
                //check visit date >= start date
                if(checkUpDate.isBefore(startDate) || checkUpDate.isAfter(LocalDate.now())) throw new RuntimeException("The Check-up Date date cannot be before the start date of the sentence.");
                //check type limited time
                if(getSentenceRs.getDate("end_date") != null) {
                    LocalDate endDate = getSentenceRs.getDate("end_date").toLocalDate();
                    //if release != null, visitDate <= release date
                    if(getSentenceRs.getDate("release_date") != null && checkUpDate.isAfter(getSentenceRs.getDate("release_date").toLocalDate()))
                        throw new RuntimeException("The occurrence date cannot be after the release  date of the sentence.");
                    //if null,visitDate <= endDAte
                    if(getSentenceRs.getDate("release_date") == null && checkUpDate.isAfter(endDate)) throw new RuntimeException("The check uo date cannot be after the  end date of the sentence.");
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);){
                ps.setString(1,assess.getProcessCode());
                ps.setString(2,assess.getSentencesId());
                ps.setInt(3,assess.getPrisonerId());
                ps.setString(4,assess.getDateOfOccurrence());
                ps.setString(5,assess.getEventType());
                ps.setInt(6,assess.getLevel());
                ps.setString(7,assess.getNote());

                ps.executeUpdate();
            }


        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"add failed: ", e);
            throw new RuntimeException("Add assess failed!");
        }
    }

    @Override
    public List<Assess> getAssess() {
        List<Assess> assessList = new ArrayList<>();
        try {

            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement(SELECT_BY_ASSESS_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Assess assess = new Assess();
                assess.setProcessCode(rs.getString("process_code"));
                assess.setSentencesId(rs.getString("sentence_id"));
                assess.setSentencesCode(rs.getInt("sentences_code"));
                assess.setPrisonerId(rs.getInt("prisoner_id"));
                assess.setPrisonerName(rs.getString("prisoner_name"));
                assess.setDateOfOccurrence(rs.getString("date_of_occurrence"));
                assess.setEventType(rs.getString("event_type"));
                assess.setLevel(rs.getInt("level"));
                assess.setNote(rs.getString("note"));
                assessList.add(assess);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return assessList;
    }

    @Override
    public void updateAssess(Assess assess, int id) {
        //check status role
        boolean isRole = false;
        //check list role
        for (ApplicationState.RoleName r : ApplicationState.getInstance().getRoleName()) {
            if(r.equals(ApplicationState.RoleName.PRISONER_MANAGEMENT) || r.equals(ApplicationState.RoleName.ULTIMATE_AUTHORITY)) {
                isRole = true;
            }
        }
        //runtime if role not equal
        if(!isRole) throw new RuntimeException("You do not have permission to perform this operation.");
        try(Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            //check visit date ith start end release,start,end of sentence
            //get sentence
            try (PreparedStatement getSentencePs = connection.prepareStatement("SELECT start_date , release_date , end_date FROM sentences WHERE sentence_id = ?")){
                getSentencePs.setString(1, assess.getSentencesId());
                ResultSet getSentenceRs = getSentencePs.executeQuery();
                if(!getSentenceRs.next()) throw new RuntimeException("Add assess failed: Sentence not found.");
                //set date
                LocalDate startDate = getSentenceRs.getDate("start_date").toLocalDate();
                //set date for visit date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate checkUpDate = LocalDate.parse(assess.getDateOfOccurrence(),formatter);
                //check visit date >= start date
                if(checkUpDate.isBefore(startDate) || checkUpDate.isAfter(LocalDate.now())) throw new RuntimeException("The Check-up Date date cannot be before the start date of the sentence.");
                //check type limited time
                if(getSentenceRs.getDate("end_date") != null) {
                    LocalDate endDate = getSentenceRs.getDate("end_date").toLocalDate();
                    //if release != null, visitDate <= release date
                    if(getSentenceRs.getDate("release_date") != null && checkUpDate.isAfter(getSentenceRs.getDate("release_date").toLocalDate()))
                        throw new RuntimeException("The occurrence date cannot be after the release  date of the sentence.");
                    //if null,visitDate <= endDAte
                    if(getSentenceRs.getDate("release_date") == null && checkUpDate.isAfter(endDate)) throw new RuntimeException("The check uo date cannot be after the  end date of the sentence.");
                }
            }
            try(PreparedStatement ps = connection.prepareStatement(UPDATE_ASSESS_QUERY)) {
                ps.setString(1,assess.getProcessCode());
                ps.setString(2,assess.getSentencesId());
                ps.setInt(3,assess.getPrisonerId());
                ps.setString(4,assess.getDateOfOccurrence());
                ps.setString(5,assess.getEventType());
                ps.setInt(6,assess.getLevel());
                ps.setString(7,assess.getNote());
                ps.setInt(8, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"update assess failed: ",e);
            throw new RuntimeException("Update assess failed.");
        }
    }

    @Override
    public void deleteAssess(int id) {
        //check status role
        boolean isRole = false;
        //check list role
        for (ApplicationState.RoleName r : ApplicationState.getInstance().getRoleName()) {
            if(r.equals(ApplicationState.RoleName.PRISONER_MANAGEMENT) || r.equals(ApplicationState.RoleName.ULTIMATE_AUTHORITY)) {
                isRole = true;
            }
        }
        //runtime if role not equal
        if(!isRole) throw new RuntimeException("You do not have permission to perform this operation.");
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(DELETE_ASSESS_QUERY)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"delete Failed: ",e);
            throw new RuntimeException("Delete Assess failed!");
        }
    }

    @Override
    public int getAssessId(String prisonerCode, String eventDate) {
        int assessId = -1;

        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_CODE_DATE_ASSESS_QUERY)) {
                ps.setString(1, prisonerCode);
                ps.setString(2, eventDate);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        assessId = rs.getInt("process_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assessId;
    }

    @Override
    public int getCountAssess() {
        int maxNumber = 0;
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(MAX_PROCESS_CODE_QUERY);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                maxNumber = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return maxNumber;
    }

    public Map<String, Integer> getBreachOfDisciplineData(){
         Map<String, Integer> data = new HashMap<>();
         try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
             PreparedStatement ps = connection.prepareStatement(BREACH_QUERY);
             ResultSet rs = ps.executeQuery();
             while (rs.next()) {
                 String prisonerName = rs.getString("prisoner_name");
                 int breachCount = rs.getInt("total_breach");
                 data.put(prisonerName, breachCount);
             }
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
         return data;
     }

    public Map<String, Integer> getBonusData(){
        Map<String, Integer> data = new HashMap<>();
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(BONUS_QUERY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String prisonerName = rs.getString("prisoner_name");
                int bonusCount  = rs.getInt("total_bonus");
                data.put(prisonerName, bonusCount );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}
