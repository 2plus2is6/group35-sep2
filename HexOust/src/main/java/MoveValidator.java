// Validates moves before placement
public class MoveValidator {
    private CaptureHandler captureHandler; // Reference to CaptureHandler

    // Constructor initializes MoveValidator
    public MoveValidator(CaptureHandler captureHandler) {
        this.captureHandler = captureHandler; // Assigns CaptureHandler
    }

    // Checks if a move is valid
    public boolean isValidMove(double q, double r, String[][] hexStatus, String currentPlayer) {
        int boardQ = (int) q + 6; // Adjusts q to board index
        int boardR = (int) r + 6; // Adjusts r to board index
        if (hexStatus[boardQ][boardR] != null) { // Checks if hex is occupied
            return false; // Returns false if occupied
        }
        if (captureHandler.wouldCapture(q, r, hexStatus, currentPlayer)) { // Checks for capture
            return true; // Allows move if capture possible
        }
        int[][] directions = { // Defines adjacent directions
                {1, -1}, {1, 0}, {0, 1}, // NE, E, SE
                {-1, 1}, {-1, 0}, {0, -1} // SW, W, NW
        };
        for (int[] dir : directions) { // Loops through directions
            int adjQ = boardQ + dir[0]; // Calculates adjacent q
            int adjR = boardR + dir[1]; // Calculates adjacent r
            if (adjQ >= 0 && adjQ < hexStatus.length && adjR >= 0 && adjR < hexStatus[0].length) { // Checks bounds
                if (hexStatus[adjQ][adjR] != null && hexStatus[adjQ][adjR].equals(currentPlayer)) { // Checks same-color adjacency
                    return false; // Returns false if adjacent same color
                }
            }
        }
        return true; // Returns true if valid
    }
}