package parking.model;

import java.time.LocalDate;

/** NEW (Task 12): visitor buys a one-day pass; parking that day costs the pass price only. */
public class OneDayPass implements Pass {
    private static final double PRICE = 50;
    private final LocalDate date;

    public OneDayPass(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Pass date is required");
        this.date = date;
    }

    @Override public boolean isValidOn(LocalDate d) { return date.equals(d); }

    @Override public double getPrice() { return PRICE; }

    @Override public String describe() { return "OneDayPass[" + date + ", Rs " + PRICE + "]"; }
}
