import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class IDSearch {

    public static void search(Warehouse warehouse) {
        System.out.println(warehouse);
        for (int depthLimit = 0; depthLimit < warehouse.numRows * warehouse.numCols; depthLimit++) {
            System.out.println("Globina iskanja je " + depthLimit);

            Stack<Warehouse> stack = new Stack<>();
            ArrayList<Warehouse> visited = new ArrayList<>();

            stack.push(warehouse);
            visited.add(warehouse);

            StringBuilder mvs = new StringBuilder();
            while (!stack.isEmpty()) {
                Warehouse curState = stack.peek();
                if (curState.equals(warehouse)) mvs.setLength(0);
                if (curState.isSolved()) {
                    System.out.println("Resitev IDDFS v stanju " + curState);
                    System.out.println(mvs);
                    return;
                }

                boolean found = false;
                if (curState.getMoves().size() <= depthLimit) {
                    // najdi neobiskano naslednje stanje
                    for (int fromCol = 0; fromCol < curState.numCols; fromCol++) {
                        for (int toCol = 0; toCol < curState.numCols; toCol++) {
                            if (curState.canMove(fromCol, toCol)) {
                                Warehouse nextState = new Warehouse(curState.state, warehouse.finalState);
                                String mv = nextState.move(fromCol, toCol).toString();
                                if (!containsState(visited, nextState.state)) {
                                    mvs.append(mv).append("\n");
                                    stack.push(nextState);
                                    visited.add(nextState);
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                }

                if (!found) {
                    stack.pop();
                }
            }

            System.out.println("-----------------------------------------------------------");
        }
    }
    public static boolean containsState(ArrayList<Warehouse> list, char[][] state) {
        for (Warehouse warehouse : list) {
            if (Arrays.deepEquals(warehouse.state, state)) {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) throws IOException {
        String initialFile = "primer1_zacetna.txt";
        String finalFile = "primer1_koncna.txt";
        char[][] initialState = Warehouse.readStateFromFile(initialFile);
        char[][] finalState = Warehouse.readStateFromFile(finalFile);
        Warehouse w = new Warehouse(initialState,finalState);
        search(w);
    }
}
