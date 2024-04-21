package AdventureModel;

/**
 * ObservableAchievement is used to implement the Observer pattern for
 * achievements. It is an abstract class that is extended by the
 * AchievableAction class.
 */
public abstract class ObservableAchievement {

    /**
     * attach is used to add an AchievementObserver to the list of observers.
     * @param o The AchievementObserver to be added.
     */
    public abstract void attach(AchievementObserver o);

    /**
     * detach is used to remove an AchievementObserver from the list of
     * observers.
     * @param o The AchievementObserver to be removed.
     */
    public abstract void detach(AchievementObserver o);

    /**
     * notifyObservers is used to notify all AchievementObservers that the
     * state of the achievable action has changed.
     */
    public abstract void notifyObservers();
}
