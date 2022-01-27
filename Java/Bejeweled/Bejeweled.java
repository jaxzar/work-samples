/**
 * The main class for a basic GUI for drawing graphical objects.
 * @author Timothy Baker
 * @version May 4, 2020
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bejeweled extends Application {

    public static final int BUFFER = 10;
    public static final int CANVAS_SIZE = 350;
    public static final int GRID_SIZE = 8;
    public static final int ANIMATION_TIME = 350;
    private Jewel[][] grid;
    private static JewelEventManager jewelMgr;
    private Pane pane;

    /**
    * Constructs a new GUI by calling
    * the Application superclass constructor
    */
    public Bejeweled() {
        super();
    }

    /**
    * The required JavaFX start method
    * @param primaryStage the primary stage for this application
    * @see javafx.application.Application
    */
    public void start(Stage primaryStage) {

        pane = new Pane();
        jewelMgr = new JewelEventManager();
        grid = new Jewel[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++){
                Jewel g = new Jewel(i*Jewel.SIZE + BUFFER, j*Jewel.SIZE + BUFFER);
                g.setGrid(i,j);
                g.setApp(this);
                g.draw();
                grid[i][j] = g;
                jewelMgr.makeClickable(g);
                pane.getChildren().add(g.getShadow());
                pane.getChildren().add(g.getShape());
            }
        }

        Scene scene = new Scene(pane, CANVAS_SIZE, CANVAS_SIZE);
        primaryStage.setTitle("Bejeweled");
        primaryStage.getIcons().add(new Image("file:bejeweled.png"));
        primaryStage.setScene(scene);
        primaryStage.show();  

        checkForMatch();
    }

    // redraw the entire grid
    public void redraw() {
        pane.getChildren().clear();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++){
                grid[i][j].draw();
                pane.getChildren().add(grid[i][j].getShadow());
                pane.getChildren().add(grid[i][j].getShape());
            }
        }
    }

    public void performSwap(Jewel obj1, Jewel obj2) {
        int temp = obj2.shape;

        obj2.buildShape(obj1.shape);
        jewelMgr.makeClickable(obj2);

        obj1.buildShape(temp);
        jewelMgr.makeClickable(obj1);

        redraw();
        checkForMatch();
    }

    public void checkForMatch(){
        List<Jewel> matches = new ArrayList<Jewel>(12);

        //horizontal check
        for (int i = 0; i < GRID_SIZE;i++){
            for (int j = 0; j < GRID_SIZE - 2; j++){
                if (grid[i][j].shape == grid[i][j+1].shape &&
                    grid[i][j+1].shape == grid[i][j+2].shape) {
                    if (!matches.contains(grid[i][j]))
                        matches.add(grid[i][j]);
                    if (!matches.contains(grid[i][j+1]))
                        matches.add(grid[i][j+1]);
                    if (!matches.contains(grid[i][j+2]))
                        matches.add(grid[i][j+2]);
                }
            }
        }

        //vertical check
        for (int j = 0; j < GRID_SIZE; j++){
            for (int i = 0; i < GRID_SIZE-2;i++){
                if (grid[i][j].shape == grid[i+1][j].shape &&
                    grid[i+1][j].shape == grid[i+2][j].shape) {
                    if (!matches.contains(grid[i][j]))
                        matches.add(grid[i][j]);
                    if (!matches.contains(grid[i+1][j]))
                        matches.add(grid[i+1][j]);
                    if (!matches.contains(grid[i+2][j]))
                        matches.add(grid[i+2][j]);
                }
            }
        }

        for (Jewel j: matches) {
            j.activateAnimation();
        }
        redraw();

        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(ANIMATION_TIME);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

                for (Jewel j: matches) {
                    j.randomizeJewel();
                    jewelMgr.makeClickable(j);
                }

                redraw();
            }
        });
        new Thread(sleeper).start();
    }

    public static class JewelEventManager {

        Map<Shape,Jewel> eventMap;

        public JewelEventManager() {
            eventMap = new HashMap<Shape,Jewel>();
        }

        public void makeClickable(Jewel j) {
            j.getShape().setOnMousePressed(jewelClickHandler);
            eventMap.put(j.getShape(),j);
        }

        EventHandler<MouseEvent> jewelClickHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                Node n = (Node)e.getSource();
                if (n instanceof Shape) {
                    Jewel j = eventMap.get((Shape)n);
                    j.fireClickEvent();
                }
            }
        };
    }

    /**
    * the main method to start this program
    * @param args Arguments
    */
    public static void main(String[] args) {
        // launches this application
        launch(args);
    }

}
