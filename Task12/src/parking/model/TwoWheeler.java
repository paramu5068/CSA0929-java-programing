package parking.model;

public class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) { super(regNo); }

    @Override protected double ratePerHour() { return Rates.TWO_WHEELER_PER_HOUR; }
}
