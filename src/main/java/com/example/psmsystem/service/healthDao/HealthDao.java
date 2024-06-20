package com.example.psmsystem.service.healthDao;

import com.example.psmsystem.database.DbConnection;
import com.example.psmsystem.model.health.Health;
import com.example.psmsystem.model.health.IHealthDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HealthDao implements IHealthDao<Health> {
    Logger LOGGER = Logger.getLogger(HealthDao.class.getName());
    private static final String INSERT_QUERY = "INSERT INTO healths (health_code, sentence_id, prisoner_id, weight, height, checkup_date, status, level) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_HEALTH_QUERY = "UPDATE healths SET health_code =?,sentence_id = ?, prisoner_id = ?, weight = ?, height = ?, checkup_date = ?, status = ?, level = ? WHERE health_id = ?";
    private static final String DELETE_HEALTH_QUERY = "DELETE FROM healths WHERE health_id = ?";
    private static final String SELECT_BY_HEALTH_QUERY = "SELECT h.*,s.sentences_code,p.prisoner_name " +
            "FROM healths h JOIN sentences s ON s.sentence_id = h.sentence_id " +
            "JOIN prisoners p ON p.prisoner_id = h.prisoner_id " +
            "ORDER BY h.checkup_date";
    private static final String SELECT_BY_CODE_DATE_HEALTH_QUERY = "SELECT * FROM healths WHERE hearthCode = ? AND checkup_date = ?";
    private static final String MAX_HEALTH_CODE_QUERY = "SELECT MAX(CAST(SUBSTRING(health_code, 2) AS UNSIGNED)) AS max_health_code FROM healths WHERE health_code REGEXP '^H[0-9]+$'";

    private static final String GET_HEALTH_DATA_BY_MONTH_YEAR = "SELECT "
            + "MONTH(checkup_date) AS month_number, "
            + "SUM(CASE WHEN status = false THEN 1 ELSE 0 END) AS strong_count, "
            + "SUM(CASE WHEN status = true THEN 1 ELSE 0 END) AS weak_count "
            + "FROM healths "
            + "WHERE YEAR(checkup_date) = ? "
            + "GROUP BY month_number";
    private static final String SELECT_DISTINCT_YEARS = "SELECT DISTINCT YEAR(checkup_date) AS year FROM healths ORDER BY year DESC";

    @Override
    public void addHealth(Health health) {
        try(Connection connection = DbConnection.getDatabaseConnection().getConnection())
        {
            //check visit date ith start end release,start,end of sentence
            //get sentence
            try (PreparedStatement getSentencePs = connection.prepareStatement("SELECT start_date , release_date , end_date FROM sentences WHERE sentence_id = ?")){
                getSentencePs.setString(1, health.getSentenceId());
                ResultSet getSentenceRs = getSentencePs.executeQuery();
                if(!getSentenceRs.next()) throw new RuntimeException("Add Health failed: Sentence not found.");
                //set date
                LocalDate startDate = getSentenceRs.getDate("start_date").toLocalDate();
                //set date for visit date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate checkUpDate = LocalDate.parse(health.getCheckupDate(),formatter);
                //check visit date >= start date
                if(checkUpDate.isBefore(startDate) || checkUpDate.isAfter(LocalDate.now())) throw new RuntimeException("The Check-up Date date cannot be before the start date of the sentence.");
                //check type limited time
                if(getSentenceRs.getDate("end_date") != null) {
                    LocalDate endDate = getSentenceRs.getDate("end_date").toLocalDate();
                    //if release != null, visitDate <= release date
                    if(getSentenceRs.getDate("release_date") != null && checkUpDate.isAfter(getSentenceRs.getDate("release_date").toLocalDate()))
                        throw new RuntimeException("The check up date cannot be after the release  date of the sentence.");
                    //if null,visitDate <= endDAte
                    if(getSentenceRs.getDate("release_date") == null && checkUpDate.isAfter(endDate)) throw new RuntimeException("The check uo date cannot be after the  end date of the sentence.");
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);){
                ps.setString(1,health.getHealthCode());
                ps.setString(2,health.getSentenceId());
                ps.setInt(3,health.getPrisonerId());
                ps.setDouble(4,health.getWeight());
                ps.setDouble(5,health.getHeight());
                ps.setString(6,health.getCheckupDate());
                ps.setBoolean(7, health.getLevel() != 0);
                ps.setInt(8,health.getLevel());
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Add health failed: ",e);
            throw new RuntimeException("Add Health failed!");
        }
    }

    @Override
    public List<Health> getHealth() {
        List<Health> healthList = new ArrayList<>();
        try {

            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement(SELECT_BY_HEALTH_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Health health = new Health();
                health.setHealthCode(rs.getString("health_code"));
                health.setPrisonerId(rs.getInt("prisoner_id"));
                health.setSentenceId(rs.getString("sentence_id"));
                health.setSentenceCode(rs.getInt("sentences_code"));
                health.setPrisonerName(rs.getString("prisoner_name"));
                health.setWeight(rs.getDouble("weight"));
                health.setHeight(rs.getDouble("height"));
                health.setCheckupDate(rs.getString("checkup_date"));
                health.setStatus(rs.getBoolean("status"));
                health.setLevel(rs.getInt("level"));
                health.setId(rs.getInt("health_id"));
                healthList.add(health);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return healthList;
    }

    @Override
    public void updateHealth(Health health, int id) {

        try(Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            //check visit date ith start end release,start,end of sentence
            //get sentence
            try (PreparedStatement getSentencePs = connection.prepareStatement("SELECT start_date , release_date , end_date FROM sentences WHERE sentence_id = ?")){
                getSentencePs.setString(1, health.getSentenceId());
                ResultSet getSentenceRs = getSentencePs.executeQuery();
                if(!getSentenceRs.next()) throw new RuntimeException("Add Health failed: Sentence not found.");
                //set date
                LocalDate startDate = getSentenceRs.getDate("start_date").toLocalDate();
                //set date for visit date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate checkUpDate = LocalDate.parse(health.getCheckupDate(),formatter);
                //check visit date >= start date
                if(checkUpDate.isBefore(startDate) || checkUpDate.isAfter(LocalDate.now())) throw new RuntimeException("The Check-up Date date cannot be before the start date of the sentence.");
                //check type limited time
                if(getSentenceRs.getDate("end_date") != null) {
                    LocalDate endDate = getSentenceRs.getDate("end_date").toLocalDate();
                    //if release != null, visitDate <= release date
                    if(getSentenceRs.getDate("release_date") != null && checkUpDate.isAfter(getSentenceRs.getDate("release_date").toLocalDate()))
                        throw new RuntimeException("The check up date cannot be after the release  date of the sentence.");
                    //if null,visitDate <= endDAte
                    if(getSentenceRs.getDate("release_date") == null && checkUpDate.isAfter(endDate)) throw new RuntimeException("The check uo date cannot be after the  end date of the sentence.");
                }
            }
            try(PreparedStatement ps = connection.prepareStatement(UPDATE_HEALTH_QUERY)) {
                ps.setString(1,health.getHealthCode());
                ps.setString(2,health.getSentenceId());
                ps.setInt(3,health.getPrisonerId());
                ps.setDouble(4,health.getWeight());
                ps.setDouble(5,health.getHeight()/100);
                ps.setString(6,health.getCheckupDate());
                ps.setBoolean(7,health.getLevel() != 0);
                ps.setInt(8,health.getLevel());
                ps.setInt(9, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Update health failed: ",e);
            throw new RuntimeException("Update health failed. ");
        }
    }

    @Override
    public void deleteHealth(int id) {
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(DELETE_HEALTH_QUERY)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getVisitationId(String prisonerCode, String checkupDate) {
        int healthId = -1;

        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_CODE_DATE_HEALTH_QUERY)) {
                ps.setString(1, prisonerCode);
                ps.setString(2, checkupDate);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        healthId = rs.getInt("health_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return healthId;
    }

    @Override
    public int getCountHealth() {
        int maxNumber = 0;
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(MAX_HEALTH_CODE_QUERY);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                maxNumber = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return maxNumber;
    }

    public Map<Integer, Integer> getStrongHealthDataByMonthYear(int year) {
        Map<Integer, Integer> strongHealthDataMap = new HashMap<>();
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_HEALTH_DATA_BY_MONTH_YEAR)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int monthNumber = rs.getInt("month_number");
                int strongCount = rs.getInt("strong_count");
                strongHealthDataMap.put(monthNumber, strongCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return strongHealthDataMap;
    }

    public Map<Integer, Integer> getWeakHealthDataByMonthYear(int year) {
        Map<Integer, Integer> weakHealthDataMap = new HashMap<>();
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection();
             PreparedStatement ps = connection.prepareStatement(GET_HEALTH_DATA_BY_MONTH_YEAR)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int monthNumber = rs.getInt("month_number");
                int weakCount = rs.getInt("weak_count");
                weakHealthDataMap.put(monthNumber, weakCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return weakHealthDataMap;
    }

    public List<Integer> getListYear() {
        List<Integer> years = new ArrayList<>();
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DISTINCT_YEARS)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                years.add(rs.getInt("year"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exception
        }
        return years;
    }
}
