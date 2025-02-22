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
}

