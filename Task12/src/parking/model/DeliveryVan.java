package parking.model;

import java.time.Duration;
import java.time.LocalDateTime;

/** NEW (Task 12): double the car rate for every started hour that begins between 09:00 and 17:00. */
public class DeliveryVan extends Vehicle {
    private static final double BASE_PER_HOUR = 30;      // car rate
    private static final int PEAK_START = 9;
    private static final int PEAK_END = 17;
    private static final double PEAK_MULTIPLIER = 2;

    public DeliveryVan(String regNo) { super(regNo); }

    @Override protected double ratePerHour() { return BASE_PER_HOUR; }

    /** Needs the clock time, so the in/out overload is the one that is specialised. */
    @Override public double calculateFee(LocalDateTime in, LocalDateTime out) {
        if (in == null || out == null)
            throw new IllegalArgumentException("Entry and exit time are required");
        if (out.isBefore(in))
            throw new IllegalArgumentException("Exit time is before entry time");
        long minutes = Duration.between(in, out).toMinutes();
        int hours = Math.toIntExact((minutes + 59) / 60);
        double total = 0;
        for (int i = 0; i < hours; i++) {
            int startHour = in.plusHours(i).getHour();
            boolean peak = startHour >= PEAK_START && startHour < PEAK_END;
            total += peak ? PEAK_MULTIPLIER * ratePerHour() : ratePerHour();
        }
        return total;
    }
}
