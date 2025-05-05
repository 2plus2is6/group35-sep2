import javafx.scene.canvas.GraphicsContext; // Used for drawing on canvas
import javafx.scene.paint.Color; // Defines colors for hexagons
import java.util.Arrays; // Used for array operations
import java.util.List; // Used for lists of coordinates
import java.awt.*; // Used for Point class
import java.util.ArrayList; // Used for dynamic lists

/**
 * Represents the hexagonal game board for HexOust, managing rendering, stone placement, and game state.
 * The board uses a hexagonal grid with cube coordinates (q, r, s) and handles interactions with the UI.
 */
public class Board {
    private static final int BASE = 6; // Defines the grid radius (base-7 grid, so 13x13 array)
    private static final double HEX_SIZE = 30; // Size of each hexagon in pixels
    private static final double CENTER_X = 410; // Board center x-coordinate
    private static final double CENTER_Y = 345; // Board center y-coordinate
    private final Renderer renderer; // Updates UI
    private final Player player; // Manages turns
    private final String[][] hexStatus; // 2D array to track hex occupancy (Red, Blue, or null)
    private final MoveValidator moveValidator; // Validates moves
    private final CaptureHandler captureHandler; // Handles capturing opponent stones

    /**
     * Constructs a new Board instance with the given dependencies.
     * @param renderer The renderer for updating the UI
     * @param player The player manager for tracking turns
     * @param gameManager The game manager for coordinating game logic
     */
    public Board(Renderer renderer, Player player, GameManager gameManager) {
        this.renderer = renderer; // Assigns renderer
        this.player = player; // Assigns player
        this.captureHandler = new CaptureHandler(this); // Initializes capture handler
        this.moveValidator = new MoveValidator(captureHandler); // Initializes move validator
        this.hexStatus = new String[2 * BASE + 1][2 * BASE + 1]; // Creates 13x13 array
        resetBoard(); // Ensures initial state is cleared
    }

    /**
     * Renders the hexagonal grid on the canvas.
     * @param gc The graphics context used to draw the grid
     */
    public void render(GraphicsContext gc) {
        ArrayList<ArrayList<Point>> hexagons = generateHexagons(); // Generates hexagon corners
        drawHexagons(gc, hexagons); // Draws hexagons
    }




    /**
     * Removes captured stones from the board and updates the UI.
     * @param capturedStones List of coordinates of stones to remove
     * @param gc The graphics context for redrawing the hexes
     */
    public void removeStones(List<int[]> capturedStones, GraphicsContext gc) {
        for (int[] hex : capturedStones) { // Iterate over captured stones
            hexStatus[hex[0]][hex[1]] = null; // Clear the stone from the board
            HexCube hexCube = createHexCubeFromIndices(hex[0], hex[1]); // Convert indices to hex
            ArrayList<Point> corners = HexCube.polygonCorners(hexCube, CENTER_X, CENTER_Y, HEX_SIZE); // Gets corners
            drawHexagon(gc, corners, Color.LIGHTGRAY); // Redraws as empty
        }
    }
    /**
     * Places a stone at the clicked position if the move is valid.
     * @param gc The graphics context for drawing the stone
     * @param x The x-coordinate of the click in pixels
     * @param y The y-coordinate of the click in pixels
     * @param currentPlayer The player making the move ("Red" or "Blue")
     * @return True if the stone was placed successfully, false otherwise
     */
    public boolean fillHex(GraphicsContext gc, double x, double y, String currentPlayer) {
        HexCube clickedHex = pixelToHex(x, y); // Convert pixel coordinates to hex coordinates
        if (!isWithinBounds(clickedHex)) { // Check if the hex is within the grid
            return false; // Exits if out of bounds
        }
        System.out.println("Clicked at: (" + x + ", " + y + "), Hex: (q=" + clickedHex.q + ", r=" + clickedHex.r + ")"); // Logs click
        if (!moveValidator.isValidMove(clickedHex.q, clickedHex.r, hexStatus, currentPlayer)) { // Validates move
            System.out.println("Invalid move!"); // Logs invalid move
            return false; // Return false if the move is not allowed
        }
        placeStone(clickedHex, currentPlayer, gc); // Places stone
        boolean captureOccurred = handleCapture(clickedHex, currentPlayer, gc);// Check for captures
        return true; // Return true if stone is placed successfully
    }

    /**
     * Draws all hexagons on the canvas with a specified color.
     * @param gc The graphics context for drawing
     * @param hexagons The list of hexagons, each represented by its corners
     */
    private void drawHexagons(GraphicsContext gc, ArrayList<ArrayList<Point>> hexagons) {
        for (ArrayList<Point> hexagon : hexagons) { // Loops through hexagons
            drawHexagon(gc, hexagon, Color.LIGHTGRAY); // Draws as light gray
        }
    }


    /**
     * Generates the corners of all hexagons in the grid.
     * @return A list of hexagon corners, where each hexagon is a list of Points
     */
    private ArrayList<ArrayList<Point>> generateHexagons() {
        ArrayList<ArrayList<Point>> hexagons = new ArrayList<>(); // Stores hexagon corners
        for (int q = -BASE; q <= BASE; q++) { // Loops through q coordinates
            for (int r = -BASE; r <= BASE; r++) { // Loops through r coordinates
                int s = -q - r; // Calculates s
                if (Math.abs(s) <= BASE) { // Checks valid hex
                    HexCube hex = new HexCube(q, r, s); // Creates hex
                    ArrayList<Point> corners = HexCube.polygonCorners(hex, CENTER_X, CENTER_Y, HEX_SIZE); // Gets corners
                    hexagons.add(corners); // Adds corners
                }
            }
        }
        return hexagons; // Returns hexagons
    }
    /**
     * Checks if a hex is within the board's bounds.
     * @param hex The hex to check, in cube coordinates
     * @return True if the hex is within bounds, false otherwise
     */
    boolean isWithinBounds(HexCube hex) {
        return Math.abs(hex.q) <= BASE && Math.abs(hex.r) <= BASE && Math.abs(hex.s) <= BASE; // Returns true if in bounds
    }

    /**
     * Places a stone on the board and updates the UI.
     * @param hex The hex where the stone is placed
     * @param currentPlayer The player placing the stone ("Red" or "Blue")
     * @param gc The graphics context for drawing the stone
     */
    private void placeStone(HexCube hex, String currentPlayer, GraphicsContext gc) {
        hexStatus[(int) hex.q + BASE][(int) hex.r + BASE] = currentPlayer; // Update the board state
        ArrayList<Point> corners = HexCube.polygonCorners(hex, CENTER_X, CENTER_Y, HEX_SIZE); // Get pixel corners of the hex
        // Draw the stone in the player's color (Red or Blue)
        drawHexagon(gc, corners, currentPlayer.equals("Red") ? Color.RED : Color.BLUE);
    }

    /**
     * Handles capturing opponent stones after a move.
     * @param hex The hex where the stone was placed
     * @param currentPlayer The player who made the move
     * @param gc The graphics context for updating the UI
     * @return True if a capture occurred, false otherwise
     */
    private boolean handleCapture(HexCube hex, String currentPlayer, GraphicsContext gc) {
        // Check if the move results in capturing opponent stones
        boolean captureOccurred = captureHandler.checkAndCapture(
                hex.q, hex.r, hexStatus, currentPlayer, gc); // Checks for captures
        if (captureOccurred) { // If capture occurred
            System.out.println(currentPlayer + " captured pieces!"); // Log the capture
            updateBoardUI(gc); // Redraw the board to reflect the capture
            Player.grantExtraTurn(); // Grants extra turn
        }
        return captureOccurred; // Returns capture status
    }

    /**
     * Draws a single hexagon on the canvas with the specified color.
     * @param gc The graphics context for drawing
     * @param corners The list of corner points for the hexagon
     * @param color The color to fill the hexagon
     */
    private void drawHexagon(GraphicsContext gc, ArrayList<Point> corners, Color color) {
        double[] xPoints = new double[6]; // Stores x-coordinates
        double[] yPoints = new double[6]; // Stores y-coordinates
        for (int i = 0; i < 6; i++) { // Loops through corners
            xPoints[i] = corners.get(i).x; // Sets x-coordinate
            yPoints[i] = corners.get(i).y; // Sets y-coordinate
        }
        gc.setFill(color); // Sets fill color
        gc.fillPolygon(xPoints, yPoints, 6); // Fills hexagon
        gc.setStroke(Color.BLACK); // Sets outline color
        gc.strokePolygon(xPoints, yPoints, 6); // Draws outline
    }

    /**
     * Resets the board by clearing all stones.
     */
    public void resetBoard() {
        for (String[] status : hexStatus) { // Loops through rows
            Arrays.fill(status, null); // Clears row
        }
    }




    /**
     * Converts board indices to a HexCube object.
     * @param qIndex The q-index in the hexStatus array
     * @param rIndex The r-index in the hexStatus array
     * @return A HexCube representing the corresponding hex
     */
    private HexCube createHexCubeFromIndices(int qIndex, int rIndex) {
        int q = qIndex - BASE; // Converts to q
        int r = rIndex - BASE; // Converts to r
        return new HexCube(q, r, -q - r); // Create and return the hex
    }

    /**
     * Updates the board's UI by redrawing empty hexes.
     * @param gc The graphics context for redrawing
     */
    void updateBoardUI(GraphicsContext gc) {
        for (int qIndex = 0; qIndex < hexStatus.length; qIndex++) { // Loops through q indices
            for (int rIndex = 0; rIndex < hexStatus[qIndex].length; rIndex++) { // Loops through r indices
                HexCube hex = createHexCubeFromIndices(qIndex, rIndex); // Creates hex
                if (!isWithinBounds(hex)) { // Skips out-of-bounds
                    continue; // Continues loop
                }
                if (hexStatus[qIndex][rIndex] == null) { // Checks if empty
                    removeStoneFromBoard(gc, qIndex, rIndex); // Redraws as empty
                }
            }
        }
    }

    /**
     * Redraws a single hex as empty (light gray).
     * @param gc The graphics context for drawing
     * @param q The q-index of the hex
     * @param r The r-index of the hex
     */
    private void removeStoneFromBoard(GraphicsContext gc, int q, int r) {
        HexCube hex = createHexCubeFromIndices(q, r); // Creates hex
        ArrayList<Point> corners = HexCube.polygonCorners(hex, CENTER_X, CENTER_Y, HEX_SIZE); // Gets corners
        drawHexagon(gc, corners, Color.LIGHTGRAY); // Redraw the hex in light gray
    }

    /**
     * Represents a hexagon using cube coordinates (q, r, s).
     * Ensures q + r + s = 0 for valid hex coordinates.
     */
    public static class HexCube {
        final double q; // q coordinate
        final double r; // r coordinate
        final double s; // s coordinate

        /**
         * Constructs a HexCube with the given coordinates.
         * @param q The q-coordinate
         * @param r The r-coordinate
         * @param s The s-coordinate
         */
        HexCube(double q, double r, double s) {
            this.q = q; // Assigns q
            this.r = r; // Assigns r
            this.s = s; // Assigns s
        }

        /**
         * Calculates the pixel corners of a hexagon for drawing.
         * @param hex The hex to calculate corners for
         * @param startX The x-coordinate of the board's center
         * @param startY The y-coordinate of the board's center
         * @param size The size of the hexagon in pixels
         * @return A list of Points representing the hexagon's corners
         */
        static ArrayList<Point> polygonCorners(HexCube hex, double startX, double startY, double size) {
            ArrayList<Point> corners = new ArrayList<>(); // Stores corners
            double x = startX + size * (3.0 / 2 * hex.q); // Converts q to x
            double y = startY + size * Math.sqrt(3) * (hex.r + hex.q / 2.0); // Converts r to y
            for (int i = 0; i < 6; i++) { // Loops through corners
                double angle = Math.toRadians(60 * i); // Calculates angle
                corners.add(new Point((int) (x + size * Math.cos(angle)), (int) (y + size * Math.sin(angle)))); // Adds corner
            }
            return corners; // Returns corners
        }
    }

    /**
     * Updates the turn indicator in the UI to reflect the current player.
     */
    public void updateTurnIndicator() {
        renderer.updateTurn(player.getCurrentPlayer()); // Update the UI with the current player
    }

    /**
     * Retrieves the current state of the board.
     * @return A 2D array representing the board's hex status
     */
    public String[][] getHexStatus() {
        return hexStatus; // Returns board state
    }

    /**
     * Converts pixel coordinates to hex coordinates.
     * @param x The x-coordinate in pixels
     * @param y The y-coordinate in pixels
     * @return A HexCube representing the nearest hex
     */
    HexCube pixelToHex(double x, double y) {
        // Calculate q and r using pixel-to-hex conversion formulas
        double q = (2.0 / 3 * (x - CENTER_X)) / HEX_SIZE;
        double r = (-1.0 / 3 * (x - CENTER_X) + Math.sqrt(3) / 3 * (y - CENTER_Y)) / HEX_SIZE; // Calculates r
        return hexRound(q, r); // Rounds to nearest hex
    }

    /**
     * Rounds floating-point hex coordinates to the nearest integer hex.
     * @param q The floating-point q-coordinate
     * @param r The floating-point r-coordinate
     * @return A HexCube with rounded integer coordinates
     */
    private HexCube hexRound(double q, double r) {
        double s = -q - r; // Calculates s
        int intQ = (int) Math.round(q); // Rounds q
        int intR = (int) Math.round(r); // Rounds r
        int intS = (int) Math.round(s); // Rounds s

        // Calculate rounding errors for each coordinate
        double qDiff = Math.abs(intQ - q); // Calculates q error
        double rDiff = Math.abs(intR - r); // Calculates r error
        double sDiff = Math.abs(intS - s); // Calculates s error

        // Adjust the largest error to ensure q + r + s = 0
        if (qDiff > rDiff && qDiff > sDiff) { // Checks q error
            intQ = -intR - intS; // Adjusts q
        } else if (rDiff > sDiff) { // Checks r error
            intR = -intQ - intS; // Adjusts r
        }
        return new HexCube(intQ, intR, -intQ - intR); // Returns rounded hex
    }
}
