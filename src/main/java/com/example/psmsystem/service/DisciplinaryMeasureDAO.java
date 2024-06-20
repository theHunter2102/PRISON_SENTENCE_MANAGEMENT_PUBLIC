package com.example.psmsystem.service;

import com.example.psmsystem.database.DbConnection;
import com.example.psmsystem.dto.DisciplinaryMeasureDTO;
import com.example.psmsystem.model.DisciplinaryMeasure;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DisciplinaryMeasureDAO {
    private final static Logger LOGGER = Logger.getLogger(DisciplinaryMeasureDAO.class.getName());

    public List<DisciplinaryMeasureDTO> getAll() {
        List<DisciplinaryMeasureDTO> DisciplinaryMeasureDTOS = new ArrayList<>();
        try(Connection connection = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT s.sentences_code, d.* " +
                    "FROM disciplinary_measures d " +
                    "JOIN sentences s ON d.sentence_id = s.sentence_id ")) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DisciplinaryMeasureDTO DisciplinaryMeasureDTO = new DisciplinaryMeasureDTO();
                DisciplinaryMeasureDTO.setInformationLevel(rs.getInt("level"));
                DisciplinaryMeasureDTO.setSentenceCode(rs.getInt("sentences_code"));
                DisciplinaryMeasureDTO.setDisciplinaryMeasure(new DisciplinaryMeasure());
                DisciplinaryMeasureDTO.getDisciplinaryMeasure().setId(rs.getInt("disciplinary_measure_id")) ;
                DisciplinaryMeasureDTO.getDisciplinaryMeasure().setSentenceId(rs.getInt("sentence_id")); ;
                DisciplinaryMeasureDTO.getDisciplinaryMeasure().setDateOfOccurrence(rs.getDate("date_of_occurrence")); ;
                DisciplinaryMeasureDTO.getDisciplinaryMeasure().setLevel(rs.getInt("level")); ;
                DisciplinaryMeasureDTO.getDisciplinaryMeasure().setNote(rs.getString("note")); ;

                DisciplinaryMeasureDTOS.add(DisciplinaryMeasureDTO);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Failed: ",e.getMessage());
            throw new RuntimeException("An Error in system. Please try again");
        }
        return DisciplinaryMeasureDTOS;
    }


    public DisciplinaryMeasure add(DisciplinaryMeasure object) {
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
            try (PreparedStatement addDisciplinaryMeasurePs = connection.prepareStatement("INSERT INTO `disciplinary_measures` (`sentence_id`, `date_of_occurrence`, `level`, `note`) VALUES (?,?,?,?,?)")){
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

    public DisciplinaryMeasureDTO getOneById(int id) {
        try(Connection connection = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT s.sentences_code, d.* " +
                    "FROM disciplinary_measures d " +
                    "JOIN sentences s ON s.sentence_id = s.sentence_id" +
                    " WHERE d.disciplinary_measure_id = ?")) {
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();

            if(!rs.next()) throw new RuntimeException("DisciplinaryMeasure not found");
            DisciplinaryMeasureDTO DisciplinaryMeasureDTO = new DisciplinaryMeasureDTO();
            DisciplinaryMeasureDTO.setInformationLevel(rs.getInt("level"));
            DisciplinaryMeasureDTO.setSentenceCode(rs.getInt("sentences_code"));
            DisciplinaryMeasureDTO.setDisciplinaryMeasure(new DisciplinaryMeasure());
            DisciplinaryMeasureDTO.getDisciplinaryMeasure().setId(rs.getInt("DisciplinaryMeasure_id")) ;
            DisciplinaryMeasureDTO.getDisciplinaryMeasure().setSentenceId(rs.getInt("sentence_id")); ;
            DisciplinaryMeasureDTO.getDisciplinaryMeasure().setDateOfOccurrence(rs.getDate("date_of_occurrence")); ;
            DisciplinaryMeasureDTO.getDisciplinaryMeasure().setLevel(rs.getInt("level")); ;
            DisciplinaryMeasureDTO.getDisciplinaryMeasure().setNote(rs.getString("note"));


            return DisciplinaryMeasureDTO;
        } catch (SQLException  e) {
            LOGGER.log(Level.SEVERE,"Failed: ",e.getMessage());
            throw new RuntimeException("An Error in system. Please try again");
        }
    }

    public void update(DisciplinaryMeasure object) {
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
            try (PreparedStatement updateDisciplinaryMeasurePs = connection.prepareStatement("UPDATE `disciplinary_measures` SET `sentence_id` = ?, `date_of_occurrence` = ?, `level` = ?, `note` = ? WHERE `disciplinary_measures` = ?")){
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
            try (PreparedStatement isDisciplinaryMeasurePs = connection.prepareStatement("SELECT * FROM disciplinary_measures WHERE DisciplinaryMeasure_id = ?")){
                ResultSet isDisciplinaryMeasureRs = isDisciplinaryMeasurePs.executeQuery();
                if(!isDisciplinaryMeasureRs.next()) throw new RuntimeException("DisciplinaryMeasure not found");
            }
            try (PreparedStatement deleteDisciplinaryMeasurePs = connection.prepareStatement("DELETE  FROM disciplinary_measures WHERE DisciplinaryMeasure_id = ?")){
                deleteDisciplinaryMeasurePs.setInt(1,id);
                deleteDisciplinaryMeasurePs.executeUpdate();
            }
        }catch (SQLException  e) {
            LOGGER.log(Level.SEVERE,"delete failed: ", e.getMessage());
            throw new RuntimeException("An Error in system. Please try again");
        }
    }
}
