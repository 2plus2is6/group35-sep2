import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// Displays "To make a move" with turn indicator
public class Renderer {
    private final Label turnIndicator; // Stores the text to be displayed for the turn indicator

    public Renderer(Label turnIndicator) {
        this.turnIndicator = turnIndicator;
        turnIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 20)); // Better text appearance
        turnIndicator.setTextFill(Color.RED); // Default: Red's Turn
        turnIndicator.setStyle("-fx-padding: 10px; -fx-alignment: center;");
        updateTurn("Red");
    }

    // Updates the turn label
    public void updateTurn(String player) {
        turnIndicator.setText(player + "'s Turn - To Make a Move");

        if (player.equals("Red")) {
            turnIndicator.setTextFill(Color.RED);
        } else {
            turnIndicator.setTextFill(Color.BLUE);
        }
    }

    // ✅ SPRINT 4: Displays the winning message when a player wins
    public void displayWinMessage(String winner) {
        turnIndicator.setText(winner + " wins!");
        turnIndicator.setTextFill(winner.equals("Red") ? Color.RED : Color.BLUE);
    }
}
