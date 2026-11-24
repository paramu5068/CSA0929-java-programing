public class FeeService {
    public String pay(String regNo, String amountText) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter registration number.");
        }
        final double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Enter a valid amount.");
        }
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        // Demonstration boundary. Replace this section with a JDBC repository
        // using PreparedStatement and a transaction in a database-backed version.
        return String.format("Payment successful for %s. Amount: %.2f", regNo.trim(), amount);
    }

    public String receipt(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter registration number to view receipt.");
        }
        return "Demo receipt for " + regNo.trim() + ". Connect FeeRepository for stored receipts.";
    }
}
