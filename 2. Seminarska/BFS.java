import java.util.*;

class BFS {

    static String initialFile = "primer1_zacetna.txt";
    static String finalFile = "primer1_koncna.txt";

    public static List<Warehouse.Move> search(Warehouse initial, Warehouse finalState) {
        // create a queue to store the nodes (i.e., states) that need to be explored
        Queue<Warehouse> queue = new LinkedList<>();
        // add the initial state to the queue
        queue.add(initial);

        // create a set to store the states that have already been explored
        Set<String> explored = new HashSet<>();

        while (!queue.isEmpty()) {
            // remove the front node from the queue
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

        Warehouse initial = new Warehouse(initialState, finalState);
        Warehouse finalWarehouse = new Warehouse(finalState, finalState);

        List<Warehouse.Move> moves = search(initial, finalWarehouse);
        System.out.println(moves.size() + " moves");
    }

}
