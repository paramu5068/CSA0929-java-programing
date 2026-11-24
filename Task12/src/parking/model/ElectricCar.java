package parking.model;

/** Multilevel inheritance (ElectricCar -> Car -> Vehicle) plus an interface capability. */
public class ElectricCar extends Car implements Chargeable {
    public ElectricCar(String regNo) { super(regNo); }

    @Override public double chargingFee(int kWh) {
        if (!Chargeable.isValidEnergy(kWh))
            throw new IllegalArgumentException("Energy cannot be negative: " + kWh);
        return kWh * Rates.EV_PER_KWH;
    }
}
