import javafx.application.Platform; // Used for JavaFX thread operations
import javafx.embed.swing.JFXPanel; // Initializes JavaFX toolkit
import org.junit.jupiter.api.BeforeAll; // Runs setup once before all tests
import org.junit.jupiter.api.BeforeEach; // Runs setup before each test
import org.junit.jupiter.api.Test; // Marks test methods
import java.util.concurrent.CountDownLatch; // Synchronizes JavaFX thread
import static org.junit.jupiter.api.Assertions.*; // Provides assertion methods

/**
 * Tests the functionality of the MoveValidator class in the HexOust game.
 * Includes unit tests for validating moves based on adjacency, occupancy, and capture conditions.
 */
public class MoveValidatorTest {
    private MoveValidator moveValidator; // MoveValidator instance under test
    private Board board; // Board instance for game state
    private String[][] hexStatus; // Board state array
    private Player player; // Player instance for game context
    private CaptureHandler captureHandler; // CaptureHandler instance for capture logic

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
                player = new Player(); // Initialize player
                Renderer renderer = new Renderer(new javafx.scene.control.Label(), new javafx.stage.Stage()); // Initialize renderer
                GameManager gameManager = new GameManager(null, player, renderer, new javafx.stage.Stage(), null); // Initialize game manager
                board = new Board(renderer, player, gameManager); // Initialize board
                gameManager.setBoard(board); // Link board to game manager
                captureHandler = new CaptureHandler(board); // Initialize capture handler
                moveValidator = new MoveValidator(captureHandler); // Initialize move validator
                hexStatus = board.getHexStatus(); // Get the board state
                board.resetBoard(); // Reset the board
            } finally {
                latch.countDown(); // Signal setup completion
            }
        });
        latch.await(); // Wait for setup to complete
    }

    /**
     * Tests that a move is invalid when adjacent to a same-color stone without a capture.
     */
    @Test
    void testInvalidAdjacentSameColor() {
        hexStatus[6][6] = "Red"; // Place a Red stone at the center
        boolean isValid = moveValidator.isValidMove(1, 0, hexStatus, "Red"); // Try placing adjacent Red
        assertFalse(isValid, "Adjacent same color without capture should be invalid"); // Verify invalid move
    }

    /**
     * Tests that a move is invalid when placing a stone in an occupied hex.
     */
    @Test
    void testInvalidMoveOccupiedHex() {
        hexStatus[6][6] = "Red"; // Place a Red stone at the center
        boolean isValid = moveValidator.isValidMove(0, 0, hexStatus, "Blue"); // Try placing in same hex
        assertFalse(isValid, "Placing a stone in an occupied hex should be invalid"); // Verify invalid move
    }

    /**
     * Tests that a move is valid when placing a stone in an empty hex.
     */
    @Test
    void testValidMoveEmptyHex() {
        boolean isValid = moveValidator.isValidMove(0, 0, hexStatus, "Red"); // Try placing in empty hex
        assertTrue(isValid, "Placing a stone in an empty hex should be valid"); // Verify valid move
    }

    /**
     * Tests that a move is valid when it results in a capture.
     */
    @Test
    void testValidMoveWithCapture() {
        hexStatus[6][6] = "Blue"; // Place a Blue stone at the center
        hexStatus[7][6] = "Red"; // Place a Red stone East
        hexStatus[5][6] = "Red"; // Place a Red stone West
        hexStatus[6][5] = "Red"; // Place a Red stone North
        hexStatus[6][7] = "Red"; // Place a Red stone South
        hexStatus[7][5] = "Red"; // Place a Red stone Northeast
        boolean isValid = moveValidator.isValidMove(-1, 1, hexStatus, "Red"); // Try placing Southeast
        assertTrue(isValid, "Move resulting in a capture should be valid"); // Verify valid move
    }
}