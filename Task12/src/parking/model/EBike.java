package parking.model;

/** NEW (Task 12): e-bike, flat fee per visit. Can also use a charging point. */
public class EBike extends Vehicle implements Chargeable {
    private static final double FLAT_FEE = 20;
    private static final double CHARGE_PER_KWH = 12;

    public EBike(String regNo) { super(regNo); }

    @Override protected double ratePerHour() { return 0; }      // not used: fee is flat

    @Override public double calculateFee(int minutes) {
        if (minutes < 0)
            throw new IllegalArgumentException("Duration cannot be negative: " + minutes);
        return FLAT_FEE;
    }

    @Override public double chargingFee(int kWh) {
        if (!Chargeable.isValidEnergy(kWh))
            throw new IllegalArgumentException("Energy cannot be negative: " + kWh);
        return kWh * CHARGE_PER_KWH;
    }
}
