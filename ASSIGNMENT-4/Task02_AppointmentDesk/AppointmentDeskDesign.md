# Task 2 – Appointment Desk Design
See `Report/Assignment4_Answers.md`, Task 2, for the complete event table, component tree, handler pseudocode, event trace and alternative complexity analysis.

The essential doctor-refresh implementation is:

```java
doctorBox.removeAllItems();
List<Doctor> doctors = doctorMap.getOrDefault(
    (String) departmentBox.getSelectedItem(), Collections.emptyList());
for (Doctor doctor : doctors) {
    doctorBox.addItem(doctor.getName());
}
```

The booking handler validates all required fields before invoking the service. While a booking is in progress, the close handler asks the receptionist to confirm before closing.
