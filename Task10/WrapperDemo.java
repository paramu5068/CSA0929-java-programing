import java.util.HashMap;
import java.util.Map;

/** Task 10: wrapper classes, autoboxing and the == trap. */
public class WrapperDemo {
    public static void main(String[] args) {
        Integer a = 127, b = 127;      // autoboxing, small values come from a cache
        Integer c = 1000, d = 1000;    // new objects each time
        System.out.println("127  == 127  : " + (a == b));
        System.out.println("1000 == 1000 : " + (c == d) + "   <- compares REFERENCES");
        System.out.println("1000 equals  : " + c.equals(d) + "  <- compares VALUES");

        Map<String, Integer> exits = new HashMap<>();
        exits.merge("VISITOR", 1, Integer::sum);     // autoboxing in the project
        exits.merge("VISITOR", 1, Integer::sum);
        int n = exits.get("VISITOR");                // auto-unboxing
        System.out.println("exits by VISITOR = " + n);

        try {
            int missing = exits.get("STAFF");        // null unboxed -> NullPointerException
            System.out.println(missing);
        } catch (NullPointerException e) {
            System.out.println("Unboxing null -> NullPointerException");
        }
    }
}
