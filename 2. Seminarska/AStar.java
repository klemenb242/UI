import java.util.*;

class AStar {

    static String initialFile = "primer4_zacetna.txt";
    static String finalFile = "primer4_koncna.txt";

    static Counter counter = new Counter();

    public static List<Warehouse.Move> search(Warehouse initialWarehouse) {
        counter.startTiming();
        // create a priority queue to store the nodes (i.e., states) that need to be
        // explored
        // the priority queue is ordered by the f-value of each node (i.e., the sum of
        // its g-value and h-value)
        PriorityQueue<Warehouse> queue = new PriorityQueue<>(new Comparator<Warehouse>() {
            public int compare(Warehouse a, Warehouse b) {
                return Double.compare(a.fValue(), b.fValue());
            }
        });
        // add the initial state to the queue
        queue.add(initialWarehouse);

        // create a set to store the states that have already been explored
        Set<String> explored = new HashSet<>();

        while (!queue.isEmpty()) {
            // remove the node with the lowest f-value from the queue
            Warehouse current = queue.poll();
            counter.incrementExploredNodes();

            // update the search statistics
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

        // if no solution was found, return an empty list of moves
        counter.stopTiming();
        return new ArrayList<>();
    }

    public static void main(String[] args) throws Exception {
        char[][] initialState = Warehouse.readStateFromFile(initialFile);
        char[][] finalState = Warehouse.readStateFromFile(finalFile);
        List<Warehouse.Move> moves = search(new Warehouse(initialState, finalState));
        Warehouse temp = new Warehouse(initialState, finalState);
        Helper.simulateMoves(temp, moves);
        System.out.println(AStar.counter);
    }

}
