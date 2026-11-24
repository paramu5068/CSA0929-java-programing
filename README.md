# CSA09-<RegisterNumber>-Assignment1 – Campus Smart-Parking System

* **Name:** <your name>
* **Register number:** <your register number>
* **Java version:** OpenJDK 21 (any JDK 17+ works – `instanceof` pattern matching needs 16+)

## Folder map

| Folder | Task | Contents |
|---|---|---|
| `Task02/` | 2 – data types, casting | `CastingDemo.java` |
| `Task03/` | 3 – control logic | `ParkingFacility.java` (Complete the Code), `pseudocode.md` |
| `Task06/` | 6 – inheritance | `ConstructorOrderDemo.java`, `uml.md` |
| `Task08/` | 8 – polymorphism | `FeeDemo.java` (Complete the Code) |
| `Task10/` | 10 – wrappers | `WrapperDemo.java` |
| `Task11/` | 4,5,6,7,8,9,10,11 – the full system | packages `parking.model`, `parking.service`, `parking.app` |
| `Task12/` | 12 – changed requirements | Task 11 + new classes + tests + `CHANGES_vs_Task11.diff` |

Tasks 1, 4, 5, 7, 9 are mostly analysis in the report; their code lives in `Task11/`
(Task 4 constructors/copy ctor/chaining = `ParkingTicket`, `ParkingFacility`; Task 5 static = `ParkingTicket`;
Task 7 overriding/equals = `Vehicle` family; Task 9 abstraction = `Vehicle`, `Chargeable`, `Pass`).

## How to compile and run (needs `javac` and `java` on PATH)

Everything at once: `bash run_all.sh`

One by one:
```
cd Task03 && javac ParkingFacility.java && java ParkingFacility
cd Task08 && javac FeeDemo.java && java FeeDemo
cd Task11 && javac -d out $(find src -name '*.java')
             java -cp out parking.app.Main
             java -cp out parking.app.TestRunner
             java -cp out parking.app.LookupBenchmark
cd Task12 && javac -d out $(find src -name '*.java')
             java -cp out parking.app.TestRunner      # old tests, unchanged (regression)
             java -cp out parking.app.Task12Tests     # new tests
```
(Windows PowerShell: use `javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName`.)

## Results (fill in after YOU run each task)

| Task | Result |
|---|---|
| 2 | Wrong fee Rs 60 vs correct Rs 90 for 130 min car stay |
| 3 | Output matches expected: `[0, 0] [0, 1] [1, 0] null / true false false / [0, 1]` |
| 6 | Constructors run Vehicle -> Car -> ElectricCar |
| 8 | Output matches expected: Rs 30.0 / 90.0 / 210.0 / true |
| 10 | `Integer` `==` is true for 127 but false for 1000 |
| 11 | 41 / 41 tests pass; hand-calculated day total Rs 360 = program output |
| 12 | 23 / 23 new tests pass; 41 / 41 old tests still pass; 1 class modified, 3 added |

Add your own output screenshots in each task folder.
