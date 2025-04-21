import javafx.embed.swing.JFXPanel; // We need this to initialize JavaFX in headless mode, i.e., without the UI
import javafx.scene.canvas.GraphicsContext; // This is used to draw on the canvas (our board)
import javafx.scene.canvas.Canvas; // The canvas is where we draw the hexagonal grid
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll; // This is used to run setup before any tests run
import org.junit.jupiter.api.BeforeEach; // Runs setup before each individual test
import org.junit.jupiter.api.Test; // Marks a method as a test case
import static org.junit.jupiter.api.Assertions.*; // These are assertion methods we use to check if the tests pass

public class Sprint4Tests {
    private GameManager gameManager; // This is the class that manages the game flow
    private Player player; // This is the current player (Red or Blue)
    private Board board; // This represents the game board, where the game is played
    private Renderer renderer;

    // This method runs once before all tests, it initializes the JavaFX toolkit
    @BeforeAll
    public static void initJavaFX() {
        new JFXPanel(); // Starts the JavaFX toolkit without needing a UI
    }

    // This method runs before each test to set up a fresh instance of the game for each test
    @BeforeEach
    void setUp() {
        // Initialize renderer and other components for the game
        Label turnLabel = new Label();
        renderer = new Renderer(turnLabel); // Renderer will update the turn display on the UI

        player = new Player();
        board = new Board(renderer, player, null); // The game board is initialized with the player and renderer

        gameManager = new GameManager(board, player, renderer); // Create GameManager with the board, player, and renderer
    }

    // Test 1: Restart Button Functionality
    @Test
    void testRestartButtonFunctionality() {
        // Start the game and simulate a move
        GraphicsContext gc = new Canvas().getGraphicsContext2D();
        double centerX = 410;  // Horizontal center position
        double centerY = 345;  // Vertical center position

        gameManager.makeMove(gc, centerX, centerY); // Simulate a move

        // Call the restart method (simulating clicking on the Restart button)
        // For now, this is a mock of how the restart button would work
        gameManager.setBoard(new Board(renderer, player, null));  // Restart the game by resetting the board

        // Verify that the board is reset and the current player is set to "Red"
        assertNull(board.getHexStatus()[6][6], "Board was not reset correctly");
        assertEquals("Red", player.getCurrentPlayer(), "Current player should be Red after restart");
    }

    // Test 2: Winning Condition after Player Makes a Move
    @Test
    void testWinningCondition() {
        // Set up the board so the opponent has no stones left
        board.getHexStatus()[6][6] = "Red";  // Red stone in the center
        board.getHexStatus()[5][6] = "Red";  // Red stone around
        board.getHexStatus()[7][6] = "Red";  // Red stone around
        // Ensure Blue has no stones left
        board.getHexStatus()[5][7] = null;
        board.getHexStatus()[7][5] = null;

        // Simulate a move for Red
        GraphicsContext gc = new Canvas().getGraphicsContext2D();
        double centerX = 410;
        double centerY = 345;
        gameManager.makeMove(gc, centerX, centerY);

        // Verify that the win condition is triggered and no further moves are allowed
        assertTrue(gameManager.checkWinCondition(), "The winning condition was not detected after the move");
    }

    // Test 3: Capture Mechanism
    @Test
    void testCaptureMechanism() {
        // Set up the board such that the current player (Red) can capture the opponent's (Blue) stones
        board.getHexStatus()[6][6] = "Blue";  // Blue stone in the center
        board.getHexStatus()[5][6] = "Red";   // Surround with Red stones
        board.getHexStatus()[7][6] = "Red";   // Surround with Red stones
        board.getHexStatus()[6][5] = "Red";   // Surround with Red stones
        board.getHexStatus()[6][7] = "Red";   // Surround with Red stones

        // Simulate a move for Red that captures Blue
        GraphicsContext gc = new Canvas().getGraphicsContext2D();
        double clickX = 380.8; // Adjust X for the click
        double clickY = 363.2; // Adjust Y for the click

        gameManager.makeMove(gc, clickX, clickY);  // Perform the move

        // Verify that Blue's stone was captured
        assertNull(board.getHexStatus()[6][6], "Blue stone was not captured");

        // Verify that Red gets an extra turn
        assertEquals("Red", player.getCurrentPlayer(), "Red should have an extra turn after capture");
    }

    // Test 4: Move Validation
    @Test
    void testMoveValidation() {
        // Set up the board with a Red stone placed at the center
        board.getHexStatus()[6][6] = "Red";

        // Try placing another Red stone in an adjacent hex (invalid move)
        boolean isValid = gameManager.makeMove(new Canvas().getGraphicsContext2D(), 380.8, 345);  // Invalid placement next to Red

        // Assert that the move is invalid and the board is not updated
        assertFalse(isValid, "Move was not rejected as it is adjacent to the same color");
    }
}
