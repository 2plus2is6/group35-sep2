import org.junit.jupiter.api.BeforeEach; // This annotation runs the setUp method before each test case
import org.junit.jupiter.api.Test; // This annotation marks the method as a test method
import static org.junit.jupiter.api.Assertions.*; // Provides assertion methods like assertEquals

public class PlayerTest {
    private Player player; // The Player object that will be tested

    // The @BeforeEach method runs before every test. It ensures the player object is freshly initialized for each test.
    @BeforeEach
    void setUp() {
        player = new Player(); // Initialize a new Player object before each test
    }

    // This test checks if the initial player is "Red" when the game starts
    @Test
    void testInitialPlayerIsRed() {
        // Assert that the current player is "Red" when the game begins
        assertEquals("Red", player.getCurrentPlayer(), "Initial player should be Red");
    }

    // This test checks if switching the turn once correctly changes the player to "Blue"
    @Test
    void testSwitchTurnOnceChangesToBlue() {
        player.switchTurn(); // Switch turn once, should change to Blue
        // Assert that the current player is "Blue" after one switch
        assertEquals("Blue", player.getCurrentPlayer(), "After one turn switch, the player should be Blue");
    }

    // This test checks if switching the turn twice brings it back to "Red"
    @Test
    void testSwitchTurnTwiceReturnsToRed() {
        player.switchTurn(); // First switch, should change to Blue
        player.switchTurn(); // Second switch, should change back to Red
        // Assert that after two switches, the player is back to "Red"
        assertEquals("Red", player.getCurrentPlayer(), "After two turn switches, the player should be Red");
    }
}
