import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple five-field comma-delimited teaching store:
 * passId,regNo,name,routeNo,amount
 * Damaged records are skipped. Production CSV should use a proper CSV parser.
 */
public class PassStore {
    private final Path file;

    public PassStore(Path file) {
        this.file = file;
    }

    public void add(String passId, String regNo, String name, int route, double amount)
            throws IOException {
        if (passId == null || passId.isBlank() || regNo == null || regNo.isBlank()
                || name == null || name.isBlank() || route < 0 || amount < 0) {
            throw new IllegalArgumentException("Invalid pass data");
        }
        if (passId.contains(",") || regNo.contains(",") || name.contains(",")
                || name.contains("\n") || name.contains("\r")) {
            throw new IllegalArgumentException("Commas/newlines are not supported by this simple format");
        }
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(String.join(",", passId, regNo, name,
                    Integer.toString(route), Double.toString(amount)));
            w.newLine();
        }
    }

    /** Returns the five fields for passId, or null if no valid matching row exists. */
    public String[] find(String passId) throws IOException {
        if (!Files.exists(file)) return null;
        try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (fields.length != 5) continue;
                try {
                    Integer.parseInt(fields[3]);
                    Double.parseDouble(fields[4]);
                } catch (NumberFormatException ex) {
                    continue;
                }
                if (fields[0].equals(passId)) return fields;
            }
        }
        return null;
    }

    public List<String[]> listByRoute(int route) throws IOException {
        List<String[]> result = new ArrayList<>();
        if (!Files.exists(file)) return result;
        try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (fields.length != 5) continue;
                try {
                    if (Integer.parseInt(fields[3]) == route) {
                        Double.parseDouble(fields[4]);
                        result.add(fields);
                    }
                } catch (NumberFormatException ignored) {
                    // Skip damaged numeric fields.
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        Path f = Files.createTempFile("passes", ".csv");
        PassStore store = new PassStore(f);
        store.add("P1001", "192311001", "Arun", 12, 4500);
        Files.writeString(f, "P1002,DAMAGED LINE\n", StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
        store.add("P1003", "192311003", "Divya", 7, 3800);
        System.out.println(java.util.Arrays.toString(store.find("P1003")));
        System.out.println(java.util.Arrays.toString(store.find("P1002")));
        System.out.println("Route 7 count: " + store.listByRoute(7).size());
        Files.deleteIfExists(f);
    }
}
