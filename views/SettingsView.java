package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Allows users to access accessibility settings
 */
public class SettingsView {
    /** The button to change the background colour of the application */
    public static Button colourButton = new Button("Change Colour");
    /** The button to enter the font size changing view */
    public static Button fontSizeButton = new Button("Change Font Size");
    /** The button to change the font */
    public static Button fontButton = new Button("Change Font");
    /** The AdventureGameView this view was called from */
    private AdventureGameView adventureGameView;

    /**
     * Initializes the entire view
     * @param adventureGameView The view for this AdventureGame
     */
    public SettingsView(AdventureGameView adventureGameView) {
        this.adventureGameView = adventureGameView;

        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(adventureGameView.stage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));
        if (this.adventureGameView.backgroundIsWhite) {
            dialogVbox.setStyle("-fx-background-color: #FFFFFF;");
        } else {
            dialogVbox.setStyle("-fx-background-color: #121212;");
        }

        colourButton = new Button("Change Colours");
        colourButton.setId("ColourButton"); // DO NOT MODIFY ID
        colourButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white;");
        colourButton.setPrefSize(200, 50);
        if (this.adventureGameView.isSerif) {
            colourButton.setFont(new Font(this.adventureGameView.defaultFontSize));
        } else {
            colourButton.setFont(Font.font("Serif", this.adventureGameView.defaultFontSize));
        }
        AdventureGameView.makeNodeAccessible(colourButton, "colours", "This button changes the background and font colours", "Use this button to change the background and font colours.");
        colourButton.setOnAction(e -> {
            this.adventureGameView.changeBackground();
            dialog.close();
        });

        fontSizeButton = new Button("Change Font Size");
        fontSizeButton.setId("FontSizeButton"); // DO NOT MODIFY ID
        fontSizeButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white; -fx-cursor: hand");
        fontSizeButton.setPrefSize(200, 50);
        fontSizeButton.setFont(new Font(this.adventureGameView.defaultFontSize));
        AdventureGameView.makeNodeAccessible(fontSizeButton, "font size", "This button changes the font size", "Use this button to change the name of the font.");
        addFontSizeEvent();

        fontButton = new Button("Change Font");
        fontButton.setId("FontButton"); // DO NOT MODIFY ID
        fontButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white;");
        fontButton.setPrefSize(200, 50);
        fontButton.setFont(new Font(this.adventureGameView.defaultFontSize));
        AdventureGameView.makeNodeAccessible(fontButton, "font button", "This button changes the font", "Use this button to change the font.");
        addFontEvent();

        VBox settingsBox = new VBox(10, colourButton, fontSizeButton, fontButton);
        settingsBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().add(settingsBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Adds the event handler for the fontSizeButton
     */
    public void addFontSizeEvent() {
        fontSizeButton.setOnAction(e -> {
            this.adventureGameView.gridPane.requestFocus();
            FontView fontView = new FontView(adventureGameView, this);
        });
    }

    /**
     * Adds the event handler for the fontButton
     */
    public void addFontEvent() {
        fontButton.setOnAction(e -> {
            this.adventureGameView.changeFonts();
        });
    }
}
