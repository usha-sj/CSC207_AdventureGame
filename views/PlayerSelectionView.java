package views;

import AdventureModel.*;
import AdventureModel.Archer;
import AdventureModel.Elf;
import AdventureModel.Warrior;
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
import javafx.stage.Stage;
import java.io.File;


/**
 * The start screen so users can pick their player type of the current adventure game.
 */
public class PlayerSelectionView {
    /** The stage for the modal window */
    private Stage stage;
    /** The GridPane for the view */
    private GridPane gridPane = new GridPane();
    /** The different player types */
    Button warriorButton, elfButton, archerButton; //buttons


    /**
     * Initialize the PlayerSelectionView
     *
     * @param model The current AdventureGameView for the adventure
     * @param stage The stage for the view to be loaded on
     */
    public PlayerSelectionView(AdventureGame model, Stage stage) {
        this.stage = stage;
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
        RowConstraints row2 = new RowConstraints();
        RowConstraints row3 = new RowConstraints(550);
        RowConstraints row4 = new RowConstraints();
        row1.setVgrow( Priority.SOMETIMES );
        row3.setVgrow( Priority.SOMETIMES );

        gridPane.getColumnConstraints().addAll(column1, column2, column1);
        gridPane.getRowConstraints().addAll(row1, row1, row3, row1);


        // add sound
        Media special = new Media(new File("start/sounds/players.mp3").toURI().toString());
        MediaPlayer sound = new MediaPlayer(special);
        sound.play();

        // Warrior Player type
        Image warImg = new Image("start/players/warrior.jpeg");
        ImageView warImgView = new ImageView(warImg);
        warImgView.setFitHeight(400);
        warImgView.setFitWidth(200);
        warImgView.setPreserveRatio(true);

        warriorButton = new Button("Warrior");
        warriorButton.setId("Warrior");
        customizeButton(warriorButton, 100, 200);
        makeNodeAccessible(warriorButton, "Warrior", "This button chooses the Warrior player.", "This button chooses the Warrior player. Click it in order to select your player as Warrior, which has the special move of: Fists.");
        warriorButton.setGraphic(warImgView);
        warriorButton.setContentDisplay(ContentDisplay.TOP);
        warriorButton.setOnAction(e ->{
            model.player = new Warrior(model.rooms.get("1"));
            sound.pause();
            this.stage.close();
        });

        // Elf Player Type
        Image elfImg = new Image("start/players/elf.jpeg");
        ImageView elfImgView = new ImageView(elfImg);
        elfImgView.setFitHeight(400);
        elfImgView.setFitWidth(200);
        elfImgView.setPreserveRatio(true);

        elfButton = new Button("Elf");
        elfButton.setId("Elf");
        customizeButton(elfButton, 100, 400);
        makeNodeAccessible(elfButton, "Elf", "This button chooses the Elf player.", "This button chooses the Elf player. Click it in order to select your player as Elf, which has the special move of: Magic.");
        elfButton.setPrefSize(100,300);
        elfButton.setGraphic(elfImgView);
        elfButton.setContentDisplay(ContentDisplay.TOP);
        elfButton.setOnAction(e ->{
            model.player = new Elf(model.rooms.get("1"));
            sound.pause();
            this.stage.close();
        });

        // Archer Player Type
        Image arcImg = new Image("start/players/archer.jpeg");
        ImageView arcImgView = new ImageView(arcImg);
        arcImgView.setFitHeight(400);
        arcImgView.setFitWidth(200);
        arcImgView.setPreserveRatio(true);

        archerButton = new Button("Archer");
        archerButton.setId("Archer");
        customizeButton(archerButton, 100, 50);
        makeNodeAccessible(archerButton, "Archer", "This button chooses the Archer player.", "This button chooses the Archer player. Click it in order to select your player as Archer, which has the special move of: Arrows.");
        archerButton.setGraphic(arcImgView);
        archerButton.setContentDisplay(ContentDisplay.TOP);
        archerButton.setOnAction(e ->{
            model.player = new Archer(model.rooms.get("1"));
            sound.pause();
            this.stage.close();
        });

        // Welcome label
        Label welcomeLabel =  new Label("Welcome to Walter's World!");
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.setStyle("-fx-text-fill: white;");
        welcomeLabel.setFont(new Font("Arial", 20));
        // Pick a player label
        Label selectLabel =  new Label("Pick your player! Each player has a special skill to fight Trolls!");
        selectLabel.setAlignment(Pos.CENTER);
        selectLabel.setStyle("-fx-text-fill: white;");
        selectLabel.setFont(new Font("Arial", 20));

        HBox welcomeMsg = new HBox();
        welcomeMsg.getChildren().addAll(welcomeLabel);
        welcomeMsg.setAlignment(Pos.CENTER);

        HBox playerButtons = new HBox();
        playerButtons.getChildren().addAll(warriorButton, elfButton, archerButton);
        playerButtons.setSpacing(10);
        playerButtons.setAlignment(Pos.CENTER);

        //add all the widgets to the GridPane
        gridPane.add( welcomeLabel, 0, 0, 3, 1 );  // Add label
        gridPane.add( playerButtons, 0, 2, 3, 1 );  // Add buttons
        gridPane.add( selectLabel, 0, 3, 3, 1 );  // Add label

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
        inputButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-cursor: hand;");
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
}
