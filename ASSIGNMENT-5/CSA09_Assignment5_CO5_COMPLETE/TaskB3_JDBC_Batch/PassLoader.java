import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PassLoader {
    /** Loads records in batches of 1,000 inside one transaction. */
    public static int loadAll(Connection con, List<String[]> passes) throws SQLException {
        String sql = "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES (?, ?, ?, ?, ?)";
        boolean oldAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        int n = 0;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String[] p : passes) {
                if (p == null || p.length != 5) {
                    throw new IllegalArgumentException("Each pass must contain exactly five fields");
                }
                ps.setString(1, p[0]);
                ps.setString(2, p[1]);
                ps.setString(3, p[2]);
                ps.setInt(4, Integer.parseInt(p[3]));
                ps.setDouble(5, Double.parseDouble(p[4]));
                ps.addBatch();
                n++;
                if (n % 1000 == 0) ps.executeBatch();
            }
            if (n % 1000 != 0) ps.executeBatch();
            con.commit();
            return n;
        } catch (SQLException | RuntimeException e) {
            try {
                con.rollback();
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);
            }
            throw e;
        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }
}
