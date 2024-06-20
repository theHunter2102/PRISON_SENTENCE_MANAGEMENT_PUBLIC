package com.example.psmsystem.service;

import com.example.psmsystem.database.DbConnection;
import com.example.psmsystem.dto.CommendationDTO;
import com.example.psmsystem.model.Commendation;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommendationDAO {
    private final static Logger LOGGER = Logger.getLogger(CommendationDAO.class.getName());

    public List<CommendationDTO> getAll(int page, int pageSize) {
        List<CommendationDTO> commendationDTOS = new ArrayList<>();
        int offset =(page - 1) * pageSize;
        try(Connection connection = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT s.sentences_code, c.* " +
                    "FROM commendations c " +
                    "JOIN sentences s ON c.sentence_id = s.sentence_id " +
                    "LIMIT ? OFFSET ?")) {
            ps.setInt(1,pageSize);
            ps.setInt(2,offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CommendationDTO commendationDTO = new CommendationDTO();
                commendationDTO.setInformationLevel(rs.getInt("level"));
                commendationDTO.setSentenceCode(rs.getInt("sentences_code"));
                commendationDTO.setCommendation(new Commendation());
                commendationDTO.getCommendation().setId(rs.getInt("commendation_id")) ;
                commendationDTO.getCommendation().setSentenceId(rs.getInt("sentence_id")); ;
                commendationDTO.getCommendation().setDateOfOccurrence(rs.getDate("date_of_occurrence")); ;
                commendationDTO.getCommendation().setLevel(rs.getInt("level")); ;
                commendationDTO.getCommendation().setNote(rs.getString("note")); ;

                commendationDTOS.add(commendationDTO);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Failed: ",e.getMessage());
            throw new RuntimeException("An Error in system. Please try again");
        }
        return commendationDTOS;
    }


    public Commendation add(Commendation object) {
        //check date
        if(object.getDateOfOccurrence() != null
                && object.getDateOfOccurrence().after(Calendar.getInstance().getTime()))
            throw new RuntimeException("The date of occurrence must be in the past or present.");
        //check level
        if(object.getLevel() < 1 || object.getLevel() > 4)
            throw new RuntimeException("Level must be: 1- 4");
        //add
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()){
            //check sentence
            try (PreparedStatement isSentencePs = connection.prepareStatement("SELECT * FROM sentences WHERE sentences_id = ?")){
                isSentencePs.setInt(1,object.getSentence_id());
                ResultSet isSentenceRs = isSentencePs.executeQuery();
                if(!isSentenceRs.next()) throw  new RuntimeException("Sentence not found!");
                //set sentence_id
                object.setSentenceId(isSentenceRs.getInt("sentence_id"));
            }
            //add
            try (PreparedStatement addDisciplinaryMeasurePs = connection.prepareStatement("INSERT INTO `commendations` (`sentence_id`, `date_of_occurrence`, `level`, `note`) VALUES (?,?,?,?,?)")){
                addDisciplinaryMeasurePs.setInt(1,object.getSentence_id());
                if(object.getDateOfOccurrence() != null) {
                    addDisciplinaryMeasurePs.setDate(2, (java.sql.Date) object.getDateOfOccurrence());
                } else {
                    addDisciplinaryMeasurePs.setNull(2, Types.DATE);
                }
                addDisciplinaryMeasurePs.setInt(3,object.getLevel());
                addDisciplinaryMeasurePs.setString(4,object.getNote());
            }
        } catch (SQLException  e) {
            LOGGER.log(Level.SEVERE,"add failed: ", e.getMessage());
            throw new RuntimeException("An Error in system. Please try again");
        }
        return object;
    }

    public CommendationDTO getOneById(int id) {
        try(Connection connection = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT s.sentences_code, c.* " +
                    "FROM commendations c " +
                    "JOIN sentences s ON c.sentence_id = s.sentence_id" +
                    " WHERE c.commendation_id = ?")) {
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();

            if(!rs.next()) throw new RuntimeException("Commendation not found");
            CommendationDTO CommendationDTO = new CommendationDTO();
            CommendationDTO.setInformationLevel(rs.getInt("level"));
            CommendationDTO.setSentenceCode(rs.getInt("sentences_code"));
            CommendationDTO.setCommendation(new Commendation());
            CommendationDTO.getCommendation().setId(rs.getInt("commendation_id")) ;
            CommendationDTO.getCommendation().setSentenceId(rs.getInt("sentence_id")); ;
            CommendationDTO.getCommendation().setDateOfOccurrence(rs.getDate("date_of_occurrence")); ;
            CommendationDTO.getCommendation().setLevel(rs.getInt("level")); ;
            CommendationDTO.getCommendation().setNote(rs.getString("note"));


            return CommendationDTO;
        } catch (SQLException  e) {
            LOGGER.log(Level.SEVERE,"Failed: ",e.getMessage());
            throw new RuntimeException("An Error in system. Please try again");
        }
    }

    public void update(Commendation object) {
        //check date
        if(object.getDateOfOccurrence() != null
                && object.getDateOfOccurrence().after(Calendar.getInstance().getTime()))
            throw new RuntimeException("The date of occurrence must be in the past or present.");
        //check level
        if(object.getLevel() < 1 || object.getLevel() > 4)
            throw new RuntimeException("Level must be: 1- 4");
        //update
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()){
            //check sentence
            try (PreparedStatement isSentencePs = connection.prepareStatement("SELECT * FROM sentences WHERE sentences_id = ?")){
                isSentencePs.setInt(1,object.getSentence_id());
                ResultSet isSentenceRs = isSentencePs.executeQuery();
                if(!isSentenceRs.next()) throw  new RuntimeException("Sentence not found!");
                //set sentence_id
                object.setSentenceId(isSentenceRs.getInt("sentence_id"));
            }
            //update
            try (PreparedStatement updateDisciplinaryMeasurePs = connection.prepareStatement("UPDATE `commendations` SET `sentence_id` = ?, `date_of_occurrence` = ?, `level` = ?, `note` = ? WHERE `commendation_id` = ?")){
                updateDisciplinaryMeasurePs.setInt(1,object.getSentence_id());
                if(object.getDateOfOccurrence() != null) {
                    updateDisciplinaryMeasurePs.setDate(2, (java.sql.Date) object.getDateOfOccurrence());
                } else {
                    updateDisciplinaryMeasurePs.setNull(2,Types.DATE);
                }
                updateDisciplinaryMeasurePs.setInt(3,object.getLevel());
                updateDisciplinaryMeasurePs.setString(4,object.getNote());
                updateDisciplinaryMeasurePs.setInt(5, object.getId());
            }
        }catch (SQLException  e) {
            LOGGER.log(Level.SEVERE,"Failed: ",e.getMessage());
            throw new RuntimeException("An Error in system. Please try again");
        }
    }

    public void deleteById(int id) {
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()){
            try (PreparedStatement isCommendationPs = connection.prepareStatement("SELECT * FROM commendations WHERE commendation_id = ?")){
                ResultSet isCommendationRs = isCommendationPs.executeQuery();
                if(!isCommendationRs.next()) throw new RuntimeException("Commendation not found");
            }
            try (PreparedStatement deleteCommendationPs = connection.prepareStatement("DELETE  FROM commendations WHERE commendation_id = ?")){
                deleteCommendationPs.setInt(1,id);
                deleteCommendationPs.executeUpdate();
            }
        }catch (SQLException  e) {
            LOGGER.log(Level.SEVERE,"delete failed: ", e.getMessage());
            throw new RuntimeException("An Error in system. Please try again");
        }
    }
}
