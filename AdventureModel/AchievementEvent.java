package AdventureModel;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * This class represents an event that is fired when an achievement is unlocked.
 * It contains the name of the achievement that was unlocked.
 */
public class AchievementEvent extends Event {
    /**
     * The type of event that is fired when an achievement is unlocked.
     * The event type is ACHIEVEMENT_UNLOCKED.
     */
    public static EventType<AchievementEvent> ACHIEVEMENT_UNLOCKED = new EventType<>(Event.ANY, "ACHIEVEMENT_UNLOCKED");
    /**
     * The name of the achievement that was unlocked.
     */
    private final String name;
    /**
     * Constructs an AchievementEvent object.
     * @param eventType The type of event that is fired when an achievement is unlocked.
     * @param name The name of the achievement that was unlocked.
     */
    public AchievementEvent(EventType<? extends Event> eventType, String name) {
        super(eventType);
        this.name = name;
    }

    /**
     * Gets the name of the achievement that was unlocked.
     * @return The name of the achievement that was unlocked.
     */
    public String getName() { return name; }
}
