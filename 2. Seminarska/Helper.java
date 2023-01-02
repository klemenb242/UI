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

class Counter {
    int numExploredNodes = 0;
    int maxDepth = 0;
    int maxMemory = 0;
    double timeInSeconds = 0;
    // timeInSeconds
    private long startTime = System.currentTimeMillis();
    private long endTime = System.currentTimeMillis();

    public void startTiming() {
        startTime = System.currentTimeMillis();
    }

    public void stopTiming() {
        endTime = System.currentTimeMillis();
        timeInSeconds = (endTime - startTime) / 1000.0;
    }

    public boolean checkMaxDepth(int depth) {
        maxDepth = Math.max(maxDepth, depth);
        return maxDepth == depth;
    }

    public boolean checkMaxMemory(int memory) {
        maxMemory = Math.max(maxMemory, memory);
        return maxMemory == memory;
    }

    public int incrementExploredNodes() {
        return addToExploredNodes(1);
    }

    public int addToExploredNodes(int num) {
        numExploredNodes += num;
        return numExploredNodes;
    }

    public void reset() {
        numExploredNodes = 0;
        maxDepth = 0;
        maxMemory = 0;
        timeInSeconds = 0;
    }

    public String toString() {
        // every property in a new line
        return "Explored nodes: " + numExploredNodes + "\nMax depth: " + maxDepth + "\nMax memory: " + maxMemory
                + "\nTime in seconds: " + timeInSeconds;
    }

}
