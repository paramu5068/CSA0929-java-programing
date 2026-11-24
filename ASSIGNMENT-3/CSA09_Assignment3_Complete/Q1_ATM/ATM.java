import java.util.InputMismatchException;
import java.util.Scanner;

class CardReaderException extends Exception {
    CardReaderException(String message) { super(message); }
}

class InsufficientFundsException extends RuntimeException {
    InsufficientFundsException(String message) { super(message); }
}

public class ATM {
    static double withdraw(String account, double amount, double balance) throws CardReaderException {
        if (account == null || account.isBlank()) throw new CardReaderException("Card/account could not be read");
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal must be positive");
        if (amount > balance) throw new InsufficientFundsException("Insufficient balance");
        return balance - amount;
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Account number: ");
            String account = sc.nextLine();
            System.out.print("Withdrawal amount: ");
            double amount = Double.parseDouble(sc.nextLine());
            double remaining = withdraw(account, amount, 5000.0);
            System.out.printf("Withdrawal successful. Balance: %.2f%n", remaining);
        } catch (NumberFormatException | InputMismatchException e) {
            System.out.println("Invalid numeric input: " + e.getMessage());
        } catch (CardReaderException e) {
            System.out.println("Card error: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("Transaction declined: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount: " + e.getMessage());
        } finally {
            System.out.println("ATM session closed (finally executed).");
        }
    }
}
