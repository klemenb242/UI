import java.util.*;

class AStar {

    static String initialFile = "primer5_zacetna.txt";
    static String finalFile = "primer5_koncna.txt";

    public static List<Warehouse.Move> search(Warehouse initialWarehouse) {
        // create a priority queue to store the nodes (i.e., states) that need to be
        // explored
        // the priority queue is ordered by the f-value of each node (i.e., the sum of
        // its g-value and h-value)
        PriorityQueue<Warehouse> queue = new PriorityQueue<>(new Comparator<Warehouse>() {
            public int compare(Warehouse a, Warehouse b) {
                return Double.compare(a.gValue() + a.hValue(), b.gValue() + b.hValue());
            }
        });
        // add the initial state to the queue
        queue.add(initialWarehouse);

        // create a set to store the states that have already been explored
        Set<String> explored = new HashSet<>();

        while (!queue.isEmpty()) {
            // remove the node with the lowest f-value from the queue
            Warehouse current = queue.poll();

            // if the current state is the final state, return the moves that led to it
            if (current.isSolved()) {
                return current.getMoves();
            }

            // mark the current state as explored
            explored.add(current.toString());

            // for each move that can be made from the current state
            for (int fromCol = 0; fromCol < current.numCols; fromCol++) {
                for (int toCol = 0; toCol < current.numCols; toCol++) {
                    // try to make the move
                    try {
                        Warehouse next = current.deepClone();
                        Warehouse.Move move = next.move(fromCol, toCol);
                        // if the move is valid and the resulting state has not been explored
                        if (!explored.contains(next.toString())) {
                            queue.add(next);
                        }
                    } catch (IllegalArgumentException e) {
                        // if the move is not valid, do nothing
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
        System.out.println(moves.size() + " moves");
    }

}
