import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.util.ArrayList;
//Generates the hexagonal grid using cube coordinates (q, r, s). Draws hexagons using gc.strokePolygon().
public class Board {
    private final int base = 6; // Base-7 Hexagonal Grid (N=6 means distance from center)
    private final double sizeOfHex = 30;  // Size of each hexagon
    private Renderer renderer; // Store Renderer instance

    public Board(Renderer renderer) {
        this.renderer = renderer; // Assign Renderer
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
            drawHexagon(gc, hexagon);
        }
    }


    private void drawHexagon(GraphicsContext gc, ArrayList<Point> corners) {
        //.strokepolygon() requires separate x and y arrays of coordinates since it doesn't accept ArrayList<point>
        double[] xPoints = new double[6]; //Stores x coordinates of hexagon corners
        double[] yPoints = new double[6]; //Stores y coordinates of hexagon corners

        for (int i = 0; i < 6; i++) {
            xPoints[i] = corners.get(i).x; // Fetches x-coordinate from ArrayList
            yPoints[i] = corners.get(i).y; // Fetches y-coordinate from ArrayList
        }

        //strokepolygon() takes X and Y coordinates array and number of points(i.e. 6 (Hexagon))
        gc.strokePolygon(xPoints, yPoints, 6);
    }



    public static class HexCube {
        private int q, r, s;

        public HexCube(int q, int r, int s) { //constructor
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
}

