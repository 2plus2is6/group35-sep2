import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InputHandler {
    private Button exitButton; //Stores the exit button

    public InputHandler(Stage stage) {
        exitButton = new Button("Exit Game"); // The text to be displayed
        //Formatting
        exitButton.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-background-color: PURPLE; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );

        exitButton.setOnAction(e -> stage.close()); // The action the button is required to follow upon being clicked
    }

    public Button getExitButton() {
        return exitButton;
    }
}
