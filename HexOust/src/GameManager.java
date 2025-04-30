import javafx.application.Platform; // Used for JavaFX thread operations
import javafx.scene.canvas.GraphicsContext; // Used for drawing on canvas
import javafx.scene.control.Alert; // Used for game-over dialog
import javafx.scene.control.ButtonBar; // Used for dialog button types
import javafx.scene.control.ButtonType; // Used for dialog buttons
import javafx.stage.Stage;

import java.util.Optional; // Used for handling dialog results

/**
 * Manages core game logic, including moves, win conditions, and game state.
 */
public class GameManager {
    private final Player player; // Tracks current player
    private Board board; // Manages game board
    private final MoveValidator moveValidator; // Validates player moves
    private final CaptureHandler captureHandler; // Handles piece captures
    private final Renderer renderer; // Updates UI elements
    private final Stage stage; // Main application window
    private final GraphicsContext gc; // Canvas drawing context
    private boolean opponentHadStones; // Tracks opponent's stone history

    /**
     * Constructs a GameManager with dependencies.
     * @param board The game board (may be null initially)
     * @param player The player manager
     * @param renderer The UI renderer
     * @param stage The main window
     * @param gc The canvas drawing context
     */
    public GameManager(Board board, Player player, Renderer renderer, Stage stage, GraphicsContext gc) {
        this.board = board; // Assigns board
        this.player = player; // Assigns player
        this.renderer = renderer; // Assigns renderer
        this.stage = stage; // Assigns stage
        this.gc = gc; // Assigns graphics context
        this.captureHandler = new CaptureHandler(board); // Initializes capture handler
        this.moveValidator = new MoveValidator(captureHandler); // Initializes move validator
        this.opponentHadStones = false; // Initializes opponent stone flag
    }

    /**
     * Sets the game board and updates dependencies.
     * @param board The game board
     */
    public void setBoard(Board board) {
        this.board = board; // Updates board reference
        this.captureHandler.board = board; // Updates capture handler's board
    }

    /**
     * Gets the renderer.
     * @return The renderer
     */
    public Renderer getRenderer() {
        return renderer; // Returns renderer
    }

    /**
     * Resets the game state for a new game.
     */
    public void reset() {
        opponentHadStones = false; // Resets opponent stone flag
        renderer.hideWinMessage(); // Hides win message
        renderer.showTurnIndicator(); // Shows turn indicator
        renderer.updateTurn("Red"); // Sets turn to Red
    }

    /**
     * Processes a player's move at the given coordinates.
     * @param gc The graphics context for drawing
     * @param x The x-coordinate of the click
     * @param y The y-coordinate of the click
     */
    public void makeMove(GraphicsContext gc, double x, double y) {
        Board.HexCube clickedHex = board.pixelToHex(x, y); // Converts click to hex
        String currentPlayer = player.getCurrentPlayer(); // Gets current player
        if (!isValidMove(clickedHex.q, clickedHex.r, currentPlayer)) { // Validates move
            System.out.println("Invalid move!"); // Logs invalid move
            return; // Exits if invalid
        }
        executeMove(gc, x, y, currentPlayer); // Executes move
        handlePostMoveLogic(currentPlayer); // Handles captures and win conditions
    }

    // Validates a move
    private boolean isValidMove(double q, double r, String currentPlayer) {
        return moveValidator.isValidMove(q, r, board.getHexStatus(), currentPlayer); // Checks move validity
    }

    // Executes a move by placing a stone
    private void executeMove(GraphicsContext gc, double x, double y, String currentPlayer) {
        board.fillHex(gc, x, y, currentPlayer); // Places stone on board
    }

    // Handles captures, win conditions, and turn updates
    private void handlePostMoveLogic(String currentPlayer) {
        Board.HexCube clickedHex = board.pixelToHex(gc.getCanvas().getWidth() / 2, gc.getCanvas().getHeight() / 2); // Gets recent move hex
        boolean captureOccurred = captureHandler.checkAndCapture(
                clickedHex.q, clickedHex.r, board.getHexStatus(), currentPlayer, gc); // Checks for captures
        if (hasPlayerWon(currentPlayer)) { // Checks win condition
            endGame(currentPlayer); // Ends game if won
            return; // Exits
        }
        if (captureOccurred) { // If capture occurred
            if (hasPlayerWon(currentPlayer)) { // Checks win condition again
                endGame(currentPlayer); // Ends game if won
                return; // Exits
            }
            player.grantExtraTurn(); // Grants extra turn
        } else {
            player.switchTurn(); // Switches turn
        }
        board.updateTurnIndicator(); // Updates turn display
    }

    // Checks if the current player has won
    private boolean hasPlayerWon(String currentPlayer) {
        String opponent = currentPlayer.equals("Red") ? "Blue" : "Red"; // Determines opponent
        boolean opponentNowHasStones = false; // Tracks current opponent stones
        for (String[] row : board.getHexStatus()) { // Loops through board
            for (String cell : row) { // Loops through cells
                if (cell != null && cell.equals(opponent)) { // Checks opponent stones
                    opponentNowHasStones = true; // Sets flag
                    opponentHadStones = true; // Updates history
                }
            }
        }
        return opponentHadStones && !opponentNowHasStones; // Returns true if opponent had stones but none now
    }

    // Ends the game and shows dialog
    private void endGame(String winner) {
        renderer.showWinMessage(winner); // Shows win message
        Alert alert = createGameOverAlert(); // Creates game-over dialog
        Optional<ButtonType> result = alert.showAndWait(); // Shows dialog
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) { // Checks restart choice
            restartGame(); // Restarts game
        } else {
            Platform.exit(); // Exits application
        }
    }

    // Creates game-over dialog
    private Alert createGameOverAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Creates dialog
        alert.setTitle("Game Over"); // Sets title
        alert.setHeaderText("Play Again?"); // Sets message
        alert.getButtonTypes().setAll( // Sets buttons
                new ButtonType("Restart", ButtonBar.ButtonData.YES), // Restart option
                new ButtonType("Exit", ButtonBar.ButtonData.NO) // Exit option
        );
        return alert; // Returns dialog
    }

    // Restarts the game
    private void restartGame() {
        board.resetBoard(); // Resets board
        player.resetPlayer(); // Resets player
        reset(); // Resets game state
        board.render(gc); // Renders board
    }
}