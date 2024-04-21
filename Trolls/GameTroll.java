package Trolls;

import AdventureModel.Player;
import javafx.animation.PauseTransition;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import views.AdventureGameView;
import java.util.ArrayList;
import java.util.Random;

/**
 * The GameTroll that players may encounter in the adventure.
 */
public class GameTroll extends Troll {
    /** The label for the action */
    private final Label actionLabel;
    /** The rewards for the player */
    private ArrayList<?> rewards;
    /** The strength of the player */
    private int playerStrength;
    /** The state of the troll */
    public int state;
    /** The grid for the view */
    public GridPane grid;
    /** The player's choice */
    private int choice;
    /** The player that encounters the troll */
    public Player player;

    /**
     * Initializes the GameTroll's attributes
     * @param player The player that encounters the troll
     */
    public GameTroll(Player player){
        this.state = 1; // 0, 1, 2. -- do nothing, back, and forward.
        this.actionLabel = new Label();
        Random random = new Random();
        this.choice = random.nextInt(3);
    }

    /**
     * Sets up the central view area for the troll interaction
     * @param view The current AdventureGameView
     */
    @Override
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
        actionLabel.setText("click rock, paper or scissors");
        GridPane.setValignment(actionLabel, VPos.CENTER);
        GridPane.setHalignment(actionLabel, HPos.CENTER);
        grid.add(actionLabel, 1, 0);
        // pick rock paper scissors;


        //0: rock, 1: paper 2: scissors.

        Button button1 = new Button("PAPER");
        button1.setPrefSize(250, 250);
        grid.add(button1, 1, 2);
        System.out.println(choice);
        button1.setOnAction(actionEvent -> {
            if(choice == 0){
                PauseTransition pause = new PauseTransition();
                actionLabel.setText("won, moving forward");
                pause.setOnFinished(e -> {
                    trollSuccess(view);
                });
                pause.play();
            }
            else if (choice == 1){
                actionLabel.setText("tied, pick again");
                Random random = new Random();
                choice = random.nextInt(3);
            }
            else if (choice == 2){
                PauseTransition pause = new PauseTransition();
                actionLabel.setText("lost, moving back");
                pause.setOnFinished(e -> {
                    trollFail(view);
                });
                pause.play();
            }
        });
        Button button2 = new Button("SCISSORS");
        button2.setPrefSize(250, 250);
        grid.add(button2, 2, 2);
        button2.setOnAction(actionEvent -> {
            if(choice == 1){
                PauseTransition pause = new PauseTransition();
                actionLabel.setText("won, moving forward");
                pause.setOnFinished(e -> {
                    trollSuccess(view);
                });
                pause.play();
            }
            else if (choice == 2){
                actionLabel.setText("tied, pick again");
                Random random = new Random();
                choice = random.nextInt(3);
            }
            else if (choice == 0){
                PauseTransition pause = new PauseTransition();
                actionLabel.setText("lost, moving back");
                pause.setOnFinished(e -> {
                    trollFail(view);
                });
                pause.play();
            }
        });
        Button button3 = new Button("ROCK");
        button3.setPrefSize(250, 250);
        grid.add(button3, 0, 2);
        button3.setOnAction(actionEvent -> {
            if(choice == 1){
                PauseTransition pause = new PauseTransition();
                actionLabel.setText("won, moving forward");
                pause.setOnFinished(e -> {
                    trollSuccess(view);
                });
                pause.play();
            }
            else if (choice == 0){
                actionLabel.setText("tied, pick again");
                Random random = new Random();
                choice = random.nextInt(3);
            }
            else if (choice == 2){
                PauseTransition pause = new PauseTransition();
                actionLabel.setText("lost, moving back");
                pause.setOnFinished(e -> {
                    trollFail(view);
                });
                pause.play();
            }
        });
    }
}
