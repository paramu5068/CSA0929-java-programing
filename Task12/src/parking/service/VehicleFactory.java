package parking.service;

import parking.model.Car;
import parking.model.DeliveryVan;
import parking.model.EBike;
import parking.model.ElectricCar;
import parking.model.TwoWheeler;
import parking.model.Vehicle;

/** The single place that turns a type name (from input) into a Vehicle object. */
public final class VehicleFactory {
    private VehicleFactory() { }

    public static Vehicle create(String type, String regNo) {
        if (type == null) throw new IllegalArgumentException("Vehicle type is required");
        switch (type.trim().toLowerCase()) {
            case "twowheeler":
            case "bike":
                return new TwoWheeler(regNo);
            case "car":
                return new Car(regNo);
            case "ev":
            case "electriccar":
                return new ElectricCar(regNo);
            case "ebike":
                return new EBike(regNo);
            case "van":
            case "deliveryvan":
                return new DeliveryVan(regNo);
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}
