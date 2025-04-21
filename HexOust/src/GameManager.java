import javafx.scene.canvas.GraphicsContext;

public class GameManager {
    private final Player player;
    private final Renderer renderer;
    private Board board;
    private CaptureHandler captureHandler;

    public GameManager(Board board, Player player, Renderer renderer) {
        this.board = board;
        this.player = player;
        this.renderer = renderer;
        this.captureHandler = new CaptureHandler(board); // Initialize capture handler
    }

    // Method to make a move
    // Method to make a move
    public boolean makeMove(GraphicsContext gc, double x, double y) {
        // Process the move and place the stone
        board.fillHex(gc, x, y, player.getCurrentPlayer());

        // Check if a capture occurred (for capture mechanics)
        boolean captureOccurred = CaptureHandler.checkAndCapture(x, y, board.getHexStatus(), player.getCurrentPlayer(), gc);

        // Check for the win condition after placing the stone
        checkWinCondition(); // Check if someone has won, but don't stop the game

        if (!captureOccurred) {
            // If no capture occurred, switch turns
            player.switchTurn();
            renderer.updateTurn(player.getCurrentPlayer());
        }
        return captureOccurred;
    }


    // Check for win condition: if one color is completely eliminated
    boolean checkWinCondition() {
        String[][] hexStatus = board.getHexStatus();
        int redCount = 0;
        int blueCount = 0;

        // Count the number of Red and Blue stones on the board
        for (int i = 0; i < hexStatus.length; i++) {
            for (int j = 0; j < hexStatus[i].length; j++) {
                if ("Red".equals(hexStatus[i][j])) redCount++;
                if ("Blue".equals(hexStatus[i][j])) blueCount++;
            }
        }

        // Declare the winner if one color has no stones left
        if (redCount > 0 && blueCount == 0) {
            renderer.displayWinMessage("Red");
            return true;
        } else if (blueCount > 0 && redCount == 0) {
            renderer.displayWinMessage("Blue");
            return true;
        }

        return false; // No winner yet
    }
}
