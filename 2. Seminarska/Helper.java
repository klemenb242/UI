import java.util.*;

public class Helper {

    public static Warehouse simulateMoves(Warehouse initialWarehouse, List<Warehouse.Move> moves) throws Exception {
        return simulateMoves(initialWarehouse, moves, false);
    }

    public static Warehouse simulateMoves(Warehouse initialWarehouse, List<Warehouse.Move> moves, boolean print)
            throws Exception {
        try {
            System.out.println("Initial warehouse: \n" + initialWarehouse);
            for (Warehouse.Move move : moves) {
                System.out.println(move);
                initialWarehouse.move(move.getFromCol(), move.getToCol());
                if (print)
                    System.out.println(initialWarehouse);
            }
            System.out.println("Solved: " + initialWarehouse.isSolved());
            System.out.println("MOVES: " + moves.size());
            return initialWarehouse;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        Warehouse w = Warehouse.createFromFile("test_zacetna.txt", "test_koncna.txt");
        w.move(0, 2);
        System.out.println(w.stateDistance);
        w.move(0, 1);
        System.out.println(w.stateDistance);
        w.move(2, 1);
        System.out.println(w.stateDistance);
        System.out.println(w.isSolved());
        System.out.println(w);
    }

}
