package parking.model;

public class Car extends Vehicle {
    public Car(String regNo) { super(regNo); }

    @Override protected double ratePerHour() { return Rates.CAR_PER_HOUR; }
}
