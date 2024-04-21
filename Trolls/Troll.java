package Trolls;

import javafx.scene.layout.GridPane;
import views.AdventureGameView;
import java.util.ArrayList;

/**
 * Abstract class for the trolls
 */
public abstract class Troll {
    /** The state of the troll */
    public int state;
    /** The gridPane for the view */
    public GridPane grid;
    /** The rewards for defeating the troll */
    private ArrayList<?> rewards;
    /** The strength of the player for the troll */
    private int playerStrength;

    /**
     * Sets up the central view area for the troll interaction
     * @param grid The gridPane for the view
     * @param view The current AdventureGameView for the adventure
     */
    public void playSpace(GridPane grid, AdventureGameView view){

    }

    /**
     * Called when the player defeats the troll
     * @param view The current AdventureGameView for the adventure
     */
    public void trollSuccess(AdventureGameView view){
        setState2(this);
        view.trollObserver(this);
    }

    /**
     * Called when the player fails to defeat the troll
     * @param view The current AdventureGameView for the adventure
     */
    public void trollFail(AdventureGameView view){
        setState1(this);
        view.trollObserver(this);
    }

    /**
     * Sets the state of the troll to 2
     * @param troll The current troll
     */
    protected void setState2(Troll troll){
        troll.state = 2;
    }

    /**
     * Sets the state of the troll to 1
     * @param troll The current troll
     */
    protected void setState1(Troll troll){
        troll.state = 1;
    }
}


