package Trolls;

import AdventureModel.Player;
import java.util.ArrayList;

/**
 * Concrete builder implementing TrollBuilder for CombatTroll.
 */
public class CombatTBuilder implements TrollBuilder {
    /** The rewards list for the Troll */
    private ArrayList<?> rewardsList;
    /** The player strength for the Troll */
    private int playerStrength;

    /**
     * Set the rewards list for the Troll.
     */
    @Override
    public void setRewardsList(ArrayList<?> rewardsList){
        this.rewardsList = rewardsList;
    }

    /**
     * Set the player strength for the Troll.
     */
    @Override
    public void setPlayerStrength(int playerStrength){
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
    public CombatTroll getResult(Player player){
        return new CombatTroll(player);
    }
}
