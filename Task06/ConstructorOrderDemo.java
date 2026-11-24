/** Task 6: order in which constructors run when an ElectricCar is created. */
class Vehicle {
    protected final String regNo;
    Vehicle(String regNo) {
        this.regNo = regNo;
        System.out.println("1. Vehicle constructor   (regNo set to " + regNo + ")");
    }
    String describe() { return "Vehicle " + regNo; }
}

class Car extends Vehicle {
    Car(String regNo) {
        super(regNo);
        System.out.println("2. Car constructor       (rate 30 /hour ready)");
    }
    @Override String describe() { return "Car -> " + super.describe(); }
}

class ElectricCar extends Car {
    ElectricCar(String regNo) {
        super(regNo);
        System.out.println("3. ElectricCar constructor (charging support ready)");
    }
    @Override String describe() { return "ElectricCar -> " + super.describe(); }
}

public class ConstructorOrderDemo {
    public static void main(String[] args) {
        System.out.println("new ElectricCar(\"TN03C3\") ...");
        ElectricCar e = new ElectricCar("TN03C3");
        System.out.println(e.describe());
    }
}
