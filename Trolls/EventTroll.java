package Trolls;

import AdventureModel.Player;
import javafx.animation.PauseTransition;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import views.AdventureGameView;

/**
 * The EventTroll a player may encounter during the adventure
 */
public class EventTroll extends Troll {
    /** The label for the action */
    private final Label actionLabel;
    /** The choice the player makes */
    private int choice;
    /** The list of rewards for the troll */
    private ArrayList<?> rewards;
    /** The player strength for the Troll */
    private int playerStrength;
    /** The state of the troll */
    public int state;
    /** The grid for the view */
    public GridPane grid;
    /** The player that encounters the troll */
    public Player player;

    /**
     * Initializes attributes for the troll
     *
     * @param player The player that encounters the troll
     */
    public EventTroll(Player player) {
        this.state = 1; // 0, 1, 2. -- do nothing, back, and forward.
        this.actionLabel = new Label();
        Random random = new Random();
        this.choice = random.nextInt(2);
        this.player = player;
    }

    /**
     * Sets up the central view area for the troll interaction
     */
    public void playSpace(GridPane grid, AdventureGameView view) {
        this.grid = grid;
        grid.setPrefSize(1000, 1000);
        ColumnConstraints col1 = new ColumnConstraints(200);
        ColumnConstraints col2 = new ColumnConstraints(200);
        ColumnConstraints col3 = new ColumnConstraints(200);
        RowConstraints row1 = new RowConstraints(167);
        RowConstraints row2 = new RowConstraints(167);
        RowConstraints row3 = new RowConstraints(167);
        grid.getColumnConstraints().addAll(col1, col2, col3);
        grid.getRowConstraints().addAll(row1, row2, row3);
        grid.setHgap(10);
        grid.setVgap(10);
        BackgroundFill backgroundFill = new BackgroundFill(Color.BLACK, null, null);
        Background background = new Background(backgroundFill);
        grid.setBackground(background);
        actionLabel.setAlignment(Pos.CENTER);
        actionLabel.setStyle(" -fx-font-size: 16px;");
        actionLabel.setTextFill(Color.rgb(255, 255, 255));
        actionLabel.setWrapText(true);
        Random random = new Random();
        int num = random.nextInt(3);

        if (num == 0) {
            actionLabel.setText("You've found an abandoned troll forge! There is a little leftover iron. What will you do?");
            Image forge = new Image("Trolls/img/forge.png");
            ImageView forgeView = new ImageView(forge);
            forgeView.setFitWidth(200);
            forgeView.setPreserveRatio(true);
            grid.add(forgeView, 1, 1);

            Button button1 = new Button("Repair Armor");
            button1.setOnAction(actionEvent -> {
                actionLabel.setText("Health increased");
                readingPause(player, 2, 1);
                trollSuccess(view);
            });
            Button button2 = new Button("Reinforce weapon");
            button2.setOnAction(actionEvent -> {
                actionLabel.setText("Attack increased");
                readingPause(player, 1, 2);
                trollSuccess(view);

            });
            grid.add(button1, 0, 2);
            grid.add(button2, 2, 2);
            buttonDecorator(button1, button2);
        } else if (num == 1) {
            actionLabel.setText("You've stepped into a troll's trap, a boulder is barreling towards you. What will you do?");
            Image img = new Image("Trolls/img/boulder.png");
            ImageView imgv = new ImageView(img);
            imgv.setFitWidth(200);
            imgv.setPreserveRatio(true);
            grid.add(imgv, 1, 1);
            Button button1 = new Button("Block the boulder with your weapon");
            button1.setOnAction(actionEvent -> {
                actionLabel.setText("your weapon is dull and broken");
                readingPause(player, 1, 0.7);
                trollSuccess(view);
            });
            Button button2 = new Button("Run away from the boulder");
            button2.setOnAction(actionEvent -> {
                actionLabel.setText("you get ran over, your armor is chipped and broken");
                readingPause(player, 0.7, 1);
                trollSuccess(view);
            });
            grid.add(button1, 0, 2);
            grid.add(button2, 2, 2);
            buttonDecorator(button1, button2);
        } else {
            actionLabel.setText("you find a fast food bag and a strange syringe along the side of the road. What will you do?");
            Image img = new Image("Trolls/img/burger or tren.png");
            ImageView imgv = new ImageView(img);
            imgv.setFitWidth(200);
            imgv.setPreserveRatio(true);
            grid.add(imgv, 1, 1);

            Button button1 = new Button("burger");

            button1.setOnAction(actionEvent -> {
                actionLabel.setText("burger makes you big boned, increasing your health");
                readingPause(player, 1.5, 1);
                trollSuccess(view);
            });
            Button button2 = new Button("tren(dy) bolo(g)ne");
            button2.setOnAction(actionEvent -> {
                actionLabel.setText("burger makes you big boned, increasing your health");
                readingPause(player, 1, 1.5);
                trollSuccess(view);
            });
            grid.add(button1, 0, 2);
            grid.add(button2, 2, 2);
            buttonDecorator(button1, button2);
        }
        GridPane.setValignment(actionLabel, VPos.CENTER);
        GridPane.setHalignment(actionLabel, HPos.CENTER);
        grid.add(actionLabel, 1, 0);
        //0: rock, 1: paper 2: scissors.
    }

    /**
     * Sets up the buttons for the troll interaction
     *
     * @param one the first button
     * @param two the second button
     */
    public void buttonDecorator(Button one, Button two){
        one.setPrefSize(200, 167);
        one.setStyle("-fx-font-size: 15px; -fx-cursor: hand;");
        one.setWrapText(true);
        one.setAlignment(Pos.CENTER);
        two.setStyle("-fx-font-size: 15px; -fx-cursor: hand;");
        two.setPrefSize(200, 167);
        two.setWrapText(true);
        two.setAlignment(Pos.CENTER);
    }

    /**
     * Sets up the buttons for the troll interaction
     *
     * @param player The player that encounters the troll
     * @param hpChange The change in health for the player
     * @param atkChange The change in attack for the player
     */
    public void readingPause(Player player, double hpChange, double atkChange){
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            player.health += (int) Math.round(hpChange * player.health);
            player.attack += (int) Math.round(atkChange * player.attack);
        });
    }
}



