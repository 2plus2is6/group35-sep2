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

/**
 * The main application class for HexOust, a hexagonal strategy game.
 * Sets up the JavaFX application, initializes game components, and handles the UI layout.
 */
public class Main extends Application {
    private Board board;                       // Holds the hexagonal grid representation
    private Player player;                     // Manages the current player state
    private GameManager gameManager;           // Coordinates the game logic

    /**
     * The entry point for launching the HexOust JavaFX application.
     * @param args Command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);                          // Start the JavaFX runtime
    }

    /**
     * Initializes and displays the primary stage with the game UI.
     * @param stage The primary stage for the application
     * @throws Exception If an error occurs during initialization
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("HexOust");  // Set the window title

        // Load the Montserrat font from the resources for consistent styling
        Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Regular.ttf"), 12);

        Canvas canvas = new Canvas(800, 800);             // Create a canvas for drawing the board
        GraphicsContext gc = canvas.getGraphicsContext2D(); // Get the graphics context for rendering

        Label turnIndicator = new Label();                // Create a label to show the current turn
        Renderer renderer = new Renderer(turnIndicator, stage); // Initialize the renderer
        renderer.updateTurn("Red");                       // Set the initial turn to Red

        player = new Player();                            // Initialize the player manager
        gameManager = new GameManager(board, player, renderer, stage, gc); // Initialize the game manager
        board = new Board(renderer, player, gameManager); // Initialize the board
        gameManager.setBoard(board);                      // Link the board to the game manager
        board.resetBoard();                               // Clear the board to start fresh
        board.render(gc);                                 // Draw the empty board

        InputHandler inputHandler = new InputHandler(stage, gc, gameManager, board, player); // Set up input handling
        Button restartButton = inputHandler.getRestartButton(); // Get the restart button
        Button exitButton = inputHandler.getExitButton();   // Get the exit button

        // Create the welcome label for the header
        Label welcome = new Label("Welcome to HexOust");
        welcome.setStyle(                                  // Style the welcome text
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ECF0F1;"
        );

        // Create the credit label for the team
        Label credit = new Label("Created by Bit Warriors");
        credit.setStyle(                                    // Style the credit text
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #BDC3C7;"
        );

        turnIndicator.setStyle(                             // Style the turn indicator
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #E74C3C;"
        );

        // Arrange header elements vertically with spacing
        VBox header = new VBox(5, welcome, credit, turnIndicator, renderer.getWinMessageLabel());
        header.setAlignment(Pos.CENTER);                   // Center the header content
        header.setPadding(new Insets(10));                 // Add padding around the header

        BorderPane mainPane = new BorderPane();            // Create the main layout
        mainPane.setTop(header);                           // Place the header at the top
        mainPane.setCenter(canvas);                        // Place the canvas in the center

        // Apply a dark green background and set the default font
        mainPane.setStyle(
                "-fx-background-color: #013220;" +             // Set deep forest green background
                        "-fx-font-family: 'Montserrat';"               // Set Montserrat as default font
        );

        // Add mouse click handler to process game moves
        canvas.setOnMouseClicked((MouseEvent event) -> {
            double x = event.getX();                       // Get the x-coordinate of the click
            double y = event.getY();                       // Get the y-coordinate of the click
            gameManager.makeMove(gc, x, y);                // Process the move
        });

        // Wrap the main pane in a StackPane for button positioning
        StackPane root = new StackPane(mainPane);

        // Position the restart button near the bottom-left
        StackPane.setAlignment(restartButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(restartButton, new Insets(0, 0, 30, 30));
        root.getChildren().add(restartButton);

        // Position the exit button near the bottom-right
        StackPane.setAlignment(exitButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(exitButton, new Insets(0, 30, 30, 0));
        root.getChildren().add(exitButton);

        Scene scene = new Scene(root, 1000, 900);           // Create the scene with specified size
        stage.setScene(scene);                             // Set the scene on the stage
        stage.show();                                      // Display the window
    }
}