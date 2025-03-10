import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
//Generates the hexagonal grid using cube coordinates (q, r, s). Draws hexagons using gc.strokePolygon().
public class Board {
    private final int base = 6; // Base-7 Hexagonal Grid (N=6 means distance from center)
    private final double sizeOfHex = 30;  // Size of each hexagon
    private final Renderer renderer; // Store Renderer instance
    private final Player player;
    private final String[][] hexStatus; //Stores info about a hexagon whether it is filled with a colour or not
    private final MoveValidator moveValidator; //Checks hexStatus before allowing a move

    public Board(Renderer renderer, Player player) {
        this.renderer = renderer; // Assign Renderer
        this.player = player;
        this.moveValidator = new MoveValidator();
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

    public void fillHex(GraphicsContext gc, double x, double y) { //Handles player's click on the board, receives x and y position and fills the hexagon
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
        if (moveValidator.isValidMove(q, r, hexStatus)) {
            //Stores the move in hexStatus
            String currentPlayer = player.getCurrentPlayer();

            //Uses [(int)q + base][(int)r + base] to adjust for negative indexes
            hexStatus[(int)q + base][(int)r + base] = currentPlayer;


            // Find the correct hexagon corners and creates a hexCube to represent the clicked hex
            HexCube hex = new HexCube(q, r, -q - r);
            ArrayList<Point> corners = HexCube.polygonCorners(hex, 410, 345, sizeOfHex);

            // Fill the correct hexagon
            drawHexagon(gc, corners, currentPlayer.equals("Red") ? javafx.scene.paint.Color.RED : Color.BLUE);

            // Switch turns
            player.switchTurn();
            renderer.updateTurn(player.getCurrentPlayer());
        } else {
            renderer.showInvalidMoveMessage();
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



    public static class HexCube {
        private final double q;
        private final double r;
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

    private HexCube pixelToHex(double x, double y) { 
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

