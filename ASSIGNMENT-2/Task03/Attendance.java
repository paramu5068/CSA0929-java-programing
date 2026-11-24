import java.util.*;

class Student {
    private final String regNo;
    private final String name;
    Student(String regNo, String name) { this.regNo=regNo; this.name=name; }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student other=(Student)o;
        return Objects.equals(regNo, other.regNo);
    }
    // Contract: equal objects must have equal hash codes.
    @Override public int hashCode() { return Objects.hash(regNo); }
    @Override public String toString() { return regNo + " " + name; }
}
public class Attendance {
    static void printTest(String title, Student... scans) {
        Set<Student> set=new HashSet<>(Arrays.asList(scans));
        System.out.println(title + " -> size=" + set.size() + " " + set);
    }
    public static void main(String[] args) {
        printTest("Test 1: distinct IDs",
            new Student("192311001","Arun"), new Student("192311002","Divya"));
        printTest("Test 2: repeated ID",
            new Student("192311001","Arun"), new Student("192311002","Divya"),
            new Student("192311001","Arun"));
        printTest("Test 3: same ID, different display name",
            new Student("192311003","Kavi"), new Student("192311003","Kavitha"));

        Set<Student> present=new HashSet<>();
        present.add(new Student("192311001","Arun"));
        present.add(new Student("192311002","Divya"));
        present.add(new Student("192311001","Arun"));
        System.out.println("Unique students: " + present.size());
    }
}
