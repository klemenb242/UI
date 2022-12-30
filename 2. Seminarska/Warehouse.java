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
    private LinkedList<Move> moves;

    public double fitness;

    public Warehouse(char[][] initialState, char[][] finalState) {
        this.state = initialState;
        this.numRows = initialState.length;
        this.numCols = initialState[0].length;
        this.finalState = finalState;
        this.moves = new LinkedList<>();
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public double stateScore() {
        double score = 0.0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (state[i][j] == BLOCK_NULL) {
                    continue;
                }
                if (state[i][j] == finalState[i][j]) {
                    // if block is in the correct position, add 1.0 to the score
                    score += 1.0;
                } else {
                    // check if element is in the same column
                    for (int row = 0; row < numRows; row++) {
                        if (state[i][j] == finalState[row][j]) {
                            // if block is in the same column, add 0.2 to the score
                            score += 0.2;
                        }
                    }
                    // check if block is in an adjacent position to its desired position
                    for (int row = Math.max(0, i - 1); row <= Math.min(numRows - 1, i + 1); row++) {
                        for (int col = Math.max(0, j - 1); col <= Math.min(numCols - 1, j + 1); col++) {
                            if (state[i][j] == finalState[row][col]) {
                                // if block is in an adjacent position, add 0.1 to the score
                                score += 0.1;
                            }
                        }
                    }
                }
            }
        }
        return score;
    }

    public boolean isSolved() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (state[i][j] != finalState[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public Move move(int fromCol, int toCol) throws IllegalArgumentException {
        if (fromCol == toCol) {
            throw new IllegalArgumentException("FROM and TO columns are the same.");
        }
        if (fromCol < 0 || fromCol >= numCols || toCol < 0 || toCol >= numCols) {
            throw new IllegalArgumentException("Invalid column index.");
        }
        // if fromCOl is empty
        if (this.getColumnTop(fromCol) == BLOCK_NULL) {
            throw new IllegalArgumentException("No block in FROM column " + fromCol);
        }
        // if toCol is full
        if (this.isCoulmnFull(toCol)) {
            throw new IllegalArgumentException("TO column " + toCol + " is full.");
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
        Move move = new Move(fromRow, fromCol, toRow, toCol);
        addMove(move);
        return move;
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
}