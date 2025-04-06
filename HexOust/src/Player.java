public class Player { // Keeps track of the current player
    private String activePlayer; // "Red" or "Blue"
    private static boolean extraTurn = false; // Instance variable


    public Player() {
        activePlayer = "Red"; // Always commences with Red
    }

    public String getCurrentPlayer() {
        return activePlayer; //Returns current player
    }

    // Method to switch turns between the players
    public void switchTurn() {
        // Only switch turns if the current player doesn't have an extra turn
        if (!extraTurn) {
            // Toggle between Red and Blue
            activePlayer = activePlayer.equals("Red") ? "Blue" : "Red";
        }
        // Reset the extraTurn flag after handling it
        extraTurn = false;
    }

    // Static method to grant an extra turn to the current player
    public static void grantExtraTurn() {
        // Set the extraTurn flag to true, meaning the player will get another turn
        extraTurn = true;
    }

}
