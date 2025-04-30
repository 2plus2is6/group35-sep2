// Manages player turns
public class Player {
    private String activePlayer; // Current player ("Red" or "Blue")
    private static boolean extraTurn = false; // Tracks extra turn status

    // Constructor initializes Player
    public Player() {
        activePlayer = "Red"; // Sets initial player to Red
    }

    // Returns current player
    public String getCurrentPlayer() {
        return activePlayer; // Returns active player
    }

    // Switches player turns
    public void switchTurn() {
        if (!extraTurn) { // Checks if no extra turn
            activePlayer = activePlayer.equals("Red") ? "Blue" : "Red"; // Toggles player
        }
        extraTurn = false; // Resets extra turn flag
    }

    // Grants an extra turn
    public static void grantExtraTurn() {
        extraTurn = true; // Sets extra turn flag
    }

    // Resets player state
    public void resetPlayer() {
        activePlayer = "Red"; // Sets player to Red
        extraTurn = false; // Resets extra turn
    }
}