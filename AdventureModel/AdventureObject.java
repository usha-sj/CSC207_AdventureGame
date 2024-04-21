package AdventureModel;

import java.io.Serializable;
import java.util.HashMap;
import java.lang.Math;

/**
 * This class keeps track of the props or the objects in the game.
 * These objects have a name, description, and location in the game.
 * The player with the objects can pick or drop them as they like and
 * these objects can be used to pass certain passages in the game.
 */
public class AdventureObject implements Serializable {
    /**
     * The name of the object.
     */
    private String objectName;

    /**
     * The description of the object.
     */
    private String description;

    /**
     * The location of the object.
     */
    private Room location = null;

    /**
     * This attribute describes what the object does when it is used.
     */
    private HashMap<String, Integer> onConsume = new HashMap<>();

    /**
     * This constructor sets the name, description, and location of the object.
     *
     * @param name The name of the Object in the game.
     * @param description One line description of the Object.
     * @param location The location of the Object in the game.
     */
    public AdventureObject(String name, String description, Room location) {
        this(name, description);
        this.location = location;
    }

    /**
     * This constructor sets the name, description, and location of the object.
     *
     * @param name The name of the Object in the game.
     * @param description One line description of the Object.
     */
    public AdventureObject(String name, String description) {
        this.objectName = name;
        this.description = description;
    }

    /**
     * Getter method for the name attribute.
     *
     * @return name of the object
     */
    public String getName(){
        return this.objectName;
    }

    /**
     * Getter method for the description attribute.
     *
     * @return description of the game
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * This method returns the location of the object if the object is still in
     * the room. If the object has been pickUp up by the player, it returns null.
     *
     * @return returns the location of the object if the objects is still in
     * the room otherwise, returns null.
     */
    public Room getLocation(){
        return this.location;
    }

    /**
     * This method returns the HashMap describing how the object should affect the player when consumed.
     * @return the HashMap describing how the object should affect the player when consumed.
     */
    public HashMap<String, Integer> getOnConsume() {
        return onConsume;
    }

    /**
     * This method sets the HashMap describing how the object should affect the player when consumed.
     * @param onConsume the HashMap describing how the object should affect the player when consumed.
     */
    public void setOnConsume(HashMap<String, Integer> onConsume) {
        this.onConsume = onConsume;
    }

    /**
     * If the object is consumable, this method affects the player based on onConsume property.
     *
     * @param player the player which consumes the object
     */
    public void consume(Player player) {
        for (var pair : this.onConsume.entrySet()) {
            var attribute = pair.getKey();
            var value = pair.getValue();
            if (value == null) value = 0;
            switch (attribute) {
                case "health" -> player.health = Math.min(Math.max(player.health + value, 0), 100);
                case "attack" -> player.attack += value;
            }
        }
    }
}
