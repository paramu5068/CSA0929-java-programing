import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DatabaseInspector {
    public static void main(String[] args) throws Exception {
        try (Connection con = DriverManager.getConnection(System.getenv("DB_URL"),
                System.getenv("DB_USER"), System.getenv("DB_PASSWORD"))) {
            DatabaseMetaData md = con.getMetaData();
            System.out.println("Database: " + md.getDatabaseProductName());
            System.out.println("Database version: " + md.getDatabaseProductVersion());
            System.out.println("Driver: " + md.getDriverName());
            System.out.println("Driver version: " + md.getDriverVersion());
            try (ResultSet tables = md.getTables(con.getCatalog(), null, "%", new String[]{"TABLE"})) {
                System.out.println("Tables:");
                while (tables.next()) System.out.println(" - " + tables.getString("TABLE_NAME"));
            }
        }
    }
}
