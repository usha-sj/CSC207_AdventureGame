package AdventureModel;
import Trolls.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Random;

/**
 * Class AdventureGame.  Handles all the necessary tasks to run the Adventure game.
 */
public class AdventureGame implements Serializable {
    /** An attribute to store the Introductory text of the game. */
    private final String directoryName;
    /** A variable to store the Help text of the game. This text is displayed when the user types "HELP" command. */
    private String helpText;
    /** A list of all the rooms in the game. */
    public HashMap<String, Room> rooms;
    /** A stores the pieces of the secret string used to access the secret room. */
    private final ArrayList<String> secretStringChunks;
    /** A HashMap to store synonyms of commands. */
    private HashMap<String,String> synonyms = new HashMap<>();
    /** List of action verbs (other than motions) that exist in all games. Motion vary depending on the room and game. */
    private final String[] actionVerbs = {"QUIT","INVENTORY","TAKE","DROP"};
    /** A list of all the AdventureAchievement objects in the game. */
    private final ArrayList<AdventureAchievement> achievements = new ArrayList<>();
    /** A list of all the AchievableAction objects that the AdventureAchievement objects observe. */
    private final HashMap<String, AchievableAction> achievables = new HashMap<>();
    /** The Player of the game.*/
    public Player player;
    /** A list of all the object names in the game. */
    private final List<String> objectNames = new ArrayList<>();

    /**
     * Initializes attributes for the AdventureGame class.
     *
     * @param name the name of the adventure
     */
    public AdventureGame(String name){
        this.synonyms = new HashMap<>();
        this.rooms = new HashMap<>();
        this.secretStringChunks = new ArrayList<>();
        this.directoryName = "Games/" + name; //all games files are in the Games directory!
        try {
            setUpGame();
        } catch (IOException e) {
            throw new RuntimeException("An Error Occurred: " + e.getMessage());
        }
        this.objectNames.add("TROLL STUFF");
    }

    /**
     * Save the current state of the game to a file
     * 
     * @param file pointer to file to write to
     */
    public void saveModel(File file) {
        try {
            FileOutputStream outfile = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(outfile);
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Automatically save the current state of the game to a file with a default name
     */
    private void autosaveModel() {
        try {
            Files.createDirectory(Paths.get("Games/Saved"));
        } catch (IOException e) {
            // Directory already exists, this is fine
        }
        saveModel(new File("Games/Saved/Autosave_" +
                new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(new Date()) + ".ser"));
    }

    /**
     * Uses an AdventureLoader to load all necessary data from game files and set up the AdventureGame model
     *
     * @throws IOException in the case of a file I/O error
     */
    public void setUpGame() throws IOException {
        String directoryName = this.directoryName;
        AdventureLoader loader = new AdventureLoader(this, directoryName);
        loader.loadGame();
        setUpSecretRoom();
        this.player = new Player(this.rooms.get("1"));
    }

    /**
     * Sets up everything required for assigning secret room string pieces to rooms and accessing the secret room
     */
    private void setUpSecretRoom() {
        // Make a random string for the secret roomString
        var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        var randStrB = new StringBuilder();
        Random rnd = new Random();
        while (randStrB.length() < 5) {
            int index = (int) (rnd.nextFloat() * chars.length());
            randStrB.append(chars.charAt(index));
        }
        var randStr = randStrB.toString();
        // Count the number of normal (not FORCED or special) rooms in the game
        var validRooms = this.rooms.values().stream().filter(r -> {
            try {
                Integer.parseInt(r.getRoomNumber());
                for (var dest : r.getMotionTable().getDirection())
                    if (dest.getDirection().equals("FORCED")) return false;
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }).toList();
        int numRooms = validRooms.size();
        // Split the random string into chunks based on the number of rooms
        switch (numRooms) {
            case 1 -> this.secretStringChunks.add(randStr);
            case 2 -> {
                this.secretStringChunks.add(randStr.substring(0, 2));
                this.secretStringChunks.add(randStr.substring(2));
            }
            case 3 -> {
                this.secretStringChunks.add(randStr.substring(0, 2));
                this.secretStringChunks.add(randStr.substring(2, 3));
                this.secretStringChunks.add(randStr.substring(3));
            }
            case 4 -> {
                this.secretStringChunks.add(randStr.substring(0, 1));
                this.secretStringChunks.add(randStr.substring(1, 2));
                this.secretStringChunks.add(randStr.substring(2, 3));
                this.secretStringChunks.add(randStr.substring(3));
            }
            default -> {
                for (int i = 0; i < 5; i++) this.secretStringChunks.add(randStr.substring(i, i + 1));
            }
        }
        System.out.println("Secret string chunks: " + String.join("", this.secretStringChunks));
        System.out.println("Number of rooms: " + randStr);
        // Ensure there exists a secret room in the game
        var room = this.rooms.get("SECRET_ROOM");
        if (room == null || numRooms == 0) throw new RuntimeException("No secret room found in game, or no normal rooms in the game.");
        this.rooms.remove("SECRET_ROOM");
        this.rooms.put(String.join("", this.secretStringChunks), room);
        // Randomly select rooms to display the secret string chunks in
        var rand = new Random();
        var usedRooms = new HashSet<Integer>();
        for (String secretStringChunk : this.secretStringChunks) {
            int num = rand.nextInt(numRooms);
            while (usedRooms.contains(num)) num = rand.nextInt(numRooms);
            validRooms.get(num).setSecretStringPiece(secretStringChunk);
            usedRooms.add(num);
        }
    }

    /**
     * Tokenizes the input string and returns a string array of tokens that represents the command.
     *
     * @param input string from the command line
     * @return a string array of tokens that represents the command.
     */
    public String[] tokenize(String input){
        input = input.toUpperCase();
        String[] commandArray = input.split(" ");
        if (Objects.equals(commandArray[0], "TAKE") || Objects.equals(commandArray[0], "DROP") ||
            Objects.equals(this.synonyms.get(commandArray[0]), "TAKE") || Objects.equals(this.synonyms.get(commandArray[0]), "DROP"))
            commandArray = input.split(" ", 2);
        int i = 0;
        while (i < commandArray.length) {
            if (this.synonyms.containsKey(commandArray[i])){
                commandArray[i] = this.synonyms.get(commandArray[i]);
            }
            i++;
        }
        return commandArray;
    }

    /**
     * Moves the player in the given direction, if possible.
     * Return false if the player wins or dies as a result of the move.
     *
     * @param direction the move command
     * @return false, if move results in death or a win (and game is over).  Else, true.
     */
    public boolean movePlayer(String direction) {
        direction = direction.toUpperCase();
        PassageTable motionTable = this.player.getCurrentRoom().getMotionTable(); //where can we move?
        if (!motionTable.optionExists(direction)) return true; //no move

        ArrayList<Passage> possibilities = new ArrayList<>();
        for (Passage entry : motionTable.getDirection()) {
            if (entry.getDirection().equals(direction)) { //this is the right direction
                possibilities.add(entry); // are there possibilities?
            }
        }

        //try the blocked passages first
        Passage chosen = null;
        for (Passage entry : possibilities) {
            if (chosen == null && entry.getIsBlocked()) {
                if (this.player.getInventory().contains(entry.getKeyName()) || this.player.getInventory().contains("SPIRIT OF ADVENTURE")) {
                    chosen = entry; //we can make it through, given our stuff
                    break;
                }
            } else { chosen = entry; } //the passage is unlocked
        }
        if (chosen == null) return true; //doh, we just can't move.
        String roomNumber = chosen.getDestinationRoom();
        Room room = this.rooms.get(roomNumber);
        this.player.setCurrentRoom(room);
        // Check if the player has visited all other rooms
        boolean achieved = true;
        for (var r : this.rooms.values()) {
            try {
                Integer.parseInt(r.getRoomNumber());
                boolean skip = false;
                for (var dest : r.getMotionTable().getDirection()) {
                    if (dest.getDirection().equals("FORCED")) {
                        skip = true;
                        break;
                    }
                }
                if (skip) continue;
                if (!Objects.equals(r.getRoomName(), this.player.getCurrentRoom().getRoomName()) && !r.getVisited()) {
                    achieved = false;
                    break;
                }
            } catch (NumberFormatException e) {
                if (Objects.equals(r.getRoomName(), "Secret Room") && !Objects.equals(r.getRoomName(), this.player.getCurrentRoom().getRoomName()) && !r.getVisited()) {
                    achieved = false;
                    break;
                }
            }
        }
        if (achieved) unlockAchievement("Master Adventurer");
        unlockAchievement("Adventure Is Out There");
        return !this.player.getCurrentRoom().getMotionTable().getDirection().get(0).getDirection().equals("FORCED");
    }

    /**
     * Chooses a troll to "encounter" randomly
     * @return the troll that was chosen
     */
    public Troll trollEncounter() {
        Director director = new Director();
        Random rand = new Random();
        int select =  rand.nextInt(10);
        if (Objects.equals(this.player.getCurrentRoom().getRoomName(), "Secret Room")) select = 3;
        if (select <= 3){
            CombatTBuilder builder = new CombatTBuilder();
            director.constructCombatTroll(builder);
            return builder.getResult(this.player);//player.attack, player.health
        }
        else if (select <= 7){
            GameTBuilder builder = new GameTBuilder();
            director.constructGameTroll(builder);
            return builder.getResult(this.player);
        }
        else {
            EventTBuilder builder = new EventTBuilder();
            director.constructEventTroll(builder);
            return builder.getResult(this.player);
        }
    }

    /**
     * Interprets the user's action.
     *
     * @param command String representation of the command.
     * @return null if nothing is special or a specific string output to display to the user
     */
    public String interpretAction(String command){
        String[] inputArray = tokenize(command); //look up synonyms
        // Check if the user entered any permutation of the secret pieces
        boolean secretStringUsed = true;
        for (var piece : this.secretStringChunks) {
            if (!inputArray[0].contains(piece)) {
                secretStringUsed = false;
                break;
            }
        }
        secretStringUsed = secretStringUsed && inputArray[0].length() == 5;
        if (secretStringUsed) {
            this.player.getCurrentRoom().visit();
            unlockAchievement("Adventure Is Out There");
            this.player.setCurrentRoom(this.rooms.get(String.join("", this.secretStringChunks)));
            unlockAchievement("X Marks The Spot");
            return null;
        }
        PassageTable motionTable = this.player.getCurrentRoom().getMotionTable(); //where can we move?

        if (motionTable.optionExists(inputArray[0])) {
            if (!movePlayer(inputArray[0])) {
                if (this.player.getCurrentRoom().getMotionTable().getDirection().get(0).getDestinationRoom().equals("0")) {
                    unlockAchievement("Adept Adventurer");
                    return "GAME OVER";
                }
                else return "FORCED";
            } //something is up here! We are dead or we won.
            // successful movement
            return null;
        } else if (Arrays.asList(this.actionVerbs).contains(inputArray[0])) {
            if (inputArray[0].equals("QUIT")) { return "GAME OVER"; } //time to stop!
            else if (inputArray[0].equals("INVENTORY") && this.player.getInventory().isEmpty()) return "INVENTORY IS EMPTY";
            else if (inputArray[0].equals("INVENTORY") && !this.player.getInventory().isEmpty()) return "THESE OBJECTS ARE IN YOUR INVENTORY:\n" + this.player.getInventory().toString();
            else if (inputArray[0].equals("TAKE") && inputArray.length < 2) return "THE TAKE COMMAND REQUIRES AN OBJECT";
            else if (inputArray[0].equals("DROP") && inputArray.length < 2) return "THE DROP COMMAND REQUIRES AN OBJECT";
            else if (inputArray[0].equals("TAKE")) {
                String displayName = String.join(" ", Arrays.copyOfRange(inputArray, 1, inputArray.length));
                for (var pair : this.synonyms.entrySet())
                    if (Objects.equals(pair.getValue(), displayName)) displayName = pair.getKey();
                if (this.player.getCurrentRoom().checkIfObjectInRoom(String.join(" ", Arrays.copyOfRange(inputArray, 1, inputArray.length)))) {
                    this.player.takeObject(String.join(" ", Arrays.copyOfRange(inputArray, 1, inputArray.length)));
                    return "YOU HAVE TAKEN:\n" + displayName;
                } else {
                    return "THIS OBJECT IS NOT HERE:\n" + displayName;
                }
            }
            else if (inputArray[0].equals("DROP")) {
                String displayName = String.join(" ", Arrays.copyOfRange(inputArray, 1, inputArray.length));
                for (var pair : this.synonyms.entrySet())
                    if (Objects.equals(pair.getValue(), displayName)) displayName = pair.getKey();
                if (this.player.checkIfObjectInInventory(String.join(" ", Arrays.copyOfRange(inputArray, 1, inputArray.length)))) {
                    this.player.dropObject(String.join(" ", Arrays.copyOfRange(inputArray, 1, inputArray.length)));
                    return "YOU HAVE DROPPED:\n" + displayName;
                } else {
                    return "THIS OBJECT IS NOT IN YOUR INVENTORY:\n" + displayName;
                }
            }
        }
        return "INVALID COMMAND.";
    }

    /**
     * Unlocks an achievement if it is not already unlocked. Does nothing if the achievement is already unlocked.
     * @param name the name of the achievement to unlock
     */
    private void unlockAchievement(String name) {
        var achievable = this.achievables.get(name);
        if (achievable.getState() == AchievementState.Unlocked) return;
        achievable.unlock();
        autosaveModel();
    }

    /**
     * Getter method for directoryName
     * @return directoryName
     */
    public String getDirectoryName() {
        return this.directoryName;
    }

    /**
     * Getter method for helpText
     * @return helpText
     */
    public String getInstructions() {
        return helpText;
    }

    /**
     * Getter method for player
     * @return the Player object of this AdventureGame
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Getter method for rooms 
     * @return map of key value pairs (integer to room)
     */
    public HashMap<String, Room> getRooms() {
        return this.rooms;
    }

    /**
     * Getter method for synonyms 
     * @return map of key value pairs (synonym to command)
     */
    public HashMap<String, String> getSynonyms() {
        return this.synonyms;
    }

    /**
     * Setter method for helpText
     * @param help which is text to set
     */
    public void setHelpText(String help) {
        this.helpText = help;
    }


    /**
     * Getter method for achievables
     * @return hashmap that maps strings to achievable actions that the achievements observe
     */
    public HashMap<String, AchievableAction> getAchievables() { return this.achievables; }

    /**
     * Getter method for achievements
     * @return list of achievements
     */
    public ArrayList<AdventureAchievement> getAchievements() { return this.achievements; }

    /**
     * Facilitates the retrieval of a specific achievement by name
     * @param name the name of the achievement to get
     * @return the achievement with the given name, or null if it does not exist
     */
    public AdventureAchievement getAchievement(String name) {
        return this.achievements.stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Facilitates the addition of an object name to the list of object names in the game
     * @param name the name of the object to add
     */
    public void addObjectName(String name) {
        this.objectNames.add(name);
    }

    /**
     * Getter for the list of all the object names in the game
     * @return a list of all the object names in the game
     */
    public List<String> getObjectNames() {
        return this.objectNames;
    }
}