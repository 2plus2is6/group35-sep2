import javafx.application.Application; // Base class for JavaFX applications
import javafx.scene.Scene; // Represents the game window
import javafx.scene.canvas.Canvas; // Used to draw the hex board
import javafx.scene.canvas.GraphicsContext; // Drawing functions for canvas
import javafx.scene.control.Button; // Used for restart and exit buttons
import javafx.scene.control.Label; // Used for turn and win indicators
import javafx.scene.input.MouseEvent; // Handles mouse clicks
import javafx.scene.layout.BorderPane; // Arranges UI elements
import javafx.scene.layout.HBox; // Horizontal layout for buttons
import javafx.scene.layout.StackPane; // Stacks UI elements
import javafx.geometry.Pos; // Sets alignment for layouts
import javafx.stage.Stage; // Main application window

// Main class for the HexOust game
public class Main extends Application {
    private Board board; // Board containing the hexagonal grid
    private Player player; // Player instance for turn management
    private GameManager gameManager; // Manages game logic

    // Entry point for the application
    public static void main(String[] args) {
        launch(args); // Starts the JavaFX application
    }

    // Sets up the game UI and logic
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("HexOust - Sprint 4"); // Sets window title

        Canvas canvas = new Canvas(800, 800); // Creates canvas for drawing
        GraphicsContext gc = canvas.getGraphicsContext2D(); // Gets GraphicsContext for drawing

        Label turnIndicator = new Label(); // Creates turn indicator label
        Renderer renderer = new Renderer(turnIndicator, stage); // Initializes Renderer
        renderer.updateTurn("Red"); // Sets initial turn to Red

        player = new Player(); // Initializes Player
        gameManager = new GameManager(board, player, renderer, stage, gc); // Initializes GameManager
        board = new Board(renderer, player, gameManager); // Initializes Board
        gameManager.setBoard(board); // Sets Board in GameManager
        board.resetBoard(); // Resets board state
        board.render(gc); // Renders initial board

        InputHandler inputHandler = new InputHandler(stage, gc, gameManager, board, player); // Initializes InputHandler
        Button restartButton = inputHandler.getRestartButton(); // Gets restart button
        Button exitButton = inputHandler.getExitButton(); // Gets exit button

        StackPane topPane = new StackPane(); // Creates top pane for indicators
        topPane.getChildren().addAll(turnIndicator, renderer.getWinMessageLabel()); // Adds turn and win labels

        HBox restartContainer = new HBox(); // Creates container for restart button
        restartContainer.setAlignment(Pos.BOTTOM_LEFT); // Aligns restart button left
        restartContainer.getChildren().add(restartButton); // Adds restart button

        HBox exitContainer = new HBox(); // Creates container for exit button
        exitContainer.setAlignment(Pos.BOTTOM_RIGHT); // Aligns exit button right
        exitContainer.getChildren().add(exitButton); // Adds exit button

        BorderPane root = new BorderPane(); // Creates main layout
        root.setTop(topPane); // Places indicators at top
        root.setLeft(restartContainer); // Places restart button at bottom-left
        root.setRight(exitContainer); // Places exit button at bottom-right
        root.setCenter(canvas); // Places canvas in center

        canvas.setOnMouseClicked((MouseEvent event) -> { // Sets mouse click handler
            double x = event.getX(); // Gets x-coordinate of click
            double y = event.getY(); // Gets y-coordinate of click
            gameManager.makeMove(gc, x, y); // Processes move
        });

        Scene scene = new Scene(root, 1000, 900); // Creates window scene
        stage.setScene(scene); // Sets scene to stage
        stage.show(); // Shows window
    }
}