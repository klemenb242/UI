import java.util.*;

class BFS {

    static String initialFile = "primer4_zacetna.txt";
    static String finalFile = "primer4_koncna.txt";

    public static Counter counter = new Counter();

    public static List<Warehouse.Move> search(Warehouse initial) {
        counter.startTiming();
        // create a queue to store the nodes (i.e., states) that need to be explored
        Queue<Warehouse> queue = new LinkedList<>();
        // add the initial state to the queue
        queue.add(initial);

        // create a set to store the states that have already been explored
        Set<String> explored = new HashSet<>();

        while (!queue.isEmpty()) {
            // remove the front node from the queue
            Warehouse current = queue.poll();
            counter.incrementExploredNodes();
            counter.checkMaxDepth(current.getNumberOfMoves());

            // if the current state is the final state, return the moves that led to it
            if (current.isSolved()) {
                counter.stopTiming();
                return current.getMoves();
            }

            // mark the current state as explored
            explored.add(current.toString());

            // for each move that can be made from the current state
            for (int fromCol = 0; fromCol < current.numCols; fromCol++) {
                for (int toCol = 0; toCol < current.numCols; toCol++) {
                    if (!current.canMove(fromCol, toCol)) {
                        continue;
                    }
                    Warehouse next = current.deepClone();
                    Warehouse.Move move = next.move(fromCol, toCol);
                    if (!explored.contains(next.toString())) {
                        queue.add(next);
                        counter.checkMaxMemory(queue.size());
                    }
                }
            }
        }
        counter.stopTiming();
        // if no solution was found, return an empty list of moves
        return new ArrayList<>();
    }

    public static void main(String[] args) throws Exception {
        char[][] initialState = Warehouse.readStateFromFile(initialFile);
        char[][] finalState = Warehouse.readStateFromFile(finalFile);

        Warehouse initial = new Warehouse(initialState, finalState);

        List<Warehouse.Move> moves = search(initial);
        Warehouse temp = new Warehouse(initialState, finalState);
        Helper.simulateMoves(temp, moves);
        System.out.print(BFS.counter);
    }

}
