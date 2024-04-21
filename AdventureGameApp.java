import AdventureModel.AdventureGame;
import javafx.application.Application;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.stage.Stage;
import views.AdventureGameView;
import java.io.IOException;

/**
 * Class AdventureGameApp.
 */
public class AdventureGameApp extends Application {
    /** The AdventureGame for the application */
    AdventureGame model;
    /** The AdventureGameView for the application */
    AdventureGameView view;

    /**
     * The entry point for the program
     *
     * @param args The program arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
    * JavaFX is a Framework, and to use it we will have to
    * respect its control flow!  To start the game, we need
    * to call "launch" which will in turn call "start" ...
    *
    * @param primaryStage The stage for the entire application
    * @throws IOException throws if there is an IOException while running the program
    */
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.model = new AdventureGame("TinyGame");
        this.view = new AdventureGameView(model, primaryStage);
    }
}
