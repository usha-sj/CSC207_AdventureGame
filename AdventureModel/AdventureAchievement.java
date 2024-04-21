package AdventureModel;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.scene.Node;


/**
 * This class represents an achievement in the game. It is a node so that it can
 * fire events, specifically AchievementEvents.
 *
 * It implements AchievementObserver so that it can be notified when an achieveable action has been completed.
 *
 * It is serializable so that it can be saved to a file.
 *
 * It fires AchievementEvents when it is unlocked.
 */
public class AdventureAchievement extends Node implements AchievementObserver, Serializable {
    /**
     * The name of the achievement.
     */
    protected String name;
    /**
     * The description of the achievement.
     */
    protected String description;
    /**
     * The state of the achievement.
     */
    protected AchievementState state;
    /**
     * The tier of the achievement, representing difficulty of completing.
     */
    protected AchievementTier tier;
    /**
     * The file name of the asset for the achievement.
     */
    private final String assetFileName;

    /**
     * Constructor for AdventureAchievement. It takes in the name, description, and
     * tier of the achievement. It sets the state to locked and the asset file name
     * to the first file in the achievements/achievementIcons folder that starts
     * with the name of the achievement.
     *
     * @param name The name of the achievement.
     * @param description The description of the achievement.
     * @param tier The tier of the achievement.
     */
    public AdventureAchievement(String name, String description, AchievementTier tier) {
        this.name = name;
        this.description = description;
        this.tier = tier;
        this.state = AchievementState.Locked;
        String fileName = "achievements/achievementIcons/";
        var dir = new File(fileName);
        List<String> files;
        try {
            files = Arrays.stream(Objects.requireNonNull(dir.listFiles((folder, fname) -> fname.startsWith(name.replaceAll(" ", "_")))))
                    .map(File::getName).toList();
        } catch (NullPointerException e) {
            throw new NullPointerException("Could not find achievement asset!\n" + e.getMessage());
        }
        this.assetFileName = fileName + files.get(0);
    }

    /**
     * Default constructor for AdventureAchievement. It sets the name, description,
     * and asset file name to empty strings, the state to locked, and the tier to be Bronze.
     * This was added for the sake of being able to test the observer pattern in the test files without asset files.
     */
    public AdventureAchievement() {
        this.name = "";
        this.description = "";
        this.tier = AchievementTier.Bronze;
        this.state = AchievementState.Locked;
        this.assetFileName = "";
    }

    /**
     * Gets the name of the achievement.
     * @return The name of the achievement.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the achievement.
     * @return The description of the achievement.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the state of the achievement.
     * @return The state of the achievement.
     */
    public AchievementState getState() {
        return state;
    }

    /**
     * Gets the tier of the achievement.
     * @return The tier of the achievement.
     */
    public AchievementTier getTier() {
        return tier;
    }

    /**
     * Gets the asset file name of the achievement.
     * @return The asset file name of the achievement.
     */
    public String getAssetFileName() {
        return assetFileName;
    }

    /**
     * Sets the state of the achievement and fires an ACHIEVEMENT_UNLOCKED AchievementEvent.
     * @param state The state of the achievement.
     */
    @Override
    public void update(AchievementState state) {
        this.state = state;
        this.fireEvent(new AchievementEvent(AchievementEvent.ACHIEVEMENT_UNLOCKED, this.name));
    }
}
