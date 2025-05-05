import javafx.application.Platform;          // Schedules tasks on the JavaFX Application Thread
import javafx.scene.canvas.Canvas;                // Provides a drawing surface for board rendering tests
import javafx.scene.canvas.GraphicsContext;  // Offers drawing operations on the Canvas
import javafx.scene.control.Label;       // Used for turn and win indicator controls in tests
import javafx.stage.Stage;                 // Represents the primary window for renderer initialization
import org.junit.jupiter.api.BeforeAll;     // Marks a method to run once before all tests (JavaFX setup)
import org.junit.jupiter.api.BeforeEach;    // Marks a method to run before each test (per-test setup)
import org.junit.jupiter.api.Test;   // Marks a method as an individual test case
import java.util.concurrent.CountDownLatch; // Used to synchronize JavaFX thread startup in setup
import static org.junit.jupiter.api.Assertions.*;  // Provides assertion methods for validating test results


// Tests Board functionality with unit and integration tests
/**
 * Tests the functionality of the Board class in the HexOust game.
 * Includes unit tests for individual methods and an integration test for UI updates.
 */
public class BoardTest {
    private Board board;                       // The board instance to test
    private Player player;                     // Player instance for game context
    private Renderer renderer;                 // Renderer instance for UI updates
    private GameManager gameManager;           // Game manager instance for coordination
    private GraphicsContext gc;                // Graphics context for rendering

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
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Canvas canvas = new Canvas(800, 800); // Create a test canvas
                gc = canvas.getGraphicsContext2D(); // Get the graphics context
                renderer = new Renderer(new Label(), new Stage()); // Initialize renderer
                player = new Player(); // Initialize player
                gameManager = new GameManager(null, player, renderer, new Stage(), gc); // Initialize game manager
                board = new Board(renderer, player, gameManager); // Initialize board
                gameManager.setBoard(board); // Link board to game manager
                board.resetBoard(); // Reset the board state
            } finally {
                latch.countDown(); // Signal setup completion
            }
        });
        latch.await(); // Wait for JavaFX thread to complete setup
    }

    /**
     * Unit test to verify that resetBoard clears all stones from the board.
     */
    @Test
    void testResetBoardClearsAllStones() {
        // Place a test stone at the center
        board.getHexStatus()[6][6] = "Red";
        // Reset the board to clear stones
        board.resetBoard();
        // Verify all cells are null
        for (String[] row : board.getHexStatus()) {
            for (String cell : row) {
                assertNull(cell, "Board should be empty after reset");
            }
        }
    }

    /**
     * Unit test to verify that a stone can be placed in an empty hex.
     */
    @Test
    void testPlaceStoneInEmptyHex() {
        double centerX = 410; // X-coordinate of the board center
        double centerY = 345; // Y-coordinate of the board center
        boolean placed = board.fillHex(gc, centerX, centerY, "Red"); // Attempt to place a stone
        assertTrue(placed, "Stone should be placed successfully");
        assertEquals("Red", board.getHexStatus()[6][6], "Center hex should be Red");
    }

    /**
     * Unit test to verify that placing a stone in an occupied hex fails.
     */
    @Test
    void testPlaceStoneInOccupiedHexFails() {
        double centerX = 410; // X-coordinate of the board center
        double centerY = 345; // Y-coordinate of the board center
        board.fillHex(gc, centerX, centerY, "Red"); // Place initial stone
        boolean placed = board.fillHex(gc, centerX, centerY, "Blue"); // Try to place another stone
        assertFalse(placed, "Stone placement in occupied hex should fail");
        assertEquals("Red", board.getHexStatus()[6][6], "Hex should remain Red");
    }

    /**
     * Unit test to verify pixelToHex conversion for a center click.
     */
    @Test
    void testPixelToHexAtCenter() {
        double centerX = 410; // X-coordinate of the board center
        double centerY = 345; // Y-coordinate of the board center
        Board.HexCube hex = board.pixelToHex(centerX, centerY); // Convert pixel to hex
        assertEquals(0, (int) hex.q, "q-coordinate at center should be 0");
        assertEquals(0, (int) hex.r, "r-coordinate at center should be 0");
    }

    /**
     * Unit test to verify isWithinBounds for valid and invalid hex coordinates.
     */
    @Test
    void testIsWithinBounds() {
        Board.HexCube validHex = new Board.HexCube(0, 0, 0); // Valid center hex
        Board.HexCube invalidHex = new Board.HexCube(7, 0, -7); // Invalid hex outside base-6
        assertTrue(board.isWithinBounds(validHex), "Center hex should be within bounds");
        assertFalse(board.isWithinBounds(invalidHex), "Hex outside base-6 should be out of bounds");
    }

    /**
     * Unit test to verify that placing a stone triggers a capture when surrounded.
     */
    @Test
    void testPlaceStoneTriggersCapture() {
        // Set up a scenario where Blue is surrounded by Red
        board.getHexStatus()[6][6] = "Blue"; // Center (q=0, r=0)
        board.getHexStatus()[7][6] = "Red"; // East (q=1, r=0)
        board.getHexStatus()[5][6] = "Red"; // West (q=-1, r=0)
        board.getHexStatus()[6][5] = "Red"; // North (q=0, r=-1)
        board.getHexStatus()[6][7] = "Red"; // South (q=0, r=1)
        board.getHexStatus()[7][5] = "Red"; // Northeast (q=1, r=-1)
        // Calculate click coordinates for Southwest (q=-1, r=1)
        double clickX = 410 - 30 * 1.5; // Approximate X for Southwest
        double clickY = 345 + 30 * Math.sqrt(3) / 2; // Approximate Y for Southwest
        System.out.println("Attempting capture at (" + clickX + ", " + clickY + ")");
        boolean captureOccurred = board.fillHex(gc, clickX, clickY, "Red"); // Place final stone
        System.out.println("Capture result: " + captureOccurred);
        assertTrue(captureOccurred, "Capture should occur");
        assertNull(board.getHexStatus()[6][6], "Blue stone should be captured");
    }

    /**
     * Integration test to verify that resetBoard updates the UI correctly.
     */
    @Test
    void testResetBoardUpdatesUI() {
        // Place test stones on the board
        board.getHexStatus()[6][6] = "Red";
        board.getHexStatus()[7][6] = "Blue";
        // Reset the board and update the UI
        board.resetBoard();
        board.updateBoardUI(gc);
        // Verify the board is empty after UI update
        for (String[] row : board.getHexStatus()) {
            for (String cell : row) {
                assertNull(cell, "Board should be empty after reset and UI update");
            }
        }
    }
}