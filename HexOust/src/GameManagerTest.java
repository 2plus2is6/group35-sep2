import javafx.embed.swing.JFXPanel; // We need this to initialize JavaFX in headless mode, i.e., without the UI
import javafx.scene.canvas.GraphicsContext; // This is used to draw on the canvas (our board)
import javafx.scene.control.Label; // This helps us create a label for the turn indicator
import javafx.scene.canvas.Canvas; // The canvas is where we draw the hexagonal grid
import org.junit.jupiter.api.BeforeAll; // This is used to run setup before any tests run
import org.junit.jupiter.api.BeforeEach; // Runs setup before each individual test
import org.junit.jupiter.api.Test; // Marks a method as a test case
import static org.junit.jupiter.api.Assertions.*; // These are assertion methods we use to check if the tests pass

public class GameManagerTest {
    private GameManager gameManager; // This is the class that manages the game flow
    private Player player; // This is the current player (Red or Blue)
    private Board board; // This represents the game board, where the game is played

    // This method runs once before all tests, it initializes the JavaFX toolkit
    @BeforeAll
    public static void initJavaFX() {
        new JFXPanel(); // Starts the JavaFX toolkit without needing a UI
    }

    // This method runs before each test to set up a fresh instance of the game for each test
    @BeforeEach
    void setUp() {
        // We create a label for showing whose turn it is
        Label turnLabel = new Label();
        Renderer renderer = new Renderer(turnLabel); // Renderer will update the turn display on the UI

        // Now, we initialize the player and board with the created renderer
        player = new Player();
        board = new Board(renderer, player, null); // The game board is initialized with the player and renderer

        // Create a GameManager with the board and player to control the game flow
        gameManager = new GameManager(board, player, renderer);
    }

    // This is a test case to check if a valid move actually updates the board
    @Test
    void testValidMoveUpdatesBoard() {
        GraphicsContext gc = new Canvas().getGraphicsContext2D(); // Graphics context to draw on the canvas

        // We're going to simulate a click at the center of the board (coordinates might need adjustment)
        double centerX = 410;  // Horizontal center position
        double centerY = 345;  // Vertical center position

        // Call the makeMove method to simulate a player move
        gameManager.makeMove(gc, centerX, centerY);

        // After the move, check if the stone was placed correctly at the converted board indices
        Board.HexCube clickedHex = board.pixelToHex(centerX, centerY); // Convert pixel position to board coordinates
        int q = (int) clickedHex.q + 6; // Adjust the q-coordinate
        int r = (int) clickedHex.r + 6; // Adjust the r-coordinate

        // We assert that the hex at this position is not null, meaning a stone was placed
        assertNotNull(board.getHexStatus()[q][r]);
    }

    // This test checks if the player gets an extra turn after capturing an opponent's stone
    @Test
    void testExtraTurnOnCapture() {
        // Set up: Place a Blue stone at the center of the board
        board.getHexStatus()[6][6] = "Blue";

        // Surround the Blue stone with Red stones, creating a potential capture situation
        board.getHexStatus()[7][6] = "Red";   // East
        board.getHexStatus()[5][6] = "Red";   // West
        board.getHexStatus()[6][5] = "Red";   // North
        board.getHexStatus()[6][7] = "Red";   // South
        board.getHexStatus()[7][5] = "Red";   // Northeast

        // Now simulate a click on the Southeast position (just below and to the right of the center)
        double clickX = 380.8; // Adjust X for the click
        double clickY = 363.2; // Adjust Y for the click

        // Debug: Print the hex that was clicked
        Board.HexCube clickedHex = board.pixelToHex(clickX, clickY);
        System.out.println("Clicked hex: q=" + clickedHex.q + ", r=" + clickedHex.r);

        // Make the move: Place a Red stone at the clicked position
        GraphicsContext gc = new Canvas().getGraphicsContext2D();
        gameManager.makeMove(gc, clickX, clickY);

        // Verify that the Blue stone has been captured and removed
        assertNull(board.getHexStatus()[6][6], "Blue stone not captured");

        // Verify that the Red player gets an extra turn after the capture
        assertEquals("Red", player.getCurrentPlayer(), "Extra turn not granted");
    }
}
