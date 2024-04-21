package AdventureModel;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class keeps track of the progress
 * of the player in the game.
 */
public class Player implements Serializable {
    /**
     * An integer ranging from 0-100 to denote the Player's health. Once it reaches 0, the game ends as the Player will die.
     */
    public int health;
    /**
     * An integer to denote the Player's attack power. The higher the attack power, the more damage the player can inflict on a Troll.
     */
    public int attack;

    /**
     * A string representing the special attack move the specific type of player can use against Trolls.
     */
    public String specialMove;

    /**
     * The current room that the player is located in.
     */
    private Room currentRoom;

    /**
     * The list of items that the player is carrying at the moment.
     */
    public ArrayList<AdventureObject> inventory;
    /** The maximum health for this player */
    public int maxHP;


    /**
     * Adventure Game Player Constructor
     *
     * @param currentRoom The starting Room for the Player
     */
    public Player(Room currentRoom) {
        this.inventory = new ArrayList<AdventureObject>();
        this.currentRoom = currentRoom;
        this.attack = 15;
        this.health = 50;
        this.specialMove = "n/a";
        this.maxHP = 50;
    }


    /**
     * This method adds an object into players inventory if the object is present in
     * the room and returns true. If the object is not present in the room, the method
     * returns false.
     *
     * @param object name of the object to pick up
     * @return true if picked up, false otherwise
     */
    public boolean takeObject(String object) {
        if (this.currentRoom.checkIfObjectInRoom(object)) {
            AdventureObject object1 = this.currentRoom.getObject(object);
            this.currentRoom.removeGameObject(object1);
            this.addToInventory(object1);
            return true;
        } else {
            return false;
        }
    }


    /**
     * This method checks to see if an object is in a player's inventory.
     *
     * @param s the name of the object
     * @return true if object is in inventory, false otherwise
     */
    public boolean checkIfObjectInInventory(String s) {
        for (int i = 0; i < this.inventory.size(); i++) {
            if (this.inventory.get(i).getName().equals(s)) return true;
        }
        return false;
    }


    /**
     * This method drops an object in the players inventory and adds it to the room.
     * If the object is not in the inventory, the method does nothing.
     *
     * @param s name of the object to drop
     */
    public void dropObject(String s) {
        for (int i = 0; i < this.inventory.size(); i++) {
            if (this.inventory.get(i).getName().equals(s)) {
                if (this.inventory.get(i).getOnConsume().isEmpty()) this.currentRoom.addGameObject(this.inventory.get(i));
                this.inventory.get(i).consume(this);
                this.inventory.remove(i);
                break;
            }
        }
    }

    /**
     * Setter method for the current room attribute.
     *
     * @param currentRoom The location of the player in the game.
     */
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    /**
     * This method adds an object to the inventory of the player.
     *
     * @param object Prop or object to be added to the inventory.
     */
    public void addToInventory(AdventureObject object) {
        this.inventory.add(object);
    }


    /**
     * Getter method for the current room attribute.
     *
     * @return current room of the player.
     */
    public Room getCurrentRoom() {
        return this.currentRoom;
    }

    /**
     * Getter method for string representation of inventory.
     *
     * @return ArrayList of names of items that the player has.
     */
    public ArrayList<String> getInventory() {
        ArrayList<String> objects = new ArrayList<>();
        for (int i = 0; i < this.inventory.size(); i++) {
            objects.add(this.inventory.get(i).getName());
        }
        return objects;
    }

    /**
     * Resets the player's health or HP (health points) to full capacity.
     */
    public void resetHealth() {
        this.health = 100;
    }

    /**
     * Use the player type's special move to attack a troll.
     */
    public void attack() {
    }

}
