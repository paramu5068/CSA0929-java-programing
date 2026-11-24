package parking.app;

import java.time.LocalDate;
import java.time.LocalDateTime;

import parking.model.*;
import parking.service.*;

/** Plain-Java test table (no JUnit needed): ID | test | expected | actual | result. */
public class TestRunner {
    private static int passed = 0;
    private static int failed = 0;
    static final LocalDate DAY = LocalDate.of(2026, 11, 18);

    static LocalDateTime at(int h, int m) { return DAY.atTime(h, m); }

    public static void check(String id, String what, Object expected, Object actual) {
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        if (ok) passed++; else failed++;
        System.out.printf("| %-4s | %-50s | %-14s | %-14s | %s |%n",
                id, what, expected, actual, ok ? "PASS" : "FAIL");
    }

    public static void expectThrows(String id, String what, Class<? extends Throwable> type, Runnable r) {
        String actual;
        try {
            r.run();
            actual = "no exception";
        } catch (Throwable t) {
            actual = t.getClass().getSimpleName();
        }
        check(id, what, type.getSimpleName(), actual);
    }

    public static boolean summary() {
        System.out.println();
        System.out.println("RESULT: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        return failed == 0;
    }

    public static void header() {
        System.out.printf("| %-4s | %-50s | %-14s | %-14s | %s |%n", "ID", "Test", "Expected", "Actual", "Result");
        System.out.println("|------|----------------------------------------------------|----------------|----------------|--------|");
    }

    public static void main(String[] args) {
        header();

        // ---------- NORMAL OPERATION ----------
        ParkingFacility f1 = new ParkingFacility(new int[]{2, 1});
        check("N1", "first vehicle gets floor 0 slot 0", "[0, 0]",
                java.util.Arrays.toString(f1.allocate("TN01AB1111")));
        check("N2", "TwoWheeler 150 min -> 3 h x Rs10", 30.0, new TwoWheeler("TN01A1").calculateFee(150));
        check("N3", "Car 45 min -> 1 h x Rs30", 30.0, new Car("TN02B2").calculateFee(45));
        check("N4", "ElectricCar 181 min -> 4 h x Rs30", 120.0, new ElectricCar("TN03C3").calculateFee(181));
        check("N5", "EV charging 10 kWh x Rs12", 120.0, new ElectricCar("TN03C3").chargingFee(10));

        // ---------- BOUNDARY CASES ----------
        check("B1", "zero-minute stay costs nothing", 0.0, new Car("TN02B2").calculateFee(0));
        check("B2", "exactly 60 min -> 1 hour (slab boundary)", 30.0, new Car("TN02B2").calculateFee(60));
        check("B3", "61 min -> 2 hours (slab boundary + 1)", 60.0, new Car("TN02B2").calculateFee(61));
        check("B4", "24 h exactly -> daily max Rs240", 240.0, new Car("TN02B2").calculateFee(1440));
        check("B5", "10 h in a day -> capped at Rs240", 240.0, new Car("TN02B2").calculateFee(600));
        check("B6", "24 h 1 min -> Rs240 + 1 h", 270.0, new Car("TN02B2").calculateFee(1441));
        ParkingFacility one = new ParkingFacility(new int[]{1});
        check("B7", "last free slot is allocated", "[0, 0]", java.util.Arrays.toString(one.allocate("TN01AB1111")));
        check("B8", "facility full -> null", "null", java.util.Arrays.toString(one.allocate("TN02CD2222")));
        one.release(0, 0);
        check("B9", "released slot can be reused", "[0, 0]", java.util.Arrays.toString(one.allocate("TN03EF3333")));
        check("B10", "uniform constructor 3 x 50 = 150 slots", 150, new ParkingFacility(3, 50).getCapacity());

        // ---------- INVALID CASES ----------
        expectThrows("I1", "unknown vehicle type", IllegalArgumentException.class,
                () -> VehicleFactory.create("spaceship", "TN01A1"));
        ParkingService dup = new ParkingService(new ParkingFacility(new int[]{3}));
        dup.enter(new Car("TN02B2"), at(9, 0));
        expectThrows("I2", "duplicate registration number", IllegalStateException.class,
                () -> dup.enter(new Car("tn02b2"), at(9, 5)));
        ParkingFacility inv = new ParkingFacility(new int[]{2, 1});
        check("I3", "release(5,0) invalid floor", false, inv.release(5, 0));
        check("I4", "release(0,-1) invalid slot", false, inv.release(0, -1));
        check("I5", "release a free slot", false, inv.release(0, 0));
        expectThrows("I6", "negative duration", IllegalArgumentException.class,
                () -> new Car("TN02B2").calculateFee(-5));
        expectThrows("I7", "exit of a vehicle that never entered", IllegalStateException.class,
                () -> dup.exit("TN99ZZ99", at(10, 0)));
        expectThrows("I8", "exit time before entry time", IllegalArgumentException.class,
                () -> dup.exit("TN02B2", at(8, 0)));
        ParkingService full = new ParkingService(new ParkingFacility(new int[]{1}));
        full.enter(new Car("TN02B2"), at(9, 0));
        expectThrows("I9", "entry when the facility is full", IllegalStateException.class,
                () -> full.enter(new Car("TN03C3"), at(9, 1)));
        expectThrows("I10", "monthly pass for a visitor", IllegalArgumentException.class,
                () -> new MonthlyPass("V1", UserCategory.VISITOR, DAY, DAY));
        expectThrows("I11", "charging a non-EV", IllegalArgumentException.class,
                () -> dup.exit("TN02B2", at(10, 0), 5));
        expectThrows("I12", "bad registration number", IllegalArgumentException.class,
                () -> new Car("@@"));

        // ---------- OBJECT RULES ----------
        ParkingTicket t1 = new ParkingTicket(new Car("TN02B2"), 0, 0, at(9, 0));
        ParkingTicket t2 = new ParkingTicket(new Car("TN03C3"), 0, 1, at(9, 5));
        check("O1", "ticket numbers are never reused", true, t2.getTicketNo() > t1.getTicketNo());
        int before = ParkingTicket.getIssuedCount();
        ParkingTicket copy = new ParkingTicket(t1);
        check("O2", "copy constructor keeps the ticket number", t1.getTicketNo(), copy.getTicketNo());
        check("O3", "copy constructor consumes no new number", before, ParkingTicket.getIssuedCount());
        check("O4", "Car equals ElectricCar with same regNo", true,
                new Car("TN02B2").equals(new ElectricCar("TN02B2")));
        check("O5", "different regNo -> not equal", false, new Car("TN02B2").equals(new Car("TN03C3")));
        check("O6", "equal objects have equal hashCode", true,
                new Car("TN02B2").hashCode() == new ElectricCar("TN02B2").hashCode());

        // ---------- DAY SCENARIO (hand-calculated total Rs 360) ----------
        ParkingService day = new ParkingService(new ParkingFacility(new int[]{3, 2}));
        MonthlyPass studentPass = new MonthlyPass("STU-101", UserCategory.STUDENT,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));
        MonthlyPass expiredStaff = new MonthlyPass("STF-007", UserCategory.STAFF,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31));
        day.enter(new TwoWheeler("TN01A1"), at(8, 0));
        day.enter(new Car("TN02B2"), at(9, 0));
        day.enter(new ElectricCar("TN03C3"), at(9, 15));
        day.enter(new Car("TN04S4"), UserCategory.STUDENT, studentPass, at(8, 0));
        day.enter(new Car("TN05F5"), UserCategory.STAFF, expiredStaff, at(10, 0));
        double a = day.exit("TN01A1", at(10, 30));       // 150 min -> 30
        double b = day.exit("TN02B2", at(9, 45));        // 45 min  -> 30
        double c = day.exit("TN03C3", at(12, 16), 10);   // 181 min -> 120 + 120 charging
        double d = day.exit("TN04S4", at(17, 0));        // valid monthly pass -> 0
        double e = day.exit("TN05F5", at(12, 0));        // expired pass -> 120 min -> 60
        check("D1", "visitor two-wheeler", 30.0, a);
        check("D2", "visitor car", 30.0, b);
        check("D3", "visitor EV (parking + charging)", 240.0, c);
        check("D4", "student with valid monthly pass", 0.0, d);
        check("D5", "staff with expired pass pays full", 60.0, e);
        check("D6", "total collection (hand calc 30+30+240+0+60)", 360.0, day.getTotalCollection());
        check("D7", "all slots free after everyone leaves", 5, day.getFacility().getFreeSlots());
        check("D8", "no active tickets remain", 0, day.getActiveCount());

        boolean ok = summary();
        if (!ok) System.exit(1);
    }
}
