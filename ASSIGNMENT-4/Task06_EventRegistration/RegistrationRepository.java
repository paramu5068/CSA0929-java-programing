import java.sql.*;
import java.util.*;

public class RegistrationRepository {
    private final String url = System.getenv().getOrDefault("DB_URL",
            "jdbc:mysql://localhost:3306/campus_events");
    private final String user = System.getenv().getOrDefault("DB_USER", "root");
    private final String password = System.getenv().getOrDefault("DB_PASSWORD", "");

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void insert(String studentId, String name, String email, int eventId) throws SQLException {
        String sql = "INSERT INTO registrations(student_id, student_name, email, event_id) VALUES (?, ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setInt(4, eventId);
            ps.executeUpdate();
        }
    }

    public List<Registration> findPage(Integer eventId, int limit, int offset) throws SQLException {
        String sql = "SELECT r.id, r.student_id, r.student_name, r.email, e.event_name, " +
                     "CAST(r.registered_at AS CHAR) AS registered_at " +
                     "FROM registrations r JOIN events e ON r.event_id=e.id " +
                     (eventId == null ? "" : "WHERE r.event_id=? ") +
                     "ORDER BY r.id DESC LIMIT ? OFFSET ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            if (eventId != null) ps.setInt(i++, eventId);
            ps.setInt(i++, limit);
            ps.setInt(i, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Registration> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(new Registration(rs.getInt("id"), rs.getString("student_id"),
                            rs.getString("student_name"), rs.getString("email"),
                            rs.getString("event_name"), rs.getString("registered_at")));
                }
                return rows;
            }
        }
    }
}
