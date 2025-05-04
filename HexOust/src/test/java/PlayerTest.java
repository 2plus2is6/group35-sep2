import org.junit.jupiter.api.BeforeEach; // Runs setup before each test
import org.junit.jupiter.api.Test; // Marks test methods
import static org.junit.jupiter.api.Assertions.*; // Provides assertion methods

// Tests Player functionality
public class PlayerTest {
    private Player player; // Player under test

    // Sets up test environment
    @BeforeEach
    void setUp() {
        player = new Player(); // Initializes Player
    }

    // Tests initial player
    @Test
    void testInitialPlayerIsRed() {
        assertEquals("Red", player.getCurrentPlayer(), "Initial player should be Red"); // Checks initial player
    }

    // Tests single turn switch
    @Test
    void testSwitchTurnOnceChangesToBlue() {
        player.switchTurn(); // Switches turn
        assertEquals("Blue", player.getCurrentPlayer(), "After one turn switch, the player should be Blue"); // Checks Blue turn
    }

    // Tests double turn switch
    @Test
    void testSwitchTurnTwiceReturnsToRed() {
        player.switchTurn(); // Switches to Blue
        player.switchTurn(); // Switches back to Red
        assertEquals("Red", player.getCurrentPlayer(), "After two turn switches, the player should be Red"); // Checks Red turn
    }
}