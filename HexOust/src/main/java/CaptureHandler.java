import javafx.scene.canvas.GraphicsContext; // Used for drawing on canvas
import java.util.ArrayList; // Used for dynamic lists
import java.util.Arrays; // Used for array copying
import java.util.List; // Used for lists of coordinates

// Handles logic for capturing opponent's pieces
public class CaptureHandler {
    Board board; // Reference to Board for interaction

    // Constructor initializes CaptureHandler
    public CaptureHandler(Board board) {
        this.board = board; // Assigns Board reference
    }

    // Checks if a move would result in a capture
    public boolean wouldCapture(double q, double r, String[][] hexStatus, String currentPlayer) {
        String[][] tempHexStatus = copyHexStatus(hexStatus); // Copies hexStatus for simulation
        int boardQ = (int) q + 6; // Adjusts q to board index
        int boardR = (int) r + 6; // Adjusts r to board index
        tempHexStatus[boardQ][boardR] = currentPlayer; // Simulates move
        for (int[] dir : directions()) { // Loops through adjacent directions
            int adjQ = boardQ + dir[0]; // Calculates adjacent q
            int adjR = boardR + dir[1]; // Calculates adjacent r
            if (isValid(adjQ, adjR, tempHexStatus) && // Checks if position is valid
                    tempHexStatus[adjQ][adjR] != null && // Checks if position is occupied
                    !tempHexStatus[adjQ][adjR].equals(currentPlayer)) { // Checks if opponent's piece
                List<int[]> group = new ArrayList<>(); // List for opponent's group
                boolean[][] visited = new boolean[tempHexStatus.length][tempHexStatus[0].length]; // Tracks visited positions
                findGroupDFS(adjQ, adjR, tempHexStatus[adjQ][adjR], tempHexStatus, group, visited); // Finds opponent's group
                int newGroupSize = calculateGroupSize(boardQ, boardR, tempHexStatus, currentPlayer, new boolean[tempHexStatus.length][tempHexStatus[0].length]); // Calculates player's group size
                if (group.size() < newGroupSize) { // Checks if opponent's group is smaller
                    return true; // Capture possible
                }
            }
        }
        return false; // No capture
    }

    // Copies hexStatus array
    private String[][] copyHexStatus(String[][] original) {
        String[][] copy = new String[original.length][]; // Creates new array
        for (int i = 0; i < original.length; i++) { // Loops through rows
            copy[i] = Arrays.copyOf(original[i], original[i].length); // Copies each row
        }
        return copy; // Returns copied array
    }

    // Checks and performs captures
    public boolean checkAndCapture(double q, double r, String[][] hexStatus, String currentPlayer, GraphicsContext gc) {
        int boardQ = (int) q + 6; // Adjusts q to board index
        int boardR = (int) r + 6; // Adjusts r to board index
        boolean[][] visited = new boolean[hexStatus.length][hexStatus[0].length]; // Tracks visited positions
        int newGroupSize = calculateGroupSize(boardQ, boardR, hexStatus, currentPlayer, visited); // Calculates player's group size
        List<int[]> capturedStones = new ArrayList<>(); // List for captured stones
        for (int[] dir : directions()) { // Loops through adjacent directions
            int nq = boardQ + dir[0]; // Calculates adjacent q
            int nr = boardR + dir[1]; // Calculates adjacent r
            if (isValid(nq, nr, hexStatus) && hexStatus[nq][nr] != null && !hexStatus[nq][nr].equals(currentPlayer)) { // Checks opponent's piece
                List<int[]> group = new ArrayList<>(); // List for opponent's group
                boolean[][] groupVisited = new boolean[hexStatus.length][hexStatus[0].length]; // Tracks visited positions
                findGroupDFS(nq, nr, hexStatus[nq][nr], hexStatus, group, groupVisited); // Finds opponent's group
                if (group.size() > 0 && group.size() < newGroupSize) { // Checks if group is capturable
                    capturedStones.addAll(group); // Adds to captured stones
                }
            }
        }
        if (!capturedStones.isEmpty()) { // Checks if captures occurred
            board.removeStones(capturedStones, gc); // Removes captured stones
            return true; // Capture occurred
        }
        return false; // No capture
    }

    // Finds group of same-colored pieces using DFS
    private void findGroupDFS(int q, int r, String player, String[][] hexStatus, List<int[]> group, boolean[][] visited) {
        if (!isValid(q, r, hexStatus) || visited[q][r] || hexStatus[q][r] == null || !hexStatus[q][r].equals(player)) { // Checks invalid conditions
            return; // Exits if invalid
        }
        visited[q][r] = true; // Marks position as visited
        group.add(new int[]{q, r}); // Adds position to group
        for (int[] dir : directions()) { // Loops through directions
            findGroupDFS(q + dir[0], r + dir[1], player, hexStatus, group, visited); // Recursively checks adjacent positions
        }
    }

    // Calculates size of a player's group
    private int calculateGroupSize(int q, int r, String[][] hexStatus, String player, boolean[][] visited) {
        List<int[]> group = new ArrayList<>(); // List for group positions
        findGroupDFS(q, r, player, hexStatus, group, visited); // Finds group
        return group.size(); // Returns group size
    }

    // Validates board position
    private boolean isValid(int q, int r, String[][] hexStatus) {
        return q >= 0 && q < hexStatus.length && r >= 0 && r < hexStatus[0].length; // Checks bounds
    }

    // Defines six adjacent directions
    private int[][] directions() {
        return new int[][] {
                {1, -1}, {1, 0}, {0, 1}, // Right-up, Right, Down-right
                {-1, 1}, {-1, 0}, {0, -1} // Left-down, Left, Up-left
        }; // Returns direction array
    }
}