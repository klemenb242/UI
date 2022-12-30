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
        Warehouse w = Warehouse.createFromFile("primer2_zacetna.txt", "primer2_koncna.txt");
        w.move(2, 0);
        w.move(0, 1);
        System.out.println(w.isSolved());
        System.out.println(w);
    }

}
