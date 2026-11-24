package parking.app;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import parking.model.Car;
import parking.model.ParkingTicket;

/** Task 11 alternative: time to find a ticket on exit - array vs ArrayList vs HashMap. */
public class LookupBenchmark {
    static final int LOOKUPS = 2000;

    public static void main(String[] args) {
        int[] sizes = {1_000, 5_000, 20_000};
        System.out.printf("%-8s | %-14s | %-14s | %-14s%n", "n", "array scan (ms)", "ArrayList (ms)", "HashMap (ms)");
        LocalDateTime t0 = LocalDateTime.of(2026, 11, 18, 8, 0);
        for (int n : sizes) {
            ParkingTicket[] array = new ParkingTicket[n];
            List<ParkingTicket> list = new ArrayList<>();
            Map<String, ParkingTicket> map = new HashMap<>();
            String[] keys = new String[n];
            for (int i = 0; i < n; i++) {
                keys[i] = String.format("TN%06d", i);
                ParkingTicket t = new ParkingTicket(new Car(keys[i]), 0, i, t0);
                array[i] = t;
                list.add(t);
                map.put(keys[i], t);
            }
            Random rnd = new Random(42);
            String[] wanted = new String[LOOKUPS];
            for (int i = 0; i < LOOKUPS; i++) wanted[i] = keys[rnd.nextInt(n)];

            long sink = 0;
            for (int w = 0; w < 8; w++) {                  // passes 0-6 = JIT warm-up, pass 7 = measured
                long s1 = System.nanoTime();
                for (String k : wanted)
                    for (ParkingTicket t : array)
                        if (t.getVehicle().getRegNo().equals(k)) { sink += t.getTicketNo(); break; }
                long e1 = System.nanoTime();
                for (String k : wanted)
                    for (ParkingTicket t : list)
                        if (t.getVehicle().getRegNo().equals(k)) { sink += t.getTicketNo(); break; }
                long e2 = System.nanoTime();
                for (String k : wanted) sink += map.get(k).getTicketNo();
                long e3 = System.nanoTime();
                if (w == 7)
                    System.out.printf("%-8d | %-14.2f | %-14.2f | %-14.3f%n", n,
                            (e1 - s1) / 1e6, (e2 - e1) / 1e6, (e3 - e2) / 1e6);
            }
            if (sink == 42) System.out.println();          // keeps the JIT from removing the loops
        }
        System.out.println("(" + LOOKUPS + " lookups per row; array/ArrayList = O(n) each, HashMap = O(1) average)");
    }
}
