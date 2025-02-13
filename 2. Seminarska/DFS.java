import java.util.*;

class DFS {

    static String initialFile = "primer3_zacetna.txt";
    static String finalFile = "primer3_koncna.txt";

    public static List<Warehouse.Move> search(Warehouse initial, Warehouse finalState) {
        // create a stack to store the nodes (i.e., states) that need to be explored
        Stack<Warehouse> stack = new Stack<>();
        // add the initial state to the stack
        stack.push(initial);

        // create a set to store the states that have already been explored
        Set<String> explored = new HashSet<>();

        while (!stack.isEmpty()) {
            // pop the top node from the stack
            Warehouse current = stack.pop();

            // if the current state is the final state, return the moves that led to it
            if (current.isSolved()) {
                return current.getMoves();
            }

            // mark the current state as explored
            explored.add(current.toString());

            // for each move that can be made from the current state
            for (int fromCol = 0; fromCol < current.numCols; fromCol++) {
                for (int toCol = 0; toCol < current.numCols; toCol++) {
                    // skip the move if it's illegal
                    if (!current.canMove(fromCol, toCol)) {
                        continue;
                    }

                    // make a copy of the current state
                    Warehouse next = current.deepClone();
                    // make the move on the copy
                    next.move(fromCol, toCol);

                    // if the resulting state has not been explored, add it to the stack
                    if (!explored.contains(next.toString())) {
                        stack.push(next);
                    }
                }
            }
        }

        // if no solution was found, return an empty list
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
