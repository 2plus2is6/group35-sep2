import javafx.scene.canvas.GraphicsContext;

// This class manages the game logic, including player turns and move validation.
public class GameManager {
    private final Player player; // The current player (Red or Blue)
    private Board board; // The game board
    private final MoveValidator moveValidator; // Validates the player's moves
    private final CaptureHandler captureHandler; // Handles stone captures during the game

    // Constructor to set up the GameManager with the board and player
    public GameManager(Board board, Player player) {
        this.board = board;
        this.player = player;
        this.captureHandler = new CaptureHandler(board); // Initialize capture handler
        this.moveValidator = new MoveValidator(captureHandler); // Pass capture handler to move validator
    }

    // Sets a new board
    public void setBoard(Board board) {
        this.board = board;
    }

    // Handles making a move on the board
    public void makeMove(GraphicsContext gc, double x, double y) {
        // Convert the clicked coordinates (x, y) to the board's hex coordinates (q, r)
        Board.HexCube clickedHex = board.pixelToHex(x, y);
        double q = clickedHex.q;
        double r = clickedHex.r;

        // Get the current player
        String currentPlayer = player.getCurrentPlayer();

        // Check if the move is valid, if not, print "Invalid move!" and exit
        if (!moveValidator.isValidMove(q, r, board.getHexStatus(), currentPlayer)) {
            System.out.println("Invalid move!");
            return; // Don't switch the turn if the move is invalid
        }

        // If the move is valid, place the stone on the board
        board.fillHex(gc, x, y, currentPlayer);

        // Check if any stones are captured after the move
        boolean captureOccurred = captureHandler.checkAndCapture(q, r, board.getHexStatus(), currentPlayer, gc);

        // If capture happened, the player gets an extra turn
        if (captureOccurred) {
            player.grantExtraTurn(); // Player keeps their turn
            System.out.println(currentPlayer + " gets an extra turn!"); // Notify that the player gets an extra turn
        } else {
            // If no capture, switch the turn to the other player
            player.switchTurn();
        }

        // Update the turn indicator on the board (shows whose turn it is)
        board.updateTurnIndicator();
    }

    // A static method to print out a message if a player gets an extra turn due to a capture
    public static void allowExtraTurn(String playerName) {
        System.out.println(playerName + " gets an extra turn due to capturing!");
    }
}
