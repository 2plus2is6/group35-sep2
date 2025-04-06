import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;

import java.awt.*;
import java.util.ArrayList;
//Generates the hexagonal grid using cube coordinates (q, r, s). Draws hexagons using gc.strokePolygon().
public class Board {
    private final int base = 6; // Base-7 Hexagonal Grid (N=6 means distance from center)
    private final double sizeOfHex = 30;  // Size of each hexagon
    private final Renderer renderer; // Store Renderer instance
    private final double primaryX = 410;
    private final double primaryY = 345; //Defines center point of the board for the first hexagon
    private final Player player;
    private final String[][] hexStatus; //Stores info about a hexagon whether it is filled with a colour or not
    private final MoveValidator moveValidator; //Checks hexStatus before allowing a move
    private final CaptureHandler captureHandler;



    public Board(Renderer renderer, Player player, GameManager gameManager) {
        this.renderer = renderer; // Assign Renderer
        this.player = player;
        this.captureHandler = new CaptureHandler(this); // Initialize here
        this.moveValidator = new MoveValidator(captureHandler); // Pass to MoveValidator
        this.hexStatus = new String[2 * base + 1][2 * base + 1]; //Creates a 2D array to ensure it covers entire grid
                                                                 // Both indexes store -6 to +6 values (13 values) so both the indexes represent q and r coordinates
    }

    public void render(GraphicsContext gc) {
        double primaryX = 410;
        double primaryY = 345; //Defines center point of the board for the first hexagon

        //An empty ArrayList of ArrayLists declared to stores corners for each hexagon since one ArrayList contains corners for one hexagon
        ArrayList<ArrayList<Point>> hexDisplay = new ArrayList<>();

        for (int q = -base; q <= base; q++) {
            for (int r = -base; r <= base; r++) { // Loops through cube coordinates to ensure proper hexagonal layout
                int s = -q - r; // To check q + r + s = 0

                if (Math.abs(s) <= base) { // Ensures only valid hexagons are created
                    HexCube h = new HexCube(q, r, s);

                    //Corners are required to draw hexagon so polygonCorners calculates that and returns an ArrayList<Point>
                    ArrayList<Point> corners = HexCube.polygonCorners(h, primaryX, primaryY, sizeOfHex);

                    //Corners are stored in coordinates to keep track of all hexagons
                    hexDisplay.add(corners);
                }
            }
        }
        // Draw all hexagons
        for (ArrayList<Point> hexagon : hexDisplay) { //Loops through all the recorded coordinates and draws the hexagons
            drawHexagon(gc, hexagon, Color.LIGHTGRAY);
        }
    }

    public void fillHex(GraphicsContext gc, double x, double y,  String currentPlayer) { //Handles player's click on the board, receives x and y position and fills the hexagon
        //Converts x and y to q, r, and s
        HexCube clickedHex = pixelToHex(x, y);
        double q = clickedHex.q;
        double r = clickedHex.r;
        double s = clickedHex.s;

        // Ensures whether the clicked hex is under the board limits else the function exits
        if (Math.abs(q) > base || Math.abs(r) > base || Math.abs(s) > base) {
            return; // Ignore clicks outside the hexagonal region
        }
        //Debug print info
        System.out.println("Clicked at: (" + x + ", " + y + "), Hex: (q=" + q + ", r=" + r + ")");

        //This checks if the move is valid, returns true if empty else false
        if (moveValidator.isValidMove(q, r, hexStatus, player.getCurrentPlayer())) {

            //Uses [(int)q + base][(int)r + base] to adjust for negative indexes
            hexStatus[(int) q + base][(int) r + base] = currentPlayer;


            // Find the correct hexagon corners and creates a hexCube to represent the clicked hex
            HexCube hex = new HexCube(q, r, -q - r);
            ArrayList<Point> corners = HexCube.polygonCorners(hex, primaryX, primaryY, sizeOfHex);

            // Fill the correct hexagon
            drawHexagon(gc, corners, currentPlayer.equals("Red") ? javafx.scene.paint.Color.RED : Color.BLUE);

            boolean captureOccurred = captureHandler.checkAndCapture(q, r, hexStatus, currentPlayer, gc);

            if (captureOccurred) {
                // If a capture has occurred, print a message showing which player captured pieces
                System.out.println(currentPlayer + " captured pieces!");

                // Update the board UI after the capture
                updateBoardUI(gc);

                // Grant the current player an extra turn if a capture occurred
                Player.grantExtraTurn();
            } else {
                // If the move is invalid, print an error message
                System.out.println("Invalid move!");
            }
        }
    }


    private void drawHexagon(GraphicsContext gc, ArrayList<Point> corners, Color color) {
        //.strokepolygon() requires separate x and y arrays of coordinates since it doesn't accept ArrayList<point>
        double[] xPoints = new double[6]; //Stores x coordinates of hexagon corners
        double[] yPoints = new double[6]; //Stores y coordinates of hexagon corners

        for (int i = 0; i < 6; i++) {
            xPoints[i] = corners.get(i).x; // Fetches x-coordinate from ArrayList
            yPoints[i] = corners.get(i).y; // Fetches y-coordinate from ArrayList
        }

        //This fills the hexagon with its respective colour
        gc.setFill(color);
        gc.fillPolygon(xPoints, yPoints, 6);
        gc.setStroke(Color.BLACK);

        //strokepolygon() takes X and Y coordinates array and number of points(i.e. 6 (Hexagon))
        gc.strokePolygon(xPoints, yPoints, 6);
    }

    // Removes captured stones from the board and redraws them as empty (light gray)
    public void removeStones(List<int[]> capturedStones, GraphicsContext gc) {
        // Loop through each captured stone
        for (int[] hex : capturedStones) {
            hexStatus[hex[0]][hex[1]] = null; // Clear the logical status of the hex (i.e., make it empty)

            // Calculate the actual coordinates of the hex to redraw
            int q = hex[0] - base;
            int r = hex[1] - base;
            int s = -q - r;

            // Create a HexCube object using the coordinates
            HexCube hexCube = new HexCube(q, r, s);

            // Get the corners of the hexagon to draw it correctly
            ArrayList<Point> corners = HexCube.polygonCorners(hexCube, 410, 345, sizeOfHex);

            // Redraw the hexagon as light gray (empty)
            drawHexagon(gc, corners, Color.LIGHTGRAY);
        }
    }

    // Updates the whole board UI by redrawing all hexagons
    void updateBoardUI(GraphicsContext gc) {
        // Go through each position in the hexStatus grid
        for (int qIndex = 0; qIndex < hexStatus.length; qIndex++) {
            for (int rIndex = 0; rIndex < hexStatus[qIndex].length; rIndex++) {
                // Calculate the actual Q and R coordinates for the hex
                int actualQ = qIndex - base;
                int actualR = rIndex - base;
                int s = -actualQ - actualR;

                // Skip hexagons that are out of bounds in the hexagonal grid
                if (Math.abs(actualQ) > base || Math.abs(actualR) > base || Math.abs(s) > base) {
                    continue; // Just skip the ones out of bounds
                }

                // If this hex is empty, remove it from the board (show empty hex)
                if (hexStatus[qIndex][rIndex] == null) {
                    removeStoneFromBoard(gc, qIndex, rIndex); // Call the method to remove this hex
                }
            }
        }
    }

    // Removes a single stone from the board and redraws it as empty
    private void removeStoneFromBoard(GraphicsContext gc, int q, int r) {
        // Convert array index (q, r) to actual hex coordinates
        int actualQ = q - base;
        int actualR = r - base;

        // Create a HexCube object for the given hex coordinates
        HexCube hex = new HexCube(actualQ, actualR, -actualQ - actualR);

        // Get the corners of the hexagon to draw it
        ArrayList<Point> corners = HexCube.polygonCorners(hex, primaryX, primaryY, sizeOfHex);

        // Redraw the hexagon as light gray (just like empty)
        drawHexagon(gc, corners, Color.LIGHTGRAY);  // Paint it over with gray color
    }



    public static class HexCube {
        final double q;
        final double r;
        private final double s;

        public HexCube(double q, double r, double s) { //constructor
            this.q = q;
            this.r = r;
            this.s = s;
        }
        //Method finds and returns the six corner points of a hexagon in the ArrayList<Point> form
        public static ArrayList<Point> polygonCorners(HexCube hex, double startX, double startY, double size) {

            //An Empty array List for corners is declared to store six corners of one hexagon
            ArrayList<Point> corners = new ArrayList<>();

            double x = startX + size * (3.0 / 2 * hex.q); //Converts cube coordinates to pixels
            double y = startY + size * Math.sqrt(3) * (hex.r + hex.q / 2.0);//Converts cube coordinates to pixels

            //Method uses trigonometry (cos, sin) to calculate the six corner points so that each corner is 60 degrees apart
            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(60 * i);
                corners.add(new Point((int) (x + size * Math.cos(angle)), (int) (y + size * Math.sin(angle))));
            }
            return corners;
        }
    }

    public void updateTurnIndicator() {
        renderer.updateTurn(player.getCurrentPlayer()); // Ensure Renderer is used
    }

    public String[][] getHexStatus() {
        return hexStatus;
    }



    HexCube pixelToHex(double x, double y) {
        // Takes the coordinates from the mouse click and converts it to q and r coordinates
        double q = (2.0 / 3 * (x - 410)) / sizeOfHex; // (2.0/3) accounts for horizontal hex spacing and (x - 410) for centering and multiplies because every column is 1.5 times hex width apart
        double r = (-1.0 / 3 * (x - 410) + Math.sqrt(3) / 3 * (y - 345)) / sizeOfHex; // The adjustment of x position affects the y position so -1 subtracts a fraction of x from y or r coordinate
        // Hexagons are vertically spaced by sqrt(3)/2
        return hexRound(q, r);
    }
    private HexCube hexRound(double q, double r) { // The grid only supports whole number coordinates and q and r are calculated as floating point so hexRound rounds the floating point number off to nearest hex grid position

        double s = -q - r; //Calculates third coordinate in cube
        //This rounds up the coordinates to nearest integer
        int intQ = (int) Math.round(q);
        int intR = (int) Math.round(r);
        int intS = (int) Math.round(s);

        //This finds out the difference introduced due to the rounding off
        double qDiff = Math.abs(intQ - q);
        double rDiff = Math.abs(intR - r);
        double sDiff = Math.abs(intS - s);

        //Keeping the q+r+s = 0 condition always under check, the largest rounding error is corrected by adjusting the corresponding coordinate
        if (qDiff > rDiff && qDiff > sDiff) {
            intQ = -intR - intS;
        } else if (rDiff > sDiff) {
            intR = -intQ - intS;
        }

        //Return the corrected coordinates
        return new HexCube(intQ, intR, -intQ - intR);
    }
}

