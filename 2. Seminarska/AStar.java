import java.util.*;

class AStar {

    static String initialFile = "primer5_zacetna.txt";
    static String finalFile = "primer5_koncna.txt";

    static int numExploredNodes = 0;
    static int maxDepth = 0;
    static int maxMemory = 1;

    public static List<Warehouse.Move> search(Warehouse initialWarehouse) {
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
            numExploredNodes++;

            // update the search statistics
            int currentDepth = current.gValue();
            maxDepth = Math.max(maxDepth, currentDepth);
            maxMemory = Math.max(maxMemory, queue.size());

            // if the current state is the final state, return the moves that led to it
            if (current.isSolved()) {
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
                    }
                }
            }
        }

        // if no solution was found, return an empty list of moves
        return new ArrayList<>();
    }

    public static void main(String[] args) throws Exception {
        char[][] initialState = Warehouse.readStateFromFile(initialFile);
        char[][] finalState = Warehouse.readStateFromFile(finalFile);
        List<Warehouse.Move> moves = search(new Warehouse(initialState, finalState));
        Warehouse temp = new Warehouse(initialState, finalState);
        Helper.simulateMoves(temp, moves);
        System.out.println("Number of explored nodes: " + numExploredNodes);
        System.out.println("Maximum search depth: " + maxDepth);
        System.out.println("Maximum memory usage: " + maxMemory);
    }

}
