package views;

import AdventureModel.AdventureGame;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.util.Arrays;


/**
 * View to loads Serialized adventure games.
 */
public class LoadView {
    /** The view for this AdventureGame */
    private AdventureGameView adventureGameView;
    /** The label for the view */
    public Label selectGameLabel;
    /** The button for selecting a game file */
    private Button selectGameButton;
    /** The button for closing the window */
    private Button closeWindowButton;
    /** The VBox for the view */
    public static VBox dialogVbox = new VBox(20);
    /** The ListView listing the save files for the view */
    private ListView<String> GameList;
    /** The name of the file to load */
    private String filename = null;

    /** Used to differentiate whether the player is in the start screen or a current game. */
    private boolean inGame;

    /**
     * Initializes the entire view
     *
     * @param adventureGameView The current AdventureGameView for the adventure
     * @param inGame Whether the player is in the game or in the start menu
     */
    public LoadView(AdventureGameView adventureGameView, boolean inGame) {
        // Set inGame
        this.inGame = inGame;

        //note that the buttons in this view are not accessible!!
        this.adventureGameView = adventureGameView;
        selectGameLabel = new Label(String.format(""));

        GameList = new ListView<>(); //to hold all the file names

        final Stage dialog = new Stage(); //dialogue box
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(adventureGameView.stage);

        dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));

        if (this.adventureGameView.backgroundIsWhite) {
            dialogVbox.setStyle("-fx-background-color: #FFFFFF;"); //
        } else {
            dialogVbox.setStyle("-fx-background-color: #000000;"); //
        }

        selectGameLabel.setId("CurrentGame"); // DO NOT MODIFY ID
        GameList.setId("GameList");  // DO NOT MODIFY ID
        GameList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        GameList.setCellFactory(lst -> {
            return new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(item);
                            if (item != null) setStyle("-fx-cursor: hand;");
                        }
                    };
                });
        getFiles(GameList); //get files for file selector
        selectGameButton = new Button("Change Game");
        selectGameButton.setId("ChangeGame"); // DO NOT MODIFY ID
        AdventureGameView.makeNodeAccessible(selectGameButton, "select game", "This is the button to select a game", "Use this button to indicate a game file you would like to load.");

        closeWindowButton = new Button("Close Window");
        closeWindowButton.setId("closeWindowButton"); // DO NOT MODIFY ID
        closeWindowButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white; -fx-cursor: hand;");
        closeWindowButton.setPrefSize(200, 50);
        closeWindowButton.setFont(new Font(16));
        closeWindowButton.setOnAction(e -> dialog.close());
        AdventureGameView.makeNodeAccessible(closeWindowButton, "close window", "This is a button to close the load game window", "Use this button to close the load game window.");

        //on selection, do something
        selectGameButton.setOnAction(e -> {
            try {
                selectGame(selectGameLabel, GameList);
                dialog.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox selectGameBox = new VBox(10, selectGameLabel, GameList, selectGameButton, closeWindowButton);

        // Default styles which can be modified
        GameList.setPrefHeight(100);
        if (this.adventureGameView.backgroundIsWhite) {
            selectGameLabel.setStyle("-fx-text-fill: #000000");
        } else {
            selectGameLabel.setStyle("-fx-text-fill: #FFFFFF");
        }
        selectGameLabel.setFont(new Font(16));
        selectGameButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white; -fx-cursor: hand;");
        selectGameButton.setPrefSize(200, 50);
        selectGameButton.setFont(new Font(16));
        selectGameBox.setAlignment(Pos.CENTER);
        dialogVbox.getChildren().add(selectGameBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    /**
     * Get Files to display in the on screen ListView
     * Populate the listView attribute with .ser file names
     * Files will be located in the Games/Saved directory
     *
     * @param listView the ListView containing all the .ser files in the Games/Saved directory.
     */
    private void getFiles(ListView<String> listView) {
        try {
            var dir = new File("Games/Saved");
            var files = FXCollections.observableArrayList(Arrays.stream(dir.listFiles((folder, name) -> name.endsWith(".ser"))).map(File::getName).toList());
            listView.setItems(files);
        } catch (NullPointerException e) {
            // There are no saved games
        }
    }

    /**
     * Select the Game
     * Try to load a game from the Games/Saved
     * If successful, stop any articulation and put the name of the loaded file in the selectGameLabel.
     * If unsuccessful, stop any articulation and start an entirely new game from scratch.
     * In this case, change the selectGameLabel to indicate a new game has been loaded.
     *
     * @param selectGameLabel the label to use to print errors and or successes to the user.
     * @param GameList the ListView to populate
     */
    private void selectGame(Label selectGameLabel, ListView<String> GameList) throws IOException {
        selectGame(selectGameLabel, GameList, true);
    }

    /**
     * Select the Game
     * Try to load a game from the Games/Saved
     * If successful, stop any articulation and put the name of the loaded file in the selectGameLabel.
     * If unsuccessful, stop any articulation and start an entirely new game from scratch.
     * In this case, change the selectGameLabel to indicate a new game has been loaded.
     *
     * @param selectGameLabel the label to use to print errors and or successes to the user.
     * @param GameList the ListView to populate
     * @param inGame whether the player is in the start screen or a current game
     */
    private void selectGame(Label selectGameLabel, ListView<String> GameList, boolean inGame) throws IOException {
        if (inGame) this.adventureGameView.stopArticulation();
        try {
            var game = GameList.getSelectionModel().getSelectedItem();
            if (game == null) throw new IOException("NO GAME SELECTED");
            this.adventureGameView.model = loadGame("Games/Saved/" + game);
            selectGameLabel.setText(game);
            if (inGame) {
                this.adventureGameView.updateItems();
                this.adventureGameView.updateScene("");
            }
        } catch (ClassNotFoundException e) {
            this.adventureGameView.model = new AdventureGame(this.adventureGameView.model.getDirectoryName().substring(6));
            if (inGame) {
                this.adventureGameView.updateItems();
                this.adventureGameView.updateScene("");
            }
            selectGameLabel.setText("FAILED TO LOAD SELECTED GAME.\nLOADED NEW GAME");
            throw new IOException(e);
        } catch (IOException e) {
            this.adventureGameView.model = new AdventureGame(this.adventureGameView.model.getDirectoryName().substring(6));
            if (inGame) {
                this.adventureGameView.updateItems();
                this.adventureGameView.updateScene("");
            }
            selectGameLabel.setText("FAILED TO LOAD SELECTED GAME.\nLOADED NEW GAME");
            throw e;
        }
    }

    /**
     * Load the Game from a file
     *
     * @param GameFile file to load
     * @throws IOException throws if there is any error loading the file
     * @throws ClassNotFoundException throws if the casting to AdventureGame fails
     * @return loaded Tetris Model
     */
    public AdventureGame loadGame(String GameFile) throws IOException, ClassNotFoundException {
        // Reading the object from a file
        FileInputStream file = null;
        ObjectInputStream in = null;
        try {
            file = new FileInputStream(GameFile);
            in = new ObjectInputStream(file);
            return (AdventureGame) in.readObject();
        } finally {
            if (in != null) {
                in.close();
                file.close();
            }
        }
    }
}
