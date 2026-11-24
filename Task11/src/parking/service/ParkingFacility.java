package parking.service;

import java.util.Arrays;

/** Physical building: a jagged array, so every floor can have a different number of slots. */
public class ParkingFacility {
    private final String[][] slots;      // slots[floor][slot] = registration number or null
    private final int capacity;
    private int occupied;

    public ParkingFacility(int[] slotsPerFloor) {
        if (slotsPerFloor == null || slotsPerFloor.length == 0)
            throw new IllegalArgumentException("At least one floor is required");
        slots = new String[slotsPerFloor.length][];
        int total = 0;
        for (int f = 0; f < slotsPerFloor.length; f++) {
            if (slotsPerFloor[f] <= 0)
                throw new IllegalArgumentException("Floor " + f + " must have at least one slot");
            slots[f] = new String[slotsPerFloor[f]];
            total += slotsPerFloor[f];
        }
        capacity = total;
    }

    /** Overloaded constructor chaining to the main one: same number of slots on every floor. */
    public ParkingFacility(int floors, int slotsPerFloor) {
        this(uniform(floors, slotsPerFloor));
    }

    private static int[] uniform(int floors, int slotsPerFloor) {
        if (floors <= 0) throw new IllegalArgumentException("Floors must be positive");
        int[] a = new int[floors];
        Arrays.fill(a, slotsPerFloor);
        return a;
    }

    /** First free slot as {floor, slot}, or null when the facility is full. */
    public int[] allocate(String vehicleNo) {
        if (vehicleNo == null || vehicleNo.isBlank())
            throw new IllegalArgumentException("Vehicle number is required");
        if (occupied == capacity) return null;          // early exit: no scan needed
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] != null) continue;
                slots[f][s] = vehicleNo;
                occupied++;
                return new int[]{f, s};
            }
        }
        return null;
    }

    /** Frees a slot. False if the position is invalid or the slot is already free. */
    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) return false;
        if (slot < 0 || slot >= slots[floor].length) return false;
        if (slots[floor][slot] == null) return false;
        slots[floor][slot] = null;
        occupied--;
        return true;
    }

    public int getFloorCount() { return slots.length; }
    public int getSlotsOnFloor(int floor) { return slots[floor].length; }
    public int getCapacity() { return capacity; }
    public int getOccupied() { return occupied; }
    public int getFreeSlots() { return capacity - occupied; }
}
