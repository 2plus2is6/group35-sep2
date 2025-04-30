import javafx.application.Platform; // Used for JavaFX thread operations
import javafx.embed.swing.JFXPanel; // Initializes JavaFX toolkit
import org.junit.jupiter.api.BeforeAll; // Runs setup once before all tests
import org.junit.jupiter.api.BeforeEach; // Runs setup before each test
import org.junit.jupiter.api.Test; // Marks test methods
import java.util.concurrent.CountDownLatch; // Synchronizes JavaFX thread
import static org.junit.jupiter.api.Assertions.*; // Provides assertion methods

// Tests MoveValidator functionality
public class MoveValidatorTest {
    private MoveValidator moveValidator; // MoveValidator under test
    private Board board; // Board instance
    private String[][] hexStatus; // Board state
    private Player player; // Player instance
    private CaptureHandler captureHandler; // CaptureHandler instance

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
                player = new Player(); // Initializes Player
                Renderer renderer = new Renderer(new javafx.scene.control.Label(), new javafx.stage.Stage()); // Initializes Renderer
                GameManager gameManager = new GameManager(null, player, renderer, new javafx.stage.Stage(), null); // Initializes GameManager
                board = new Board(renderer, player, gameManager); // Initializes Board
                gameManager.setBoard(board); // Sets Board in GameManager
                captureHandler = new CaptureHandler(board); // Initializes CaptureHandler
                moveValidator = new MoveValidator(captureHandler); // Initializes MoveValidator
                hexStatus = board.getHexStatus(); // Gets board state
                board.resetBoard(); // Resets board
            } finally {
                latch.countDown(); // Signals completion
            }
        });
        latch.await(); // Waits for setup completion
    }

    // Tests invalid move due to same-color adjacency
    @Test
    void testInvalidAdjacentSameColor() {
        hexStatus[6][6] = "Red"; // Places Red stone at center
        boolean isValid = moveValidator.isValidMove(1, 0, hexStatus, "Red"); // Tries adjacent Red stone
        assertFalse(isValid, "Adjacent same color without capture should be invalid"); // Checks invalid move
    }

    // Tests invalid move due to occupied hex
    @Test
    void testInvalidMoveOccupiedHex() {
        hexStatus[6][6] = "Red"; // Places Red stone at center
        boolean isValid = moveValidator.isValidMove(0, 0, hexStatus, "Blue"); // Tries same hex
        assertFalse(isValid, "Placing a stone in an occupied hex should be invalid"); // Checks invalid move
    }

    // Tests valid move in empty hex
    @Test
    void testValidMoveEmptyHex() {
        boolean isValid = moveValidator.isValidMove(0, 0, hexStatus, "Red"); // Tries empty hex
        assertTrue(isValid, "Placing a stone in an empty hex should be valid"); // Checks valid move
    }

    // Tests valid move with capture
    @Test
    void testValidMoveWithCapture() {
        hexStatus[6][6] = "Blue"; // Places Blue stone at center
        hexStatus[7][6] = "Red"; // Places Red stone East
        hexStatus[5][6] = "Red"; // Places Red stone West
        hexStatus[6][5] = "Red"; // Places Red stone North
        hexStatus[6][7] = "Red"; // Places Red stone South
        hexStatus[7][5] = "Red"; // Places Red stone Northeast
        boolean isValid = moveValidator.isValidMove(-1, 1, hexStatus, "Red"); // Tries Southeast move
        assertTrue(isValid, "Move resulting in a capture should be valid"); // Checks valid move
    }
}