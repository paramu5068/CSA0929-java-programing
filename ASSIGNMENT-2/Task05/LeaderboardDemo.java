import java.util.*;

class Team {
    final String name; final int points; final int goalDiff;
    Team(String name,int points,int goalDiff){this.name=name;this.points=points;this.goalDiff=goalDiff;}
    @Override public String toString(){return name+"("+points+","+goalDiff+")";}
}
class Leaderboard<T> {
    private final List<T> items=new ArrayList<>();
    private final Comparator<? super T> order;
    Leaderboard(Comparator<? super T> order){this.order=Objects.requireNonNull(order);}
    void add(T item){items.add(item);}
    List<T> ranking(){
        List<T> copy=new ArrayList<>(items);
        copy.sort(order);
        return copy;
    }
    void addAll(Collection<? extends T> source){items.addAll(source);}
}
public class LeaderboardDemo {
    public static void main(String[] args) {
        Comparator<Team> byRank=Comparator.comparingInt((Team t)->t.points).reversed()
            .thenComparing(Comparator.comparingInt((Team t)->t.goalDiff).reversed())
            .thenComparing(t->t.name);
        Leaderboard<Team> board=new Leaderboard<>(byRank);
        board.add(new Team("Panthers",9,4));
        board.add(new Team("Eagles",9,6));
        board.add(new Team("Cobras",6,1));
        board.add(new Team("Bisons",9,4));
        System.out.println(board.ranking());

        List<Team> tieData=List.of(new Team("Zeta",10,2),new Team("Alpha",10,2),
                                   new Team("Beta",10,5),new Team("Gamma",8,9));
        board.addAll(tieData);
        System.out.println("Second data set: "+board.ranking());
        System.out.println("Original insertion list remains unchanged; ranking() returns a sorted copy.");
    }
}
