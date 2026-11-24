package parking.app;

import java.time.LocalDate;
import java.time.LocalDateTime;

import parking.model.*;
import parking.service.*;

/** Demo run: one small day at the campus parking facility. */
public class Main {
    public static void main(String[] args) {
        LocalDate day = LocalDate.of(2026, 11, 18);
        ParkingFacility facility = new ParkingFacility(new int[]{3, 2});
        ParkingService service = new ParkingService(facility);
        System.out.println("Facility: " + facility.getFloorCount() + " floors, "
                + facility.getCapacity() + " slots");

        MonthlyPass studentPass = new MonthlyPass("STU-101", UserCategory.STUDENT,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));

        Vehicle[] arrivals = {
                VehicleFactory.create("twowheeler", "TN01A1"),
                VehicleFactory.create("car", "TN02B2"),
                VehicleFactory.create("ev", "TN03C3"),
                VehicleFactory.create("car", "TN04S4")
        };
        LocalDateTime[] times = { day.atTime(8, 0), day.atTime(9, 0), day.atTime(9, 15), day.atTime(8, 0) };

        for (int i = 0; i < arrivals.length; i++) {
            ParkingTicket t = (i == 3)
                    ? service.enter(arrivals[i], UserCategory.STUDENT, studentPass, times[i])
                    : service.enter(arrivals[i], times[i]);
            System.out.println("ENTER  " + t);
        }

        System.out.println("EXIT   TN01A1 pays Rs " + service.exit("TN01A1", day.atTime(10, 30)));
        System.out.println("EXIT   TN02B2 pays Rs " + service.exit("TN02B2", day.atTime(9, 45)));
        System.out.println("EXIT   TN03C3 pays Rs " + service.exit("TN03C3", day.atTime(12, 16), 10)
                + "  (parking + 10 kWh charging)");
        System.out.println("EXIT   TN04S4 pays Rs " + service.exit("TN04S4", day.atTime(17, 0))
                + "  (valid monthly pass)");

        System.out.println("Total collection : Rs " + service.getTotalCollection());
        System.out.println("Exits by category: " + service.getExitsByCategory());
        System.out.println("Tickets issued   : " + ParkingTicket.getIssuedCount());
        System.out.println("Free slots now   : " + facility.getFreeSlots() + "/" + facility.getCapacity());
    }
}
