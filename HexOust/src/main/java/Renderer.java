import javafx.scene.control.Label; // Used for turn and win indicators
import javafx.scene.paint.Color; // Defines colors for text
import javafx.scene.text.Font; // Sets font properties
import javafx.scene.text.FontWeight; // Sets font weight
import javafx.stage.Stage; // Main application window

/**
 * Manages rendering of UI elements like turn indicators and win messages.
 */
public class Renderer {
    private final Label turnIndicator; // Label for turn display
    private final Label winMessageLabel; // Label for win message
    private final Stage stage; // Reference to main window

    /**
     * Constructs a Renderer with a turn indicator and stage.
     * @param turnIndicator Label for displaying current player's turn
     * @param stage Main application window
     */
    public Renderer(Label turnIndicator, Stage stage) {
        this.turnIndicator = turnIndicator; // Assigns turn indicator
        this.stage = stage; // Assigns Stage
        this.winMessageLabel = new Label(); // Creates win message label
        initializeTurnIndicator(); // Sets up turn indicator
        initializeWinMessageLabel(); // Sets up win message label
    }

    // Initializes turn indicator styling
    private void initializeTurnIndicator() {
        turnIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 24)); // Sets turn font
        turnIndicator.setTextFill(Color.RED); // Sets initial color
        turnIndicator.setVisible(true); // Makes turn indicator visible
    }

    // Initializes win message label styling
    private void initializeWinMessageLabel() {
        winMessageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30)); // Sets win message font
        winMessageLabel.setTextFill(Color.RED); // Sets default color
        winMessageLabel.setVisible(false); // Hides win message initially
    }

    /**
     * Displays the win message with color based on the winner.
     * @param winner The winning player ("Red" or "Blue")
     */
    public void showWinMessage(String winner) {
        turnIndicator.setVisible(false); // Hides turn indicator
        winMessageLabel.setText(winner + " Wins!"); // Sets win message
        winMessageLabel.setTextFill(winner.equals("Blue") ? Color.LIGHTBLUE : Color.RED); // Sets color based on winner
        winMessageLabel.setVisible(true); // Shows win message
    }

    /**
     * Hides the win message and shows the turn indicator.
     */
    public void hideWinMessage() {
        winMessageLabel.setVisible(false); // Hides win message
        turnIndicator.setVisible(true); // Shows turn indicator
        turnIndicator.setTextFill(Color.RED); // Resets turn color
    }

    /**
     * Gets the win message label.
     * @return The win message label
     */
    public Label getWinMessageLabel() {
        return winMessageLabel; // Returns win message label
    }

    /**
     * Updates the turn indicator with the current player's name and color.
     * @param player The current player ("Red" or "Blue")
     */
    public void updateTurn(String player) {
        turnIndicator.setText(player + "'s Turn - To Make a Move"); // Sets turn text
        turnIndicator.setTextFill(player.equals("Red") ? Color.RED : Color.LIGHTBLUE); // Sets color based on player
    }

    /**
     * Makes the turn indicator visible.
     */
    public void showTurnIndicator() {
        turnIndicator.setVisible(true); // Makes turn indicator visible
    }
}


