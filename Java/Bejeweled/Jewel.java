import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.input.MouseEvent;
import java.util.Random;
  
public class Jewel extends Group {

    private static Random r = new Random();
    protected Shape node;
    protected Shape shadow;
    public Bejeweled app;
    public boolean isSelected;
    public boolean animated;
    public int x;
    public int y;
    public int shape;
    public int gridx;
    public int gridy;
    public Color normalColor;
    public static final int SHADOW_OFFSET = 2;
    public static final int SIZE = 40;
    public static final int OVAL = 0;
    public static final int PARALLELOGRAM = 1;
    public static final int PENTAGON = 2;
    public static final int RHOMBUS = 3;
    public static final int SQUARE = 4;
    public static final int TRAPEZOID = 5;
    public static final int TRIANGLE = 6;
    public static final Color[] polygonColors = new Color[] {
        Color.GREEN, // oval
        Color.BROWN, // parallelogram
        Color.PURPLE, // pentagon
        Color.RED, // rhombus
        Color.DARKORANGE, // square
        Color.YELLOW, // trapezoid
        Color.BLUE // triangle
    };
    public static final int[][] polygons = new int[][] {
        { 0 }, // not a polygon
        { 2, 40,    22, 20,   47, 20,   27, 40 }, // parallelogram
        { 25, 45,   10, 30,   17, 15,   33, 15,   40, 30 }, // pentagon
        { 8, 30,    25, 15,   42, 30,   25, 45 }, // rhombus
        { 38, 15,   13, 15,   13, 40,   38, 40 }, // square
        { 30, 20,   20, 20,   10, 40,   40, 40 }, // trapezoid
        { 10, 45,   25, 15,   40, 45 } // triangle
    };
    public static final int[] polygonPoints = new int[] { 0, 4, 5, 4, 4, 4, 3 };
    public static Jewel selected = null;

    public Jewel(int x, int y){
        isSelected = false;
        animated = false;
        this.x = x;
        this.y = y;
        randomizeJewel();
    }
  
    public Jewel(int x, int y, int shape){
        isSelected = false;
        animated = false;
        this.x = x;
        this.y = y;
        buildShape(shape);
    }

    public void buildShape(int s) {
        this.shape = s;
        normalColor = polygonColors[shape];
        switch (shape) {
            case OVAL:
                node = new Circle(x+24,y+30,14);
                break;
            case PARALLELOGRAM:
            case PENTAGON:
            case RHOMBUS:
            case SQUARE:
            case TRAPEZOID:
            case TRIANGLE:
                double[] points = new double[polygonPoints[shape]*2];
                for (int i = 0; i < polygonPoints[shape]*2; i+=2) {
                    points[i] = x + polygons[shape][i];
                    points[i+1] = y + polygons[shape][i+1];
                }
                node = new Polygon( points );
                break;
        }

        switch (shape) {
            case OVAL:
                shadow = new Circle(x+24-SHADOW_OFFSET,y+30-SHADOW_OFFSET,14);
                break;
            case PARALLELOGRAM:
            case PENTAGON:
            case RHOMBUS:
            case SQUARE:
            case TRAPEZOID:
            case TRIANGLE:
                double[] points = new double[polygonPoints[shape]*2];
                for (int i = 0; i < polygonPoints[shape]*2; i+=2) {
                    points[i] = x + polygons[shape][i] - SHADOW_OFFSET;
                    points[i+1] = y + polygons[shape][i+1] - SHADOW_OFFSET;
                }
                shadow = new Polygon( points );
                break;
        }
    }

    public void randomizeJewel() {
        buildShape(r.nextInt(7));
    }

    public void draw() {
        if (animated) {
            animated = false;
            node.setFill(Color.GRAY);
        } else {
            shadow.setFill(Color.BLACK);
            node.setFill(normalColor);
        }
    }

    public void drawSelected() {
        node.setFill(Color.PINK);
    }

    public void activateAnimation() {
        animated = true;
    }

    public Shape getShape() {
        return node;
    }

    public Shape getShadow() {
        return shadow;
    }

    public void setShape(Shape s) {
        node = s;
    }

    public void setApp(Bejeweled gui) {
        app = gui;
    }
  
    public void setGrid(int gx, int gy){ 
        gridx = gx;
        gridy = gy;
    }
    
    public boolean isAdjacent(Jewel g){
        if((this.gridx == g.gridx && Math.abs(this.gridy - g.gridy)==1)||
           (Math.abs(this.gridx - g.gridx)==1 && this.gridy ==g.gridy)){
            return true;
        } else {
            return false;
        }
    }

    public void fireClickEvent() {
        if (selected == null) {
            // selected a jewel
            selected = this;
            isSelected = true;
            drawSelected();
        } else {
            Jewel j = selected;
            if (!isSelected && selected.isAdjacent(this)) {
                // swapped a jewel
                selected = null;
                app.performSwap(j, this);
            } else {
                // deselected a jewel
                selected = null;
                isSelected = false;
                j.draw();
            }
        }
    }

    public String asString() {
        return "Jewel[" + gridx + "][" + gridy + "] at (" + x + ", " + y + ") = " + shape;
    }
}
