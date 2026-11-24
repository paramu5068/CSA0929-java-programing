# Task 6 – UML class diagram (Mermaid – paste into https://mermaid.live or GitHub renders it)

```mermaid
classDiagram
    class Vehicle {
        <<abstract>>
        -String regNo
        #Vehicle(String regNo)
        +getRegNo() String
        #ratePerHour()* double
        +calculateFee(int minutes) double
        +calculateFee(LocalDateTime in, LocalDateTime out) double
        +equals(Object) boolean
        +hashCode() int
    }
    class TwoWheeler { #ratePerHour() double }
    class Car { #ratePerHour() double }
    class ElectricCar { +chargingFee(int kWh) double }
    class EBike { +calculateFee(int) double
                  +chargingFee(int kWh) double }
    class DeliveryVan { +calculateFee(in, out) double }
    class Chargeable {
        <<interface>>
        +chargingFee(int kWh) double
        +chargingLabel() String
    }
    class Pass {
        <<interface>>
        +isValidOn(LocalDate) boolean
        +getPrice() double
    }
    class MonthlyPass
    class OneDayPass
    class ParkingTicket {
        -static int nextTicketNo
        -int ticketNo
        -LocalDateTime entryTime
    }
    class ParkingFacility { -String[][] slots }
    class ParkingService

    Vehicle <|-- TwoWheeler
    Vehicle <|-- Car
    Car <|-- ElectricCar
    Vehicle <|-- EBike
    Vehicle <|-- DeliveryVan
    Chargeable <|.. ElectricCar
    Chargeable <|.. EBike
    Pass <|.. MonthlyPass
    Pass <|.. OneDayPass
    ParkingService o-- ParkingFacility
    ParkingService o-- ParkingTicket
    ParkingTicket o-- Vehicle
    ParkingTicket o-- Pass
```
EBike, DeliveryVan and OneDayPass are the Task 12 additions.
Inheritance types: Vehicle -> Car -> ElectricCar is **multilevel**; Vehicle -> {TwoWheeler, Car} is **hierarchical**.
