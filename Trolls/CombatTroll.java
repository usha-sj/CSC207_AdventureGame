package Trolls;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import AdventureModel.Player;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import views.AdventureGameView;

/**
 * The troll that the player fights in the combat room.
 */
public class CombatTroll extends Troll{
    /** The rewards list for the Troll */
    private ArrayList<?> rewards;
    /** Used by the TrollObserver to check if the player won or lost against the Troll */
    public int state;
    /** The GridPane for the view */
    public GridPane grid;
    /** The troll's attack strength */
    private short attack;
    /** The troll's defense strength */
    private short defend;
    /** The troll as a Combatant object */
    private Combatant troll;
    /** The attack button's colour */
    private int atkColour;
    /** The defence button's colour */
    private int defColour;
    /** The label for the action */
    private Label actionLabel;
    /** The image for the troll's intent */
    private ImageView intentImage;
    /** The troll's health bar */
    private ProgressBar tHealthBar;
    /** The troll's health count shown inside the health bar */
    private Label tHealthCount;
    /** The player's health count shown inside the health bar */
    private final int pHealth;
    /** The troll's health count */
    private final int tHealth;
    /** The special button's colour */
    private int speColour;
    /** The player fighting the troll */
    public Player player;
    /** Whether the player used their special move already */
    private boolean speUsed;

    /**
     * Constructor for the CombatTroll.
     *
     * @param player The player fighting the troll
     */
    public CombatTroll(Player player) {
        this.player = player;
        this.state = 0;
        this.troll = new Combatant(player.maxHP * 2, player.attack);
        this.atkColour = 255;
        this.defColour = 255;
        this.speColour = 255;
        this.pHealth = player.maxHP;
        this.tHealth = this.troll.healthPoint;
        this.speUsed = false;
    }

    /**
     * The main combat loop for the troll interaction
     */
    private void combatLoop(AdventureGameView view, Button one, Button two, Button three) throws InterruptedException {
        int turnDef = 0;
        int turnAtk = 0;
        Timeline combatAnimation = new Timeline();
        String type;
        if (this.attack == 3 && this.defend == 0) {
            turnAtk = player.attack * 2;
            type = ("Reckless Charge");
        }
        else if (this.attack == 2 && this.defend == 1){
            turnAtk = player.attack * 3/2;
            turnDef = player.health / 4;
            type = ("Counter Strike");
        }
        else if (this.attack == 1 && this.defend == 2){
            turnAtk = player.attack;
            turnDef = player.health / 2;
            type = ("Shield Bash");
        }
        else if (this.attack == 0 && this.defend == 3){
            turnDef = player.health * 3/4;
            type = ("Evasion");
        } else {
            type = player.specialMove;
        }
        combatAnimation.getKeyFrames().addAll(new KeyFrame((Duration.seconds(0)), event -> {
            actionLabel.setText(type);
            one.setDisable(true);
            two.setDisable(true);
            three.setDisable(true);
        }));
        // attack the troll.

        switch (type) {
            case ("Siphoning Strike") -> {
                turnAtk = player.attack;
                player.health += player.attack;
                this.speUsed = true;
            }
            case ("Headshot") ->{
            this.speUsed = true;
            turnAtk += player.attack * 4;}

            case ("Preparation") -> {
                turnDef = player.health / 4;
                player.attack += player.attack;
                this.speUsed = true;
            }
            case ("n/a") -> {turnAtk += player.attack;}

        }

        this.troll.healthPoint -= turnAtk;
        combatAnimation.getKeyFrames().addAll(new KeyFrame((Duration.seconds(2)), event -> {
            if (troll.healthPoint < 0){
                troll.healthPoint = 0;
            }
            actionLabel.setText("troll has " + this.troll.healthPoint + " HP remaining.");
            this.tHealthBar.setProgress((double) this.troll.healthPoint /this.tHealth);
            this.tHealthCount.setText(this.troll.healthPoint + "/" + this.tHealth);
        }));

        if (troll.healthPoint <= 0) {
            actionLabel.setText("you defeated the troll, moving to the next room.");
            combatAnimation.getKeyFrames().addAll(new KeyFrame((Duration.seconds(4)), event -> {
                trollSuccess(view);
            }));
            combatAnimation.play();

        }
        else if (troll.intent.equals("attack")){

            int damageTaken = (troll.attackPoint - turnDef);
            if (damageTaken < 0){
                damageTaken = 0;
            }
            System.out.println(this.player.health);
            this.player.health -= damageTaken;
            System.out.println(this.player.health);
            combatAnimation.getKeyFrames().addAll(new KeyFrame((Duration.seconds(4)), event -> {
                actionLabel.setText("troll attacked player, has " + player.health + " HP remaining.");
                if (player.health <= 0){
                    player.health = 0;
                    one.setDisable(true);
                    two.setDisable(true);
                    three.setDisable(true);
                    // lost to troll
                    actionLabel.setText("lost to troll. GAME OVER.");
                    view.updateHealth(view.getCurrHealth(), view.getPlayerHealth());
                    PauseTransition pause = new PauseTransition(Duration.seconds(4));

                    pause.setOnFinished(event2 -> {
                        Platform.exit();
                    });
                    pause.play();
                }
                else {
                view.updateHealth(view.getCurrHealth(), view.getPlayerHealth());
                one.setDisable(false);
                two.setDisable(false);
                if (!speUsed){
                    three.setDisable(false);
                }
                updateByTurn();}
            }));
        }
        else if (troll.intent.equals("apply status")){
            this.troll.attackPoint += this.troll.attackPoint;
            combatAnimation.getKeyFrames().addAll(new KeyFrame((Duration.seconds(4)), event -> {
                actionLabel.setText("troll increased their attack by " + this.troll.attackPoint / 3);
                one.setDisable(false);
                two.setDisable(false);
                if (!speUsed){
                    three.setDisable(false);
                }
                updateByTurn();
            }));
        }
        System.out.println(this.troll.healthPoint +" "+ player.health);
        combatAnimation.play();
        this.attack = 0;
        this.defend = 0;
        this.troll.genIntent();
        this.atkColour = 255;
        this.defColour = 255;
    }

    /**
     * Sets up the central view area for the troll interaction
     */
    public void playSpace(GridPane grid, AdventureGameView view) {
        // Grid
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
        this.intentImage = new ImageView();
        grid.add(this.intentImage, 2, 2);
        // healthbar t
        this.tHealthBar = new ProgressBar();
        GridPane.setValignment(this.tHealthBar, VPos.BOTTOM);
        GridPane.setHalignment(this.tHealthBar, HPos.RIGHT);
        this.tHealthBar.setProgress(1);
        this.tHealthBar.setPrefWidth(1000);
        this.tHealthBar.setStyle("-fx-accent: red;");
        tHealthBar.setPrefHeight(20);
        grid.add(tHealthBar, 1, 0, 2, 1 );
        // numbers in troll healthbar
        this.tHealthCount = new Label();
        this.tHealthCount.setStyle("-fx-text-fill: white;");
        this.tHealthCount.setAlignment(Pos.BOTTOM_CENTER);
        GridPane.setHalignment(this.tHealthCount, HPos.CENTER);
        GridPane.setValignment(this.tHealthCount, VPos.BOTTOM);
        this.tHealthCount.setText(this.troll.healthPoint + "/" + this.tHealth);
        this.tHealthCount.setStyle("-fx-font-weight: bold;");
        grid.add(tHealthCount, 1, 0);

        // label describing what is happening in the battle.
        this.actionLabel = new Label();
        actionLabel.setAlignment(Pos.CENTER);
        actionLabel.setStyle(" -fx-font-size: 16px;");
        actionLabel.setTextFill(Color.rgb(255, 255, 255));
        actionLabel.setWrapText(true);
        actionLabel.setText("combine the buttons to use a move.");
        GridPane.setValignment(actionLabel, VPos.BOTTOM);
        GridPane.setHalignment(actionLabel, HPos.CENTER);
        actionLabel.setTextAlignment(TextAlignment.CENTER);
        grid.add(actionLabel, 0, 0);
        //draw player and troll
        updateByTurn();
        String trollFiles = "Trolls/img/";
        switch (player.specialMove) {
            case ("Siphoning Strike") -> {
                addPlayerImage(grid, trollFiles + "WARRIORICON.png");
            }
            case ("Headshot") -> {
                addPlayerImage(grid, trollFiles + "ARCHERICON.png");
            }

            case ("Preparation") -> {
                addPlayerImage(grid, trollFiles + "ELFICON.png");
            }
            default ->{
                addPlayerImage(grid, trollFiles + "erroricon.png");
            }
        }
        Image troll = new Image(trollFiles + "troll.png");
        ImageView trollImg = new ImageView(troll);
        trollImg.setFitHeight(175);
        trollImg.setPreserveRatio(true);
        GridPane.setHalignment(trollImg, HPos.CENTER);
        GridPane.setValignment(trollImg, VPos.CENTER);
        grid.add(trollImg, 2, 1);
        // button 1
        Button button1 = new Button("Attack");
        button1.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand;");
        button1.setMaxSize(200, 54);
        button1.setBackground(buttonBackgrounds(this.atkColour));
        GridPane.setValignment(button1, VPos.TOP);
        grid.add(button1, 0, 2);
        // button 2
        Button button2 = new Button("Defend");
        button2.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand;");
        button2.setMaxSize(200, 54);
        button2.setBackground(buttonBackgrounds(this.defColour));
        grid.add(button2, 0, 2);
        // button 3 - need to implement special move.
        Button button3 = new Button("Special");
        button3.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-cursor: hand;");
        button3.setMaxSize(200, 54);
        button3.setBackground(buttonBackgrounds(this.speColour));
        GridPane.setValignment(button3, VPos.BOTTOM);
        grid.add(button3, 0, 2);
        // button1 eventlistener
        button1.setOnAction(actionEvent -> {
            Media special = new Media(new File("Trolls/sfx/strike.mp3").toURI().toString());
            MediaPlayer sound3 = new MediaPlayer(special);
            sound3.play();
            this.attack += 1;
            this.atkColour -= 85;

            button1.setBackground(buttonBackgrounds(this.atkColour));
            try {
                if (this.attack + this.defend == 3){
                    combatLoop(view, button1, button2, button3);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            button1.setBackground(buttonBackgrounds(this.atkColour));
            button2.setBackground(buttonBackgrounds(this.defColour));
        });
        //button2 eventlistener
        button2.setOnAction(actionEvent -> {
            this.defend += 1;
            this.defColour -= 85;
            Media special = new Media(new File("Trolls/sfx/block.mp3").toURI().toString());
            MediaPlayer sound3 = new MediaPlayer(special);
            sound3.play();
            button2.setBackground(buttonBackgrounds(this.defColour));
            try {
                if (this.attack + this.defend == 3){
                    combatLoop(view, button1, button2, button3);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            button1.setBackground(buttonBackgrounds(this.atkColour));
            button2.setBackground(buttonBackgrounds(this.defColour));
        });
        button3.setOnAction(actionEvent -> {
            // special attack requires all energy
            if (this.defend + this.attack == 0){
                Media special = new Media(new File("Trolls/sfx/special.mp3").toURI().toString());
                MediaPlayer sound3 = new MediaPlayer(special);
                sound3.play();
                this.defend += 2;
                this.attack += 2;
                this.defColour -= 255;
                button3.setBackground(buttonBackgrounds(this.defColour));
                try {
                    if (this.attack + this.defend == 4){
                        combatLoop(view, button1, button2, button3);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                button1.setBackground(buttonBackgrounds(this.atkColour));
                button2.setBackground(buttonBackgrounds(this.defColour));
                button3.setBackground(buttonBackgrounds(this.defColour));
            }
            else{
                actionLabel.setText("special attack cannot be used in conjunction with attack or defend.");
            }

        });
    }

    /**
     * Used to update the background colours for the buttons selected by the player
     */
    private Background buttonBackgrounds(int bValue){
        BackgroundFill buttonFill = new BackgroundFill(Color.rgb(bValue, bValue, 255), null, null);
        return new Background(buttonFill);
    }

    /**
     * Used to update the troll's intent image
     */
    private void updateByTurn(){
        String trollFiles = "Trolls/img/";
        Image intention;
        if (this.troll.intent.equals("attack")){
            intention = new Image(trollFiles + "atkint.png");
        }
        else{
            intention = new Image(trollFiles + "buffint.png");
        }
        intentImage.setPreserveRatio(true);
        intentImage.setFitWidth(125);
        intentImage.setImage(intention);
        GridPane.setValignment(intentImage, VPos.CENTER);
        GridPane.setHalignment(intentImage, HPos.CENTER);
    }

    /**
     * Used to update the troll's intent, health, and attack/defend values
     */
    protected static class Combatant {
        /** The troll's health */
        private int healthPoint;
        /** The troll's attack damage */
        private int attackPoint;
        /** The troll's intent */
        private String intent;

        /**
         * Constructor for the Combatant.
         *
         * @param healthPoint The troll's health
         * @param attackPoint The troll's attack damage
         */
        protected Combatant(int healthPoint, int attackPoint) {
            this.healthPoint = healthPoint;
            this.attackPoint = attackPoint;
            genIntent();
        }

        /**
         * Used to generate the troll's intent (which action they take in a given turn)
         */
        private void genIntent() {
            Random rand = new Random();
            int intent = rand.nextInt(2);
            switch (intent) {
                case (0) -> this.intent = "attack";
                case (1) -> this.intent = "apply status";
            }

        }
    }

    /**
     * Used to handle the player's image
     *
     * @param grid The GridPane for the view
     * @param path The path to the image
     */
    private void addPlayerImage(GridPane grid, String path){
        Image player = new Image(path);
        ImageView playerImg = new ImageView(player);
        playerImg.setPreserveRatio(true);
        playerImg.setFitWidth(150);
        GridPane.setHalignment(playerImg, HPos.CENTER);
        GridPane.setValignment(playerImg, VPos.BOTTOM);
        grid.add(playerImg, 0, 1);
    }
}
