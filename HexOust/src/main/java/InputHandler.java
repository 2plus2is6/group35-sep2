import javafx.scene.canvas.GraphicsContext; // Used for drawing on the canvas
import javafx.scene.control.Button; // Used for creating interactive buttons
import javafx.stage.Stage; // Main application window

/**
 * Handles user input through buttons in the HexOust game.
 * Manages the restart and exit buttons, allowing players to reset the game or close the application.
 */
public class InputHandler {
    private final Button exitButton; // Button to exit the game
    private final Button restartButton; // Button to restart the game

    /**
     * Constructs an InputHandler with the specified dependencies and sets up button actions.
     * @param stage The main application window for closing the game
     * @param gc The graphics context for rendering the board
     * @param gameManager The game manager for resetting game state
     * @param board The game board for resetting and rendering
     * @param player The player manager for resetting player state
     */
    public InputHandler(Stage stage, GraphicsContext gc, GameManager gameManager, Board board, Player player) {
        restartButton = new Button("Restart Game"); // Create the restart button
        // Apply styling to the restart button for better visuals
        restartButton.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-background-color: PURPLE; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        exitButton = new Button("Exit Game"); // Create the exit button
        // Apply styling to the exit button for better visuals
        exitButton.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-background-color: RED; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        // Define the action for the restart button
        restartButton.setOnAction(e -> {
            board.resetBoard(); // Clear the game board
            player.resetPlayer(); // Reset the player state
            gameManager.reset(); // Reset the game manager state
            board.render(gc); // Redraw the board
        });
        // Define the action for the exit button
        exitButton.setOnAction(e -> stage.close()); // Close the application window
    }

    /**
     * Retrieves the exit button for UI integration.
     * @return The exit button
     */
    public Button getExitButton() {
        return exitButton; // Return the exit button
    }

    /**
     * Retrieves the restart button for UI integration.
     * @return The restart button
     */
    public Button getRestartButton() {
        return restartButton; // Return the restart button
    }
}