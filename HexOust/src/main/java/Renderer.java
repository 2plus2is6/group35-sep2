import javafx.scene.control.Label; // Used for turn and win indicators
import javafx.scene.paint.Color; // Defines colors for text
import javafx.scene.text.Font; // Sets font properties
import javafx.scene.text.FontWeight; // Sets font weight
import javafx.stage.Stage; // Main application window

/**
 * Manages rendering of UI elements for the HexOust game, such as turn indicators and win messages.
 * Updates the visual state of labels based on game progress.
 */
public class Renderer {
    private final Label turnIndicator; // Label to display the current player's turn
    private final Label winMessageLabel; // Label to display the win message
    private final Stage stage; // Reference to the main application window

    /**
     * Constructs a Renderer with the specified turn indicator and stage.
     * @param turnIndicator The label for displaying the current player's turn
     * @param stage The main application window
     */
    public Renderer(Label turnIndicator, Stage stage) {
        this.turnIndicator = turnIndicator; // Assign the turn indicator label
        this.stage = stage; // Assign the stage reference
        this.winMessageLabel = new Label(); // Create the win message label
        initializeTurnIndicator(); // Set up the turn indicator styling
        initializeWinMessageLabel(); // Set up the win message label styling
    }

    /**
     * Initializes the styling for the turn indicator label.
     */
    private void initializeTurnIndicator() {
        turnIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 24)); // Set font to Arial, bold, size 24
        turnIndicator.setTextFill(Color.RED); // Set initial text color to red
        turnIndicator.setVisible(true); // Make the turn indicator visible
    }

    /**
     * Initializes the styling for the win message label.
     */
    private void initializeWinMessageLabel() {
        winMessageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30)); // Set font to Arial, bold, size 30
        winMessageLabel.setTextFill(Color.RED); // Set default text color to red
        winMessageLabel.setVisible(false); // Hide the win message initially
    }

    /**
     * Displays the win message with a color based on the winner.
     * Hides the turn indicator while the win message is shown.
     * @param winner The winning player ("Red" or "Blue")
     */
    public void showWinMessage(String winner) {
        turnIndicator.setVisible(false); // Hide the turn indicator
        winMessageLabel.setText(winner + " Wins!"); // Set the win message text
        // Set the win message color based on the winner
        winMessageLabel.setTextFill(winner.equals("Blue") ? Color.LIGHTBLUE : Color.RED);
        winMessageLabel.setVisible(true); // Show the win message
    }

    /**
     * Hides the win message and makes the turn indicator visible again.
     */
    public void hideWinMessage() {
        winMessageLabel.setVisible(false); // Hide the win message
        turnIndicator.setVisible(true); // Show the turn indicator
        turnIndicator.setTextFill(Color.RED); // Reset the turn indicator color to red
    }

    /**
     * Retrieves the win message label for UI integration.
     * @return The win message label
     */
    public Label getWinMessageLabel() {
        return winMessageLabel; // Return the win message label
    }

    /**
     * Updates the turn indicator with the current player's name and corresponding color.
     * @param player The current player ("Red" or "Blue")
     */
    public void updateTurn(String player) {
        turnIndicator.setText(player + "'s Turn - To Make a Move"); // Update the turn text
        // Set the turn indicator color based on the player
        turnIndicator.setTextFill(player.equals("Red") ? Color.RED : Color.LIGHTBLUE);
    }

    /**
     * Makes the turn indicator visible in the UI.
     */
    public void showTurnIndicator() {
        turnIndicator.setVisible(true); // Make the turn indicator visible
    }

    /**
     * Displays an invalid move message in place of the turn indicator.
     * Hides the turn indicator and win message while showing the invalid move notification.
     */
    public void showInvalidMoveMessage() {
        turnIndicator.setVisible(false); // Hide the turn indicator
        winMessageLabel.setText("Invalid Move! Please Try Again"); // Set invalid move message
        winMessageLabel.setTextFill(Color.YELLOW); // Set a distinct color for invalid move
        winMessageLabel.setVisible(true); // Show the invalid move message
    }

    /**
     * Clears the invalid move message and restores the turn indicator.
     */
    public void clearInvalidMoveMessage() {
        winMessageLabel.setVisible(false); // Hide the invalid move message
        turnIndicator.setVisible(true); // Restore the turn indicator
        turnIndicator.setTextFill(Color.RED); // Reset turn indicator color
    }
}