import java.nio.file.Files;
import java.nio.file.Path;

public class PassStoreBenchmark {
    public static void main(String[] args) throws Exception {
        int[] sizes = {1_000, 10_000, 50_000, 100_000};
        for (int size : sizes) {
            Path f = Files.createTempFile("pass-growth", ".csv");
            PassStore store = new PassStore(f);
            for (int i = 0; i < size; i++) {
                store.add("P" + i, "REG" + i, "Student" + i, i % 20, 3500.0);
            }
            long start = System.nanoTime();
            String[] found = store.find("P" + (size - 1));
            long elapsed = System.nanoTime() - start;
            System.out.printf("records=%d found=%s elapsed_ms=%.3f%n",
                    size, found != null, elapsed / 1_000_000.0);
            Files.deleteIfExists(f);
        }
        System.out.println("Record the actual output in Evidence/measurements.csv.");
    }
}
