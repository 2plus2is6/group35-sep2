import org.junit.jupiter.api.BeforeEach; // Runs setup before each test
import org.junit.jupiter.api.Test; // Marks test methods
import static org.junit.jupiter.api.Assertions.*; // Provides assertion methods

/**
 * Tests the functionality of the Player class in the HexOust game.
 * Includes unit tests for initial player setup and turn-switching behavior.
 */
public class PlayerTest {
    private Player player; // Player instance under test

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        player = new Player(); // Initialize a new Player instance
    }

    /**
     * Tests that the initial player is set to Red.
     */
    @Test
    void testInitialPlayerIsRed() {
        assertEquals("Red", player.getCurrentPlayer(), "Initial player should be Red"); // Verify initial player
    }

    /**
     * Tests that switching the turn once changes the player to Blue.
     */
    @Test
    void testSwitchTurnOnceChangesToBlue() {
        player.switchTurn(); // Switch turn from Red to Blue
        assertEquals("Blue", player.getCurrentPlayer(), "After one turn switch, the player should be Blue"); // Verify Blue
    }

    /**
     * Tests that switching the turn twice returns the player to Red.
     */
    @Test
    void testSwitchTurnTwiceReturnsToRed() {
        player.switchTurn(); // Switch to Blue
        player.switchTurn(); // Switch back to Red
        assertEquals("Red", player.getCurrentPlayer(), "After two turn switches, the player should be Red"); // Verify Red
    }
}