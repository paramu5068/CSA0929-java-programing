import java.util.*;

public class PlaylistBenchmark {
    static volatile int sink;
    static final int[] SIZES = {1000, 10000, 50000};
    static long time(Runnable r) {
        long s = System.nanoTime(); r.run(); return System.nanoTime() - s;
    }
    static ArrayList<Integer> array(int n) {
        ArrayList<Integer> x = new ArrayList<>(n);
        for (int i=0;i<n;i++) x.add(i);
        return x;
    }
    static LinkedList<Integer> linked(int n) {
        LinkedList<Integer> x = new LinkedList<>();
        for (int i=0;i<n;i++) x.add(i);
        return x;
    }
    public static void main(String[] args) {
        System.out.println("size,structure,operation,average_ms (5 runs)");
        for (int n : SIZES) {
            for (String type : List.of("ArrayList", "LinkedList")) {
                long end=0, front=0, middle=0, get=0, iterate=0;
                for (int r=0;r<5;r++) {
                    List<Integer> a = type.equals("ArrayList") ? array(n) : linked(n);
                    end += time(() -> a.add(-1));
                    front += time(() -> a.add(0, -1));
                    middle += time(() -> a.add(a.size()/2, -2));
                    get += time(() -> { int v=0; for(int k=0;k<1000;k++) v += a.get((k*31)%a.size()); sink=v; });
                    iterate += time(() -> { int v=0; for(int x:a) v+=x; sink=v; });
                }
                System.out.printf(Locale.US,"%d,%s,add-end,%.4f%n",n,type,end/5.0/1e6);
                System.out.printf(Locale.US,"%d,%s,insert-front,%.4f%n",n,type,front/5.0/1e6);
                System.out.printf(Locale.US,"%d,%s,insert-middle,%.4f%n",n,type,middle/5.0/1e6);
                System.out.printf(Locale.US,"%d,%s,1000-random-gets,%.4f%n",n,type,get/5.0/1e6);
                System.out.printf(Locale.US,"%d,%s,iterate,%.4f%n",n,type,iterate/5.0/1e6);
            }
            long dequeTime=0;
            for(int r=0;r<5;r++) {
                ArrayDeque<Integer> d=new ArrayDeque<>();
                dequeTime += time(() -> { for(int i=0;i<n;i++) d.addLast(i);
                                          for(int i=0;i<n/2;i++) d.addFirst(-i);
                                          for(int i=0;i<n/2;i++) d.removeFirst(); });
            }
            System.out.printf(Locale.US,"%d,ArrayDeque,add-remove-both-ends,%.4f%n",n,dequeTime/5.0/1e6);
        }
    }
}
