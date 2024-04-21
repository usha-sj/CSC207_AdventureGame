import java.io.IOException;
import java.util.Arrays;
import AdventureModel.AdventureGame;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for AdventureGame and AdventureGameView
 */
public class BasicAdventureTest {
    /**
     * Tests if the getCommands method works in AdventureGame
     * @throws IOException if there was a problem loading the AdventureGame from the files
     */
    @Test
    void getCommandsTest() throws IOException {
        AdventureGame game = new AdventureGame("TinyGame");
        var commands = Arrays.stream(game.player.getCurrentRoom().getCommands().split(",")).toList();
        var exp = Arrays.stream("DOWN,NORTH,IN,WEST,UP,SOUTH".split(",")).toList();
        assertTrue(exp.size() == commands.size() && exp.containsAll(commands));
    }

    /**
     * Tests if the getObjectString method works in AdventureGame
     * @throws IOException if there was a problem loading the AdventureGame from the files
     */
    @Test
    void getObjectString() throws IOException {
        AdventureGame game = new AdventureGame("TinyGame");
        String objects = game.player.getCurrentRoom().getObjectString();
        assertEquals("a water bird", objects);
    }
}
