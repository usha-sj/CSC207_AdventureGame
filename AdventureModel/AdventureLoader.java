package AdventureModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class AdventureLoader. Loads an adventure from files.
 */
public class AdventureLoader {
    /** the game to return */
    private AdventureGame game;
    /** the name of the adventure */
    private String adventureName;

    /**
     * Initializes attributes for the AdventureLoader
     * @param game the game that is loaded
     * @param directoryName the directory in which game files live
     */
    public AdventureLoader(AdventureGame game, String directoryName) {
        this.game = game;
        this.adventureName = directoryName;
    }

     /**
     * Loads the game from directory
     *
     * @throws IOException Throws if there was an error reading the file
     */
    public void loadGame() throws IOException {
        parseAchievements();
        parseRooms();
        parseObjects();
        parseSynonyms();
        this.game.setHelpText(parseOtherFile("help"));
    }

    /**
    * Parses the Achievements File
    *
    * @throws IOException Throws if there was an error reading the file
    */
    private void parseAchievements() throws IOException {
        String achievementFileName = "achievements/achievements.txt";
        BufferedReader buff = new BufferedReader(new FileReader(achievementFileName));
        String line = buff.readLine();
        while (line != null) {
            AchievableAction action = new AchievableAction();
            String name = line;
            String description = buff.readLine();
            int tier = Integer.parseInt(buff.readLine());
            AdventureAchievement achievement = new AdventureAchievement(name, description, AchievementTier.values()[tier - 1]);
            action.attach(achievement);
            this.game.getAchievables().put(name, action);
            this.game.getAchievements().add(achievement);
            buff.readLine();
            line = buff.readLine();
        }
    }

     /**
     * Parse Rooms File
     *
     * @throws IOException Throws if there was an error reading the file
     */
    private void parseRooms() throws IOException {

        String roomNumber;

        String roomFileName = this.adventureName + "/rooms.txt";
        BufferedReader buff = new BufferedReader(new FileReader(roomFileName));

        while (buff.ready()) {

            String currRoom = buff.readLine(); // first line is the number of a room

            roomNumber = currRoom; //current room number

            // now need to get room name
            String roomName = buff.readLine();

            // now we need to get the description
            String roomDescription = "";
            String line = buff.readLine();
            while (!line.equals("-----")) {
                roomDescription += line + "\n";
                line = buff.readLine();
            }
            roomDescription += "\n";

            // now we make the room object
            Room room = new Room(roomName, roomNumber, roomDescription, adventureName);

            // now we make the motion table
            line = buff.readLine(); // reads the line after "-----"
            while (line != null && !line.equals("")) {
                String[] part = line.split(" \s+"); // have to use regex \\s+ as we don't know how many spaces are between the direction and the room number
                String direction = part[0];
                String dest = part[1];
                if (dest.contains("/")) {
                    String[] blockedPath = dest.split("/");
                    String dest_part = blockedPath[0];
                    String object = blockedPath[1];
                    Passage entry = new Passage(direction, dest_part, object);
                    room.getMotionTable().addDirection(entry);
                } else {
                    Passage entry = new Passage(direction, dest);
                    room.getMotionTable().addDirection(entry);
                }
                line = buff.readLine();
            }
            this.game.getRooms().put(room.getRoomNumber(), room);
        }

    }

     /**
     * Parse Objects File
     *
     * @throws IOException Throws if there was an error reading the file
     */
    public void parseObjects() throws IOException {

        String objectFileName = this.adventureName + "/objects.txt";
        BufferedReader buff = new BufferedReader(new FileReader(objectFileName));

        while (buff.ready()) {
            String objectName = buff.readLine();
            String objectDescription = buff.readLine();
            String objectLocation = buff.readLine();
            String separator = buff.readLine();
            if (separator != null && !separator.isEmpty())
                System.out.println("Formatting Error!");
            Room location = this.game.getRooms().get(objectLocation);
            AdventureObject object = new AdventureObject(objectName, objectDescription, location);
            location.addGameObject(object);
            this.game.addObjectName(objectName);
        }

    }

     /**
     * Parse Synonyms File
     *
     * @throws IOException Throws if there was an error reading the file
     */
    public void parseSynonyms() throws IOException {
        String synonymsFileName = this.adventureName + "/synonyms.txt";
        BufferedReader buff = new BufferedReader(new FileReader(synonymsFileName));
        String line = buff.readLine();
        while(line != null){
            String[] commandAndSynonym = line.split("=");
            String command1 = commandAndSynonym[0];
            String command2 = commandAndSynonym[1];
            this.game.getSynonyms().put(command1,command2);
            line = buff.readLine();
        }

    }

    /**
     * Parse Files other than Rooms, Objects and Synonyms
     *
     * @param fileName the file to parse
     * @throws IOException throws if there was an error when reading the file
     * @return The contents of the file as a String
     */
    public String parseOtherFile(String fileName) throws IOException {
        String text = "";
        fileName = this.adventureName + "/" + fileName + ".txt";
        BufferedReader buff = new BufferedReader(new FileReader(fileName));
        String line = buff.readLine();
        while (line != null) { // while not EOF
            text += line+"\n";
            line = buff.readLine();
        }
        return text;
    }

}
