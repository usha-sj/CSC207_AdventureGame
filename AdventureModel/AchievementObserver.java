package AdventureModel;

/**
 * An interface for classes that observe AcheivableAction objects.
 */
public interface AchievementObserver {
    /**
     * Updates the state of the observer.
     * @param state The new state of the observer.
     */
    public void update(AchievementState state);
}
