import AdventureModel.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Complete tests for the adventure game.
 */
public class MoreAdventureTests {

    /**
     * To test achievement system with example achievements that do not have asset files
     */
    static class TestAdventureAchievement extends AdventureAchievement {

        public TestAdventureAchievement(String name, String description, AchievementTier tier) {
            this.name = name;
            this.description = description;
            this.tier = tier;
            this.state = AchievementState.Locked;
        }
    }

    /**
     * Test the entire observer pattern for achievements
     */
    @Test
    void testAchievementObserverPattern() {
        var achievement = new TestAdventureAchievement("Test", "Test", AchievementTier.Bronze);
        var achievement2 = new TestAdventureAchievement("Test", "Test", AchievementTier.Bronze);
        var action = new AchievableAction();
        action.attach(achievement);
        assertEquals(achievement.getState(), AchievementState.Locked);
        assertEquals(achievement2.getState(), AchievementState.Locked);
        action.unlock();
        assertEquals(achievement.getState(), AchievementState.Unlocked);
        assertEquals(achievement2.getState(), AchievementState.Locked);
    }

    /**
     * Test the detach method of the observer pattern for achievements
     */
    @Test
    void testAchievementObserverDetach() {
        var achievement = new TestAdventureAchievement("Test", "Test", AchievementTier.Bronze);
        var action = new AchievableAction();
        action.attach(achievement);
        action.detach(achievement);
        assertEquals(achievement.getState(), AchievementState.Locked);
        action.unlock();
        assertEquals(achievement.getState(), AchievementState.Locked);
    }
}
