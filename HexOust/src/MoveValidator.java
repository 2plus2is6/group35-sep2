public class MoveValidator { // Checks if the clicked hexagon is a valid spot for a move

    public boolean isValidMove(double q, double r, String[][] hexStatus, String currentPlayer) {
        int boardQ = (int) q + 6; // Adjust q to match the board array index
        int boardR = (int) r + 6; // Adjust r to match the board array index

        // First check: Is this hex already taken?
        if (hexStatus[boardQ][boardR] != null) {
            return false; // Someone's already placed a stone here
        }

        // Second check: Does placing a stone here create a bigger group?
        if (enlargesGroup(q, r, hexStatus, currentPlayer)) {
            return false; // If yes, this move is not allowed
        }

        return true; // If both checks pass, it's a valid move
    }

    private boolean enlargesGroup(double q, double r, String[][] hexStatus, String currentPlayer) {
        // List of all possible directions to check neighboring hexagons to avoid a capturing move
        int[][] directions = {
                {1, -1}, {1, 0}, {0, 1},  // Right-up, Right, Down-right
                {-1, 1}, {-1, 0}, {0, -1} // Left-down, Left, Up-left
        };
        int boardQ = (int) q + 6; // Adjust to hex board indexing
        int boardR = (int) r + 6; // Adjust to hex board indexing

        // Loop through all six neighbors to check if they belong to the same colour (player) or not
        for (int[] dir : directions) {
            int neighborQ = boardQ + dir[0];
            int neighborR = boardR + dir[1];

            // Check if the neighbor is inside board boundaries or not
            if (neighborQ >= 0 && neighborQ < hexStatus.length && neighborR >= 0 && neighborR < hexStatus[0].length) {

                // If this neighbor belongs to the current player, it means the move would enlarge a group
                if (hexStatus[neighborQ][neighborR] != null && hexStatus[neighborQ][neighborR].equals(currentPlayer)) {
                    return true; // This move is NOT allowed
                }
            }
        }
        return false; // Move is fine since it doesn’t connect to an existing group
    }

}
