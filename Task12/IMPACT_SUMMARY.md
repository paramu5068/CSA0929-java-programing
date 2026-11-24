# Task 12 – what changed (evidence for part D)

| Item | Count | Detail |
|---|---|---|
| Existing production classes MODIFIED | 1 | `VehicleFactory` (2 new `case` labels) |
| Existing production classes left UNCHANGED | 12 | Vehicle, TwoWheeler, Car, ElectricCar, Chargeable, Pass, MonthlyPass, UserCategory, Rates, ParkingTicket, ParkingFacility, ParkingService |
| NEW classes (extension, not modification) | 3 | `EBike`, `DeliveryVan`, `OneDayPass` |
| NEW test class | 1 | `Task12Tests` (23 tests) |
| Old tests re-run unchanged | 41 / 41 pass | regression check |

Why the 5 uneven floors needed no change: `ParkingFacility` already used a jagged array (`String[][]`), so only the
constructor argument changes: `new ParkingFacility(new int[]{20, 30, 40, 50, 60})`.

Decision: **RETAIN + EXTEND** (open/closed principle) – no redesign needed. Full diff: `CHANGES_vs_Task11.diff`.

Known limitations / trade-offs (be ready to mention in the viva):
* The `VehicleFactory` `switch` still has to be edited for each new type (a registry map would remove even that).
* `DeliveryVan` has no daily maximum; its pricing rule lives inside the subclass, so a future rule that applies to
  ALL vehicles (e.g. a festival discount) would be better as a separate `PricingRule` object.
* `DeliveryVan.calculateFee(int minutes)` has no clock, so it uses the off-peak rate; the service always calls the
  (in, out) version.
