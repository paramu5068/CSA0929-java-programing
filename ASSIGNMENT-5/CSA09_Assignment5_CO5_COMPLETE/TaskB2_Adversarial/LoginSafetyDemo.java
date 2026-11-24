/**
 * Offline demonstration of SQL construction risk; no database or real account is used.
 * The unsafe query text changes meaning when untrusted input is concatenated.
 */
public class LoginSafetyDemo {
    public static void main(String[] args) {
        String id = "' OR '1'='1' -- ";
        String password = "wrong-password";
        String unsafe = "SELECT * FROM users WHERE id='" + id + "' AND pwd='" + password + "'";
        String safe = "SELECT * FROM users WHERE id=? AND pwd_hash=?";
        System.out.println("Crafted ID: " + id);
        System.out.println("Unsafe SQL: " + unsafe);
        System.out.println("Safe SQL template: " + safe);
        System.out.println("PreparedStatement treats the crafted ID as one literal value.");
        System.out.println("Expected safe authentication result for this unknown ID: false");
    }
}
