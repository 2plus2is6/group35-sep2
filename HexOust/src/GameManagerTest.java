import javafx.application.Platform; // Used for JavaFX thread operations
import javafx.embed.swing.JFXPanel; // Initializes JavaFX toolkit
import javafx.scene.canvas.Canvas; // Used for drawing
import javafx.scene.canvas.GraphicsContext; // Used for drawing operations
import javafx.scene.control.Label; // Used for turn indicator
import javafx.stage.Stage; // Main application window
import org.junit.jupiter.api.BeforeAll; // Runs setup once before all tests
import org.junit.jupiter.api.BeforeEach; // Runs setup before each test
import org.junit.jupiter.api.Test; // Marks test methods
import java.util.concurrent.CountDownLatch; // Synchronizes JavaFX thread
import static org.junit.jupiter.api.Assertions.*; // Provides assertion methods

// Tests GameManager functionality
public class GameManagerTest {
    private GameManager gameManager; // GameManager under test
    private Player player; // Player instance
    private Board board; // Board instance
    private GraphicsContext gc; // GraphicsContext for drawing
    private Renderer renderer; // Renderer for UI updates
    private Stage stage; // Stage for UI

    // Initializes JavaFX toolkit
    @BeforeAll
    public static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1); // Latch to wait for JavaFX
        Platform.startup(() -> { // Starts JavaFX thread
            new JFXPanel(); // Initializes JavaFX toolkit
            latch.countDown(); // Signals completion
        });
        latch.await(); // Waits for JavaFX initialization
    }

    // Sets up test environment
    @BeforeEach
    void setUp() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1); // Latch to wait for setup
        Platform.runLater(() -> { // Runs on JavaFX thread
            try {
                Canvas canvas = new Canvas(800, 800); // Creates canvas
                gc = canvas.getGraphicsContext2D(); // Gets GraphicsContext
                Label turnLabel = new Label(); // Creates turn label
                stage = new Stage(); // Creates Stage
                renderer = new Renderer(turnLabel, stage); // Initializes Renderer
                player = new Player(); // Initializes Player
                gameManager = new GameManager(null, player, renderer, stage, gc); // Initializes GameManager
                board = new Board(renderer, player, gameManager); // Initializes Board
                gameManager.setBoard(board); // Sets Board in GameManager
                board.resetBoard(); // Resets board state
                renderer.updateTurn("Red"); // Sets initial turn to Red
            } finally {
                latch.countDown(); // Signals completion
            }
        });
        latch.await(); // Waits for setup completion
    }

    // Tests valid move placement
    @Test
    void testValidMoveUpdatesBoard() {
        double centerX = 410; // X-coordinate of board center
        double centerY = 345; // Y-coordinate of board center
        gameManager.makeMove(gc, centerX, centerY); // Simulates move
        Board.HexCube clickedHex = board.pixelToHex(centerX, centerY); // Converts to hex coordinates
        int q = (int) clickedHex.q + 6; // Adjusts q to board index
        int r = (int) clickedHex.r + 6; // Adjusts r to board index
        assertNotNull(board.getHexStatus()[q][r], "Stone should be placed"); // Checks stone placement
        assertEquals("Red", board.getHexStatus()[q][r], "Stone should be Red"); // Checks stone color
    }

    // Tests capture and extra turn
    @Test
    void testExtraTurnOnCapture() {
        board.getHexStatus()[6][6] = "Blue"; // Places Blue stone at center
        board.getHexStatus()[7][6] = "Red"; // Places Red stone East
        board.getHexStatus()[5][6] = "Red"; // Places Red stone West
        board.getHexStatus()[6][5] = "Red"; // Places Red stone North
        board.getHexStatus()[6][7] = "Red"; // Places Red stone South
        board.getHexStatus()[7][5] = "Red"; // Places Red stone Northeast
        double clickX = 410 - 30 * 1.5; // X-coordinate for Southeast
        double clickY = 345 + 30 * Math.sqrt(3) / 2; // Y-coordinate for Southeast
        gameManager.makeMove(gc, clickX, clickY); // Simulates move
        assertNull(board.getHexStatus()[6][6], "Blue stone should be captured"); // Checks capture
        assertEquals("Red", player.getCurrentPlayer(), "Red should get extra turn"); // Checks extra turn
    }

    // Tests restart functionality
    @Test
    void testRestartButtonFunctionality() {
        board.getHexStatus()[6][6] = "Red"; // Places Red stone
        board.getHexStatus()[7][6] = "Blue"; // Places Blue stone
        player.switchTurn(); // Sets turn to Blue
        gameManager.reset(); // Resets game
        board.resetBoard(); // Resets board
        player.resetPlayer(); // Resets player
        for (String[] row : board.getHexStatus()) { // Loops through board
            for (String cell : row) { // Loops through cells
                assertNull(cell, "Board should be cleared"); // Checks board cleared
            }
        }
        assertEquals("Red", player.getCurrentPlayer(), "Player should be Red"); // Checks player reset
        double centerX = 410; // X-coordinate of center
        double centerY = 345; // Y-coordinate of center
        gameManager.makeMove(gc, centerX, centerY); // Simulates new move
        Board.HexCube clickedHex = board.pixelToHex(centerX, centerY); // Converts to hex
        int q = (int) clickedHex.q + 6; // Adjusts q
        int r = (int) clickedHex.r + 6; // Adjusts r
        assertNotNull(board.getHexStatus()[q][r], "New move should be allowed"); // Checks new move
    }

    // Tests Red win condition
    @Test
    void testRedWinningCondition() throws InterruptedException {
        board.getHexStatus()[6][6] = "Red"; // Places Red stone
        board.getHexStatus()[7][6] = "Blue"; // Places Blue stone
        board.getHexStatus()[7][6] = null; // Removes Blue stone
        CountDownLatch latch = new CountDownLatch(1); // Latch for JavaFX
        Platform.runLater(() -> { // Runs on JavaFX thread
            try {
                gameManager.makeMove(gc, 410, 345); // Simulates move
                assertTrue(renderer.getWinMessageLabel().isVisible(), "Win message should be displayed"); // Checks win message
                assertEquals("Red Wins!", renderer.getWinMessageLabel().getText(), "Red should win"); // Checks win text
            } finally {
                latch.countDown(); // Signals completion
            }
        });
        latch.await(); // Waits for completion
    }

    // Tests Blue win condition
    @Test
    void testBlueWinningCondition() throws InterruptedException {
        board.getHexStatus()[6][6] = "Blue"; // Places Blue stone
        board.getHexStatus()[7][6] = "Red"; // Places Red stone
        board.getHexStatus()[7][6] = null; // Removes Red stone
        player.switchTurn(); // Sets turn to Blue
        CountDownLatch latch = new CountDownLatch(1); // Latch for JavaFX
        Platform.runLater(() -> { // Runs on JavaFX thread
            try {
                gameManager.makeMove(gc, 410, 345); // Simulates move
                assertTrue(renderer.getWinMessageLabel().isVisible(), "Win message should be displayed"); // Checks win message
                assertEquals("Blue Wins!", renderer.getWinMessageLabel().getText(), "Blue should win"); // Checks win text
            } finally {
                latch.countDown(); // Signals completion
            }
        });
        latch.await(); // Waits for completion
    }

    // Tests invalid move rejection
    @Test
    void testMoveValidationInvalidMove() {
        board.getHexStatus()[6][6] = "Red"; // Places Red stone
        double centerX = 410; // X-coordinate of center
        double centerY = 345; // Y-coordinate of center
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream(); // Captures output
        System.setOut(new java.io.PrintStream(outContent)); // Redirects output
        gameManager.makeMove(gc, centerX, centerY); // Simulates invalid move
        assertEquals("Red", board.getHexStatus()[6][6], "Hex should remain Red"); // Checks no change
        assertTrue(outContent.toString().contains("Invalid move!"), "Invalid move should be logged"); // Checks error
    }
}
