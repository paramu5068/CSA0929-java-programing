import java.sql.*;
import java.util.UUID;

public class JdbcInsertBenchmark {
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(System.getenv("DB_URL"),
                System.getenv("DB_USER"), System.getenv("DB_PASSWORD"));
    }

    private static void clear(Connection c) throws SQLException {
        try (Statement s = c.createStatement()) { s.executeUpdate("DELETE FROM bus_pass"); }
    }

    private static long runStatement(Connection c, int count) throws SQLException {
        clear(c);
        long start = System.nanoTime();
        try (Statement s = c.createStatement()) {
            for (int i = 0; i < count; i++) {
                String sql = "INSERT INTO bus_pass VALUES ('S" + i + "','R" + i
                        + "','Student" + i + "'," + (i % 20) + ",3500.00)";
                s.executeUpdate(sql);
            }
        }
        return System.nanoTime() - start;
    }

    private static long runPrepared(Connection c, int count) throws SQLException {
        clear(c);
        long start = System.nanoTime();
        try (PreparedStatement p = c.prepareStatement(
                "INSERT INTO bus_pass VALUES (?,?,?,?,?)")) {
            for (int i = 0; i < count; i++) {
                p.setString(1, "P" + i); p.setString(2, "R" + i);
                p.setString(3, "Student" + i); p.setInt(4, i % 20); p.setDouble(5, 3500);
                p.executeUpdate();
            }
        }
        return System.nanoTime() - start;
    }

    private static long runBatch(Connection c, int count) throws SQLException {
        clear(c);
        long start = System.nanoTime();
        boolean old = c.getAutoCommit();
        c.setAutoCommit(false);
        try (PreparedStatement p = c.prepareStatement(
                "INSERT INTO bus_pass VALUES (?,?,?,?,?)")) {
            for (int i = 0; i < count; i++) {
                p.setString(1, "B" + i); p.setString(2, "R" + i);
                p.setString(3, "Student" + i); p.setInt(4, i % 20); p.setDouble(5, 3500);
                p.addBatch();
                if ((i + 1) % 1000 == 0) p.executeBatch();
            }
            p.executeBatch();
            c.commit();
        } catch (SQLException ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(old);
        }
        return System.nanoTime() - start;
    }

    public static void main(String[] args) throws Exception {
        int[] sizes = {10, 1000, 10000, 100000};
        try (Connection c = connect()) {
            for (int n : sizes) {
                System.out.printf("n=%d Statement_ms=%.3f%n", n, runStatement(c, n) / 1e6);
                System.out.printf("n=%d Prepared_ms=%.3f%n", n, runPrepared(c, n) / 1e6);
                System.out.printf("n=%d Batch_ms=%.3f%n", n, runBatch(c, n) / 1e6);
            }
            try (Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) AS total FROM bus_pass")) {
                if (rs.next()) System.out.println("Rows after final batch: " + rs.getInt("total"));
            }
            DatabaseMetaData md = c.getMetaData();
            System.out.println("DB: " + md.getDatabaseProductName() + " " + md.getDatabaseProductVersion());
            System.out.println("Driver: " + md.getDriverName() + " " + md.getDriverVersion());
        }
    }
}
