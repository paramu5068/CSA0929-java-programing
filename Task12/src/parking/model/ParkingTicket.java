package parking.model;

import java.time.Duration;
import java.time.LocalDateTime;

/** One ticket per parking visit. Ticket numbers come from a static counter and are never reused. */
public class ParkingTicket {

    // ---- class-level (static) state: shared by ALL tickets ----
    private static final int FIRST_TICKET_NO;
    private static int nextTicketNo;

    static {                                  // static block: runs once when the class loads
        FIRST_TICKET_NO = 1001;
        nextTicketNo = FIRST_TICKET_NO;
    }

    private static synchronized int issueNumber() { return nextTicketNo++; }

    public static synchronized int getIssuedCount() { return nextTicketNo - FIRST_TICKET_NO; }

    // ---- per-object (instance) state ----
    private final int ticketNo;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;
    private final LocalDateTime entryTime;
    private final UserCategory category;
    private final Pass pass;                  // null = no pass
    private LocalDateTime exitTime;           // null until the vehicle leaves

    /** Overloaded constructor: a plain visitor ticket (chains to the full constructor). */
    public ParkingTicket(Vehicle vehicle, int floor, int slot, LocalDateTime entryTime) {
        this(vehicle, floor, slot, entryTime, UserCategory.VISITOR, null);
    }

    /** Full constructor: the only place that validates and issues a NEW ticket number. */
    public ParkingTicket(Vehicle vehicle, int floor, int slot, LocalDateTime entryTime,
                         UserCategory category, Pass pass) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle is required");
        if (floor < 0 || slot < 0) throw new IllegalArgumentException("Invalid slot position");
        if (entryTime == null) throw new IllegalArgumentException("Entry time is required");
        if (category == null) throw new IllegalArgumentException("User category is required");
        this.ticketNo = issueNumber();
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = entryTime;
        this.category = category;
        this.pass = pass;
    }

    /** Copy constructor: a DUPLICATE of an existing ticket keeps the same number. */
    public ParkingTicket(ParkingTicket other) {
        this.ticketNo = other.ticketNo;       // not issueNumber(): no new number is consumed
        this.vehicle = other.vehicle;
        this.floor = other.floor;
        this.slot = other.slot;
        this.entryTime = other.entryTime;
        this.category = other.category;
        this.pass = other.pass;
        this.exitTime = other.exitTime;
    }

    public int getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public UserCategory getCategory() { return category; }
    public Pass getPass() { return pass; }
    public LocalDateTime getExitTime() { return exitTime; }
    public boolean isClosed() { return exitTime != null; }

    /** Validating setter: exit can never be before entry. */
    public void setExitTime(LocalDateTime exitTime) {
        if (exitTime == null) throw new IllegalArgumentException("Exit time is required");
        if (exitTime.isBefore(entryTime))
            throw new IllegalArgumentException("Exit time is before entry time");
        this.exitTime = exitTime;
    }

    public int getDurationMinutes() {
        if (exitTime == null) throw new IllegalStateException("Vehicle has not left yet");
        return Math.toIntExact(Duration.between(entryTime, exitTime).toMinutes());
    }

    @Override public String toString() {
        return "Ticket#" + ticketNo + " " + vehicle + " slot[" + floor + "," + slot + "] in " + entryTime;
    }
}
