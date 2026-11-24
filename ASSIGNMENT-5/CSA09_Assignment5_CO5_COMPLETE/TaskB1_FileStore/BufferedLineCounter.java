import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BufferedLineCounter {
    public static long countBuffered(Path file) throws IOException {
        long lines = 0;
        byte[] buffer = new byte[64 * 1024];
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file.toFile()), buffer.length)) {
            int n;
            while ((n = in.read(buffer)) != -1) {
                for (int i = 0; i < n; i++) {
                    if (buffer[i] == '\n') lines++;
                }
            }
        }
        return lines;
    }

    public static long countUnbuffered(Path file) throws IOException {
        long lines = 0;
        try (FileInputStream in = new FileInputStream(file.toFile())) {
            int value;
            while ((value = in.read()) != -1) {
                if (value == '\n') lines++;
            }
        }
        return lines;
    }

    public static void main(String[] args) throws IOException {
        Path p = Files.createTempFile("attendance", ".log");
        String row = "student,2026-11-16,present\n";
        try (var out = Files.newBufferedWriter(p)) {
            for (int i = 0; i < 100_000; i++) out.write(row);
        }
        System.out.println("Lines (buffered): " + countBuffered(p));
        System.out.println("Lines (unbuffered): " + countUnbuffered(p));
        System.out.println("File bytes: " + Files.size(p));
        Files.deleteIfExists(p);
    }
}
