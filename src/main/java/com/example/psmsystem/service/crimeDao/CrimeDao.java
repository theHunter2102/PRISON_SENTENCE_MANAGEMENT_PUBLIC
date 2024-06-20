package com.example.psmsystem.service.crimeDao;

import com.example.psmsystem.ApplicationState;
import com.example.psmsystem.database.DbConnection;
import com.example.psmsystem.model.crime.Crime;
import com.example.psmsystem.model.crime.ICrimeDao;
import com.example.psmsystem.model.health.Health;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrimeDao implements ICrimeDao<Crime> {
    Logger LOGGER = Logger.getLogger(CrimeDao.class.getName());
    private static final String INSERT_QUERY = "INSERT INTO crimes (crime_name) VALUES (?)";
    private static final String UPDATE_CRIME_QUERY = "UPDATE crimes SET crime_name = ? WHERE crime_id = ?";
    private static final String DELETE_CRIME_QUERY = "DELETE FROM crimes WHERE crime_id = ?";
    private static final String SELECT_BY_CRIME_QUERY = "SELECT * FROM crimes";
    private static final String SELECT_BY_CODE_CRIME_QUERY = "SELECT * FROM crimes WHERE crime_name = ?";
    private static final String COUNT_CRIME_QUERY = "SELECT COUNT(*) FROM crimes";

    @Override
    public void addCrime(Crime crime) {
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
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY);
            ps.setString(1,crime.getSentenceCode());

            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"Add Crime failed: ",e);
            throw new RuntimeException("Add crime failed!");
        }
    }

    @Override
    public List<Crime> getCrime() {
        List<Crime> crimeList = new ArrayList<>();
        try {
            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement(SELECT_BY_CRIME_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Crime crime = new Crime();
                crime.setSentenceCode(rs.getString("crime_name"));
                crime.setCrimeName(rs.getString("crime_name"));
                crime.setCrimeId(rs.getInt("crime_id"));
                crimeList.add(crime);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return crimeList;
    }

    @Override
    public void updateCrime(Crime crime, int id) {
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

        try(Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(UPDATE_CRIME_QUERY)) {
                ps.setString(1,crime.getSentenceCode());
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCrime(int id) {
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
            try(PreparedStatement ps = connection.prepareStatement(DELETE_CRIME_QUERY)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,"delete crime failed: ",e);
            throw new RuntimeException("Add crime failed: ");
        }
    }

    @Override
    public int getCrimeId(String crimeName) {
        int crimeId = -1;

        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_CODE_CRIME_QUERY)) {
                ps.setString(1, crimeName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        crimeId = rs.getInt("crime_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return crimeId;
    }

    @Override
    public int getCountCrime() {
        int count = 0;
        try (Connection connection = DbConnection.getDatabaseConnection().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(COUNT_CRIME_QUERY);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1); // Lấy giá trị của cột đầu tiên (đếm tổng số bản ghi)
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return count;
    }
}
