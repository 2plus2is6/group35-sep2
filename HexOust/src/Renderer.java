import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// Displays "To make a move" with turn indicator
public class Renderer {
    private Label turnIndicator; //Stores the text to be displayed for the turn indicator

    public Renderer(Label turnIndicator) {
        this.turnIndicator = turnIndicator;
        turnIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 20)); // Better text appearance
        turnIndicator.setTextFill(Color.RED); // Default: Red's Turn
        turnIndicator.setStyle("-fx-padding: 10px; -fx-alignment: center;"); // Added spacing
        updateTurn("Red");
    }


    public void updateTurn(String player) {

        //To update the turn text dynamically
        turnIndicator.setText(player + "'s Turn - To Make a Move");


        // Change color based on player's turn (NOT INCLUDED IN THIS SPRINT)
        if (player.equals("Red")) {
            turnIndicator.setTextFill(Color.RED);
        } else {
            turnIndicator.setTextFill(Color.BLUE);
        }
    }

    //Used to show an invalid move with
    public void showInvalidMoveMessage() {
        turnIndicator.setText("Invalid Cell Placement!");
        turnIndicator.setTextFill(Color.BLACK);
    }
}
