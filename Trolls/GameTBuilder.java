package Trolls;

import AdventureModel.Player;
import java.util.ArrayList;

/**
 * This class is used to build GameTroll objects.
 */
public class GameTBuilder implements TrollBuilder {
    /** The list of rewards for the troll */
    private ArrayList<?> rewardsList;
    /** The player strength for the Troll */
    private int playerStrength;

    /**
     * Set the rewards list for the Troll.
     */
    @Override
    public void setRewardsList(ArrayList<?> rewardsList) {
        this.rewardsList = rewardsList;
    }

    /**
     * Set the player strength for the Troll.
     */
    @Override
    public void setPlayerStrength(int playerStrength) {
        this.playerStrength = playerStrength;
    }

    /**
     * Set the action for the Troll.
     */
    public void setAction(){

    }

    /**
     * Build the Troll.
     * @param player the player which encounters the troll
     * @return the troll the player encounters
     */
    public GameTroll getResult(Player player){
        return new GameTroll(player);
    }
}
