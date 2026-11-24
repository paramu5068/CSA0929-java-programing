import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Optional local MySQL demonstration. Uses only a disposable test database.
 * Environment: DB_URL, DB_USER, DB_PASSWORD.
 */
public class LoginJdbcDemo {
    private static Connection connect() throws Exception {
        return DriverManager.getConnection(
                System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASSWORD"));
    }

    public static void main(String[] args) throws Exception {
        try (Connection con = connect(); Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS login_demo (id VARCHAR(80), pwd_hash VARCHAR(120))");
            st.executeUpdate("DELETE FROM login_demo");
            st.executeUpdate("INSERT INTO login_demo VALUES ('student1','hash-not-a-real-password')");
            String craftedId = "' OR '1'='1' -- ";
            String wrongPassword = "invalid";
            String unsafeSql = "SELECT * FROM login_demo WHERE id='" + craftedId
                    + "' AND pwd_hash='" + wrongPassword + "'";
            try (ResultSet rs = st.executeQuery(unsafeSql)) {
                System.out.println("Unsafe concatenated query accepted a row: " + rs.next());
            }
            String safeSql = "SELECT * FROM login_demo WHERE id=? AND pwd_hash=?";
            try (PreparedStatement ps = con.prepareStatement(safeSql)) {
                ps.setString(1, craftedId);
                ps.setString(2, wrongPassword);
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("PreparedStatement accepted a row: " + rs.next());
                }
            }
            st.executeUpdate("DROP TABLE login_demo");
        }
    }
}
