import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ResourceDemo {
    static void writeReportManual(List<Path> stores, Path report) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(report);
            for (Path store : stores) {
                try (BufferedReader reader = Files.newBufferedReader(store)) {
                    String line;
                    while ((line = reader.readLine()) != null) writer.write(line + System.lineSeparator());
                }
            }
        } finally {
            if (writer != null) writer.close();
        }
    }

    static void writeReportSafe(List<Path> stores, Path report) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(report)) {
            for (Path store : stores) {
                try (BufferedReader reader = Files.newBufferedReader(store)) {
                    String line;
                    while ((line = reader.readLine()) != null) writer.write(line + System.lineSeparator());
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("sales-demo");
        Path a = dir.resolve("store1.txt"), b = dir.resolve("store2.txt");
        Files.writeString(a, "Store 1: 1200\n");
        Files.writeString(b, "Store 2: 950\n");
        List<Path> inputs = List.of(a, b);
        Path manual = dir.resolve("manual-report.txt");
        Path safe = dir.resolve("safe-report.txt");
        writeReportManual(inputs, manual);
        writeReportSafe(inputs, safe);
        System.out.println("Manual report:\n" + Files.readString(manual).trim());
        System.out.println("Try-with-resources report:\n" + Files.readString(safe).trim());
        System.out.println("Reports identical: " + Files.readString(manual).equals(Files.readString(safe)));
    }
}
