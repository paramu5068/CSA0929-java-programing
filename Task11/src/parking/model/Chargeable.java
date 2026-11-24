package parking.model;

/** Capability interface: any vehicle that can use an EV charging point. */
public interface Chargeable {
    double chargingFee(int kWh);

    default String chargingLabel() {
        return "Charging at Rs " + chargingFee(1) + " per kWh";
    }

    static boolean isValidEnergy(int kWh) { return kWh >= 0; }
}
