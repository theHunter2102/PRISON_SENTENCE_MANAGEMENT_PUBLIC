package com.example.psmsystem.service.reportDao;

import com.example.psmsystem.database.DbConnection;
import com.example.psmsystem.model.managementvisit.ManagementVisit;
import com.example.psmsystem.model.report.IReportDao;
import com.example.psmsystem.model.report.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportDao implements IReportDao<Report> {
    private static final String SELECT_BY_REPORT_QUERY = "SELECT\n" +
            "    s.sentences_code,\n" +
            "    p.prisoner_name,\n" +
            "    COUNT(CASE WHEN ip.event_type = 'Bonus' THEN 1 ELSE NULL END) AS total_bonus,\n" +
            "    COUNT(CASE WHEN ip.event_type = 'Breach of discipline' THEN 1 ELSE NULL END) AS total_breach,\n" +
            "    s.parole_eligibility,\n" +
            "    s.status,\n" +
            "    h.level AS latest_health_level,\n" +
            "    s.release_date\n" +
            "FROM\n" +
            "    sentences s\n" +
            "    JOIN prisoners p ON s.prisoner_id = p.prisoner_id\n" +
            "    LEFT JOIN incareration_process ip ON s.sentence_id = ip.sentence_id\n" +
            "    LEFT JOIN (\n" +
            "        SELECT h1.sentence_id, h1.level\n" +
            "        FROM healths h1\n" +
            "        JOIN (\n" +
            "            SELECT sentence_id, MAX(checkup_date) AS latest_date\n" +
            "            FROM healths\n" +
            "            GROUP BY sentence_id\n" +
            "        ) h2 ON h1.sentence_id = h2.sentence_id AND h1.checkup_date = h2.latest_date\n" +
            "    ) h ON s.sentence_id = h.sentence_id\n" +
            "WHERE\n" +
            "    s.sentence_type = 'limited_time' AND s.status = false\n" +
            "GROUP BY\n" +
            "    s.sentences_code, p.prisoner_name, s.parole_eligibility, s.status, h.level, s.release_date;\n";

    private static final String UPDATE_SENTENCE_QUERY = "UPDATE sentences SET release_date = ?, parole_eligibility = ? WHERE sentences_code = ?";
    private static final String UPDATE_LOG_QUERY = "SELECT date_update FROM update_log WHERE id = 1;";

    @Override
    public List<Report> getAll() {
        List<Report> reports = new ArrayList<>();
        try {
            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement(SELECT_BY_REPORT_QUERY);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Report report = new Report();
                report.setSentenceCode(rs.getString("sentences_code"));
                report.setPrisonerName(rs.getString("prisoner_name"));
                report.setTotalReward(rs.getInt("total_bonus"));
                report.setTotalDiscipline(rs.getInt("total_breach"));
                report.setParoleEligibility(rs.getString("parole_eligibility"));
                report.setStatus(rs.getBoolean("status"));
                report.setReleaseDate(rs.getDate("release_date"));
                report.setLevel(rs.getInt("latest_health_level"));
                reports.add(report);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reports;
    }

    public boolean updateSentence(String sentenceCode, Date releaseDate, String paroleEligibility) {
        try {
            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement(UPDATE_SENTENCE_QUERY);
            statement.setDate(1, new java.sql.Date(releaseDate.getTime()));
            statement.setString(2, paroleEligibility);
            statement.setString(3, sentenceCode);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void updateUpdateLog(Date currentDate) {
        try {
            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) AS count FROM update_log");
            ResultSet rs = statement.executeQuery();
            int count = rs.next() ? rs.getInt("count") : 0;
            if (count == 0) {
                // Chưa có dữ liệu, thêm mới
                statement = conn.prepareStatement("INSERT INTO update_log (date_update) VALUES (?)");
                statement.setTimestamp(1, new java.sql.Timestamp(currentDate.getTime()));
            } else {
                // Đã có dữ liệu, cập nhật
                statement = conn.prepareStatement("UPDATE update_log SET date_update = ?");
                statement.setTimestamp(1, new java.sql.Timestamp(currentDate.getTime()));
            }
            statement.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String timeUpdate() {
        String timeUpdate = "";
        try {
            Connection conn = DbConnection.getDatabaseConnection().getConnection();
            PreparedStatement statement = conn.prepareStatement(UPDATE_LOG_QUERY);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // Lấy ngày giờ từ cột update_date trong ResultSet
                    Date date = rs.getTimestamp("date_update");

                    // Định dạng ngày giờ theo yêu cầu ("yyyy-MM-dd HH:mm:ss")
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    // Chuyển đổi ngày giờ thành chuỗi theo định dạng
                    timeUpdate = dateFormat.format(date);
                }
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return timeUpdate;
    }
}
