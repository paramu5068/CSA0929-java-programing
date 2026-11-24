package parking.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import parking.model.Chargeable;
import parking.model.ParkingTicket;
import parking.model.Pass;
import parking.model.UserCategory;
import parking.model.Vehicle;

/** Business rules: entry, exit, fee, duplicate check, daily collection. */
public class ParkingService {
    private final ParkingFacility facility;
    private final Map<String, ParkingTicket> active = new HashMap<>();   // regNo -> ticket
    private final Map<String, Integer> exitsByCategory = new TreeMap<>();
    private double totalCollection;

    public ParkingService(ParkingFacility facility) {
        if (facility == null) throw new IllegalArgumentException("Facility is required");
        this.facility = facility;
    }

    public ParkingTicket enter(Vehicle v, UserCategory category, Pass pass, LocalDateTime time) {
        if (v == null || category == null || time == null)
            throw new IllegalArgumentException("Vehicle, category and time are required");
        if (active.containsKey(v.getRegNo()))
            throw new IllegalStateException("Vehicle already parked: " + v.getRegNo());
        int[] pos = facility.allocate(v.getRegNo());
        if (pos == null)
            throw new IllegalStateException("Parking is full");
        ParkingTicket t = new ParkingTicket(v, pos[0], pos[1], time, category, pass);
        active.put(v.getRegNo(), t);
        return t;
    }

    /** Overload: walk-in visitor without a pass. */
    public ParkingTicket enter(Vehicle v, LocalDateTime time) {
        return enter(v, UserCategory.VISITOR, null, time);
    }

    public double exit(String regNo, LocalDateTime time) {
        return exit(regNo, time, 0);
    }

    /** Releases the slot and returns parking fee + charging fee (charged separately). */
    public double exit(String regNo, LocalDateTime time, int kWh) {
        String key = regNo == null ? null : regNo.trim().toUpperCase();
        ParkingTicket t = key == null ? null : active.get(key);
        if (t == null)
            throw new IllegalStateException("No active ticket for " + regNo);
        Vehicle v = t.getVehicle();
        if (kWh < 0)
            throw new IllegalArgumentException("Energy cannot be negative");
        if (kWh > 0 && !(v instanceof Chargeable))
            throw new IllegalArgumentException(v + " cannot be charged");

        t.setExitTime(time);                      // validates; throws before anything is changed
        double fee = parkingFee(t);
        if (v instanceof Chargeable c) fee += c.chargingFee(kWh);

        facility.release(t.getFloor(), t.getSlot());
        active.remove(key);                       // ticket now unreachable -> eligible for GC
        totalCollection += fee;
        exitsByCategory.merge(t.getCategory().name(), 1, Integer::sum);   // autoboxing
        return fee;
    }

    /** Pass first, otherwise the vehicle's own fee rule (dynamic dispatch). */
    private double parkingFee(ParkingTicket t) {
        Pass p = t.getPass();
        if (p != null && p.isValidOn(t.getEntryTime().toLocalDate()))
            return p.getPrice();
        return t.getVehicle().calculateFee(t.getEntryTime(), t.getExitTime());
    }

    public ParkingTicket findTicket(String regNo) { return active.get(regNo.trim().toUpperCase()); }
    public int getActiveCount() { return active.size(); }
    public double getTotalCollection() { return totalCollection; }
    public Map<String, Integer> getExitsByCategory() { return new TreeMap<>(exitsByCategory); }
    public ParkingFacility getFacility() { return facility; }
}
