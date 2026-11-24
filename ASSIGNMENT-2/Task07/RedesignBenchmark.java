import java.util.*;

public class RedesignBenchmark {
    static volatile int sink;
    static final int RUNS=7;
    static long repeatedPlus(int tickets,int fields) {
        long start=System.nanoTime(); String last="";
        for(int t=0;t<tickets;t++) {
            String s="";
            for(int f=0;f<fields;f++) s=s+"field"+f+"=data;";
            last=s;
        }
        sink=last.length(); return System.nanoTime()-start;
    }
    static long builder(int tickets,int fields) {
        long start=System.nanoTime(); String last="";
        for(int t=0;t<tickets;t++) {
            StringBuilder s=new StringBuilder(fields*14);
            for(int f=0;f<fields;f++) s.append("field").append(f).append("=data;");
            last=s.toString();
        }
        sink=last.length(); return System.nanoTime()-start;
    }
    public static void main(String[] args) {
        int[][] cases={{1,6},{100,50},{1000,50}};
        System.out.println("Q7 redesign validation: same workload for both implementations");
        for(int[] c:cases) {
            long old=0, revised=0;
            for(int r=0;r<RUNS;r++){old+=repeatedPlus(c[0],c[1]); revised+=builder(c[0],c[1]);}
            System.out.printf(Locale.US,"tickets=%d fields=%d repeatedPlus_avg_ms=%.3f StringBuilder_avg_ms=%.3f speedup=%.2fx%n",
                c[0],c[1],old/(double)RUNS/1e6,revised/(double)RUNS/1e6,(double)old/revised);
        }
        System.out.println("Interpretation: compare on the same JVM/machine; repeat runs and retain raw output.");
    }
}
