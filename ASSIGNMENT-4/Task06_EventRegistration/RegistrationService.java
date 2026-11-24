import java.sql.SQLException;
import java.util.List;

public class RegistrationService {
    private final RegistrationRepository repository = new RegistrationRepository();

    public void register(String studentId, String name, String email, int eventId)
            throws SQLException {
        if (studentId == null || studentId.trim().isEmpty() ||
            name == null || name.trim().isEmpty() ||
            email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") ||
            eventId <= 0) {
            throw new IllegalArgumentException("Enter a student ID, name, valid email and event.");
        }
        repository.insert(studentId.trim(), name.trim(), email.trim(), eventId);
    }

    public List<Registration> findPage(Integer eventId, int limit, int offset) throws SQLException {
        return repository.findPage(eventId, limit, offset);
    }
}
