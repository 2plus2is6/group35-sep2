// Import statements
import javafx.scene.canvas.GraphicsContext; // Used to draw on the canvas (for board updates)
import java.util.ArrayList; // Used for dynamic arrays (lists)
import java.util.Arrays; // Used to copy arrays
import java.util.List; // Used for working with lists of objects

// CaptureHandler class handles logic related to capturing opponent's pieces
public class CaptureHandler {
    private final Board board; // Reference to the Board class for interaction

    // Constructor initializes the CaptureHandler with the Board
    public CaptureHandler(Board board) {
        this.board = board;
    }

    // Simulates a move to check if it would result in a capture
    public boolean wouldCapture(double q, double r, String[][] hexStatus, String currentPlayer) {
        // Create a copy of the board to simulate the move
        String[][] tempHexStatus = copyHexStatus(hexStatus);
        int boardQ = (int) q + 6; // Adjust q-coordinate for the board
        int boardR = (int) r + 6; // Adjust r-coordinate for the board
        tempHexStatus[boardQ][boardR] = currentPlayer; // Simulate the player's move

        // Check all six possible directions from the placed stone
        for (int[] dir : directions()) {
            int adjQ = boardQ + dir[0]; // Adjacent q-coordinate
            int adjR = boardR + dir[1]; // Adjacent r-coordinate
            if (isValid(adjQ, adjR, tempHexStatus) && // Check if the adjacent position is valid
                    tempHexStatus[adjQ][adjR] != null && // Ensure the position is not empty
                    !tempHexStatus[adjQ][adjR].equals(currentPlayer)) { // Ensure the opponent's piece is there

                List<int[]> group = new ArrayList<>(); // List to hold the opponent's captured pieces
                boolean[][] visited = new boolean[tempHexStatus.length][tempHexStatus[0].length]; // Track visited positions
                findGroupDFS(adjQ, adjR, tempHexStatus[adjQ][adjR], tempHexStatus, group, visited); // Find the opponent's group

                int newGroupSize = calculateGroupSize(boardQ, boardR, tempHexStatus, currentPlayer, new boolean[tempHexStatus.length][tempHexStatus[0].length]);

                // If the opponent's group size is smaller than the player's new group, capture occurs
                if (group.size() < newGroupSize) {
                    return true; // Capture occurs
                }
            }
        }
        return false; // No capture
    }

    // Creates a copy of the hexStatus array (board state) to avoid modifying the original
    private String[][] copyHexStatus(String[][] original) {
        String[][] copy = new String[original.length][]; // Create a new array of the same size
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length); // Copy each row individually
        }
        return copy; // Return the copied array
    }

    // Checks and performs the capture if possible
    public boolean checkAndCapture(double q, double r, String[][] hexStatus, String currentPlayer, GraphicsContext gc) {
        int boardQ = (int) q + 6; // Adjust q-coordinate for the board
        int boardR = (int) r + 6; // Adjust r-coordinate for the board

        boolean[][] visited = new boolean[hexStatus.length][hexStatus[0].length]; // Track visited positions
        int newGroupSize = calculateGroupSize(boardQ, boardR, hexStatus, currentPlayer, visited); // Calculate the group size

        List<int[]> capturedStones = new ArrayList<>(); // List to store captured stones

        // Check all six possible directions from the placed stone
        for (int[] dir : directions()) {
            int nq = boardQ + dir[0]; // Adjacent q-coordinate
            int nr = boardR + dir[1]; // Adjacent r-coordinate

            // If adjacent position is valid and occupied by the opponent's piece
            if (isValid(nq, nr, hexStatus) && hexStatus[nq][nr] != null && !hexStatus[nq][nr].equals(currentPlayer)) {
                List<int[]> group = new ArrayList<>(); // List to hold the opponent's captured group
                boolean[][] groupVisited = new boolean[hexStatus.length][hexStatus[0].length]; // Track visited positions in the group
                findGroupDFS(nq, nr, hexStatus[nq][nr], hexStatus, group, groupVisited); // Find the opponent's group

                // If the opponent's group is smaller than the new group, it gets captured
                if (group.size() < newGroupSize) {
                    capturedStones.addAll(group); // Add captured stones to the list
                }
            }
        }

        // If there are any captured stones, remove them and return true
        if (!capturedStones.isEmpty()) {
            board.removeStones(capturedStones, gc); // Remove captured stones from the board
            return true; // Capture occurred
        }

        return false; // No capture
    }

    // Depth-first search (DFS) to find all pieces of a group of the same player
    private void findGroupDFS(int q, int r, String player, String[][] hexStatus, List<int[]> group, boolean[][] visited) {
        // If position is invalid, already visited, or not the player's piece, return
        if (!isValid(q, r, hexStatus) || visited[q][r] || hexStatus[q][r] == null || !hexStatus[q][r].equals(player)) {
            return;
        }

        visited[q][r] = true; // Mark the position as visited
        group.add(new int[]{q, r}); // Add position to the group

        // Recursively check all six directions for adjacent pieces
        for (int[] dir : directions()) {
            findGroupDFS(q + dir[0], r + dir[1], player, hexStatus, group, visited);
        }
    }

    // Calculates the size of the player's group by performing DFS
    private int calculateGroupSize(int q, int r, String[][] hexStatus, String player, boolean[][] visited) {
        List<int[]> group = new ArrayList<>(); // List to store the group's positions
        findGroupDFS(q, r, player, hexStatus, group, visited); // Perform DFS to find all pieces in the group
        return group.size(); // Return the size of the group
    }

    // Validates if the position is within the bounds of the board
    private boolean isValid(int q, int r, String[][] hexStatus) {
        return q >= 0 && q < hexStatus.length && r >= 0 && r < hexStatus[0].length; // Check if q and r are within bounds
    }

    // Defines the six possible directions to check (adjacent hexagons)
    private int[][] directions() {
        return new int[][] {
                {1, -1}, {1, 0}, {0, 1}, // Right-up, Right, Down-right
                {-1, 1}, {-1, 0}, {0, -1} // Left-down, Left, Up-left
        };
    }
}
