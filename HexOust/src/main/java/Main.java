import javafx.application.Application;         // Imports the base class for JavaFX applications
import javafx.geometry.Insets;                // Imports class for defining padding and margins
import javafx.geometry.Pos;                   // Imports class for specifying alignment constants
import javafx.scene.Scene;                    // Imports class representing the JavaFX scene graph
import javafx.scene.canvas.Canvas;            // Imports class for drawing surfaces
import javafx.scene.canvas.GraphicsContext;   // Imports class providing drawing operations
import javafx.scene.control.Button;           // Imports class for interactive button controls
import javafx.scene.control.Label;            // Imports class for displaying text elements
import javafx.scene.input.MouseEvent;         // Imports class for mouse event handling
import javafx.scene.layout.BorderPane;        // Imports class for arranging nodes in five regions
import javafx.scene.layout.StackPane;         // Imports class for stacking nodes on top of each other
import javafx.scene.layout.VBox;              // Imports class for vertical box layout
import javafx.scene.text.Font;                // Imports class for loading and using custom fonts
import javafx.stage.Stage;                    // Imports class representing the primary window

// Declares the main application class for HexOust
public class Main extends Application {
    private Board board;                       // Holds the hexagonal grid representation
    private Player player;                     // Manages the current player state
    private GameManager gameManager;           // Coordinates the game logic

    // Defines the entry point for launching the JavaFX application
    public static void main(String[] args) {
        launch(args);                          // Invokes JavaFX runtime to start the application
    }

    // Configures and displays the primary stage and its contents
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("HexOust - Sprint 4");  // Sets the title of the application window

        // Loads the Montserrat font from application resources
        Font.loadFont(
                getClass().getResourceAsStream("/fonts/Montserrat-Regular.ttf"),
                12
        );

        Canvas canvas = new Canvas(800, 800);             // Creates a drawing canvas of fixed size
        GraphicsContext gc = canvas.getGraphicsContext2D(); // Obtains the graphics context for rendering

        Label turnIndicator = new Label();                // Instantiates the label for displaying turn prompts
        Renderer renderer = new Renderer(turnIndicator, stage); // Creates the renderer with the turn label and stage
        renderer.updateTurn("Red");                       // Initializes the turn indicator to "Red"

        player = new Player();                            // Constructs the player management object
        gameManager = new GameManager(board, player, renderer, stage, gc); // Constructs the game manager
        board = new Board(renderer, player, gameManager); // Constructs the board with renderer and game logic
        gameManager.setBoard(board);                      // Assigns the board instance to the game manager
        board.resetBoard();                               // Resets all board tiles to their initial state
        board.render(gc);                                 // Renders the empty board onto the canvas

        InputHandler inputHandler = new InputHandler(stage, gc, gameManager, board, player); // Initializes input handling
        Button restartButton = inputHandler.getRestartButton(); // Retrieves the restart button control
        Button exitButton = inputHandler.getExitButton();   // Retrieves the exit button control

        // Constructs the header area containing welcome text, credits, and turn indicator
        Label welcome = new Label("Welcome to HexOust");   // Creates the title label for the game header
        welcome.setStyle(                                  // Applies font size, weight, and color to the title
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ECF0F1;"
        );

        Label credit = new Label("Created by Bit Warriors"); // Creates the credit label for the team name
        credit.setStyle(                                    // Applies styling to the credit label
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #BDC3C7;"
        );

        turnIndicator.setStyle(                             // Styles the turn indicator label for emphasis
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #E74C3C;"
        );

        VBox header = new VBox(5, welcome, credit, turnIndicator, renderer.getWinMessageLabel()); // Arranges header nodes vertically
        header.setAlignment(Pos.CENTER);                   // Centers all nodes within the header box
        header.setPadding(new Insets(10));                 // Adds uniform padding around the header content

        BorderPane mainPane = new BorderPane();            // Creates the primary layout container
        mainPane.setTop(header);                           // Positions the header at the top region
        mainPane.setCenter(canvas);                        // Positions the canvas in the center region

        // Applies an "old money" dark green backdrop and sets default font
        mainPane.setStyle(
                "-fx-background-color: #013220;" +             // Uses a deep forest green for the background
                        "-fx-font-family: 'Montserrat';"               // Sets Montserrat as the default UI font
        );

        // Registers a mouse click handler on the canvas to process game moves
        canvas.setOnMouseClicked((MouseEvent event) -> {
            double x = event.getX();                       // Captures the x-coordinate of the click
            double y = event.getY();                       // Captures the y-coordinate of the click
            gameManager.makeMove(gc, x, y);                // Delegates the move processing to the game manager
        });

        // Wraps the main pane in a StackPane to allow floating of control buttons
        StackPane root = new StackPane(mainPane);

        // Positions the restart button slightly above the bottom-left corner
        StackPane.setAlignment(restartButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(restartButton, new Insets(0, 0, 30, 30));
        root.getChildren().add(restartButton);

        // Positions the exit button slightly above the bottom-right corner
        StackPane.setAlignment(exitButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(exitButton, new Insets(0, 30, 30, 0));
        root.getChildren().add(exitButton);

        Scene scene = new Scene(root, 1000, 900);           // Creates the scene with fixed dimensions
        stage.setScene(scene);                             // Sets the scene onto the stage
        stage.show();                                      // Displays the stage to the user
    }
}
