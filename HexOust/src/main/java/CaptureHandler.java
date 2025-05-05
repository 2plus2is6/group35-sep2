import javafx.scene.canvas.GraphicsContext; // Used for drawing on the canvas
import java.util.ArrayList; // Used for dynamic lists
import java.util.Arrays; // Used for array copying
import java.util.List; // Used for lists of coordinates

/**
 * Handles the logic for capturing opponent's pieces in the HexOust game.
 * Uses depth-first search (DFS) to identify groups of stones and determines if captures occur based on group sizes.
 */
public class CaptureHandler {
    Board board; // Reference to the game board for interaction

    /**
     * Constructs a CaptureHandler with a reference to the game board.
     * @param board The game board to interact with
     */
    public CaptureHandler(Board board) {
        this.board = board; // Assign the board reference
    }

    /**
     * Checks if a move at the given coordinates would result in a capture.
     * Simulates the move and compares group sizes to determine if a capture is possible.
     * @param q The q-coordinate of the move in cube coordinates
     * @param r The r-coordinate of the move in cube coordinates
     * @param hexStatus The current state of the board
     * @param currentPlayer The player making the move ("Red" or "Blue")
     * @return True if the move would result in a capture, false otherwise
     */
    public boolean wouldCapture(double q, double r, String[][] hexStatus, String currentPlayer) {
        String[][] tempHexStatus = copyHexStatus(hexStatus); // Create a copy for simulation
        int boardQ = (int) q + 6; // Convert q to board index
        int boardR = (int) r + 6; // Convert r to board index
        tempHexStatus[boardQ][boardR] = currentPlayer; // Simulate placing the stone
        for (int[] dir : directions()) { // Check all adjacent hexes
            int adjQ = boardQ + dir[0]; // Calculate adjacent q index
            int adjR = boardR + dir[1]; // Calculate adjacent r index
            // Check if the adjacent position has an opponent's stone
            if (isValid(adjQ, adjR, tempHexStatus) && tempHexStatus[adjQ][adjR] != null &&
                    !tempHexStatus[adjQ][adjR].equals(currentPlayer)) {
                List<int[]> group = new ArrayList<>(); // List to store opponent's group
                boolean[][] visited = new boolean[tempHexStatus.length][tempHexStatus[0].length]; // Track visited hexes
                findGroupDFS(adjQ, adjR, tempHexStatus[adjQ][adjR], tempHexStatus, group, visited); // Find opponent's group
                // Calculate the size of the player's group after the move
                int newGroupSize = calculateGroupSize(boardQ, boardR, tempHexStatus, currentPlayer,
                        new boolean[tempHexStatus.length][tempHexStatus[0].length]);
                System.out.println("Opponent group size: " + group.size() + ", Player group size: " + newGroupSize);
                if (group.size() < newGroupSize) { // Compare group sizes for capture
                    System.out.println("Capture possible at (" + adjQ + "," + adjR + ")");
                    return true; // Capture is possible
                }
            }
        }
        System.out.println("No capture possible");
        return false; // No capture possible
    }

    /**
     * Creates a deep copy of the hex status array for simulation.
     * @param original The original hex status array to copy
     * @return A deep copy of the hex status array
     */
    private String[][] copyHexStatus(String[][] original) {
        String[][] copy = new String[original.length][]; // Create new array for the copy
        for (int i = 0; i < original.length; i++) { // Iterate over each row
            copy[i] = Arrays.copyOf(original[i], original[i].length); // Copy the row
        }
        return copy; // Return the copied array
    }

    /**
     * Checks for captures after a move and removes captured stones if any.
     * @param q The q-coordinate of the move in cube coordinates
     * @param r The r-coordinate of the move in cube coordinates
     * @param hexStatus The current state of the board
     * @param currentPlayer The player making the move ("Red" or "Blue")
     * @param gc The graphics context for updating the UI
     * @return True if a capture occurred, false otherwise
     */
    public boolean checkAndCapture(double q, double r, String[][] hexStatus, String currentPlayer, GraphicsContext gc) {
        int boardQ = (int) q + 6; // Convert q to board index
        int boardR = (int) r + 6; // Convert r to board index
        boolean[][] visited = new boolean[hexStatus.length][hexStatus[0].length]; // Track visited hexes
        // Calculate the player's group size starting from the placed stone
        int newGroupSize = calculateGroupSize(boardQ, boardR, hexStatus, currentPlayer, visited);
        List<int[]> capturedStones = new ArrayList<>(); // List to store captured stones
        for (int[] dir : directions()) { // Check all adjacent hexes
            int nq = boardQ + dir[0]; // Calculate adjacent q index
            int nr = boardR + dir[1]; // Calculate adjacent r index
            // Check if the adjacent hex has an opponent's stone
            if (isValid(nq, nr, hexStatus) && hexStatus[nq][nr] != null &&
                    !hexStatus[nq][nr].equals(currentPlayer)) {
                List<int[]> group = new ArrayList<>(); // List to store opponent's group
                boolean[][] groupVisited = new boolean[hexStatus.length][hexStatus[0].length]; // Track visited hexes
                findGroupDFS(nq, nr, hexStatus[nq][nr], hexStatus, group, groupVisited); // Find opponent's group
                if (group.size() > 0 && group.size() < newGroupSize) { // If opponent's group is smaller
                    capturedStones.addAll(group); // Add the group to captured stones
                }
            }
        }
        if (!capturedStones.isEmpty()) { // If there are stones to capture
            board.removeStones(capturedStones, gc); // Remove the captured stones from the board
            return true; // Indicate a capture occurred
        }
        return false; // No capture occurred
    }

    /**
     * Finds a group of same-colored stones using depth-first search (DFS).
     * @param q The starting q-index in the hexStatus array
     * @param r The starting r-index in the hexStatus array
     * @param player The player whose stones to group ("Red" or "Blue")
     * @param hexStatus The current state of the board
     * @param group The list to store the group's coordinates
     * @param visited A 2D array tracking visited positions
     */
    private void findGroupDFS(int q, int r, String player, String[][] hexStatus, List<int[]> group, boolean[][] visited) {
        // Stop if the position is invalid, visited, empty, or not the player's stone
        if (!isValid(q, r, hexStatus) || visited[q][r] || hexStatus[q][r] == null ||
                !hexStatus[q][r].equals(player)) {
            return;
        }
        visited[q][r] = true; // Mark the position as visited
        group.add(new int[]{q, r}); // Add the position to the group
        for (int[] dir : directions()) { // Check all adjacent hexes
            // Recursively explore adjacent positions
            findGroupDFS(q + dir[0], r + dir[1], player, hexStatus, group, visited);
        }
    }

    /**
     * Calculates the size of a player's group of connected stones.
     * @param q The starting q-index in the hexStatus array
     * @param r The starting r-index in the hexStatus array
     * @param hexStatus The current state of the board
     * @param player The player whose group size to calculate ("Red" or "Blue")
     * @param visited A 2D array tracking visited positions
     * @return The size of the player's group
     */
    private int calculateGroupSize(int q, int r, String[][] hexStatus, String player, boolean[][] visited) {
        List<int[]> group = new ArrayList<>(); // List to store the group
        findGroupDFS(q, r, player, hexStatus, group, visited); // Find the group using DFS
        return group.size(); // Return the number of stones in the group
    }

    /**
     * Validates if a position is within the board's bounds.
     * @param q The q-index to check
     * @param r The r-index to check
     * @param hexStatus The board's state array
     * @return True if the position is within bounds, false otherwise
     */
    private boolean isValid(int q, int r, String[][] hexStatus) {
        // Check if the indices are within the array dimensions
        return q >= 0 && q < hexStatus.length && r >= 0 && r < hexStatus[0].length;
    }

    /**
     * Provides the six adjacent directions in the hex grid.
     * @return A 2D array of direction offsets for adjacent hexes
     */
    private int[][] directions() {
        // Define the six directions: NE, E, SE, SW, W, NW
        return new int[][] {{1, -1}, {1, 0}, {0, 1}, {-1, 1}, {-1, 0}, {0, -1}};
    }
}