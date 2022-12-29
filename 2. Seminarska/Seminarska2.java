import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Warehouse {
    int numRows;
    int numCols;
    private LinkedList<State> states;
    private LinkedList<Move> moves;
    private State finalState;

    public Warehouse(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.states = new LinkedList<>();
        this.moves = new LinkedList<>();
    }

    public void addState(State state) {
        states.add(state);
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public void setFinalState(State state) {
        this.finalState = state;
    }

    public State currentState() {
        return this.states.getLast();
    }

    public State getInitialState(State state) {
        return states.getFirst();
    }

    public State getStateAt(int index) {
        return states.get(index);
    }

    public int getNumStates() {
        return states.size();
    }

    public boolean isSolved() {
        String currentStateString = currentState().toString();
        String finalStateString = finalState.toString();
        return currentStateString.equals(finalStateString);
    }

    public State move(int fromCol, int toCol) throws IllegalArgumentException {
        if (fromCol < 0 || fromCol >= numCols || toCol < 0 || toCol >= numCols) {
            throw new IllegalArgumentException("Invalid column index.");
        }
        State currentState = currentState();
        // if fromCOl is empty
        if (currentState.getColumnTop(fromCol) == '\0') {
            throw new IllegalArgumentException("No block in FROM column " + fromCol);
        }
        // if toCol is full
        if (currentState.isCoulmnFull(toCol)) {
            throw new IllegalArgumentException("TO column " + toCol + " is full.");
        }
        // create a new grid
        char[][] newGrid = new char[numRows][numCols];
        // copy the current grid
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                newGrid[i][j] = currentState.grid[i][j];
            }
        }
        // find the row of the top block in the FROM column
        int fromRow = 0;
        while ((fromRow < numRows - 1) && newGrid[fromRow][fromCol] == '\0') {
            fromRow++;
        }
        // find the first empty block row in the TO column
        int toRow = 0;
        while ((toRow < numRows) && newGrid[toRow][toCol] == '\0') {
            toRow++;
        }
        toRow--;
        // move the block
        newGrid[toRow][toCol] = currentState.getColumnTop(fromCol);
        newGrid[fromRow][fromCol] = '\0';
        // create a new state
        State newState = new State(numRows, numCols, newGrid);
        addState(newState);
        // create a new move
        Move move = new Move(fromRow, fromCol, toRow, toCol);
        addMove(move);
        return newState;
    }

    public String toString() {
        return currentState().toString();
    }

    public static Warehouse createWareHouse(String fileInitial, String fileFinal) throws IOException {
        Warehouse.State initialState = Warehouse.readStateFromFile(fileInitial);
        Warehouse.State finalState = Warehouse.readStateFromFile(fileFinal);
        if (initialState.numRows != finalState.numRows || initialState.numCols != finalState.numCols) {
            throw new IllegalArgumentException("The initial and final states must have the same dimensions.");
        }
        Warehouse warehouse = new Warehouse(initialState.numRows, initialState.numCols);
        warehouse.addState(initialState);
        warehouse.setFinalState(finalState);
        return warehouse;
    }

    public static State readStateFromFile(String fileName)
            throws FileNotFoundException, IOException {

        // read the file into the buffer
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        String line;

        // initialize list of rows
        List<char[]> rows = new ArrayList<>();

        // read the file line by line
        while ((line = br.readLine()) != null) {

            // split the line by comma to get individual characters
            String[] chars = line.split(",");

            // initialize array of characters
            char[] charArr = new char[chars.length];

            // iterate over the characters
            for (int i = 0; i < chars.length; i++) {
                // if character is empty, insert null instead of character
                if (chars[i].trim().isEmpty())
                    charArr[i] = '\0';
                else if (chars[i].trim().charAt(1) == ' ')
                    charArr[i] = '\0';
                else
                    charArr[i] = chars[i].trim().charAt(1);
            }

            // add the array of characters to the list of rows
            rows.add(charArr);
        }

        // close the buffer reader
        br.close();

        // get the number of rows and columns
        int numRows = rows.size();
        int numCols = rows.get(0).length;

        // create the 2D char array
        char[][] grid = new char[numRows][numCols];

        // populate the 2D array with characters
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                grid[i][j] = rows.get(i)[j];
            }
        }
        State state = new State(numRows, numCols, grid);
        return state;
    }

    public class Move {
        int fromRow;
        int fromCol;
        int toRow;
        int toCol;

        public Move(int fromRow, int fromCol, int toRow, int toCol) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
        }
    }

    // Class representing the state of the warehouse
    public static class State {
        public final int numRows;
        public final int numCols;
        public final char[][] grid; // grid[row][col]

        public State(int numRows, int numCols, char[][] grid) {
            this.numRows = numRows;
            this.numCols = numCols;
            this.grid = grid;
        }

        public char getColumnTop(int col) {
            for (int i = 0; i < numRows; i++) {
                if (grid[i][col] != '\0') {
                    return grid[i][col];
                }
            }
            return '\0';
        }

        public boolean isCoulmnFull(int col) {
            return this.grid[0][col] != '\0';
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numRows; i++) {
                sb.append(i + ": ");
                for (int j = 0; j < numCols; j++) {
                    // check if character is null at indexes
                    if (grid[i][j] == '\u0000') {
                        sb.append(".");
                    } else {
                        sb.append(grid[i][j]);
                    }
                }
                sb.append(" ");
            }
            return sb.toString();
        }
    }
}

public class Seminarska2 {

    public static void main(String[] args) throws IOException {
        Warehouse warehouse = Warehouse.createWareHouse("test_zacetna.txt", "test_koncna.txt");
        System.out.println(warehouse);
        warehouse.move(0, 2);
        System.out.println(warehouse);
        warehouse.move(0, 1);
        System.out.println(warehouse);
        warehouse.move(2, 1);
        System.out.println(warehouse);
        System.out.println(warehouse.isSolved());

    }

}
