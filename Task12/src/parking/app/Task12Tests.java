package parking.app;

import java.time.LocalDate;

import parking.model.*;
import parking.service.*;

/** Tests for the changed requirements. The old TestRunner is run unchanged as a regression check. */
public class Task12Tests {
    public static void main(String[] args) {
        TestRunner.header();

        // ---- e-bike: flat fee ----
        check("E1", "e-bike 30 min -> flat Rs20", 20.0, new EBike("TN10E1").calculateFee(30));
        check("E2", "e-bike 10 hours -> still Rs20", 20.0, new EBike("TN10E1").calculateFee(600));
        check("E3", "e-bike charging 5 kWh x Rs12", 60.0, new EBike("TN10E1").chargingFee(5));

        // ---- delivery van: double car rate for hours starting 09:00-17:00 ----
        DeliveryVan van = new DeliveryVan("TN20V1");
        check("V1", "van 09:00-11:00 (2 peak hours) = 60+60", 120.0, van.calculateFee(TestRunner.at(9, 0), TestRunner.at(11, 0)));
        check("V2", "van 08:30-10:30 (1 off-peak + 1 peak) = 30+60", 90.0, van.calculateFee(TestRunner.at(8, 30), TestRunner.at(10, 30)));
        check("V3", "van 16:30-18:00 (peak hour + off-peak) = 60+30", 90.0, van.calculateFee(TestRunner.at(16, 30), TestRunner.at(18, 0)));
        check("V4", "van 17:00-18:00 (starts at 17:00 = off-peak)", 30.0, van.calculateFee(TestRunner.at(17, 0), TestRunner.at(18, 0)));
        check("V5", "van 09:00-09:00 zero minutes", 0.0, van.calculateFee(TestRunner.at(9, 0), TestRunner.at(9, 0)));
        TestRunner.expectThrows("V6", "van exit before entry", IllegalArgumentException.class,
                () -> van.calculateFee(TestRunner.at(10, 0), TestRunner.at(9, 0)));

        // ---- one-day pass ----
        check("P1", "one-day pass valid on its date", true, new OneDayPass(TestRunner.DAY).isValidOn(TestRunner.DAY));
        check("P2", "one-day pass invalid next day", false, new OneDayPass(TestRunner.DAY).isValidOn(TestRunner.DAY.plusDays(1)));

        // ---- 5 floors, different number of slots on each floor ----
        ParkingFacility big = new ParkingFacility(new int[]{20, 30, 40, 50, 60});
        check("F1", "capacity of 5 uneven floors", 200, big.getCapacity());
        check("F2", "floor 4 has 60 slots", 60, big.getSlotsOnFloor(4));
        int[] pos = null;
        for (int i = 0; i < 21; i++) pos = big.allocate(String.format("TN%04d", i));
        check("F3", "21st vehicle moves to floor 1 slot 0", "[1, 0]", java.util.Arrays.toString(pos));

        // ---- factory ----
        check("X1", "factory creates EBike", "EBike", VehicleFactory.create("ebike", "TN10E1").category());
        check("X2", "factory creates DeliveryVan", "DeliveryVan", VehicleFactory.create("van", "TN20V1").category());
        TestRunner.expectThrows("X3", "unknown type still rejected", IllegalArgumentException.class,
                () -> VehicleFactory.create("spaceship", "TN01A1"));

        // ---- day scenario (hand-calculated total Rs 310) ----
        ParkingService day = new ParkingService(new ParkingFacility(new int[]{2, 3}));
        day.enter(VehicleFactory.create("ebike", "TN10E1"), TestRunner.at(9, 0));
        day.enter(VehicleFactory.create("van", "TN20V1"), TestRunner.at(8, 30));
        day.enter(VehicleFactory.create("van", "TN21V2"), TestRunner.at(9, 0));
        day.enter(new Car("TN30C3"), UserCategory.VISITOR, new OneDayPass(TestRunner.DAY), TestRunner.at(7, 0));
        day.enter(new Car("TN31C4"), UserCategory.VISITOR, new OneDayPass(TestRunner.DAY.minusDays(1)), TestRunner.at(10, 0));
        check("S1", "e-bike 09:00-15:00", 20.0, day.exit("TN10E1", TestRunner.at(15, 0)));
        check("S2", "van 08:30-10:30", 90.0, day.exit("TN20V1", TestRunner.at(10, 30)));
        check("S3", "van 09:00-11:00", 120.0, day.exit("TN21V2", TestRunner.at(11, 0)));
        check("S4", "car with today's one-day pass (12 h)", 50.0, day.exit("TN30C3", TestRunner.at(19, 0)));
        check("S5", "car with yesterday's pass pays 1 h x Rs30", 30.0, day.exit("TN31C4", TestRunner.at(11, 0)));
        check("S6", "total collection (hand calc 20+90+120+50+30)", 310.0, day.getTotalCollection());

        if (!TestRunner.summary()) System.exit(1);
    }

    private static void check(String id, String what, Object expected, Object actual) {
        TestRunner.check(id, what, expected, actual);
    }
}
