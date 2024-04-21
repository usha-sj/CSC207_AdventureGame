package Trolls;
import java.util.ArrayList;

/**
 * Used to determine what type of troll to build.
 */
public class Director {
    /**
     * Constructs an EventTroll
     * @param builder The builder to use to build the troll.
     */
    public void constructEventTroll(TrollBuilder builder) {
        // make a rewards list here
        // ArrayList<?> rewards = new ArrayList();
    }

    /**
     * Constructs a GameTroll
     * @param builder The builder to use to build the troll.
     */
    public void constructGameTroll(TrollBuilder builder) {
        builder.setRewardsList(new ArrayList<>());
        builder.setPlayerStrength(14);
    }

    /**
     * Constructs a CombatTroll
     * @param builder The builder to use to build the troll.
     */
    public void constructCombatTroll(TrollBuilder builder) {

    }
}
