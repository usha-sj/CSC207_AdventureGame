package views;

import AdventureModel.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;


/**
 * The start screen so users can pick to start a new game or load an old save
 */
public class StartView {
    /** The stage for the modal window */
    private Stage stage;
    /** The AdventureGameView this was called from */
    AdventureGameView modelView;
    /** The MusicPlayer for the background music */
    MediaPlayer music;
    /** The GridPane for the view */
    private GridPane gridPane = new GridPane();
    /** The different save and Load buttons */
    Button startButton, loadButton;
    /** The image to display */
    ImageView startView;

    /**
     * Initializes the entire view.
     *
     * @param model The current AdventureGame for the adventure
     * @param view The current AdventureGameView for the adventure
     */
    public StartView(AdventureGame model, AdventureGameView view) {
        // note that the buttons in this view are not accessible!!
        this.modelView = view;
        Stage stage = new Stage();
        this.stage = stage;
        stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle("Walter's World");

        // GridPane, anyone?
        gridPane.setPadding(new Insets(20));
        gridPane.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#000000"),
                new CornerRadii(0),
                new Insets(0)
        )));

        //Three columns, four rows for the GridPane
        ColumnConstraints column1 = new ColumnConstraints(150);
        ColumnConstraints column2 = new ColumnConstraints(650);
        ColumnConstraints column3 = new ColumnConstraints(150);
        column3.setHgrow(Priority.SOMETIMES); //let some columns grow to take any extra space
        column1.setHgrow(Priority.SOMETIMES);
        column1.setHalignment(HPos.CENTER);
        column2.setHalignment(HPos.CENTER);
        column3.setHalignment(HPos.CENTER);

        // Row constraints
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints(550);
        RowConstraints row3 = new RowConstraints();
        // Adding a new row compared to original A2 game
        RowConstraints row4 = new RowConstraints(150);
        row1.setVgrow( Priority.SOMETIMES );
        row3.setVgrow( Priority.SOMETIMES );

        gridPane.getColumnConstraints().addAll(column1, column2, column1);
        gridPane.getRowConstraints().addAll(row1, row2, row1, row4);

        // add sound
        Media special = new Media(new File("start/sounds/start.mp3").toURI().toString());
        music = new MediaPlayer(special);
        music.play();
        // Welcome label
        Label welcomeLabel =  new Label("Welcome to Walter's World!");
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.setStyle("-fx-text-fill: white;");
        welcomeLabel.setFont(new Font("Arial", 20));
        // Info label
        Label infoLabel =  new Label("Uh-oh! You've been transported to an alternate reality by Walter. Fight Trolls and find your way back home!");
        infoLabel.setAlignment(Pos.CENTER);
        infoLabel.setStyle("-fx-text-fill: white;");
        infoLabel.setFont(new Font("Arial", 20));

        // Buttons
        startButton = new Button("Start Game!");
        startButton.setId("Start Game!");
        customizeButton(startButton, 200, 50);
        makeNodeAccessible(startButton, "Start Game!", "This button starts a new game.", "This button starts a new game. Click it in order to begin playing!");
        startButton.setPrefSize(200,50);
        startButton.setContentDisplay(ContentDisplay.TOP);
        startButton.setOnAction(e ->{
            music.pause();
            new PlayerSelectionView(model, stage);
            this.stage.close();
        });

        // Give the player the option of loading an old game
        loadButton = new Button("Load Game");
        loadButton.setId("Load Game");
        customizeButton(loadButton, 200, 50);
        makeNodeAccessible(loadButton, "Load Button", "This button loads a game from a file.", "This button loads the game from a file. Click it in order to load a game that you saved at a prior date.");
        addLoadEvent();

        // Walter Image
        Image startIm = new Image("start/morpheus.png");
        startView = new ImageView(startIm);
        startView.setFitHeight(400);
        startView.setFitWidth(300);
        startView.setPreserveRatio(true);
        VBox startPane = new VBox(startView);
        startPane.setAlignment(Pos.CENTER);
        startPane.setStyle("-fx-background-color: #000000;");

        HBox welcomeMsg = new HBox();
        welcomeMsg.getChildren().addAll(welcomeLabel);
        welcomeMsg.setAlignment(Pos.CENTER);

        HBox startButtons = new HBox();
        startButtons.getChildren().addAll(startButton, loadButton);
        startButtons.setAlignment(Pos.CENTER);
        startButtons.setSpacing(10);

        //add all the widgets to the GridPane
        gridPane.add( welcomeLabel, 0, 0, 3, 1 );  // Add label
        gridPane.add( infoLabel, 0, 2, 3, 1 );  // Add label
        gridPane.add( startPane, 0, 1, 3, 1 );  // Add start view
        gridPane.add(startButtons, 0, 3, 3, 1);

        // Render everything
        var scene = new Scene(gridPane, 1000, 800);
        scene.setFill(Color.BLACK);
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.stage.showAndWait();
    }

    /**
     * Customizes the given button to have consistent styles
     *
     * @param inputButton the button to make stylish :)
     * @param w width
     * @param h height
     */
    private void customizeButton(Button inputButton, int w, int h) {
        inputButton.setPrefSize(w, h);
        inputButton.setFont(new Font("Arial", 20));
        inputButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white; -fx-cursor: hand;");
    }

    /**
     * Sets accessibility parameters for a given node
     *
     * @param node the node to add screenreader hooks to
     * @param role ARIA role
     * @param name ARIA name
     * @param shortString ARIA accessible text
     * @param longString ARIA accessible help text
     */
    public static void makeNodeAccessible(Node node, AccessibleRole role, String name, String shortString, String longString) {
        node.setAccessibleRole(role);
        node.setAccessibleRoleDescription(name);
        node.setAccessibleText(shortString);
        node.setAccessibleHelp(longString);
        node.setFocusTraversable(true);
    }

    /**
     * Overloaded method for convenience with default role of BUTTON
     *
     * @param node the node to add screenreader hooks to
     * @param name ARIA name
     * @param shortString ARIA accessible text
     * @param longString ARIA accessible help text
     */
    public static void makeNodeAccessible(Node node, String name, String shortString, String longString) {
        makeNodeAccessible(node, AccessibleRole.BUTTON, name, shortString, longString);
    }

    /**
     * This method adds the event handler for the loadButton
     */
    public void addLoadEvent() {
        loadButton.setOnAction(e -> {
            gridPane.requestFocus();
            LoadView loadView = new LoadView(this.modelView, false);
            modelView.voice = modelView.initVoice();
            if (loadView.selectGameLabel.getText().trim().endsWith(".ser")) {
                this.stage.close();
            }
        });
    }
}
