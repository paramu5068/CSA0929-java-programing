public class Registration {
    public final int id;
    public final String studentId, studentName, email, eventName, registeredAt;

    public Registration(int id, String studentId, String studentName, String email,
                        String eventName, String registeredAt) {
        this.id = id; this.studentId = studentId; this.studentName = studentName;
        this.email = email; this.eventName = eventName; this.registeredAt = registeredAt;
    }
}
