package views;

import AdventureModel.AdventureObject;
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
import java.util.Objects;

/**
 * Adds an alias to an object in the AdventureGame
 */
public class RenameObjectView {
    /** The error label for the view */
    private Label errorLabel = new Label("");
    /** The text field for the view */
    private TextField textField = new TextField("");
    /** The AdventureGameView the view is a modal of */
    private AdventureGameView adventureGameView;

    /**
     * Initializes the entire RenameObjectView
     *
     * @param adventureGameView The view for this AdventureGame
     * @param object the object to add the alias of
     */
    public RenameObjectView(AdventureGameView adventureGameView, AdventureObject object) {
        this.adventureGameView = adventureGameView;
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(adventureGameView.stage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(20, 20, 20, 20));
        dialogVbox.setStyle("-fx-background-color: #121212;");
        var renameButton = new Button("Rename");
        renameButton.setId("Rename");
        this.errorLabel.setId("errorLabel");
        this.textField.setId("textField");
        var label = new Label("Enter the desired item name: ");
        label.setStyle("-fx-text-fill: #e8e6e3;");
        label.setFont(new Font(16));
        errorLabel.setStyle("-fx-text-fill: #e8e6e3;");
        errorLabel.setFont(new Font(16));
        textField.setStyle("-fx-text-fill: #000000;");
        textField.setFont(new Font(16));
        textField.setText(object.getName());
        renameButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white; -fx-cursor: hand;");
        renameButton.setPrefSize(200, 50);
        renameButton.setFont(new Font(16));
        AdventureGameView.makeNodeAccessible(renameButton, "Rename", "This button confirms the new object name.", "Use this button to save the name for the object.");
        renameButton.setOnAction(e -> {
            // Check if the name is empty
            var desired = textField.getText().trim().toUpperCase();
            if (desired.isEmpty()) {
                errorLabel.setText("Error: Name cannot be empty");
                return;
            }
            // Check if the name would conflict with other items
            for (var obj : this.adventureGameView.model.getObjectNames()) {
                if (!Objects.equals(obj, object.getName()) && Objects.equals(obj, desired)) {
                    errorLabel.setText("Error: Name already exists");
                    return;
                }
            }
            // Find the old name and replace it if necessary
            var oldName = "";
            for (var pair : this.adventureGameView.model.getSynonyms().entrySet()) {
                if ((!Objects.equals(object.getName(), desired) && Objects.equals(pair.getValue(), desired)) || Objects.equals(pair.getKey(), desired)) {
                    errorLabel.setText("Error: Name already exists");
                    return;
                }
                if (Objects.equals(pair.getValue(), object.getName())) oldName = pair.getKey();
            }
            if (!oldName.isEmpty()) this.adventureGameView.model.getSynonyms().remove(oldName);
            if (!desired.equals(object.getName()))
                // Add the new name
                this.adventureGameView.model.getSynonyms().put(desired, object.getName());
            dialog.close();
        });
        this.textField.setOnAction(e -> renameButton.fire());
        var closeWindowButton = new Button("Close Window");
        closeWindowButton.setId("closeWindowButton"); // DO NOT MODIFY ID
        closeWindowButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white; -fx-cursor: hand;");
        closeWindowButton.setPrefSize(200, 50);
        closeWindowButton.setFont(new Font(16));
        closeWindowButton.setOnAction(e -> dialog.close());
        AdventureGameView.makeNodeAccessible(closeWindowButton, "close window", "This is a button to close the rename object window", "Use this button to close the rename object window.");
        VBox renameBox = new VBox(10, label, textField, renameButton, errorLabel, closeWindowButton);
        renameBox.setAlignment(Pos.CENTER);
        dialogVbox.getChildren().add(renameBox);
        Scene dialogScene = new Scene(dialogVbox, 400, 400);
        dialog.setScene(dialogScene);
        // Show the dialog and wait to return until the user closes it
        dialog.showAndWait();
    }
}

