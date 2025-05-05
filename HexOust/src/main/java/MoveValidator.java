/**
 * Validates moves before stone placement in the HexOust game.
 * Ensures moves comply with game rules, including board boundaries, occupancy, and adjacency conditions.
 */
public class MoveValidator {
    private CaptureHandler captureHandler; // Reference to the capture handler for checking potential captures

    /**
     * Constructs a MoveValidator with a capture handler.
     * @param captureHandler The capture handler to check for potential captures
     * @throws IllegalArgumentException if the captureHandler is null
     */
    public MoveValidator(CaptureHandler captureHandler) {
        if (captureHandler == null) throw new IllegalArgumentException("CaptureHandler cannot be null");
        this.captureHandler = captureHandler; // Assign the capture handler reference
    }

    /**
     * Checks if a move is valid based on game rules.
     * Validates board coordinates, occupancy, and adjacency conditions, including potential captures.
     * @param q The q-coordinate of the move in cube coordinates
     * @param r The r-coordinate of the move in cube coordinates
     * @param hexStatus The current state of the board
     * @param currentPlayer The player making the move ("Red" or "Blue")
     * @return True if the move is valid, false otherwise
     * @throws IllegalArgumentException if hexStatus is null
     * @throws IndexOutOfBoundsException if the coordinates are out of bounds
     */
    public boolean isValidMove(double q, double r, String[][] hexStatus, String currentPlayer) {
        if (hexStatus == null) throw new IllegalArgumentException("Hex status cannot be null");
        int boardQ = (int) q + 6; // Convert q to board index
        int boardR = (int) r + 6; // Convert r to board index
        // Check if the coordinates are within the board boundaries
        if (boardQ < 0 || boardQ >= hexStatus.length || boardR < 0 || boardR >= hexStatus[0].length) {
            throw new IndexOutOfBoundsException("Invalid board coordinates");
        }
        // Check if the target hex is already occupied
        if (hexStatus[boardQ][boardR] != null) {
            System.out.println("Move rejected: Hex (" + boardQ + "," + boardR + ") is occupied");
            return false;
        }

        // Check if the board is empty (first move is always allowed)
        boolean isBoardEmpty = true;
        outerLoop:
        for (String[] row : hexStatus) {
            for (String cell : row) {
                if (cell != null) {
                    isBoardEmpty = false;
                    break outerLoop;
                }
            }
        }
        if (isBoardEmpty) {
            System.out.println("First move on empty board, allowing placement at (" + boardQ + "," + boardR + ")");
            return true;
        }

        // Check for adjacent stones of the same color
        boolean hasAdjacentSameColor = false;
        int[][] directions = {{1, -1}, {1, 0}, {0, 1}, {-1, 1}, {-1, 0}, {0, -1}}; // Six adjacent directions
        for (int[] dir : directions) {
            int adjQ = boardQ + dir[0]; // Calculate adjacent q index
            int adjR = boardR + dir[1]; // Calculate adjacent r index
            // Check if the adjacent position is within bounds
            if (adjQ >= 0 && adjQ < hexStatus.length && adjR >= 0 && adjR < hexStatus[0].length) {
                if (hexStatus[adjQ][adjR] != null && hexStatus[adjQ][adjR].equals(currentPlayer)) {
                    hasAdjacentSameColor = true;
                    break;
                }
            }
        }
        if (hasAdjacentSameColor) {
            System.out.println("Adjacent same-color stone detected at (" + boardQ + "," + boardR + ")");
            // Check if the move allows a capture
            if (!captureHandler.wouldCapture(q, r, hexStatus, currentPlayer)) {
                System.out.println("No capture possible, move rejected");
                return false;
            }
            System.out.println("Capture possible, allowing move");
        }
        return true;
    }
}