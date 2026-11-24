import java.util.Locale;

public class StringWorkloadBenchmark {
    private static volatile int sink;
    private static final int WARMUPS = 2;
    private static final int RUNS = 5;

    interface Builder { String build(int tickets, int fields); }

    static String usingPlus(int tickets, int fields) {
        String last = "";
        for (int t = 0; t < tickets; t++) {
            String s = "";
            for (int f = 0; f < fields; f++) s = s + "F" + f + "=value;";
            last = s;
        }
        return last;
    }
    static String usingStringBuilder(int tickets, int fields) {
        String last = "";
        for (int t = 0; t < tickets; t++) {
            StringBuilder b = new StringBuilder(fields * 10);
            for (int f = 0; f < fields; f++) b.append("F").append(f).append("=value;");
            last = b.toString();
        }
        return last;
    }
    static String usingStringBuffer(int tickets, int fields) {
        String last = "";
        for (int t = 0; t < tickets; t++) {
            StringBuffer b = new StringBuffer(fields * 10);
            for (int f = 0; f < fields; f++) b.append("F").append(f).append("=value;");
            last = b.toString();
        }
        return last;
    }
    static long measure(Builder b, int tickets, int fields) {
        long start = System.nanoTime();
        String result = b.build(tickets, fields);
        long elapsed = System.nanoTime() - start;
        sink = result.length();
        return elapsed;
    }
    static void test(String label, int tickets, int fields) {
        Builder[] methods = {StringWorkloadBenchmark::usingPlus,
                             StringWorkloadBenchmark::usingStringBuilder,
                             StringWorkloadBenchmark::usingStringBuffer};
        String[] names = {"String +", "StringBuilder", "StringBuffer"};
        System.out.println("\n" + label + " (" + tickets + " tickets x " + fields + " fields)");
        for (int i = 0; i < methods.length; i++) {
            for (int w = 0; w < WARMUPS; w++) measure(methods[i], tickets, fields);
            long total = 0;
            for (int r = 0; r < RUNS; r++) total += measure(methods[i], tickets, fields);
            System.out.printf(Locale.US, "%-16s average_ms=%.3f%n", names[i],
                              total / (double) RUNS / 1_000_000.0);
        }
    }
    public static void main(String[] args) {
        test("Workload A: short ticket", 1, 6);
        test("Workload B: high volume", 10000, 50);
        test("Workload C: many fields", 100, 500);
        System.out.println("\nIntermediate-object counts are estimates, not JVM allocation measurements.");
        System.out.println("For each ticket, repeated + creates roughly one new accumulated String per field; " +
                           "builder/buffer create one mutable accumulator plus the final String.");
    }
}
