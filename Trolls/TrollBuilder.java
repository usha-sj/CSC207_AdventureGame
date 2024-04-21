package Trolls;
import java.util.ArrayList;

/**
 * TrollBuilder interface used to build trolls
 */
public interface TrollBuilder {
    /** Sets the rewards list for the troll
     * @param rewardsList The rewards list for the troll
     */
    void setRewardsList(ArrayList<?> rewardsList);

    /**
     * Set the player strength for the Troll.
     * @param playerStrength the value to set
     */
    void setPlayerStrength(int playerStrength);

    /**
     * Set the action for the Troll.
     */
    void setAction();
}
