package AdventureModel;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * This class extends ObservableAchievement, and can be observed by AchievementObserver objects.
 * This class is used to represent an action that can be performed by the player, and is
 * used to notify AchievementObserver objects when the action has been performed so that
 * they can update their state accordingly.
 *
 * @see ObservableAchievement
 * @see AchievementState
 * @see AchievementObserver
 */
public class AchievableAction extends ObservableAchievement implements Serializable {
    /**
     * The list of AchievementObserver objects that are observing this object.
     */
    private final ArrayList<AchievementObserver> observers;
    /**
     * The current state of this object.
     */
    private AchievementState state;

    /**
     * Constructs a new AchievableAction object.
     * Defaults to a locked state with no observers.
     */
    public AchievableAction() {
        this.observers = new ArrayList<>();
        this.state = AchievementState.Locked;
    }

    /**
     * Returns the current state of this object.
     * @return the current state of this object.
     */
    public AchievementState getState() {
        return state;
    }

    /**
     * Sets the current state of this object.
     * @param state the new state of this object.
     */
    public void setState(AchievementState state) {
        this.state = state;
    }

    /**
     * Sets the current state of the object to Unlocked and notifies all observers.
     */
    public void unlock() {
        this.state = AchievementState.Unlocked;
        notifyObservers();
    }

    /**
     * Notifies all observers of this object's current state.
     */
    public void notifyObservers() {
        this.observers.forEach(o -> o.update(this.state));
    }

    /**
     * Adds an AchievementObserver to the list of observers.
     * @param o the AchievementObserver to add.
     */
    public void attach(AchievementObserver o) {
        observers.add(o);
    }

    /**
     * Removes an AchievementObserver from the list of observers.
     * @param o the AchievementObserver to remove.
     */
    public void detach(AchievementObserver o) {
        observers.remove(o);
    }
}
