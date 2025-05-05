/**
 * Manages player turns and extra turn logic for the HexOust game.
 * Tracks the active player and handles turn switching and extra turn conditions.
 */
public class Player {
    private String activePlayer; // The current player ("Red" or "Blue")
    private static boolean extraTurn = false; // Flag to track if an extra turn is granted

    /**
     * Constructs a Player with the initial player set to Red.
     */
    public Player() {
        activePlayer = "Red"; // Set the starting player to Red
    }

    /**
     * Retrieves the current player.
     * @return The current player ("Red" or "Blue")
     */
    public String getCurrentPlayer() {
        return activePlayer; // Return the current player
    }

    /**
     * Switches the turn to the other player unless an extra turn is active.
     */
    public void switchTurn() {
        if (!extraTurn) { // Check if no extra turn is active
            activePlayer = activePlayer.equals("Red") ? "Blue" : "Red"; // Toggle between Red and Blue
        }
        extraTurn = false; // Reset the extra turn flag
    }

    /**
     * Grants the current player an extra turn.
     */
    public static void grantExtraTurn() {
        extraTurn = true; // Set the extra turn flag to true
    }

    /**
     * Resets the player state to the initial condition.
     */
    public void resetPlayer() {
        activePlayer = "Red"; // Reset the active player to Red
        extraTurn = false; // Reset the extra turn flag
    }
}