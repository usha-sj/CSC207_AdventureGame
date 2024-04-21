package AdventureModel;

/**
 * The Archer player type for the adventure game.
 */
public class Archer extends Player {
    /** the maximmum amount of health this player can have */
    public int maxHP;
    /**
     * Adventure Game Player Constructor
     *
     * @param currentRoom The starting Room for the Player
     */
    public Archer(Room currentRoom) {
        super(currentRoom);
        this.specialMove = "Headshot";
        this.health = 50;
        this.attack = 30;
    }

    /**
     * The attack for the Archer player type.
     */
    @Override
    public void attack() {
        super.attack();
    }
}
