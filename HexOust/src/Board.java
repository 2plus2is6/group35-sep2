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
}