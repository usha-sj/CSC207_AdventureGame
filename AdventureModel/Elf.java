package AdventureModel;

/**
 * The Elf player type for the adventure game.
 */
public class Elf extends Player{
    /** the maximmum amount of health this player can have */
    public int maxHP;
    /**
     * Adventure Game Player Constructor
     *
     * @param currentRoom The starting Room for the Player
     */
    public Elf(Room currentRoom) {
        super(currentRoom);
        this.specialMove = "Preparation";
        this.attack = 10;
        this.health = 50;
    }

    /**
     * The attack for the Elf player type.
     */
    @Override
    public void attack() {
        super.attack();
    }
}
