import java.util.*;

public class Helper {

    public static Warehouse simulateMoves(Warehouse warehouse, List<Warehouse.Move> moves) {
        for (Warehouse.Move move : moves) {
            warehouse.move(move.getFromCol(), move.getToCol());
            System.out.println(warehouse);
        }
        System.out.println("Solved in " + moves.size() + " moves");
        return warehouse;
    }

    public static void main(String[] args) throws Exception {
        Warehouse w = Warehouse.createFromFile("test_zacetna.txt", "test_koncna.txt");
        w.move(0, 2);
        System.out.println(w.stateScore);
        w.move(0, 1);
        System.out.println(w.stateScore);
        w.move(2, 1);
        System.out.println(w.stateScore);
        System.out.println(w.isSolved());
        System.out.println(w);
    }

}
