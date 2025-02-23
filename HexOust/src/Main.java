import javafx.application.Application;
import javafx.scene.Scene; // Represents the game window
import javafx.scene.canvas.Canvas; // Draw the hex board
import javafx.scene.canvas.GraphicsContext; // Drawing function for canvas
import javafx.scene.control.Button; // Used to create Exit button
import javafx.stage.Stage;
import javafx.scene.control.Label; // Used to create turn indicator
import javafx.scene.layout.BorderPane; // Arrange UI elements
import javafx.scene.layout.VBox; // Vertical layout for elements (Exit button)



//Creates a JavaFX window, sets up the Canvas and calls Board.render()
public class Main extends Application {

    private Board board; //Board contains the hexagonal grid

    public static void main(String[] args) {
        launch(args); //Starts the JavaFX application
    }


    @Override
    public void start(Stage stage) throws Exception {
        //A
        stage.setTitle("HexOust - Sprint 1"); //Set window title

        //K
        Canvas canvas = new Canvas(800, 800); //Canvas is used to draw
        GraphicsContext gc = canvas.getGraphicsContext2D(); //We draw using GraphicsContext since it allows to draw shapes

        //U
        Label turnIndicator = new Label(); //To display the player's turn
        //renderer is used only when it's updateTurn function is implemented to show the turn at the top
        Renderer renderer = new Renderer(turnIndicator); //Updates the turn indicator text
        //K
        board = new Board(renderer);
        board.render(gc); //Used for displaying
        //U
        InputHandler inputHandler = new InputHandler(stage); //Handles the exit button
        Button exitButton = inputHandler.getExitButton(); // Fetches exit button from InputHandler

        //U
        VBox buttonContainer = new VBox(); // Created to hold the exit button
        buttonContainer.getChildren().add(exitButton); // Adding exit button to make it appear on the screen
        buttonContainer.setStyle("-fx-alignment: bottom-right; -fx-padding: 0px 20px 40px 0px;"); //Alignment for the button

        //U
        VBox vbox = new VBox(); // Stack UI elements (i.e. turnIndicator and hexBoard)
        vbox.getChildren().addAll(turnIndicator, canvas); // Assigns first row to turn indicator and second to hexagonal board
        vbox.setStyle("-fx-alignment: center; -fx-padding: 10px;"); // Centering and Padding

        //U
        BorderPane root = new BorderPane(); // Creates a main layout
        root.setTop(vbox); // Put the indicator on top
        root.setBottom(buttonContainer); // Adds exit button at the bottom-right

        //A
        Scene scene = new Scene(root, 1000, 900); //Creates the JavaFX window
        stage.setScene(scene);
        stage.show(); //Shows the JavaFX window
    }
}
