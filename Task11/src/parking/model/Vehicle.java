package parking.model;

import java.time.Duration;
import java.time.LocalDateTime;

/** Abstract parent of every vehicle category. */
public abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null)
            throw new IllegalArgumentException("Registration number is required");
        String clean = regNo.trim().toUpperCase();
        if (!clean.matches("[A-Z0-9]{4,12}"))
            throw new IllegalArgumentException("Invalid registration number: " + regNo);
        this.regNo = clean;
    }

    public final String getRegNo() { return regNo; }

    /** The only thing a subclass MUST define. */
    protected abstract double ratePerHour();

    public String category() { return getClass().getSimpleName(); }

    /**
     * Compile-time polymorphism #1: fee for a duration in minutes.
     * Every started hour is charged in full; one full day costs at most DAILY_MAX_HOURS hours.
     */
    public double calculateFee(int minutes) {
        if (minutes < 0)
            throw new IllegalArgumentException("Duration cannot be negative: " + minutes);
        int days = minutes / Rates.MINUTES_PER_DAY;
        int rest = minutes % Rates.MINUTES_PER_DAY;
        double dailyMax = Rates.DAILY_MAX_HOURS * ratePerHour();
        int hours = (rest + 59) / 60;                       // ceiling, integer arithmetic only
        double restFee = Math.min(hours * ratePerHour(), dailyMax);
        return days * dailyMax + restFee;
    }

    /** Compile-time polymorphism #2: fee from entry and exit time. Subclasses may override. */
    public double calculateFee(LocalDateTime in, LocalDateTime out) {
        if (in == null || out == null)
            throw new IllegalArgumentException("Entry and exit time are required");
        if (out.isBefore(in))
            throw new IllegalArgumentException("Exit time is before entry time");
        long minutes = Duration.between(in, out).toMinutes();
        return calculateFee(Math.toIntExact(minutes));
    }

    @Override public boolean equals(Object o) {
        return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
    }

    @Override public int hashCode() { return regNo.hashCode(); }

    @Override public String toString() { return category() + "[" + regNo + "]"; }
}
