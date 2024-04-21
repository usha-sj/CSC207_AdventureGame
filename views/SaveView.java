package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Saves Serialized adventure games.
 */
public class SaveView {
    /** The success message */
    static String saveFileSuccess = "Saved Adventure Game!!";
    /** The error message if the file already exists */
    static String saveFileExistsError = "Error: File already exists";
    /** The error message if the file doesn't end in .ser */
    static String saveFileNotSerError = "Error: File must end with .ser";
    /** The label to display error messages */
    private Label saveFileErrorLabel = new Label("");
    /** The prompt for the input field */
    private Label saveGameLabel = new Label(String.format("Enter name of file to save"));
    /** The input field for the user */
    private TextField saveFileNameTextField = new TextField("");
    /** The button to save the alias */
    private Button saveGameButton = new Button("Save Game");
    /** The button to close the window */
    private Button closeWindowButton = new Button("Close Window");
    /** The VBox for the view */
    public static VBox dialogVbox = new VBox(20);
    /** The AdventureGameView for this view */
    private AdventureGameView adventureGameView;

    /**
     * Initializes the SaveView
     *
     * @param adventureGameView The view for this AdventureGame
     */
    public SaveView(AdventureGameView adventureGameView) {
        this.adventureGameView = adventureGameView;
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(adventureGameView.stage);
        dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));

        if (this.adventureGameView.backgroundIsWhite) {
            dialogVbox.setStyle("-fx-background-color: #FFFFFF;"); //
        } else {
            dialogVbox.setStyle("-fx-background-color: #000000;"); //
        }

        saveGameLabel.setId("SaveGame"); // DO NOT MODIFY ID
        saveFileErrorLabel.setId("SaveFileErrorLabel");
        saveFileNameTextField.setId("SaveFileNameTextField");

        if (this.adventureGameView.backgroundIsWhite) {
            saveGameLabel.setStyle("-fx-text-fill: #000000;");
            saveFileErrorLabel.setStyle("-fx-text-fill: #000000;");
        } else {
            saveGameLabel.setStyle("-fx-text-fill: #FFFFFF;");
            saveFileErrorLabel.setStyle("-fx-text-fill: #FFFFFF;");
        }
        saveGameLabel.setFont(new Font(16));
        saveFileErrorLabel.setFont(new Font(16));
        saveFileNameTextField.setStyle("-fx-text-fill: #000000;");
        saveFileNameTextField.setFont(new Font(16));

        String gameName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".ser";
        saveFileNameTextField.setText(gameName);

        saveGameButton = new Button("Save board");
        saveGameButton.setId("SaveBoardButton"); // DO NOT MODIFY ID
        saveGameButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white; -fx-cursor: hand;");
        saveGameButton.setPrefSize(200, 50);
        saveGameButton.setFont(new Font(16));
        AdventureGameView.makeNodeAccessible(saveGameButton, "save game", "This is a button to save the game", "Use this button to save the current game.");
        saveGameButton.setOnAction(e -> saveGame());

        closeWindowButton = new Button("Close Window");
        closeWindowButton.setId("closeWindowButton"); // DO NOT MODIFY ID
        closeWindowButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white; -fx-cursor: hand;");
        closeWindowButton.setPrefSize(200, 50);
        closeWindowButton.setFont(new Font(16));
        closeWindowButton.setOnAction(e -> dialog.close());
        AdventureGameView.makeNodeAccessible(closeWindowButton, "close window", "This is a button to close the save game window", "Use this button to close the save game window.");

        VBox saveGameBox = new VBox(10, saveGameLabel, saveFileNameTextField, saveGameButton, saveFileErrorLabel, closeWindowButton);
        saveGameBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().add(saveGameBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Saves the Game
     * Save the game to a serialized (binary) file.
     * Get the name of the file from saveFileNameTextField.
     * Files will be saved to the Games/Saved directory.
     * If the file already exists, set the saveFileErrorLabel to the text in saveFileExistsError
     * If the file doesn't end in .ser, set the saveFileErrorLabel to the text in saveFileNotSerError
     * Otherwise, load the file and set the saveFileErrorLabel to the text in saveFileSuccess
     */
    private void saveGame() {
        String fileName = this.saveFileNameTextField.getText();
        if (!fileName.endsWith(".ser")) {
            this.saveFileErrorLabel.setText(saveFileNotSerError);
            return;
        }
        try {
            Files.createDirectory(Paths.get("Games/Saved"));
        } catch (IOException e) {
            // Directory already exists, this is fine
        }
        var file = new File("Games/Saved/" + fileName);
        if (file.exists()) {
            this.saveFileErrorLabel.setText(saveFileExistsError);
            return;
        }
        this.adventureGameView.model.saveModel(file);
        this.saveFileErrorLabel.setText(saveFileSuccess);
    }
}

