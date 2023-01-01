import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.*;

class Warehouse {
    private static final char BLOCK_NULL = '\0';

    int numRows;
    int numCols;
    public char[][] state;
    private char[][] finalState;
    private LinkedList<Move> moves = new LinkedList<>();
    public double stateDistance = -1;

    public Warehouse(char[][] initialState, char[][] finalState) {
        this.state = Warehouse.cloneState(initialState);
        this.numRows = initialState.length;
        this.numCols = initialState[0].length;
        this.finalState = finalState;
    }

    private double distanceForPosition(Position currentP, Position finalP) {
        double distance = Math.sqrt(
                Math.pow(currentP.getRow() - finalP.getRow(), 2) + Math.pow(currentP.getCol() - finalP.getCol(), 2));
        // if the block is in the same column, but not in the right row, then it is
        // still far off from final position
        if (currentP.getCol() == finalP.getCol()) {
            distance += this.numCols;
        }
        return distance;
    }

    public double calculateStateDistance() {
        double score = 0.0;
        // loop over all rows and columns
        HashMap<Character, Position> seenBlocks = new HashMap<>();
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numCols; column++) {
                if (state[row][column] == finalState[row][column]) {
                    continue;
                }
                if (seenBlocks.containsKey(state[row][column])) {
                    Position seenPos = seenBlocks.get(state[row][column]);
                    score += distanceForPosition(new Position(row, column), seenPos);
                }
                if (seenBlocks.containsKey(finalState[row][column])) {
                    Position seenPos = seenBlocks.get(finalState[row][column]);
                    score += distanceForPosition(seenPos, new Position(row, column));
                }
                if (state[row][column] != BLOCK_NULL) {
                    seenBlocks.put(state[row][column], new Position(row, column));
                }
                if (finalState[row][column] != BLOCK_NULL) {
                    seenBlocks.put(finalState[row][column], new Position(row, column));
                }
            }
        }
        return score;
    }

    public boolean isSolved() {
        return stateDistance == 0.0;
    }

    public Move move(int fromCol, int toCol) throws IllegalArgumentException {
        if (!canMove(fromCol, toCol)) {
            throw new IllegalArgumentException("Can`t move there.");
        }
        // find the row of the top block in the FROM column
        int fromRow = 0;
        while ((fromRow < numRows - 1) && state[fromRow][fromCol] == BLOCK_NULL) {
            fromRow++;
        }
        // find the first empty block row in the TO column
        int toRow = 0;
        while ((toRow < numRows) && state[toRow][toCol] == BLOCK_NULL) {
            toRow++;
        }
        toRow--;
        // move the block
        state[toRow][toCol] = state[fromRow][fromCol];
        state[fromRow][fromCol] = BLOCK_NULL;
        // create a new move
        Move move = new Move(new Position(fromRow, fromCol), new Position(toRow, toCol));
        addMove(move);
        this.stateDistance = calculateStateDistance();
        return move;
    }

    public boolean canMove(int fromCol, int toCol) {
        if (fromCol == toCol) {
            return false;
        }
        if (fromCol < 0 || fromCol >= numCols || toCol < 0 || toCol >= numCols) {
            return false;
        }
        // if fromCOl is empty
        if (this.getColumnTop(fromCol) == BLOCK_NULL) {
            return false;
        }
        // if toCol is full
        if (this.isCoulmnFull(toCol)) {
            return false;
        }
        return true;
    }

    public Move makeRandomMove() {
        Random random = new Random();
        Move move = null;
        int fromCol;
        int toCol;
        while (move == null) {
            fromCol = random.nextInt(numCols);
            toCol = random.nextInt(numCols);
            try {
                move = move(fromCol, toCol);
            } catch (Exception ignore) {
            }
        }
        return move;
    }

    public char getColumnTop(int col) {
        for (int i = 0; i < numRows; i++) {
            if (state[i][col] != BLOCK_NULL) {
                return state[i][col];
            }
        }
        return BLOCK_NULL;
    }

    public boolean isCoulmnFull(int col) {
        return state[0][col] != BLOCK_NULL;
    }

    public String stateToString(char[][] state) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numRows; i++) {
            sb.append(i + ": ");
            for (int j = 0; j < numCols; j++) {
                // check if character is null at indexes
                if (state[i][j] == BLOCK_NULL) {
                    sb.append(".");
                } else {
                    sb.append(state[i][j]);
                }
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    public String toString() {
        return stateToString(state);
    }

    public Warehouse deepClone() {
        Warehouse clone = new Warehouse(state, finalState);
        LinkedList<Move> clonedMoves = new LinkedList<>(moves);
        clone.overwriteMoves(clonedMoves);
        clone.stateDistance = this.stateDistance;
        return clone;
    }

    public static char[][] cloneState(char[][] state) {
        char[][] clonedState = new char[state.length][state[0].length];
        for (int i = 0; i < state.length; i++) {
            clonedState[i] = state[i].clone();
        }
        return clonedState;
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public void overwriteMoves(LinkedList<Move> moves) {
        this.moves = moves;
    }

    public int getNumberOfMoves() {
        return moves.size();
    }

    public LinkedList<Move> getMoves() {
        return moves;
    }

    public double fValue() {
        return gValue() + hValue();
    }

    public int gValue() {
        return moves.size() * 3;
    }

    public double hValue() {
        return stateDistance;
    }

    public static Warehouse createFromFile(String fileInitial, String fileFinal) throws IOException {
        char[][] initialState = Warehouse.readStateFromFile(fileInitial);
        char[][] finalState = Warehouse.readStateFromFile(fileFinal);
        Warehouse warehouse = new Warehouse(initialState, finalState);
        return warehouse;
    }

    public static char[][] readStateFromFile(String fileName)
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
                    charArr[i] = BLOCK_NULL;
                else if (chars[i].trim().charAt(1) == ' ')
                    charArr[i] = BLOCK_NULL;
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
        char[][] state = new char[numRows][numCols];

        // populate the 2D array with characters
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                state[i][j] = rows.get(i)[j];
            }
        }
        return state;
    }

    public class Position {
        int row;
        int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    public class Move {
        Position from;
        Position to;

        public Move(Position from, Position to) {
            this.from = from;
            this.to = to;
        }

        public Position getFrom() {
            return from;
        }

        public int getFromCol() {
            return from.getCol();
        }

        public int getToCol() {
            return to.getCol();
        }

        public Position getTo() {
            return to;
        }

        public String toString() {
            return from.getCol() + " -> " + to.getCol();
        }
    }

}