import javafx.application.Platform; // Used for JavaFX thread operations
import javafx.scene.canvas.GraphicsContext; // Used for drawing on the canvas
import javafx.scene.control.Alert; // Used for game-over dialog
import javafx.scene.control.ButtonBar; // Used for dialog button types
import javafx.scene.control.ButtonType; // Used for dialog buttons
import javafx.stage.Stage;

import java.util.Optional; // Used for handling dialog results

/**
 * Manages the core logic of the HexOust game, including move processing, win conditions, and game state.
 * Coordinates interactions between the board, players, and UI to ensure smooth gameplay.
 */
public class GameManager {
    private final Player player; // Tracks the current player and turn state
    private Board board; // Manages the game board and stone placement
    private final MoveValidator moveValidator; // Validates player moves
    private final CaptureHandler captureHandler; // Handles capturing opponent pieces
    private final Renderer renderer; // Updates UI elements like turn indicators
    private final GraphicsContext gc; // Canvas drawing context
    private boolean opponentHadStones; // Tracks if the opponent previously had stones

    /**
     * Constructs a GameManager with the specified dependencies.
     * @param board The game board (may be null initially)
     * @param player The player manager
     * @param renderer The UI renderer
     * @param stage The main application window
     * @param gc The graphics context for drawing
     */
    public GameManager(Board board, Player player, Renderer renderer, Stage stage, GraphicsContext gc) {
        this.board = board; // Assign the board reference
        this.player = player; // Assign the player reference
        this.renderer = renderer; // Assign the renderer reference
        this.gc = gc; // Assign the graphics context
        this.captureHandler = new CaptureHandler(board); // Initialize capture handler
        this.moveValidator = new MoveValidator(captureHandler); // Initialize move validator
        this.opponentHadStones = false; // Set initial opponent stone flag
    }

    /**
     * Sets the game board and updates related dependencies.
     * @param board The game board to set
     */
    public void setBoard(Board board) {
        this.board = board; // Update the board reference
        this.captureHandler.board = board; // Update the capture handler's board reference
    }

    /**
     * Resets the game state to start a new game.
     */
    public void reset() {
        opponentHadStones = false; // Reset the opponent stone history
        renderer.hideWinMessage(); // Hide any existing win message
        renderer.showTurnIndicator(); // Show the turn indicator
        renderer.updateTurn("Red"); // Set the starting player to Red
    }

    /**
     * Processes a player's move based on the click coordinates.
     * @param gc The graphics context for drawing the move
     * @param x The x-coordinate of the click
     * @param y The y-coordinate of the click
     */
    public void makeMove(GraphicsContext gc, double x, double y) {
        Board.HexCube clickedHex = board.pixelToHex(x, y); // Convert pixel coordinates to hex
        String currentPlayer = player.getCurrentPlayer(); // Get the current player
        if (!isValidMove(clickedHex.q, clickedHex.r, currentPlayer)) { // Check if the move is valid
            renderer.showInvalidMoveMessage(); // Display invalid move message
            System.out.println("Invalid move!"); // Log the invalid move
            return; // Exit if the move is not allowed
        }
        renderer.clearInvalidMoveMessage(); // Clear invalid message on valid move
        executeMove(gc, x, y, currentPlayer); // Execute the valid move
        handlePostMoveLogic(currentPlayer); // Handle captures and game end conditions
    }

    /**
     * Validates if a move is allowed at the given coordinates.
     * @param q The q-coordinate of the move
     * @param r The r-coordinate of the move
     * @param currentPlayer The player making the move ("Red" or "Blue")
     * @return True if the move is valid, false otherwise
     */
    private boolean isValidMove(double q, double r, String currentPlayer) {
        return moveValidator.isValidMove(q, r, board.getHexStatus(), currentPlayer); // Delegate to validator
    }

    /**
     * Executes a move by placing a stone on the board.
     * @param gc The graphics context for drawing
     * @param x The x-coordinate of the click
     * @param y The y-coordinate of the click
     * @param currentPlayer The player making the move
     */
    private void executeMove(GraphicsContext gc, double x, double y, String currentPlayer) {
        board.fillHex(gc, x, y, currentPlayer); // Place the stone on the board
    }

    /**
     * Handles logic after a move, including captures, win checks, and turn updates.
     * @param currentPlayer The player who made the move
     */
    private void handlePostMoveLogic(String currentPlayer) {
        // Use center coordinates as a proxy for the latest move (simplified assumption)
        Board.HexCube clickedHex = board.pixelToHex(gc.getCanvas().getWidth() / 2, gc.getCanvas().getHeight() / 2);
        // Check for and process any captures
        boolean captureOccurred = captureHandler.checkAndCapture(clickedHex.q, clickedHex.r,
                board.getHexStatus(), currentPlayer, gc);
        if (hasPlayerWon(currentPlayer)) { // Check if the game is won
            endGame(currentPlayer); // End the game
            return; // Exit the method
        }
        if (captureOccurred) { // If a capture happened
            if (hasPlayerWon(currentPlayer)) { // Check win condition again after capture
                endGame(currentPlayer); // End the game
                return; // Exit the method
            }
            player.grantExtraTurn(); // Grant an extra turn for the capture
        } else {
            player.switchTurn(); // Switch to the other player
        }
        board.updateTurnIndicator(); // Update the turn indicator in the UI
    }

    /**
     * Checks if the current player has won the game.
     * A player wins if the opponent had stones but has none left.
     * @param currentPlayer The player to check ("Red" or "Blue")
     * @return True if the player has won, false otherwise
     */
    private boolean hasPlayerWon(String currentPlayer) {
        String opponent = currentPlayer.equals("Red") ? "Blue" : "Red"; // Determine the opponent
        boolean opponentNowHasStones = false; // Track current opponent stones
        for (String[] row : board.getHexStatus()) { // Iterate over each row
            for (String cell : row) { // Iterate over each cell
                if (cell != null && cell.equals(opponent)) { // Check for opponent's stones
                    opponentNowHasStones = true; // Set flag if opponent has stones
                    opponentHadStones = true; // Update history
                }
            }
        }
        // Win if opponent had stones before but none now
        return opponentHadStones && !opponentNowHasStones;
    }

    /**
     * Ends the game and displays a win dialog with restart or exit options.
     * @param winner The winning player ("Red" or "Blue")
     */
    private void endGame(String winner) {
        renderer.showWinMessage(winner); // Display the win message
        Alert alert = createGameOverAlert(); // Create the game-over dialog
        Optional<ButtonType> result = alert.showAndWait(); // Show the dialog and get the result
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) { // Check for restart
            restartGame(); // Restart the game
        } else {
            Platform.exit(); // Exit the application
        }
    }

    /**
     * Creates a dialog for the game-over state with restart and exit options.
     * @return The configured Alert dialog
     */
    private Alert createGameOverAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Create a confirmation dialog
        alert.setTitle("Game Over"); // Set the dialog title
        alert.setHeaderText("Play Again?"); // Set the main message
        // Set custom buttons for restart and exit
        alert.getButtonTypes().setAll(new ButtonType("Restart", ButtonBar.ButtonData.YES),
                new ButtonType("Exit", ButtonBar.ButtonData.NO));
        return alert; // Return the configured dialog
    }

    /**
     * Restarts the game by resetting all components.
     */
    private void restartGame() {
        board.resetBoard(); // Clear the board
        player.resetPlayer(); // Reset the player state
        reset(); // Reset the game manager state
        board.render(gc); // Redraw the board
    }
}