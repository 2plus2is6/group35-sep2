public class MoveValidator {
    private CaptureHandler captureHandler;

    public MoveValidator(CaptureHandler captureHandler) {
        this.captureHandler = captureHandler;
    }

    public boolean isValidMove(double q, double r, String[][] hexStatus, String currentPlayer) {
        int boardQ = (int) q + 6; // Adjust to board indices
        int boardR = (int) r + 6;

        // Check if hex is already occupied
        if (hexStatus[boardQ][boardR] != null) {
            return false;
        }

        // Check if the move would result in a capture (allowed even if adjacent)
        if (captureHandler.wouldCapture(q, r, hexStatus, currentPlayer)) {
            return true; // Allow the move if a capture is possible
        }

        // Check adjacent hexes for same-color stones
        int[][] directions = {
                {1, -1}, {1, 0}, {0, 1}, // NE, E, SE
                {-1, 1}, {-1, 0}, {0, -1} // SW, W, NW
        };

        for (int[] dir : directions) {
            int adjQ = boardQ + dir[0];
            int adjR = boardR + dir[1];
            if (adjQ >= 0 && adjQ < hexStatus.length && adjR >= 0 && adjR < hexStatus[0].length) {
                if (hexStatus[adjQ][adjR] != null && hexStatus[adjQ][adjR].equals(currentPlayer)) {
                    return false; // Adjacent same color without capture
                }
            }
        }

        return true; // Valid move
    }

}