import javafx.scene.canvas.GraphicsContext; // Used for drawing on canvas
import javafx.scene.control.Button; // Used for restart and exit buttons
import javafx.stage.Stage; // Main application window

// Handles user input via buttons
public class InputHandler {
    private final Button exitButton; // Exit button
    private final Button restartButton; // Restart button

    // Constructor initializes buttons
    public InputHandler(Stage stage, GraphicsContext gc, GameManager gameManager, Board board, Player player) {
        restartButton = new Button("Restart Game"); // Creates restart button
        restartButton.setStyle( // Styles restart button
                "-fx-font-size: 16px; " +
                        "-fx-background-color: PURPLE; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        exitButton = new Button("Exit Game"); // Creates exit button
        exitButton.setStyle( // Styles exit button
                "-fx-font-size: 16px; " +
                        "-fx-background-color: RED; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        restartButton.setOnAction(e -> { // Sets restart button action
            board.resetBoard(); // Resets board
            player.resetPlayer(); // Resets player
            gameManager.reset(); // Resets GameManager
            board.render(gc); // Renders board
        });
        exitButton.setOnAction(e -> stage.close()); // Sets exit button action
    }

    // Returns exit button
    public Button getExitButton() {
        return exitButton; // Returns exit button
    }

    // Returns restart button
    public Button getRestartButton() {
        return restartButton; // Returns restart button
    }
}