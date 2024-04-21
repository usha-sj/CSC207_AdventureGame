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
 * The view for changing the font size of the view
 */
public class FontView {

    /** The button for increasing font size */
    private Button increaseButton = new Button("Increase Size");
    /** The button for decreasing font size */
    private Button decreaseButton = new Button("Decrease Size");
    /** The view for this AdventureGame */
    private AdventureGameView adventureGameView;
    /** The SettingsView this view is called from */
    private SettingsView settingsView;

    /**
     * Sets up the entire view
     *
     * @param adventureGameView The view for this AdventureGame
     * @param settingsView The SettingsView this view is called from
     */
    public FontView(AdventureGameView adventureGameView, SettingsView settingsView) {
        this.adventureGameView = adventureGameView;
        this.settingsView = settingsView;
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
        increaseButton = new Button("+");
        increaseButton.setId("IncreaseFontButton"); // DO NOT MODIFY ID
        increaseButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white; -fx-cursor: hand;");
        increaseButton.setPrefSize(120, 50);
        increaseButton.setFont(new Font(adventureGameView.defaultFontSize));
        AdventureGameView.makeNodeAccessible(increaseButton, "increase font", "This is a button to increase font size", "Use this button to increase the font size.");
        increaseButton.setOnAction(e -> increaseFonts());

        decreaseButton = new Button("-");
        decreaseButton.setId("DecreaseFontButton"); // DO NOT MODIFY ID
        decreaseButton.setStyle("-fx-background-color: #319abd; -fx-text-fill: white; -fx-cursor: hand;");
        decreaseButton.setPrefSize(120, 50);
        decreaseButton.setFont(new Font(adventureGameView.defaultFontSize));
        AdventureGameView.makeNodeAccessible(decreaseButton, "decrease font", "This is a button to decrease font size", "Use this button to decrease the font size.");
        decreaseButton.setOnAction(e -> decreaseFonts());

        VBox fontBox = new VBox(10, increaseButton, decreaseButton);
        fontBox.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().add(fontBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Increases the font size of a button
     *
     * @param button The button to increase the font size of
     */
    private void increaseFont(Button button) {
        Font font = button.getFont();
        double fontSize = font.getSize();
        if (fontSize < 20) {
            button.setFont(new Font(fontSize + 1));
            this.adventureGameView.updateFontSize((int) (fontSize + 1));
        }
    }

    /**
     * Increases the font size of all buttons and labels
     */
    private void increaseFonts() {
        increaseFont(increaseButton);
        increaseFont(decreaseButton);
        increaseFont(settingsView.colourButton);
        increaseFont(settingsView.fontSizeButton);
        increaseFont(settingsView.fontButton);
        increaseFont(adventureGameView.saveButton);
        increaseFont(adventureGameView.helpButton);
        increaseFont(adventureGameView.loadButton);
        increaseFont(adventureGameView.settingsButton);
    }

    /**
     * Decreases the font size of a button
     *
     * @param button The button to decrease the font size of
     */
    private void decreaseFont(Button button) {
        Font font = button.getFont();
        double fontSize = font.getSize();
        if (fontSize > 10) {
            button.setFont(new Font(fontSize - 1));
            this.adventureGameView.updateFontSize((int) (fontSize - 1));
        }
    }

    /**
     * Decreases the font size of all buttons and labels
     */
    private void decreaseFonts() {
        decreaseFont(increaseButton);
        decreaseFont(decreaseButton);
        decreaseFont(settingsView.colourButton);
        decreaseFont(settingsView.fontSizeButton);
        decreaseFont(settingsView.fontButton);
        decreaseFont(adventureGameView.saveButton);
        decreaseFont(adventureGameView.helpButton);
        decreaseFont(adventureGameView.loadButton);
        decreaseFont(adventureGameView.settingsButton);
    }
}