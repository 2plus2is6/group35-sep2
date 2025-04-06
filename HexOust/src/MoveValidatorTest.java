import org.junit.jupiter.api.BeforeEach; // This annotation is used to set up the test environment before each test runs
import org.junit.jupiter.api.Test; // This annotation marks the method as a test case
import static org.junit.jupiter.api.Assertions.*; // These are assertion methods used for validating the test results

public class MoveValidatorTest {
    private MoveValidator moveValidator; // This is the class we're testing
    private Board board; // The game board where the moves are made
    private String[][] hexStatus; // The board's status to track filled/unfilled hexes

    // This method is run before each test case, it initializes the objects needed for the test
    @BeforeEach
    void setUp() {
        // Initializing the board with mock dependencies (null values used here for simplicity)
        board = new Board(null, new Player(), null); // A new Board with mocked dependencies
        CaptureHandler captureHandler = new CaptureHandler(board); // Creating the CaptureHandler to handle capture logic
        moveValidator = new MoveValidator(captureHandler); // Initializing MoveValidator with the CaptureHandler
        hexStatus = board.getHexStatus(); // Get the board's hexStatus to check the state of each hex
    }

    // This is a test case that checks if placing a stone on an adjacent hex of the same color is considered invalid
    @Test
    void testInvalidAdjacentSameColor() {
        // Place a Red stone at the center of the board (board indices 6,6)
        hexStatus[6][6] = "Red";

        // Now, try placing another Red stone on an adjacent hex (board indices 7,6) to simulate an invalid move
        boolean isValid = moveValidator.isValidMove(1, 0, hexStatus, "Red");

        // Assert that the move is invalid (should return false) because two stones of the same color cannot be adjacent unless it's a capture
        assertFalse(isValid, "Adjacent same color without capture should be invalid");
    }
}
