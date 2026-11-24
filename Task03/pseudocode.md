# Task 3 – Pseudocode

> Format note: rewrite these in your college's required pseudocode standard (the "SIMATS Engineering – Standard for
> Algorithmic Pseudocode") – keep the logic, adjust keywords/layout/numbering to match that document.

## Algorithm 1: ALLOCATE_SLOT(vehicleNo)
```
INPUT : vehicleNo
OUTPUT: {floor, slot} of the slot given, or NONE if the facility is full

1  IF vehicleNo is empty THEN REJECT input
2  IF occupied = capacity THEN RETURN NONE            // early exit, no scan
3  FOR floor <- 0 TO floors - 1 DO
4      FOR slot <- 0 TO slotsOnFloor[floor] - 1 DO
5          IF slots[floor][slot] is not free THEN CONTINUE    // skip this slot
6          slots[floor][slot] <- vehicleNo
7          occupied <- occupied + 1
8          RETURN {floor, slot}                      // leaves BOTH loops
9      END FOR
10 END FOR
11 RETURN NONE
```

## Algorithm 2: RELEASE_SLOT(floor, slot)
```
1  IF floor < 0 OR floor >= floors THEN RETURN false
2  IF slot < 0 OR slot >= slotsOnFloor[floor] THEN RETURN false
3  IF slots[floor][slot] is free THEN RETURN false
4  slots[floor][slot] <- free ; occupied <- occupied - 1
5  RETURN true
```

## Algorithm 3: COMPUTE_FEE(minutes, ratePerHour)
Slabs: every started hour is charged in full; one full day is capped at DAILY_MAX_HOURS (8) hours' fee.
```
INPUT : minutes (integer >= 0), ratePerHour
OUTPUT: fee

1  IF minutes < 0 THEN REJECT input
2  days      <- minutes DIV 1440                     // integer division on purpose
3  rest      <- minutes MOD 1440
4  dailyMax  <- 8 x ratePerHour
5  hours     <- (rest + 59) DIV 60                   // ceiling with integers only
6  IF hours x ratePerHour > dailyMax THEN restFee <- dailyMax
7  ELSE restFee <- hours x ratePerHour
8  RETURN days x dailyMax + restFee
```
Worked check: 130 min, Car (30): days=0, rest=130, hours=(130+59) DIV 60 = 3, fee = 90.
