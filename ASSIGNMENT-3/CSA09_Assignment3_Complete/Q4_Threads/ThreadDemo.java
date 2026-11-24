import java.util.concurrent.*;

public class ThreadDemo {
    static class Station extends Thread {
        private final int scripts;
        Station(String name, int scripts) { super(name); this.scripts = scripts; }
        public void run() {
            try {
                Thread.sleep(100L + (long)(Math.random() * 300));
                System.out.println(getName() + " scanned " + scripts + " scripts");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(getName() + " interrupted");
            }
        }
    }
    static class RunnableStation implements Runnable {
        private final String name;
        RunnableStation(String name) { this.name = name; }
        public void run() {
            System.out.println(name + " (Runnable) processed its batch");
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Station[] stations = {
            new Station("Station-1", 25), new Station("Station-2", 30),
            new Station("Station-3", 20), new Station("Station-4", 25)
        };
        for (Station s : stations) s.start();
        for (Station s : stations) s.join();
        System.out.println("All stations completed; total scanned: 100");
        Thread r = new Thread(new RunnableStation("Station-5"));
        r.start();
        r.join();

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<Integer> f1 = pool.submit(() -> 25);
            Future<Integer> f2 = pool.submit(() -> 30);
            System.out.println("Executor total: " + (f1.get() + f2.get()));
        } catch (ExecutionException e) {
            System.out.println("Station task failed: " + e.getCause());
        } finally {
            pool.shutdown();
        }
    }
}
