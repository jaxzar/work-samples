/*
 * NumberizerApp.java
 */

package numberizer;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class NumberizerApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new NumberizerView(this));
        this.getMainFrame().setSize(356, 322);
        this.getMainFrame().setResizable(false);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of NumberizerApp
     */
    public static NumberizerApp getApplication() {
        return Application.getInstance(NumberizerApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(NumberizerApp.class, args);
    }
}
