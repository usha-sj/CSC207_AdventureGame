package views;

import AdventureModel.*;
import Trolls.*;
import com.sun.speech.freetts.*;
import com.sun.speech.freetts.audio.AudioPlayer;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.scene.control.ProgressBar;
import javafx.scene.AccessibleRole;
import java.io.File;
import java.util.*;

/**
 * This is the Class that will visualize the model.
 */
public class AdventureGameView {
    /** The AdventureGame model */
    AdventureGame model;
    /** Stage on which all is rendered */
    Stage stage;
    /** Buttons used in the view */
    Button saveButton, loadButton, helpButton, settingsButton;
    /** Used to determine if the help menu is on display */
    Boolean helpToggle = false;
    /** Used to determine if the combat menu is on display */
    Boolean combatToggle = false;
    /** The main GridPane for the view which holds all the other Nodes */
    GridPane gridPane = new GridPane();
    /** The label holding the room description */
    Label roomDescLabel = new Label();
    /** The VBox holding the room items */
    VBox objectsInRoom = new VBox();
    /** The VBox holding the inventory items */
    VBox objectsInInventory = new VBox();
    /** The ImageView holding the room image */
    ImageView roomImageView;
    /** The TextField for user input */
    TextField inputTextField;
    /** The Label for the player's current inventory */
    Label invLabel = new Label("Your Inventory");
    /** The Label for the objects in the current room */
    Label objLabel =  new Label("Objects in Room");
    /** The Label for the player's current health */
    Label statsLabel =  new Label("Player Health: ");
    /** The Label for the user input */
    Label commandLabel = new Label("What would you like to do?");
    /** The MediaPlayer which articulates pre-recorded room description */
    private MediaPlayer mediaPlayer;
    /** The boolean which determines if the media is playing */
    private boolean mediaPlaying;
    /** The label within the health bar to show the player's health */
    private Label currHealth;
    /** The progress bar to show the player's health */
    private ProgressBar playerHealth;
    /** To know if the background is set to white */
    public boolean backgroundIsWhite = false;
    /** To know if the font is set to serif */
    public boolean isSerif = false;
    /** To know if the roomPane is white */
    private boolean roomPaneIsWhite = false;
    /** To know if the text entry area is white */
    private boolean textEntryIsWhite = false;
    /** To know if the scroll pane is white */
    private boolean scIsWhite = false;
    /** To know the default font size */
    public int defaultFontSize = 16;
    /** The AdioPlayer for runtime-generated audio */
    protected AudioPlayer audioPlayer;
    /** The Thread that runs runtime-generated audio */
    private Thread thread;
    /** The Voice for runtime-generated audio */
    public Voice voice;

    /**
     * Initializes attributes for the AdventureGameView
     *
     * @param model the AdventureGame object to view graphically
     * @param stage the JavaFX stage object where all Nodes are rendered
     */
    public AdventureGameView(AdventureGame model, Stage stage) {
        this.model = model;
        this.stage = stage;
        this.voice = initVoice();
        // Render everything
        initUI();
        // Add event handlers for achievements
        this.model.getAchievements().forEach(a -> a.addEventHandler(AchievementEvent.ACHIEVEMENT_UNLOCKED, this::achievementUnlocked));
    }

    /**
     * This method sets the JavaFX stage for the game.
     */
    public void initUI() {
        this.stage.setTitle("Walter's World");

        //Inventory + Room items
        objectsInInventory.setSpacing(10);
        objectsInInventory.setAlignment(Pos.TOP_CENTER);
        objectsInRoom.setSpacing(10);
        objectsInRoom.setAlignment(Pos.TOP_CENTER);

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

        // Row constraints
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints(550);
        RowConstraints row3 = new RowConstraints();
        // Adding a new row compared to original A2 game to fit a player "stats" row.
        RowConstraints row4 = new RowConstraints(150);
        row1.setVgrow( Priority.SOMETIMES );
        row3.setVgrow( Priority.SOMETIMES );

        gridPane.getColumnConstraints().addAll(column1, column2, column1);
        gridPane.getRowConstraints().addAll(row1, row2, row1, row4);

        // Buttons
        var achievements = new ImageView("achievements/achievementAssets/trophy-icon.png");
        achievements.setId("Achievements");
        achievements.setPreserveRatio(true);
        achievements.setFitWidth(50);
        achievements.setStyle("-fx-cursor: hand;");
        makeNodeAccessible(achievements, "Achievements", "Open Achievements Menu.", "This button opens the achievements menu. Click it to view locked and unlocked achievements.");
        achievements.setAccessibleRole(AccessibleRole.BUTTON);
        achievements.setOnMouseClicked(e -> {
            stopArticulation();
            this.gridPane.requestFocus();
            var achievementView = new AchievementView(this);
        });

        saveButton = new Button("Save");
        saveButton.setId("Save");
        customizeButton(saveButton, 120, 50);
        makeNodeAccessible(saveButton, "Save Button", "This button saves the game.", "This button saves the game. Click it in order to save your current progress, so you can play more later.");
        addSaveEvent();

        loadButton = new Button("Load");
        loadButton.setId("Load");
        customizeButton(loadButton, 120, 50);
        makeNodeAccessible(loadButton, "Load Button", "This button loads a game from a file.", "This button loads the game from a file. Click it in order to load a game that you saved at a prior date.");
        addLoadEvent();

        helpButton = new Button("Help");
        helpButton.setId("Help");
        customizeButton(helpButton, 120, 50);
        makeNodeAccessible(helpButton, "Help Button", "This button gives game instructions.", "This button gives instructions on the game controls. Click it to learn how to play.");
        addInstructionEvent();

        settingsButton = new Button("Settings");
        settingsButton.setId("Settings");
        customizeButton(settingsButton, 120, 50);
        makeNodeAccessible(settingsButton, "Settings Button", "This button opens the game's settings.", "This button allows you to change the game's settings. Click it to modify specific aspects of the game.");
        addSettingsEvent();

        HBox topButtons = new HBox();
        topButtons.getChildren().addAll(achievements, saveButton, helpButton, loadButton, settingsButton);
        topButtons.setSpacing(10);
        topButtons.setAlignment(Pos.CENTER);

        inputTextField = new TextField();
        inputTextField.setFont(new Font("Arial", 16));
        inputTextField.setFocusTraversable(true);

        inputTextField.setAccessibleRole(AccessibleRole.TEXT_AREA);
        inputTextField.setAccessibleRoleDescription("Text Entry Box");
        inputTextField.setAccessibleText("Enter commands in this box.");
        inputTextField.setAccessibleHelp("This is the area in which you can enter commands you would like to play.  Enter a command and hit return to continue.");
        addTextHandlingEvent(); //attach an event to this input field

        // labels for inventory and room items
        objLabel =  new Label("Objects in Room");
        objLabel.setAlignment(Pos.CENTER);

        if (backgroundIsWhite) {
            objLabel.setStyle("-fx-text-fill: black;");
        } else {
            objLabel.setStyle("-fx-text-fill: white;");
        }
        objLabel.setFont(new Font("Arial", 16));

        invLabel =  new Label("Your Inventory");
        invLabel.setAlignment(Pos.CENTER);

        if (backgroundIsWhite) {
            invLabel.setStyle("-fx-text-fill: black;");
        } else {
            invLabel.setStyle("-fx-text-fill: white;");
        }
        invLabel.setFont(new Font("Arial", 16));

        // label for Player Stats label
        statsLabel.setAlignment(Pos.CENTER);
        if (backgroundIsWhite) {
            statsLabel.setStyle("-fx-text-fill: black;");
        } else {
            statsLabel.setStyle("-fx-text-fill: white;");
        }
        statsLabel.setFont(new Font("Arial", 16));
        GridPane.setHalignment(statsLabel, HPos.RIGHT);

        // Create progress bars to represent the player's stats (health)

        playerHealth = new ProgressBar();
        playerHealth.setPrefHeight(20);
        playerHealth.setPrefWidth(414);
        playerHealth.setStyle("-fx-accent: red;");
        GridPane.setHalignment(playerHealth, HPos.LEFT);

        gridPane.add(playerHealth, 1, 2, 2, 1);
        this.currHealth = new Label();
        this.currHealth.setAlignment(Pos.BOTTOM_CENTER);
        GridPane.setHalignment(this.currHealth, HPos.CENTER);
        GridPane.setValignment(this.currHealth, VPos.CENTER);
        this.currHealth.setStyle("-fx-text-fill: white;");;
        this.currHealth.setStyle("-fx-font-weight: bold;");
        this.currHealth.setPadding(Insets.EMPTY);
        gridPane.add(currHealth, 1, 2);
        updateHealth(currHealth, playerHealth);

        //add all the widgets to the GridPane
        gridPane.add(objLabel, 0, 0, 1, 1);  // Add label
        gridPane.add(topButtons, 1, 0, 1, 1);  // Add buttons
        gridPane.add(invLabel, 2, 0, 1, 1);  // Add label
        gridPane.add(statsLabel,0, 2, 1,1); // Add Character Stats label

        if (backgroundIsWhite) {
            commandLabel.setStyle("-fx-text-fill: black;");
        } else {
            commandLabel.setStyle("-fx-text-fill: white;");
        }
        commandLabel.setFont(new Font("Arial", 16));

        // Before we load the AdventureGameView, we need to display the start screen:
        new StartView(this.model, this);

        updateScene(""); //method displays an image and whatever text is supplied
        updateItems(); //update items shows inventory and objects in rooms

        // Adding the text area and submit button to a VBox
        VBox textEntry = new VBox();
        textEntry.setStyle("-fx-background-color: #000000;"); // changing to white
        textEntry.setPadding(new Insets(20, 20, 20, 20));
        textEntry.getChildren().addAll(commandLabel, inputTextField);
        textEntry.setSpacing(10);
        textEntry.setAlignment(Pos.CENTER);
        gridPane.add(textEntry, 0, 3, 3, 1);

        // Add event handlers for achievements
        this.model.getAchievements().forEach(a -> a.addEventHandler(AchievementEvent.ACHIEVEMENT_UNLOCKED, this::achievementUnlocked));

        // Render everything
        var scene = new Scene(gridPane, 1000, 800);
        scene.setFill(Color.BLACK);
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.stage.show();
    }

    /**
     * Updates the player's health bar
     *
     * @param currHealth the label to display the player's current health
     * @param playerHealth the progress bar to display the player's health
     */
    public void updateHealth(Label currHealth, ProgressBar playerHealth){
        if (this.model.player.health > this.model.player.maxHP){
            this.model.player.health = this.model.player.maxHP;
        }
        playerHealth.setProgress((double) this.model.player.health / this.model.player.maxHP);
        currHealth.setText(this.model.player.health + "/" + this.model.player.maxHP);

    }

    /**
     * This method updates the scene backgrounds to match accessibility needs
     */
    public void changeBackground() {
        if (backgroundIsWhite) {
            gridPane.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#000000"),
                new CornerRadii(0),
                new Insets(0))));
                SaveView.dialogVbox.setStyle("-fx-background-color: #000000;");
                LoadView.dialogVbox.setStyle("-fx-background-color: #000000;");
        }
        else {
            gridPane.setBackground(new Background(new BackgroundFill(
                    Color.valueOf("#FFFFFF"),
                    new CornerRadii(0),
                    new Insets(0))));
                    SaveView.dialogVbox.setStyle("-fx-background-color: #FFFFFF;");
                    LoadView.dialogVbox.setStyle("-fx-background-color: #FFFFFF;");
        }
        backgroundIsWhite = !backgroundIsWhite;
        VBox roomPane = new VBox(roomImageView,roomDescLabel);
        roomPane.setPadding(new Insets(10));
        roomPane.setAlignment(Pos.TOP_CENTER);
        if (!roomPaneIsWhite) {
            roomPane.setStyle("-fx-background-color: #FFFFFF;");
        } else {
            roomPane.setStyle("-fx-background-color: #000000;");
        }
        gridPane.add(roomPane, 1, 1);
        stage.sizeToScene();
        roomPaneIsWhite = !roomPaneIsWhite;

        if (roomPaneIsWhite) {
            objLabel.setStyle("-fx-text-fill: black;");
            invLabel.setStyle("-fx-text-fill: black;");
            roomDescLabel.setStyle("-fx-text-fill: black;");
            statsLabel.setStyle("-fx-text-fill: black;");
        } else {
            objLabel.setStyle("-fx-text-fill: white;");
            invLabel.setStyle("-fx-text-fill: white;");
            roomDescLabel.setStyle("-fx-text-fill: white;");
            statsLabel.setStyle("-fx-text-fill: white;");
        }

        // changing the colour of the text input label
        if (backgroundIsWhite) {
            commandLabel.setStyle("-fx-text-fill: black; -fx-background-color: #FFFFFF;");
        } else {
            commandLabel.setStyle("-fx-text-fill: white; -fx-background-color: #000000;");}
        commandLabel.setFont(new Font("Arial", 16));

        ScrollPane scO = new ScrollPane(objectsInRoom);
        scO.setPadding(new Insets(10));
        if (scIsWhite) {
            scO.setStyle("-fx-background: #000000; -fx-background-color:transparent;"); // about to switch these to white
        } else {
            scO.setStyle("-fx-background: #FFFFFF; -fx-background-color:transparent;"); // about to switch these to white
        }
        scO.setFitToWidth(true);
        gridPane.add(scO,0,1);

        ScrollPane scI = new ScrollPane(objectsInInventory);
        scI.setFitToWidth(true);
        if (scIsWhite) {
            scI.setStyle("-fx-background: #000000; -fx-background-color:transparent;");
        } else {
            scI.setStyle("-fx-background: #FFFFFF; -fx-background-color:transparent;");
        }
        gridPane.add(scI,2,1);
        scIsWhite = !scIsWhite;
    }

    /**
     * Updates the defaultFontSize to match accessibility needs
     */
    public void updateFontSize(int new_size) {defaultFontSize = new_size;}

    /**
     * Updates the font to match accessibility needs
     */
    public void changeFonts() {
        // save button
        if (isSerif) {
            saveButton.setFont(new Font("Arial", defaultFontSize));
            loadButton.setFont(new Font("Arial", defaultFontSize));
            helpButton.setFont(new Font("Arial", defaultFontSize));
            settingsButton.setFont(new Font("Arial", defaultFontSize));
            SettingsView.colourButton.setFont(new Font("Arial", defaultFontSize));
            SettingsView.fontSizeButton.setFont(new Font("Arial", defaultFontSize));
            SettingsView.fontButton.setFont(new Font("Arial", defaultFontSize));
        } else {
            saveButton.setFont(Font.font("Serif", defaultFontSize));
            loadButton.setFont(Font.font("Serif", defaultFontSize));
            helpButton.setFont(Font.font("Serif", defaultFontSize));
            settingsButton.setFont(Font.font("Serif", defaultFontSize));
            SettingsView.colourButton.setFont(Font.font("Serif", defaultFontSize));
            SettingsView.fontSizeButton.setFont(Font.font("Serif", defaultFontSize));
            SettingsView.fontButton.setFont(Font.font("Serif", defaultFontSize));
        }
        isSerif = !isSerif;
    }

    /**
     * Sets accessibility attributes for the provided Node object
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
     * Edits the given inputButton's attributes to provide common styles
     *
     * @param inputButton the button to make stylish :)
     * @param w width
     * @param h height
     */
    public static void customizeButton(Button inputButton, int w, int h) {
        inputButton.setPrefSize(w, h);
        inputButton.setFont(new Font("Arial", 16));
        inputButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white; -fx-cursor: hand"); // changed the colour to light blue
    }

    /**
     * Add an event handler to the myTextField attribute 
     *
     * Your event handler should respond when users 
     * hits the ENTER or TAB KEY. If the user hits 
     * the ENTER Key, strip white space from the
     * input to myTextField and pass the stripped 
     * string to submitEvent for processing.
     *
     * If the user hits the TAB key, move the focus 
     * of the scene onto any other node in the scene 
     * graph by invoking requestFocus method.
     */
    private void addTextHandlingEvent() {
        this.inputTextField.setOnAction(e -> {
            submitEvent(this.inputTextField.getText().trim());
            this.inputTextField.clear();});
        this.inputTextField.setOnKeyPressed(e -> {
            if (e.getCode() != KeyCode.TAB) return;
            this.objectsInRoom.requestFocus();
        });
    }

    /**
     * This method is called when an achievement is unlocked and handles the
     * visual and audible aspects of the achievement being unlocked.
     * @param event the AchievementEvent that was fired
     */
    private void achievementUnlocked(AchievementEvent event) {
        // Grab the achievement from the event and create a box to display it
        var achievement = this.model.getAchievement(event.getName());
        var box = makeAchievementVBox(achievement);
        box.setPrefSize(0, 40);
        box.setMaxSize(0, 65);
        box.setOnMouseClicked(e -> {
            stopArticulation();
            this.gridPane.requestFocus();
            var achievementView = new AchievementView(this);
        });
        Label lab1 = null;
        Label lab2 = null;
        // Add the box to the gridPane
        var children = box.getChildren();
        for (var child : children) {
            switch (child.getId()) {
                case "label1" -> lab1 = (Label) child;
                case "label2" -> lab2 = (Label) child;
            }
        }
        Label label1 = lab1;
        Label label2 = lab2;
        StackPane borderedImage = (StackPane) lab2.getGraphic();
        gridPane.add(box, 1, 2, 1, 1);
        GridPane.setHalignment(box, HPos.CENTER);
        // Animate the achievement box into the scene
        var timeIn = new Timeline();
        timeIn.getKeyFrames().add(new KeyFrame(Duration.seconds(1.95),
                new KeyValue(box.prefWidthProperty(), 300, Interpolator.EASE_BOTH),
                new KeyValue(box.maxWidthProperty(), 450, Interpolator.EASE_BOTH)));
        timeIn.setOnFinished(e -> {
            label1.setWrapText(true);
            label2.setWrapText(true);
            var timeOut = new Timeline();
            timeOut.getKeyFrames().add(
                            new KeyFrame(Duration.seconds(0.65),
                                    new KeyValue(box.maxWidthProperty(), 0, Interpolator.EASE_OUT),
                                    new KeyValue(label1.prefWidthProperty(), 0, Interpolator.EASE_OUT),
                                    new KeyValue(label2.prefWidthProperty(), 0, Interpolator.EASE_OUT),
                                    new KeyValue(borderedImage.opacityProperty(), 0, Interpolator.EASE_OUT)));
            timeOut.setOnFinished(e2 -> gridPane.getChildren().remove(box));
            // Give time to view the achievement box
            var pause = new PauseTransition(Duration.seconds(4));
            pause.setOnFinished(e1 -> {
                    box.minWidth(0);
                    box.setFillWidth(false);
                    label1.setWrapText(false);
                    label2.setWrapText(false);
                    label1.setPrefWidth(label1.getWidth());
                    label2.setPrefWidth(label2.getWidth());
                    label1.setMinWidth(0);
                    label2.setMinWidth(0);
                    timeOut.play();
                    });
            pause.play();
        });
        // Play the achievement unlocked sound
        String musicFile = "achievements/achievementAssets/achievement-unlocked.wav";
        Media sound = new Media(new File(musicFile).toURI().toString());
        var audioPlayer = new MediaPlayer(sound);
        audioPlayer.setVolume(1.25);
        audioPlayer.setRate(0.85);
        audioPlayer.play();
        var pause2 = new PauseTransition(Duration.seconds(2));
        pause2.setOnFinished(e -> audioPlayer.dispose());
        timeIn.play();
        pause2.play();
    }

    /**
     * This method creates a VBox to display an achievement.
     * @param a the achievement to display
     * @return the VBox containing the achievement details
     */
    public static VBox makeAchievementVBox(AdventureAchievement a) {
        var box = new VBox();
        box.setId(a.getName());
        box.setStyle("-fx-padding: 5 10 0; -fx-border-style: solid; -fx-border-width: 4; -fx-border-insets: -4;" +
                "-fx-border-radius: 10; -fx-border-color: #323440; -fx-background-color: #323440;");
        box.setOnMouseEntered(e -> box.setStyle(box.getStyle() + "; -fx-cursor: hand"));
        box.toFront();
        var label1 = new Label(a.getTier().toString() + " Achievement Unlocked: " + a.getName());
        label1.setFont(new Font("Bahnschrift SemiBold SemiCondensed", 16));
        label1.textFillProperty().setValue(Color.ALICEBLUE);
        label1.setId("label1");
        box.getChildren().add(label1);
        var image = new ImageView(a.getAssetFileName());
        image.setPreserveRatio(true);
        image.setFitWidth(40);
        var border = new ImageView("achievements/achievementAssets/" + a.getTier().toString().toLowerCase() + "-border.png");
        border.setPreserveRatio(true);
        border.setFitWidth(40);
        StackPane borderedImage = new StackPane();
        borderedImage.getChildren().addAll(image, border);
        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0);
        borderGlow.setOffsetX(0);
        borderGlow.setColor(switch (a.getTier()) {
            case Gold -> Color.GOLD;
            case Silver -> Color.SILVER;
            case Bronze -> Color.TAN;
        });
        borderGlow.setWidth(20);
        borderGlow.setHeight(40);
        borderedImage.setEffect(borderGlow);
        makeNodeAccessible(borderedImage, AccessibleRole.IMAGE_VIEW, "Achievement Icon", "Achievement Icon", "Achievement Icon");
        borderedImage.setId("borderedImage");
        var label2 = new Label(a.getDescription());
        label2.setFont(new Font("Bahnschrift SemiBold SemiCondensed", 12));
        label2.textFillProperty().setValue(Color.ALICEBLUE);
        label2.graphicTextGapProperty().setValue(10);
        label2.setGraphic(borderedImage);
        label2.setId("label2");
        box.getChildren().add(label2);
        makeNodeAccessible(box, AccessibleRole.NODE, "Achievement Unlocked",
                "Achievement Unlocked", a.getTier().toString() + " achievement unlocked: \n" + a.getName());
        box.setPrefSize(300, 40);
        box.setMaxSize(450, 65);
        box.setFillWidth(true);
        return box;
    }


    /**
     * Handles the user's command
     *
     * @param text the command that needs to be processed
     */
    private void submitEvent(String text) {
        text = text.strip(); //get rid of white space
        stopArticulation(); //if speaking, stop
        this.model.player.getCurrentRoom().visit();
        this.helpToggle = false;
        if (text.equalsIgnoreCase("LOOK") || text.equalsIgnoreCase("L")) {
            String roomDesc = this.model.getPlayer().getCurrentRoom().getRoomDescription();
            String objectString = this.model.getPlayer().getCurrentRoom().getObjectString();
            if (!objectString.isEmpty()) roomDescLabel.setText(roomDesc + "\n\nObjects in this room:\n" + objectString);
            articulateRoomDescription(true); //all we want, if we are looking, is to repeat description.
            return;
        } else if (text.equalsIgnoreCase("HELP") || text.equalsIgnoreCase("H")) {
            showInstructions();
            return;
        } else if (text.equalsIgnoreCase("COMMANDS") || text.equalsIgnoreCase("C")) {
            showCommands(); //this is new!  We did not have this command in A1
            return;
        }
        //try to move!
        String output = this.model.interpretAction(text); //process the command!
        if (output == null && this.model.getPlayer().getCurrentRoom().getRoomNumber().contains("T")){
            updateTrollScene();
            updateItems();
        }
        else if (output == null && this.model.getPlayer().getCurrentRoom().getRoomNumber().contains("NPC")){
            if (!this.model.getPlayer().getCurrentRoom().getVisited()){
                updateNPCScene();
                updateItems();
                articulateRoomDescription();
            }
            else{
                this.model.movePlayer("EXIT");
                this.roomDescLabel.setText("ALREADY TALKED WITH THIS NPC.");
            }
            this.inputTextField.setDisable(false);
        }
        else if (output == null || (!output.equals("GAME OVER") && !output.equals("FORCED") && !output.equals("HELP"))) {
            updateScene(output);
            updateItems();
            this.model.player.getCurrentRoom().visit();
            this.inputTextField.setDisable(false);
        } else if (output.equals("GAME OVER")) {
            updateScene("");
            updateItems();
            this.model.player.getCurrentRoom().visit();
            this.inputTextField.setDisable(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(event -> {
                Platform.exit();
            });
            pause.play();
        } else if (output.equals("FORCED")) {
            updateScene("");
            updateItems();
            this.model.player.getCurrentRoom().visit();
            this.inputTextField.setDisable(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(event -> {
                submitEvent("FORCED");
            });
            pause.play();
        }
    }


    /**
     * Update the text in the GUI (within roomDescLabel)
     * to show all the moves that are possible from the 
     * current room.
     */
    private void showCommands() {
        formatText(this.model.getPlayer().getCurrentRoom().getCommands());
    }


    /**
     * Show the current room, and print some text below it.
     * If the input parameter is not null, it will be displayed
     * below the image.
     * Otherwise, the current room description will be displayed
     * below the image.
     * 
     * @param textToDisplay the text to display below the image.
     */
    public void updateScene(String textToDisplay) {
        getRoomImage(); //get the image of the current room
        formatText(textToDisplay); //format the text to display
        roomDescLabel.setPrefWidth(500);
        roomDescLabel.setPrefHeight(500);
        roomDescLabel.setTextOverrun(OverrunStyle.CLIP);
        roomDescLabel.setWrapText(true);
        VBox roomPane = new VBox(roomImageView,roomDescLabel);
        roomPane.setPadding(new Insets(10));
        roomPane.setAlignment(Pos.TOP_CENTER);

        if (!roomPaneIsWhite) {
            roomPane.setStyle("-fx-background-color: #000000;");
        } else {
            roomPane.setStyle("-fx-background-color: #FFFFFF;");
        }

        gridPane.add(roomPane, 1, 1);
        updateHealth(getCurrHealth(), playerHealth);
        stage.sizeToScene();

        //finally, articulate the description
        if (textToDisplay == null || textToDisplay.isBlank()) articulateRoomDescription();
    }

    /**
     * Format text for display.
     * 
     * @param textToDisplay the text to be formatted for display.
     */
    private void formatText(String textToDisplay) {
        if (textToDisplay == null || textToDisplay.isBlank()) {
            String roomDesc = this.model.getPlayer().getCurrentRoom().getRoomDescription() + "\n";
            String objectString = this.model.getPlayer().getCurrentRoom().getObjectString();
            roomDescLabel.setText(roomDesc);
            if (objectString != null && !objectString.isEmpty()) roomDescLabel.setText(roomDesc + "\n\nObjects in this room:\n" + objectString);
            var piece = this.model.player.getCurrentRoom().getSecretStringPiece();
            if (!Objects.equals(piece, "")) roomDescLabel.setText(roomDescLabel.getText() + "\n\n\nSecret Key Fragment: " + piece);
            else roomDescLabel.setText(roomDesc);
        } else roomDescLabel.setText(textToDisplay);

        if (backgroundIsWhite) {
            roomDescLabel.setStyle("-fx-text-fill: black;");
        } else {
            roomDescLabel.setStyle("-fx-text-fill: white;");
        }
        roomDescLabel.setFont(new Font("Arial", 16));
        roomDescLabel.setAlignment(Pos.CENTER);
    }

    /**
     * Get the image for the current room and place 
     * it in the roomImageView 
     */
    private void getRoomImage() {
        String roomNumber = this.model.getPlayer().getCurrentRoom().getRoomNumber();
        String roomImage = this.model.getDirectoryName() + "/room-images/" + roomNumber + ".png";

        Image roomImageFile = new Image(roomImage);
        roomImageView = new ImageView(roomImageFile);
        roomImageView.setPreserveRatio(true);
        roomImageView.setFitWidth(400);
        roomImageView.setFitHeight(400);

        //set accessible text
        roomImageView.setAccessibleRole(AccessibleRole.IMAGE_VIEW);
        roomImageView.setAccessibleText(this.model.getPlayer().getCurrentRoom().getRoomDescription());
        roomImageView.setFocusTraversable(true);
    }

    /**
     * This method is partially completed, but you are asked to finish it off.
     *
     * The method should populate the objectsInRoom and objectsInInventory Vboxes.
     * Each Vbox should contain a collection of nodes (Buttons, ImageViews, you can decide)
     * Each node represents a different object.
     * 
     * Images of each object are in the assets 
     * folders of the given adventure game.
     */
    public void updateItems() {
        addChildObjectViews(this.objectsInRoom.getChildren(), this.model.getPlayer().getCurrentRoom().objectsInRoom, false);
        ScrollPane scO = new ScrollPane(objectsInRoom);
        scO.setPadding(new Insets(10));

        if (scIsWhite) {
            scO.setStyle("-fx-background: #FFFFFF; -fx-background-color:transparent;"); // about to switch these to white
        } else {
            scO.setStyle("-fx-background: #000000; -fx-background-color:transparent;"); // about to switch these to white
        }
        scO.setFitToWidth(true);
        gridPane.add(scO,0,1);

        addChildObjectViews(this.objectsInInventory.getChildren(), this.model.getPlayer().inventory, true);
        ScrollPane scI = new ScrollPane(objectsInInventory);
        scI.setFitToWidth(true);

        if (scIsWhite) {
            scI.setStyle("-fx-background: #FFFFFF; -fx-background-color:transparent;");
        } else {
            scI.setStyle("-fx-background: #000000; -fx-background-color:transparent;");
        }
        gridPane.add(scI,2,1);
    }

    /**
     * Adds each AdventureObject in objects as clickable images to children.
     * The click action is based on inventory (if the item is in the inventory,
     * the click action will DROP the object, otherwise it will TAKE the object).
     *
     * @param children the ObservableList of children for the parent Node of which to add the object images to
     * @param objects the list of objects to convert into clickable ImageView instances
     * @param inventory boolean to identify whether the objects list is from the Player's inventory or not
     */
    private void addChildObjectViews(ObservableList<Node> children, List<AdventureObject> objects, boolean inventory) {
        children.clear();
        for (var obj : objects) {
            Image file;
            if (Objects.equals(obj.getName().toUpperCase(), "TROLL STUFF"))
                file = new Image("Trolls/img/" + obj.getName().replaceAll(" ", "-") + ".png");
            else
                file = new Image(this.model.getDirectoryName() + "/objectImages/" + obj.getName().replaceAll(" ", "_") + ".jpg");
            var objImageView = new ImageView(file);
            objImageView.setPreserveRatio(true);
            objImageView.setFitWidth(100);
            objImageView.setAccessibleHelp(obj.getName());
            objImageView.setAccessibleText(obj.getDescription());
            var editButton = new ImageView("assets/editIcon.png");
            editButton.setPreserveRatio(true);
            editButton.setFitWidth(20);
            editButton.setAccessibleHelp("Rename");
            editButton.setAccessibleText("Rename this object.");
            editButton.setAccessibleRole(AccessibleRole.BUTTON);
            StackPane image = new StackPane();
            image.getChildren().addAll(objImageView, editButton);
            StackPane.setAlignment(editButton, Pos.TOP_RIGHT);
            editButton.toFront();
            var desc = inventory ? "DROP " + obj.getName() : "TAKE " + obj.getName();
            // Give the image the button role to show that it can be clicked to perform an action
            objImageView.setAccessibleRole(AccessibleRole.BUTTON);
            objImageView.setAccessibleRoleDescription(desc);
            image.setFocusTraversable(true);
            image.setOnMouseEntered(e -> objImageView.setStyle("-fx-cursor: hand"));
            editButton.setOnMouseClicked(e -> {
                e.consume();
                stopArticulation();
                this.gridPane.requestFocus();
                var renameView = new RenameObjectView(this, obj);
                updateItems();
            });
            objImageView.setOnMouseClicked(e -> {
                if (!this.inputTextField.isDisable()) submitEvent(desc);
                e.consume();});
            String displayName = obj.getName();
            for (var pair : this.model.getSynonyms().entrySet())
                if (Objects.equals(pair.getValue(), displayName)) displayName = pair.getKey();
            var label = new Label(displayName, image);
            label.setContentDisplay(ContentDisplay.TOP);
            label.setFont(new Font("Arial", 16));
            label.setOnMouseEntered(e -> label.setStyle("-fx-cursor: hand"));
            label.setOnMouseClicked(e -> {if (!this.inputTextField.isDisable()) submitEvent(desc);});
            children.add(label);
        }
    }

    /**
     * Show the game instructions.
     *
     * If helpToggle is FALSE:
     * -- display the help text in the CENTRE of the gridPane (i.e. within cell 1,1)
     * -- use whatever GUI elements to get the job done!
     * -- set the helpToggle to TRUE
     * -- REMOVE whatever nodes are within the cell beforehand!
     *
     * If helpToggle is TRUE:
     * -- redraw the room image in the CENTRE of the gridPane (i.e. within cell 1,1)
     * -- set the helpToggle to FALSE
     * -- Again, REMOVE whatever nodes are within the cell beforehand!
     */
    public void showInstructions() {
        if (this.helpToggle) {
            updateScene("");
            stopPlayback();
        } else {
            formatText(this.model.getInstructions());
            ScrollPane scroll = new ScrollPane();
            Thread insThread = new Thread(() -> {
                speakText(this.model.getInstructions());
            });
            insThread.start();
            scroll.setFitToWidth(true);
            VBox roomPane = new VBox(roomDescLabel);
            roomPane.setAlignment(Pos.TOP_CENTER);

            if (backgroundIsWhite) {
                roomPane.setStyle("-fx-background-color: #FFFFFF;"); // setting this to white
            } else {
                roomPane.setStyle("-fx-background-color: #000000;"); // setting this to white
            }
            roomDescLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
            scroll.setContent(roomPane);
            gridPane.add(scroll, 1, 1);
            stage.sizeToScene();
        }
        this.helpToggle = !this.helpToggle;
    }

    /**
     * This method is called when the player encounters a troll.
     * It will display the troll game in the GUI.
     */
    public void updateTrollScene() {
        updateScene(this.model.getPlayer().getCurrentRoom().getRoomDescription());
        updateItems();
        Troll troll = this.model.trollEncounter();
        // after troll introduction the game starts.
        // need to create the game with graphics within the troll methods, then pass it here.
        GridPane trollGrid = new GridPane();
        troll.playSpace(trollGrid, this);
        inputTextField.setDisable(true);
        helpButton.setDisable(true);
        loadButton.setDisable(true);
        saveButton.setDisable(true);
        updateScene("");
        gridPane.add(trollGrid, 1, 1);
    }

    /**
     * This method is called when the player encounters an NPC.
     * It will display the NPC game in the GUI.
     */
    public void updateNPCScene(){
        updateScene(this.model.getPlayer().getCurrentRoom().getRoomDescription());
        updateItems();
        String npcName = this.model.getPlayer().getCurrentRoom().getRoomName();
        this.model.getPlayer().getCurrentRoom().visit();

        switch (npcName.toLowerCase()){
            case "n1":
                Button blue = new Button("");
                Button red = new Button("");
                blue.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
                red.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
                GridPane.setValignment(blue, VPos.TOP);
                GridPane.setValignment(red, VPos.BOTTOM);
                GridPane.setHalignment(blue, HPos.RIGHT);
                GridPane.setHalignment(red, HPos.RIGHT);
                blue.setPrefSize(100, 50);
                red.setPrefSize(100, 50);
                gridPane.add(blue, 1, 3);
                gridPane.add(red, 1,1);
                makeNodeAccessible(blue, "blue pill", "select blue pill", "Pressing the enter key will select the blue pill option from this NPC.");
                makeNodeAccessible(red, "red pill", "select the red pill", "Pressing the enter key will select the red pill option from this NPC.");
                blue.setOnMouseClicked(e -> {
                    PauseTransition pause = new PauseTransition(Duration.seconds(4));
                    roomDescLabel.setText(" 'GAME' OVER. YOU WAKE UP IN YOUR BED, BELIEVING WHATEVER YOU WANT TO BELIEVE. IN WALTER'S WORLD YOU SHALL FOREVER REMAIN...");
                    helpButton.setDisable(true);
                    settingsButton.setDisable(true);
                    saveButton.setDisable(true);
                    loadButton.setDisable(true);
                    gridPane.getChildren().remove(blue);
                    gridPane.getChildren().remove(red);
                    inputTextField.setDisable(true);
                    pause.setOnFinished(e1 ->{
                        Platform.exit();
                    });
                    pause.play();
                });
                red.setOnMouseClicked(e -> {
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    roomDescLabel.setText("yumbly strobery falbvor :))))");
                    gridPane.getChildren().remove(blue);
                    gridPane.getChildren().remove(red);
                    pause.setOnFinished(e2 ->{
                        this.model.movePlayer("EXIT");
                        updateScene("");
                        updateItems();
                    });
                    pause.play();
                });
                break;
            case "n2":
                PauseTransition delay = new PauseTransition(Duration.millis(4727));
                Random random = new Random();
                String move = String.valueOf("3");
                delay.setOnFinished(e -> {
                    this.model.getPlayer().setCurrentRoom(this.model.getRooms().get(move));
                    updateScene("");
                });
                delay.play();
                break;
            case "n3":
                PauseTransition delay2 = new PauseTransition(Duration.millis(4727));
                delay2.setOnFinished(e -> {
                    this.model.player.attack *= 2;
                    this.model.movePlayer("EXIT");
                    updateScene("");
                });
                delay2.play();
                break;
        }
    }

     /**
     * This method handles the event related to the
     * troll button.
     *
     * @param troll the troll object to be passed to the troll game
     */
    public void trollObserver(Troll troll) {
        if (troll.state == 1) {
            this.model.movePlayer("BACK");
            updateScene("");
        }
        else if (troll.state == 2) {
            this.model.movePlayer("FORWARD");
            // Give player a random win object
            var reward = new AdventureObject("TROLL STUFF", "Stuff dropped from a troll you defeated. Who knows what it does.");
            var rand = new Random();
            reward.setOnConsume(new HashMap<>(Map.of("health", rand.nextInt(10, 20))));
            if (!this.model.player.getCurrentRoom().getVisited()){
                this.model.getPlayer().inventory.add(reward);
                this.model.player.getCurrentRoom().visit();
            }
            updateScene("");
            updateItems();
        }
        this.inputTextField.setDisable(false);
        this.saveButton.setDisable(false);
        this.helpButton.setDisable(false);
        this.loadButton.setDisable(false);
    }

    /**
     * This method handles the event related to the
     * help button.
     */
    public void addInstructionEvent() {
        helpButton.setOnAction(e -> {
            stopArticulation(); //if speaking, stop
            showInstructions();
        });
    }

    /**
     * This method handles the event related to the
     * save button.
     */
    public void addSaveEvent() {
        saveButton.setOnAction(e -> {
            gridPane.requestFocus();
            SaveView saveView = new SaveView(this);
        });
    }

    /**
     * This method handles the event related to the
     * load button.
     */
    public void addLoadEvent() {
        loadButton.setOnAction(e -> {
            gridPane.requestFocus();
            LoadView loadView = new LoadView(this, true);
            this.voice = initVoice();
        });
    }

    /**
     * This method adds the event handler related to the
     * settings button.
     */
    public void addSettingsEvent() {
        settingsButton.setOnAction(e -> {
            gridPane.requestFocus();
            SettingsView settingsView = new SettingsView(this);
        });
    }

    /**
     * This method articulates Room Descriptions
     * @param longDescription whether to play the long description audio or not
     */
    public void articulateRoomDescription(boolean longDescription) {
        String adventureName = this.model.getDirectoryName();
        String roomName = this.model.getPlayer().getCurrentRoom().getRoomName();
        String musicFile = "./" + adventureName + "/sounds/" + roomName.toLowerCase();
        musicFile += longDescription ? "-long.mp3" : "-short.mp3";
        musicFile = musicFile.replace(" ","-");
        File file = new File(musicFile);
        if(file.isFile()) {
            Media sound = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
        else{
            String text = longDescription ? this.model.player.getCurrentRoom().getRoomDescription() : this.model.player.getCurrentRoom().getRoomName();
            Thread thread = new Thread(() -> {
                speakText(text);
            });
            thread.start();
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e ->{
                thread.interrupt();
            });
            pause.play();
        }

        mediaPlaying = true;
    }

    /**
     * This overload articulates long Room Descriptions if the current room has not been visited
     * and short Room Descriptions if it has.
     */
    public void articulateRoomDescription() {
        articulateRoomDescription(!this.model.getPlayer().getCurrentRoom().getVisited());
    }

    /**
     * This method stops articulations 
     * (useful when transitioning to a new room or loading a new game)
     */
    public void stopArticulation() {
        if (this.voice != null && this.voice.isLoaded()){
            stopPlayback();
        }
        if (mediaPlaying && mediaPlayer != null) {
            mediaPlayer.stop(); //shush!

            mediaPlaying = false;
        }
    }

    /**
     * This method returns the gridPane
     * @return the gridPane
     */
    public GridPane getGridpane() {
        return this.gridPane;
    }

    /**
     * This method returns the progress bar of the player's health
     * @return the progress bar of the player's health
     */
    public ProgressBar getPlayerHealth() {
        return this.playerHealth;
    }

    /**
     * This method returns the label of the player's current health
     * @return the label of the player's current health
     */
    public Label getCurrHealth() {
        return this.currHealth;
    }

    /**
     * This method returns the model of the game
     * @return the AdventureGame model
     */
    public AdventureGame getModel() {
        return this.model;
    }

    /**
     * Speak the given text
     * @param text the text to speak
     */
    public void speakText(String text) {
        // Check if voice is available
        if (voice == null) {
            System.err.println("Cannot find a voice named kevin16. Exiting.");
            System.exit(1);
        }
        // Allocate resources for the voice
        voice.allocate();
        voice.speak(text);
    }

    /**
     * Stop the current voice playback
     */
    private void stopPlayback() {
        AudioPlayer aPLayer = voice.getAudioPlayer();
        aPLayer.cancel();
        voice.deallocate();
        this.voice = initVoice();
    }

    /**
     * This method initializes the voice
     * @return the voice
     */
    public Voice initVoice() {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        return VoiceManager.getInstance().getVoice("kevin16");
    }
}
