/** Task 2: where integer division / casting gives a WRONG fee, and how the design avoids it. */
public class CastingDemo {
    public static void main(String[] args) {
        int minutes = 130;          // 2 h 10 min
        double carRate = 30;        // Rs per hour

        // WRONG 1: int / int truncates -> 130 / 60 = 2 (the extra 10 minutes vanish)
        double wrong1 = (minutes / 60) * carRate;

        // WRONG 2: the cast is applied AFTER the division, so the damage is already done
        double wrong2 = (double) (minutes / 60) * carRate;

        // WRONG 3: (int) truncates 2.1666... to 2 (a cast never rounds up)
        double wrong3 = (int) (minutes / 60.0) * carRate;

        // RIGHT: ceiling using integer arithmetic only (what the project uses)
        int hours = (minutes + 59) / 60;
        double right = hours * carRate;

        System.out.println("wrong1 (int division)      = Rs " + wrong1);
        System.out.println("wrong2 (cast after divide) = Rs " + wrong2);
        System.out.println("wrong3 ((int) truncation)  = Rs " + wrong3);
        System.out.println("right  ((m+59)/60)         = Rs " + right);
        System.out.println("right  (Math.ceil)         = Rs " + Math.ceil(minutes / 60.0) * carRate);

        // Narrowing risk: long -> int overflows silently
        long hugeMinutes = 3_000_000_000L;
        System.out.println("(int) 3,000,000,000L       = " + (int) hugeMinutes);
        try {
            Math.toIntExact(hugeMinutes);
        } catch (ArithmeticException e) {
            System.out.println("Math.toIntExact detects it : " + e.getMessage());
        }

        // Floating point precision: never use double for exact money comparisons
        System.out.println("0.1 + 0.2 == 0.3 ?         = " + (0.1 + 0.2 == 0.3));
    }
}
