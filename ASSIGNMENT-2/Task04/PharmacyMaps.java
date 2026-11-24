import java.util.*;

public class PharmacyMaps {
    static volatile int sink;
    static Map<String,Integer> create(String type, int n) {
        Map<String,Integer> m = switch(type) {
            case "TreeMap" -> new TreeMap<>();
            case "LinkedHashMap" -> new LinkedHashMap<>();
            default -> new HashMap<>();
        };
        for(int i=n-1;i>=0;i--) m.put(String.format("MED%06d",i), 100+i);
        return m;
    }
    static long nanos(Runnable r) { long s=System.nanoTime(); r.run(); return System.nanoTime()-s; }
    public static void main(String[] args) {
        int[] sizes={1000,10000,50000};
        Random random=new Random(42);
        for(int n:sizes) {
            System.out.println("\nMedicines="+n);
            for(String type:List.of("HashMap","TreeMap","LinkedHashMap")) {
                Map<String,Integer> m=create(type,n);
                String[] keys=new String[100000];
                for(int i=0;i<keys.length;i++) keys[i]=String.format("MED%06d",random.nextInt(n));
                long lookup=nanos(()->{int sum=0; for(String k:keys) sum+=m.get(k); sink=sum;});
                long codeReport=nanos(()->{TreeMap<String,Integer> sorted=new TreeMap<>(m); sink=sorted.size();});
                long insertionReport=nanos(()->{List<String> ordered=new ArrayList<>(m.keySet()); sink=ordered.size();});
                System.out.printf(Locale.US,"%s lookup100k_ms=%.3f codeReport_ms=%.3f insertionOrderReport_ms=%.3f%n",
                    type,lookup/1e6,codeReport/1e6,insertionReport/1e6);
                System.out.println("  native iteration first 5 keys: " +
                    m.keySet().stream().limit(5).toList());
            }
        }
        System.out.println("\nNote: HashMap has no ordering guarantee; LinkedHashMap preserves insertion order; TreeMap sorts by key.");
    }
}
