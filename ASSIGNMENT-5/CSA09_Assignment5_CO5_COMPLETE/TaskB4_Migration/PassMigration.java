import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PassMigration {
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(System.getenv("DB_URL"),
                System.getenv("DB_USER"), System.getenv("DB_PASSWORD"));
    }

    public static void main(String[] args) throws Exception {
        Path input = Path.of("passes.csv");
        List<String[]> valid = new ArrayList<>();
        int lineNo = 0;
        try (BufferedReader r = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                lineNo++;
                String[] p = line.split(",", -1);
                try {
                    if (p.length != 5) throw new IllegalArgumentException("expected 5 fields");
                    if (p[0].isBlank() || p[1].isBlank() || p[2].isBlank())
                        throw new IllegalArgumentException("blank required field");
                    int route = Integer.parseInt(p[3]);
                    double amount = Double.parseDouble(p[4]);
                    if (route < 0 || amount < 0) throw new IllegalArgumentException("negative route/amount");
                    valid.add(p);
                } catch (RuntimeException ex) {
                    System.out.println("Rejected line " + lineNo + ": " + ex.getMessage());
                }
            }
        }

        try (Connection con = connect()) {
            int loaded = PassLoader.loadAll(con, valid);
            System.out.println("Valid records submitted: " + valid.size());
            System.out.println("Rows loaded: " + loaded);
        }
    }
}
