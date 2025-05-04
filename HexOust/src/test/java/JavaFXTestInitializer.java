import javafx.embed.swing.JFXPanel;

public class JavaFXTestInitializer {
    private static boolean isInitialized = false;

    public static void initialize() {
        if (!isInitialized) {
            new JFXPanel(); // Initializes the JavaFX toolkit
            isInitialized = true;
        }
    }
}