package com.example.psmsystem.service.managementvisitDao;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.database.DbConnection;
import com.example.psmsystem.model.managementvisit.IManagementVisitDao;
import com.example.psmsystem.model.managementvisit.ManagementVisit;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ManagementVisitDao implements IManagementVisitDao<ManagementVisit> {
    private static final String INSERT_QUERY = "INSERT INTO visit_log (sentence_id,  prisoner_id, visitor_name, identity_card, relationship, visit_date, start_time, end_time, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_MANAGEMENTVISIT_QUERY = "UPDATE visit_log SET sentence_id = ?, prisoner_id = ?, visitor_name = ?, identity_card = ?, relationship = ?, visit_date = ?, start_time = ?, end_time = ?, notes = ? WHERE Visit_log_id = ?";
    private static final String DELETE_MANAGEMENTVISIT_QUERY = "DELETE FROM visit_log WHERE Visit_log_id  = ?";
    private static final String SELECT_BY_MANAGEMENTVISIT_QUERY = "SELECT s.sentences_code, v.prisoner_id, p.prisoner_name, v.visitor_name,  v.identity_card, v.relationship, v.visit_date, v.start_time, v.end_time, v.notes FROM visit_log v JOIN prisoners p ON p.prisoner_id = v.prisoner_id JOIN sentences s ON s.sentence_id = v.sentence_id ORDER BY visit_date";
    private static final String SELECT_BY_CODE_DATE_MANAGEMENTVISIT_QUERY = "SELECT * FROM visit_log WHERE prisoner_id = ? AND visit_date = ?";
    private static final String COUNT_MANAGEMENTVISIT_QUERY = "SELECT COUNT(*) FROM visit_log";
    private static final String COUNT_VISITS_BY_MONTH_QUERY = "SELECT MONTH(visit_date) AS month, COUNT(*) AS visit_count FROM visit_log GROUP BY MONTH(visit_date)";

    @Override
    public void addManagementVisit(ManagementVisit managementVisit) {
        //check status role
        boolean isRole = false;
        //check list role
        for (ApplicationState.RoleName r : ApplicationState.getInstance().getRoleName()) {
            if (r.equals(ApplicationState.RoleName.VISIT_CONTROL) || r.equals(ApplicationState.RoleName.ULTIMATE_AUTHORITY)) {
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
                getSentencePs.setString(1, managementVisit.getSentenceId());
                ResultSet getSentenceRs = getSentencePs.executeQuery();
                if(!getSentenceRs.next()) throw new RuntimeException("Add Health failed: Sentence not found.");
                //set date
                LocalDate startDate = getSentenceRs.getDate("start_date").toLocalDate();
                //set date for visit date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate checkUpDate = LocalDate.parse(managementVisit.getVisitDate(),formatter);
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
                ps.setString(1,managementVisit.getSentenceId());
                ps.setInt(2,managementVisit.getPrisonerId());
                ps.setString(3,managementVisit.getVisitorName());
                ps.setString(4,managementVisit.getIdentityCard());
                ps.setString(5,managementVisit.getRelationship());
                ps.setString(6,managementVisit.getVisitDate());
                ps.setString(7,managementVisit.getStartTime());
                ps.setString(8,managementVisit.getEndTime());
                ps.setString(9,managementVisit.getNotes());

                ps.executeUpdate();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ManagementVisit> getManagementVisits() {
        List<ManagementVisit> ManagementVisitList = new ArrayList<>();
        try {

            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement(SELECT_BY_MANAGEMENTVISIT_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                ManagementVisit managementVisit = new ManagementVisit();
                managementVisit.setSentenceCode(rs.getInt("sentences_code"));
                managementVisit.setPrisonerId(rs.getInt("prisoner_id"));
                managementVisit.setPrisonerName(rs.getString("prisoner_name"));
                managementVisit.setVisitorName(rs.getString("visitor_name"));
                managementVisit.setIdentityCard(rs.getString("identity_card"));
                managementVisit.setRelationship(rs.getString("relationship"));
                managementVisit.setVisitDate(rs.getString("visit_date"));
                managementVisit.setStartTime(rs.getString("start_time"));
                managementVisit.setEndTime(rs.getString("end_time"));
                managementVisit.setNotes(rs.getString("notes"));
                ManagementVisitList.add(managementVisit);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ManagementVisitList;
    }

    @Override
    public void updateManagementVisit(ManagementVisit managementVisit, int id) {
        //check status role
        boolean isRole = false;
        //check list role
        for (ApplicationState.RoleName r : ApplicationState.getInstance().getRoleName()) {
            if (r.equals(ApplicationState.RoleName.VISIT_CONTROL) || r.equals(ApplicationState.RoleName.ULTIMATE_AUTHORITY)) {
                isRole = true;
                break;
            }
        }
        //runtime if role not equal
        if(!isRole) throw new RuntimeException("You do not have permission to perform this operation.");


        try(Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            //check visit date ith start end release,start,end of sentence
            //get sentence
            try (PreparedStatement getSentencePs = connection.prepareStatement("SELECT start_date , release_date , end_date FROM sentences WHERE sentence_id = ?")){
                getSentencePs.setString(1, managementVisit.getSentenceId());
                ResultSet getSentenceRs = getSentencePs.executeQuery();
                if(!getSentenceRs.next()) throw new RuntimeException("Add Health failed: Sentence not found.");
                //set date
                LocalDate startDate = getSentenceRs.getDate("start_date").toLocalDate();
                //set date for visit date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate checkUpDate = LocalDate.parse(managementVisit.getVisitDate(),formatter);
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
            try(PreparedStatement ps = connection.prepareStatement(UPDATE_MANAGEMENTVISIT_QUERY)) {
                ps.setString(1,managementVisit.getSentenceId());
                ps.setInt(2,managementVisit.getPrisonerId());
                ps.setString(3,managementVisit.getVisitorName());
                ps.setString(4,managementVisit.getIdentityCard());
                ps.setString(5,managementVisit.getRelationship());
                ps.setString(6,managementVisit.getVisitDate());
                ps.setString(7,managementVisit.getStartTime());
                ps.setString(8,managementVisit.getEndTime());
                ps.setString(9,managementVisit.getNotes());
                ps.setInt(10, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteManagementVisit(int id) {
        //check status role
        boolean isRole = false;
        //check list role
        for (ApplicationState.RoleName r : ApplicationState.getInstance().getRoleName()) {
            if (r.equals(ApplicationState.RoleName.VISIT_CONTROL) || r.equals(ApplicationState.RoleName.ULTIMATE_AUTHORITY)) {
                isRole = true;
                break;
            }
        }
        //runtime if role not equal
        if(!isRole) throw new RuntimeException("You do not have permission to perform this operation.");

        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(DELETE_MANAGEMENTVISIT_QUERY)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Delete visit failed!");
        }
    }

    @Override
    public int getVisitationId(int prisonerCode, String visitDate) {
        int visitationId = -1;

        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_CODE_DATE_MANAGEMENTVISIT_QUERY)) {
                ps.setInt(1, prisonerCode);
                ps.setString(2, visitDate);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        visitationId = rs.getInt("Visit_log_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return visitationId;
    }

    @Override
    public int getCountManagementVisit() {
        int count = 0;
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(COUNT_MANAGEMENTVISIT_QUERY);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1); // Lấy giá trị của cột đầu tiên (đếm tổng số bản ghi)
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return count;
    }

    public Map<String, Integer> countVisitsByMonth() {
        Map<String, Integer> visitsByMonth = new HashMap<>();

        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(COUNT_VISITS_BY_MONTH_QUERY);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String month = rs.getString("month");
                int visitCount = rs.getInt("visit_count");
                visitsByMonth.put(month, visitCount);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return visitsByMonth;
    }
}
