package views;

import AdventureModel.AchievementState;
import AdventureModel.AdventureAchievement;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Objects;


/**
 * Class AchievementView.
 *
 * Shows locked and unlocked achievements of the current adventure game.
 */
public class AchievementView {

    /** The parent AdventureGameView */
    private AdventureGameView adventureGameView;
    /** The stage for the modal window */
    private Stage stage;
    /** The GridPane for the view */
    private GridPane gridPane = new GridPane();
    /** The button to close the view */
    private Button exit = new Button("Back to Game");
    /** The VBox for the locked achievements */
    private VBox lockedAchievements = new VBox();
    /** The VBox for the unlocked achievements */
    private VBox unlockedAchievements = new VBox();
    /** The VBox for the selected achievement */
    private VBox selectedAchievementVBox = new VBox();

    /**
     * AchievementView constructor initializes the AchievementView as a modal of the adventureGameView.
     *
     * @param adventureGameView The current AdventureGameView for the adventure
     */
    public AchievementView(AdventureGameView adventureGameView) {
        // note that the buttons in this view are not accessible!!
        this.adventureGameView = adventureGameView;
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(adventureGameView.stage);
        lockedAchievements.setAlignment(Pos.TOP_CENTER);
        lockedAchievements.setSpacing(10);
        unlockedAchievements.setAlignment(Pos.TOP_CENTER);
        unlockedAchievements.setSpacing(10);
        // GridPane, anyone?
        gridPane.setPadding(new Insets(20));
        gridPane.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#000000"),
                new CornerRadii(0),
                new Insets(0)
        )));

        // Three columns, three rows for the GridPane
        ColumnConstraints column1 = new ColumnConstraints(200);
        ColumnConstraints column2 = new ColumnConstraints(550);
        ColumnConstraints column3 = new ColumnConstraints(200);
        column3.setHgrow(Priority.SOMETIMES); // let some columns grow to take any extra space
        column1.setHgrow(Priority.SOMETIMES);
        column1.setHalignment(HPos.CENTER);
        column2.setHalignment(HPos.CENTER);
        column3.setHalignment(HPos.CENTER);

        // Row constraints
        RowConstraints row1 = new RowConstraints(100);
        RowConstraints row2 = new RowConstraints(50);
        RowConstraints row3 = new RowConstraints();
        RowConstraints row4 = new RowConstraints();
        row2.setVgrow(Priority.SOMETIMES);
        row3.setVgrow(Priority.SOMETIMES);

        gridPane.getColumnConstraints().addAll(column1, column2, column1);
        gridPane.getRowConstraints().addAll(row1, row2, row3, row4);

        // Buttons
        exit.setId("CloseAchievements");
        exit.setPrefHeight(50);
        exit.setFont(new Font("Arial", 16));
        exit.setStyle("-fx-background-color: #319abd; -fx-text-fill: white; -fx-cursor: hand;");
        AdventureGameView.makeNodeAccessible(exit, "Back to Game", "This button returns to the game.", "This button returns to the game. Click it in order to close the achievements menu, so you can continue playing.");
        exit.setOnAction(e -> stage.close());

        // Title
        Label title =  new Label("Achievements");
        title.setAlignment(Pos.TOP_CENTER);
        title.setStyle("-fx-text-fill: white;");
        title.setFont(new Font("Arial", 20));

        // labels for locked and unlocked achievements
        Label locked =  new Label("Locked");
        locked.setAlignment(Pos.TOP_CENTER);
        locked.setStyle("-fx-text-fill: white;");
        locked.setFont(new Font("Arial", 16));

        Label unlocked =  new Label("Unlocked");
        unlocked.setAlignment(Pos.TOP_CENTER);
        unlocked.setStyle("-fx-text-fill: white;");
        unlocked.setFont(new Font("Arial", 16));

        //add all the widgets to the GridPane
        gridPane.add(exit, 0, 0, 1, 1);  // Add close
        gridPane.add(title, 1, 0, 1, 1);  // Add title
        gridPane.add(locked, 0, 1, 1, 1);  // Add label
        gridPane.add(unlocked, 2, 1, 1, 1);  // Add label
        updateAchievements();
        ScrollPane scO = new ScrollPane(lockedAchievements);
        scO.setPadding(new Insets(10));
        scO.setStyle("-fx-background: #000000; -fx-background-color:transparent;");
        scO.setFitToWidth(true);
        gridPane.add(scO,0,2);
        ScrollPane sc1 = new ScrollPane(unlockedAchievements);
        sc1.setPadding(new Insets(10));
        sc1.setStyle("-fx-background: #000000; -fx-background-color:transparent;");
        sc1.setFitToWidth(true);
        gridPane.add(sc1,2,2);
        stage.sizeToScene();
        Scene scene = new Scene(gridPane, 1000, 750);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Updates the selected achievement to be displayed in the center.
     *
     * @param a The achievement to display
     */
    private void updateSelected(AdventureAchievement a) {
        gridPane.getChildren().remove(selectedAchievementVBox);
        var box = new VBox();
        box.setId(a.getName());
        box.setStyle("-fx-padding: 25 15");
        box.toFront();
        var image = new ImageView(a.getAssetFileName());
        image.setPreserveRatio(true);
        image.setFitWidth(100);
        var border = new ImageView("achievements/achievementAssets/" + a.getTier().toString().toLowerCase() + "-border.png");
        border.setPreserveRatio(true);
        border.setFitWidth(100);
        DropShadow borderGlow = new DropShadow();
        borderGlow.setWidth(90);
        borderGlow.setHeight(80);
        borderGlow.setOffsetY(0);
        borderGlow.setOffsetX(0);
        borderGlow.setColor(switch (a.getTier()) {
            case Gold -> Color.GOLD;
            case Silver -> Color.SILVER;
            case Bronze -> Color.TAN;
        });
        StackPane borderedImage = new StackPane();
        borderedImage.getChildren().addAll(image, border);
        borderedImage.setEffect(borderGlow);
        AdventureGameView.makeNodeAccessible(borderedImage, AccessibleRole.IMAGE_VIEW, "Achievement Icon", "Achievement Icon", "Achievement Icon");
        borderedImage.setId("borderedImage");
        borderedImage.setPadding(new Insets(40));
        box.getChildren().add(borderedImage);
        var label1 = new Label(a.getTier().toString() + " Achievement:");
        label1.setFont(new Font("Bahnschrift SemiBold SemiCondensed", 22));
        label1.textFillProperty().setValue(Color.ALICEBLUE);
        label1.setId("label1");
        label1.setWrapText(true);
        label1.setPadding(new Insets(10, 0, 10, 0));
        box.getChildren().add(label1);
        var label2 = new Label(a.getName());
        label2.setFont(new Font("Bahnschrift SemiBold SemiCondensed", 18));
        label2.textFillProperty().setValue(Color.ALICEBLUE);
        label2.setId("label2");
        label2.setWrapText(true);
        label2.setPadding(new Insets(5, 0, 5, 0));
        box.getChildren().add(label2);
        var label3 = new Label(a.getDescription());
        label3.setFont(new Font("Bahnschrift SemiBold SemiCondensed", 15));
        label3.textFillProperty().setValue(Color.ALICEBLUE);
        label3.setId("label3");
        label3.setWrapText(true);
        label3.setPadding(new Insets(5, 0, 5, 0));
        box.getChildren().add(label3);
        AdventureGameView.makeNodeAccessible(box, AccessibleRole.NODE, "Achievement Details",
                "Achievement Details", a.getTier().toString() + " achievement details: \n" + a.getName());
        box.setFillWidth(true);
        selectedAchievementVBox = box;
        gridPane.add(selectedAchievementVBox, 1, 2);
    }

    /**
     * Updates the locked and unlocked achievements columns to be displayed in the view.
     *
     * @param clicked The achievement to be highlighted (the one displayed in the center)
     */
    private void updateAchievements(VBox clicked) {
        this.lockedAchievements.getChildren().clear();
        this.unlockedAchievements.getChildren().clear();
        for (var a : this.adventureGameView.model.getAchievements()) {
            var box = AdventureGameView.makeAchievementVBox(a);
            box.setOnMouseClicked(e -> {
                // Don't update if the achievement is already selected.
                if (Objects.equals(a.getName(), this.selectedAchievementVBox.getId())) return;
                updateAchievements(box);
                updateSelected(a);
            });
            if (clicked != null && Objects.equals(clicked.getId(), a.getName()))
                box.setStyle(box.getStyle() + "-fx-border-color: #1D1F26; -fx-background-color: #1D1F26;");
            if (a.getState() == AchievementState.Locked) lockedAchievements.getChildren().add(box);
            else unlockedAchievements.getChildren().add(box);
        }
    }

    /**
     * Updates the locked and unlocked achievements columns to be displayed in the view.
     */
    private void updateAchievements() {
        updateAchievements(null);
    }
}
