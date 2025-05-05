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

/**
 * Tests the functionality of the GameManager class in the HexOust game.
 * Includes unit tests for move handling, capture logic, restart functionality, win conditions, and invalid moves.
 */
public class GameManagerTest {
    private GameManager gameManager; // GameManager instance under test
    private Player player; // Player instance for game context
    private Board board; // Board instance for game state
    private GraphicsContext gc; // GraphicsContext for rendering
    private Renderer renderer; // Renderer for UI updates
    private Stage stage; // Stage for UI

    /**
     * Initializes the JavaFX toolkit before all tests.
     */
    @BeforeAll
    public static void initJavaFX() {
        JavaFXTestInitializer.initialize(); // Start the JavaFX runtime
    }

    /**
     * Sets up the test environment before each test.
     * @throws InterruptedException if the latch await is interrupted
     */
    @BeforeEach
    void setUp() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1); // Create latch for synchronization
        Platform.runLater(() -> { // Execute setup on JavaFX thread
            try {
                Canvas canvas = new Canvas(800, 800); // Create a test canvas
                gc = canvas.getGraphicsContext2D(); // Get the graphics context
                Label turnLabel = new Label(); // Create a turn indicator label
                stage = new Stage(); // Create a test stage
                renderer = new Renderer(turnLabel, stage); // Initialize renderer
                player = new Player(); // Initialize player
                gameManager = new GameManager(null, player, renderer, stage, gc); // Initialize game manager
                board = new Board(renderer, player, gameManager); // Initialize board
                gameManager.setBoard(board); // Link board to game manager
                board.resetBoard(); // Reset the board
                renderer.updateTurn("Red"); // Set initial turn to Red
            } finally {
                latch.countDown(); // Signal setup completion
            }
        });
        latch.await(); // Wait for setup to complete
    }

    /**
     * Tests that a valid move updates the board state correctly.
     */
    @Test
    void testValidMoveUpdatesBoard() {
        double centerX = 410; // X-coordinate of the board center
        double centerY = 345; // Y-coordinate of the board center
        gameManager.makeMove(gc, centerX, centerY); // Simulate a valid move
        Board.HexCube clickedHex = board.pixelToHex(centerX, centerY); // Convert to hex coordinates
        int q = (int) clickedHex.q + 6; // Adjust q to board index
        int r = (int) clickedHex.r + 6; // Adjust r to board index
        assertNotNull(board.getHexStatus()[q][r], "Stone should be placed"); // Verify stone placement
        assertEquals("Red", board.getHexStatus()[q][r], "Stone should be Red"); // Verify stone color
    }

    /**
     * Tests that a capture triggers an extra turn for the player.
     */
    @Test
    void testExtraTurnOnCapture() {
        board.getHexStatus()[6][6] = "Blue"; // Place Blue stone at center
        board.getHexStatus()[7][6] = "Red"; // Place Red stone East
        board.getHexStatus()[5][6] = "Red"; // Place Red stone West
        board.getHexStatus()[6][5] = "Red"; // Place Red stone North
        board.getHexStatus()[6][7] = "Red"; // Place Red stone South
        board.getHexStatus()[7][5] = "Red"; // Place Red stone Northeast
        double clickX = 410 - 30 * 1.5; // X-coordinate for Southwest
        double clickY = 345 + 30 * Math.sqrt(3) / 2; // Y-coordinate for Southwest
        gameManager.makeMove(gc, clickX, clickY); // Simulate move to capture
        assertNull(board.getHexStatus()[6][6], "Blue stone should be captured"); // Verify capture
        assertEquals("Red", player.getCurrentPlayer(), "Red should get extra turn"); // Verify extra turn
    }

    /**
     * Tests that the restart functionality resets the game state.
     */
    @Test
    void testRestartButtonFunctionality() {
        board.getHexStatus()[6][6] = "Red"; // Place Red stone
        board.getHexStatus()[7][6] = "Blue"; // Place Blue stone
        player.switchTurn(); // Switch turn to Blue
        gameManager.reset(); // Reset game state
        board.resetBoard(); // Reset board
        player.resetPlayer(); // Reset player
        // Verify board is cleared
        for (String[] row : board.getHexStatus()) {
            for (String cell : row) {
                assertNull(cell, "Board should be cleared");
            }
        }
        assertEquals("Red", player.getCurrentPlayer(), "Player should be Red"); // Verify player reset
        double centerX = 410; // X-coordinate of center
        double centerY = 345; // Y-coordinate of center
        gameManager.makeMove(gc, centerX, centerY); // Simulate new move
        Board.HexCube clickedHex = board.pixelToHex(centerX, centerY); // Convert to hex
        int q = (int) clickedHex.q + 6; // Adjust q
        int r = (int) clickedHex.r + 6; // Adjust r
        assertNotNull(board.getHexStatus()[q][r], "New move should be allowed"); // Verify new move
    }

    /**
     * Tests the win condition for the Red player.
     * @throws InterruptedException if the latch await is interrupted
     */
    @Test
    void testRedWinningCondition() throws InterruptedException {
        // Place initial Blue stones
        board.getHexStatus()[6][6] = "Blue"; // Center (q=0, r=0)
        board.getHexStatus()[7][6] = "Blue"; // East (q=1, r=0)
        // Simulate initial move to set opponentHadStones
        double initialClickX = 410 - 30 * 1.5; // West (q=-1, r=0)
        double initialClickY = 345;
        gameManager.makeMove(gc, initialClickX, initialClickY); // Valid move
        // Surround Blue stones with Red
        board.getHexStatus()[6][5] = "Red"; // North (q=0, r=-1)
        board.getHexStatus()[6][7] = "Red"; // South (q=0, r=1)
        board.getHexStatus()[7][5] = "Red"; // Northeast (q=1, r=-1)
        board.getHexStatus()[5][7] = "Red"; // Southwest (q=-1, r=1)
        board.getHexStatus()[5][6] = "Red"; // West (q=-1, r=0)
        // Simulate final move to capture
        double finalClickX = 410 + 30 * 1.5; // Southeast X
        double finalClickY = 345 + 30 * Math.sqrt(3) / 2; // Southeast Y
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                gameManager.makeMove(gc, finalClickX, finalClickY); // Capture Blue stones
                assertTrue(renderer.getWinMessageLabel().isVisible(), "Win message should be displayed");
                assertEquals("Red Wins!", renderer.getWinMessageLabel().getText(), "Red should win");
            } finally {
                latch.countDown();
            }
        });
        latch.await(); // Wait for JavaFX thread
    }

    /**
     * Tests the win condition for the Blue player.
     * @throws InterruptedException if the latch await is interrupted
     */
    @Test
    void testBlueWinningCondition() throws InterruptedException {
        // Place initial Red stones
        board.getHexStatus()[6][6] = "Red"; // Center (q=0, r=0)
        board.getHexStatus()[7][6] = "Red"; // East (q=1, r=0)
        player.switchTurn(); // Switch to Blue
        // Simulate initial move to set opponentHadStones
        double initialClickX = 410 - 30 * 1.5; // West (q=-1, r=0)
        double initialClickY = 345;
        gameManager.makeMove(gc, initialClickX, initialClickY); // Valid move
        // Surround Red stones with Blue
        board.getHexStatus()[6][5] = "Blue"; // North (q=0, r=-1)
        board.getHexStatus()[6][7] = "Blue"; // South (q=0, r=1)
        board.getHexStatus()[7][5] = "Blue"; // Northeast (q=1, r=-1)
        board.getHexStatus()[5][7] = "Blue"; // Southwest (q=-1, r=1)
        board.getHexStatus()[5][6] = "Blue"; // West (q=-1, r=0)
        // Simulate final move to capture
        double finalClickX = 410 + 30 * 1.5; // Southeast X
        double finalClickY = 345 + 30 * Math.sqrt(3) / 2; // Southeast Y
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                gameManager.makeMove(gc, finalClickX, finalClickY); // Capture Red stones
                assertTrue(renderer.getWinMessageLabel().isVisible(), "Win message should be displayed");
                assertEquals("Blue Wins!", renderer.getWinMessageLabel().getText(), "Blue should win");
            } finally {
                latch.countDown();
            }
        });
        latch.await(); // Wait for JavaFX thread
    }

    /**
     * Tests that an invalid move is rejected and logged.
     */
    @Test
    void testMoveValidationInvalidMove() {
        board.getHexStatus()[6][6] = "Red"; // Place a Red stone
        double centerX = 410; // X-coordinate of center
        double centerY = 345; // Y-coordinate of center
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream(); // Capture console output
        System.setOut(new java.io.PrintStream(outContent)); // Redirect output
        gameManager.makeMove(gc, centerX, centerY); // Simulate invalid move
        assertEquals("Red", board.getHexStatus()[6][6], "Hex should remain Red"); // Verify no change
        assertTrue(outContent.toString().contains("Invalid move!"), "Invalid move should be logged"); // Verify log
    }
}