import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultProcessor {
    record Outcome(String file, boolean success, String message) {}

    static class ScoreTask implements Callable<Outcome> {
        private final Path file;
        ScoreTask(Path file) { this.file = file; }
        public Outcome call() {
            try {
                String text = Files.readString(file).trim();
                if (text.isEmpty()) throw new IllegalArgumentException("Empty score file");
                int score = Integer.parseInt(text);
                if (score < 0 || score > 100) throw new IllegalArgumentException("Score outside 0..100");
                String grade = score >= 90 ? "A" : score >= 75 ? "B" : score >= 60 ? "C" : "D";
                return new Outcome(file.getFileName().toString(), true, "score=" + score + ", grade=" + grade);
            } catch (IOException | NumberFormatException | IllegalArgumentException e) {
                return new Outcome(file.getFileName().toString(), false, e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Path dir;
        if (args.length > 0) dir = Paths.get(args[0]);
        else {
            dir = Files.createTempDirectory("result-input");
            Files.writeString(dir.resolve("student1.txt"), "92");
            Files.writeString(dir.resolve("student2.txt"), "76");
            Files.writeString(dir.resolve("student3.txt"), "bad-data");
            // student4.txt intentionally missing from the task list is not submitted.
        }
        int threads = args.length > 1 ? Integer.parseInt(args[1]) : 4;
        List<Path> files;
        try (var stream = Files.list(dir)) {
            files = stream.filter(p -> p.toString().endsWith(".txt")).toList();
        }
        ConcurrentLinkedQueue<Outcome> results = new ConcurrentLinkedQueue<>();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future<Outcome>> futures = new ArrayList<>();
            for (Path file : files) futures.add(pool.submit(new ScoreTask(file)));
            for (Future<Outcome> future : futures) {
                try { results.add(future.get()); }
                catch (ExecutionException e) { results.add(new Outcome("unknown", false, e.getCause().toString())); }
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.MINUTES);
        }
        long success = results.stream().filter(Outcome::success).count();
        long failed = results.size() - success;
        results.stream().sorted(Comparator.comparing(Outcome::file))
            .forEach(o -> System.out.println(o.file() + " -> " + (o.success() ? "OK " : "FAILED ") + o.message()));
        System.out.println("Summary: successes=" + success + ", failures=" + failed +
            ", total=" + results.size() + ", threads=" + threads);
    }
}
