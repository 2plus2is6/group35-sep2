public class Player { // Keeps track of the current player
    private String activePlayer; // "Red" or "Blue"

    public Player() {
        activePlayer = "Red"; // Always commences with Red
    }

    public String getCurrentPlayer() {
        return activePlayer;
    }

    public void switchTurn() {
        activePlayer = activePlayer.equals("Red") ? "Blue" : "Red";
    }
}
