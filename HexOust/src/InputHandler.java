import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InputHandler {
    private final Button exitButton; //Stores the exit button
    private final Button restartButton;

    public InputHandler(Stage stage, Runnable onRestart) {
        exitButton = new Button("Exit Game"); // The text to be displayed
        exitButton.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-background-color: PURPLE; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        exitButton.setOnAction(e -> stage.close());

        restartButton = new Button("Restart Game");
        restartButton.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-background-color: DARKCYAN; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-background-radius: 5px;"
        );
        restartButton.setOnAction(e -> onRestart.run());
    }

    // ✅ FIX: These methods should be INSIDE the class
    public Button getExitButton() {
        return exitButton;
    }

    public Button getRestartButton() {
        return restartButton;
    }
}
