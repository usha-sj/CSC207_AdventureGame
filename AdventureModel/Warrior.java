package AdventureModel;

/**
 * The Warrior player type for the adventure game.
 */
public class Warrior extends Player{
    /** the maximmum amount of health this player can have */
    public int maxHP;
    /**
     * Adventure Game Player Constructor
     *
     * @param currentRoom The starting Room for the Player
     */
    public Warrior(Room currentRoom) {
        super(currentRoom);
        this.specialMove = "Siphoning Strike";
        this.health = 50;
        this.attack = 20;
    }

    /**
     * The attack for the Warrior player type.
     */
    @Override
    public void attack() {
        super.attack();
    }
}
