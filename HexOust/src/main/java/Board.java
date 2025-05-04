import javafx.scene.canvas.GraphicsContext; // Used for drawing on canvas
import javafx.scene.paint.Color; // Defines colors for hexagons
import java.util.Arrays; // Used for array operations
import java.util.List; // Used for lists of coordinates
import java.awt.*; // Used for Point class
import java.util.ArrayList; // Used for dynamic lists

/**
 * Represents the hexagonal game board, handling rendering and stone placement.
 */
public class Board {
    private static final int BASE = 6; // Base-7 grid size
    private static final double HEX_SIZE = 30; // Hexagon size in pixels
    private static final double CENTER_X = 410; // Board center x-coordinate
    private static final double CENTER_Y = 345; // Board center y-coordinate
    private final Renderer renderer; // Updates UI
    private final Player player; // Manages turns
    private final String[][] hexStatus; // Tracks hex occupancy
    private final MoveValidator moveValidator; // Validates moves
    private final CaptureHandler captureHandler; // Handles captures

    /**
     * Constructs a Board with dependencies.
     * @param renderer The UI renderer
     * @param player The player manager
     * @param gameManager The game manager
     */
    public Board(Renderer renderer, Player player, GameManager gameManager) {
        this.renderer = renderer; // Assigns renderer
        this.player = player; // Assigns player
        this.captureHandler = new CaptureHandler(this); // Initializes capture handler
        this.moveValidator = new MoveValidator(captureHandler); // Initializes move validator
        this.hexStatus = new String[2 * BASE + 1][2 * BASE + 1]; // Creates 13x13 array
    }

    /**
     * Renders the hexagonal grid.
     * @param gc The graphics context
     */
    public void render(GraphicsContext gc) {
        ArrayList<ArrayList<Point>> hexagons = generateHexagons(); // Generates hexagon corners
        drawHexagons(gc, hexagons); // Draws hexagons
    }

    // Generates hexagon corners
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

    // Draws all hexagons
    private void drawHexagons(GraphicsContext gc, ArrayList<ArrayList<Point>> hexagons) {
        for (ArrayList<Point> hexagon : hexagons) { // Loops through hexagons
            drawHexagon(gc, hexagon, Color.LIGHTGRAY); // Draws as light gray
        }
    }

    /**
     * Places a stone at the clicked position if valid.
     * @param gc The graphics context
     * @param x The x-coordinate of the click
     * @param y The y-coordinate of the click
     * @param currentPlayer The current player
     * @return True if move was successful, false otherwise
     */
    public boolean fillHex(GraphicsContext gc, double x, double y, String currentPlayer) {
        HexCube clickedHex = pixelToHex(x, y); // Converts to hex coordinates
        if (!isWithinBounds(clickedHex)) { // Checks bounds
            return false; // Exits if out of bounds
        }
        System.out.println("Clicked at: (" + x + ", " + y + "), Hex: (q=" + clickedHex.q + ", r=" + clickedHex.r + ")"); // Logs click
        if (!moveValidator.isValidMove(clickedHex.q, clickedHex.r, hexStatus, currentPlayer)) { // Validates move
            System.out.println("Invalid move!"); // Logs invalid move
            return false; // Exits if invalid
        }
        placeStone(clickedHex, currentPlayer, gc); // Places stone
        return handleCapture(clickedHex, currentPlayer, gc); // Handles captures
    }

    // Checks if hex is within bounds
    private boolean isWithinBounds(HexCube hex) {
        return Math.abs(hex.q) <= BASE && Math.abs(hex.r) <= BASE && Math.abs(hex.s) <= BASE; // Returns true if in bounds
    }

    // Places a stone on the board
    private void placeStone(HexCube hex, String currentPlayer, GraphicsContext gc) {
        hexStatus[(int) hex.q + BASE][(int) hex.r + BASE] = currentPlayer; // Updates hex status
        ArrayList<Point> corners = HexCube.polygonCorners(hex, CENTER_X, CENTER_Y, HEX_SIZE); // Gets corners
        drawHexagon(gc, corners, currentPlayer.equals("Red") ? Color.RED : Color.BLUE); // Draws stone
    }

    // Handles captures after a move
    private boolean handleCapture(HexCube hex, String currentPlayer, GraphicsContext gc) {
        boolean captureOccurred = captureHandler.checkAndCapture(
                hex.q, hex.r, hexStatus, currentPlayer, gc); // Checks for captures
        if (captureOccurred) { // If capture occurred
            System.out.println(currentPlayer + " captured pieces!"); // Logs capture
            updateBoardUI(gc); // Updates UI
            Player.grantExtraTurn(); // Grants extra turn
        }
        return captureOccurred; // Returns capture status
    }

    // Draws a hexagon
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
     * Resets the board to an empty state.
     */
    public void resetBoard() {
        for (int i = 0; i < hexStatus.length; i++) { // Loops through rows
            Arrays.fill(hexStatus[i], null); // Clears row
        }
    }

    /**
     * Removes captured stones from the board.
     * @param capturedStones List of captured stone coordinates
     * @param gc The graphics context
     */
    public void removeStones(List<int[]> capturedStones, GraphicsContext gc) {
        for (int[] hex : capturedStones) { // Loops through captured stones
            hexStatus[hex[0]][hex[1]] = null; // Clears stone
            HexCube hexCube = createHexCubeFromIndices(hex[0], hex[1]); // Creates hex
            ArrayList<Point> corners = HexCube.polygonCorners(hexCube, CENTER_X, CENTER_Y, HEX_SIZE); // Gets corners
            drawHexagon(gc, corners, Color.LIGHTGRAY); // Redraws as empty
        }
    }

    // Creates HexCube from board indices
    private HexCube createHexCubeFromIndices(int qIndex, int rIndex) {
        int q = qIndex - BASE; // Converts to q
        int r = rIndex - BASE; // Converts to r
        return new HexCube(q, r, -q - r); // Returns HexCube
    }

    // Updates board UI
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

    // Redraws a single hex as empty
    private void removeStoneFromBoard(GraphicsContext gc, int q, int r) {
        HexCube hex = createHexCubeFromIndices(q, r); // Creates hex
        ArrayList<Point> corners = HexCube.polygonCorners(hex, CENTER_X, CENTER_Y, HEX_SIZE); // Gets corners
        drawHexagon(gc, corners, Color.LIGHTGRAY); // Redraws as empty
    }

    /**
     * Represents a hexagon in cube coordinates.
     */
    public static class HexCube {
        final double q; // q coordinate
        final double r; // r coordinate
        final double s; // s coordinate

        // Constructor for HexCube
        HexCube(double q, double r, double s) {
            this.q = q; // Assigns q
            this.r = r; // Assigns r
            this.s = s; // Assigns s
        }

        // Calculates hexagon corners
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
     * Updates the turn indicator.
     */
    public void updateTurnIndicator() {
        renderer.updateTurn(player.getCurrentPlayer()); // Updates turn display
    }

    /**
     * Gets the board's hex status.
     * @return The hex status array
     */
    public String[][] getHexStatus() {
        return hexStatus; // Returns board state
    }

    // Converts pixel coordinates to hex
    HexCube pixelToHex(double x, double y) {
        double q = (2.0 / 3 * (x - CENTER_X)) / HEX_SIZE; // Calculates q
        double r = (-1.0 / 3 * (x - CENTER_X) + Math.sqrt(3) / 3 * (y - CENTER_Y)) / HEX_SIZE; // Calculates r
        return hexRound(q, r); // Rounds to nearest hex
    }

    // Rounds hex coordinates
    private HexCube hexRound(double q, double r) {
        double s = -q - r; // Calculates s
        int intQ = (int) Math.round(q); // Rounds q
        int intR = (int) Math.round(r); // Rounds r
        int intS = (int) Math.round(s); // Rounds s
        double qDiff = Math.abs(intQ - q); // Calculates q error
        double rDiff = Math.abs(intR - r); // Calculates r error
        double sDiff = Math.abs(intS - s); // Calculates s error
        if (qDiff > rDiff && qDiff > sDiff) { // Checks q error
            intQ = -intR - intS; // Adjusts q
        } else if (rDiff > sDiff) { // Checks r error
            intR = -intQ - intS; // Adjusts r
        }
        return new HexCube(intQ, intR, -intQ - intR); // Returns rounded hex
    }
}
