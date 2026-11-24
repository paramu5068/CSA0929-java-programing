import java.util.concurrent.locks.*;

class Wallet {
    private int balance;
    private final int id;
    Wallet(int id, int balance) { this.id = id; this.balance = balance; }
    int getId() { return id; }
    int getBalance() { return balance; }
    void debit(int amount) { balance -= amount; }
    void credit(int amount) { balance += amount; }
}
public class TransferDemo {
    static void transferOrdered(Wallet from, Wallet to, int amount) {
        Wallet first = from.getId() < to.getId() ? from : to;
        Wallet second = first == from ? to : from;
        synchronized (first) {
            synchronized (second) {
                if (from.getBalance() >= amount) {
                    from.debit(amount);
                    to.credit(amount);
                }
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Wallet a = new Wallet(1, 1000), b = new Wallet(2, 1000);
        Thread t1 = new Thread(() -> { for (int i=0;i<100;i++) transferOrdered(a,b,1); });
        Thread t2 = new Thread(() -> { for (int i=0;i<100;i++) transferOrdered(b,a,1); });
        t1.start(); t2.start(); t1.join(); t2.join();
        System.out.println("Combined wallet balance: " + (a.getBalance()+b.getBalance()));
    }
}
