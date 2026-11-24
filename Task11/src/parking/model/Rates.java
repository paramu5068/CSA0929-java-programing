package parking.model;

/** Package-private (default access): rate constants are an implementation detail of parking.model. */
final class Rates {
    private Rates() { }

    static final double TWO_WHEELER_PER_HOUR = 10;
    static final double CAR_PER_HOUR = 30;
    static final double EV_PER_KWH = 12;
    static final int DAILY_MAX_HOURS = 8;       // daily maximum = 8 hours' fee
    static final int MINUTES_PER_DAY = 24 * 60;
}
