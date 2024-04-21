package AdventureModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class contains the information about a 
 * room in the Adventure Game.
 */
public class Room implements Serializable {

    /**
     * The name of the current adventure
     */
    private final String adventureName;
    /**
     * The number of the room.
     */
    private String roomNumber;

    /**
     * The name of the room.
     */
    private String roomName;

    /**
     * The description of the room.
     */
    private String roomDescription;

    /**
     * The passage table for the room.
     */
    private PassageTable motionTable = new PassageTable();

    /**
     * The list of objects in the room.
     */
    public ArrayList<AdventureObject> objectsInRoom = new ArrayList<AdventureObject>();

    /**
     * A boolean to store if the room has been visited or not
     */
    private boolean isVisited;
    /**
     * The secret string piece for the room.
     */
    private String secretStringPiece;

    /**
     * AdvGameRoom constructor.
     *
     * @param roomName: The name of the room.
     * @param roomNumber: The number of the room.
     * @param roomDescription: The description of the room.
     * @param adventureName The name of the current adventure
     */
    public Room(String roomName, String roomNumber, String roomDescription, String adventureName){
        this.roomName = roomName;
        this.roomNumber = roomNumber;
        this.roomDescription = roomDescription;
        this.adventureName = adventureName;
        this.isVisited = false;
        this.secretStringPiece = "";
    }
    /**
     * Returns a comma delimited list of every
     * object's description that is in the given room,
     * e.g. "a can of tuna, a beagle, a lamp".
     *
     * @return delimited string of object descriptions
     */
    public String getObjectString() {
        var s = new StringBuilder();
        for (var obj : this.objectsInRoom) {
            s.append(obj.getDescription());
            s.append(',');
        }
        if (!s.isEmpty())
            s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

    /**
     * Returns a comma delimited list of every
     * move that is possible from the given room,
     * e.g. "DOWN,UP,NORTH,SOUTH".
     *
     * @return delimited string of possible moves
     */
    public String getCommands() {
        var set = new HashSet<String>();
        for (var com : this.getMotionTable().passageTable)
            set.add(com.getDirection());
        return String.join(",", set);
    }

    /**
     * This method adds a game object to the room.
     *
     * @param object to be added to the room.
     */
    public void addGameObject(AdventureObject object){
        this.objectsInRoom.add(object);
    }

    /**
     * This method removes a game object from the room.
     *
     * @param object to be removed from the room.
     */
    public void removeGameObject(AdventureObject object){
        this.objectsInRoom.remove(object);
    }

    /**
     * This method checks if an object is in the room.
     *
     * @param objectName Name of the object to be checked.
     * @return true if the object is present in the room, false otherwise.
     */
    public boolean checkIfObjectInRoom(String objectName){
        for (AdventureObject adventureObject : objectsInRoom) {
            if (adventureObject.getName().equals(objectName)) return true;
        }
        return false;
    }

    /**
     * Sets the visit status of the room to true.
     */
    public void visit(){
        isVisited = true;
    }

    /**
     * Getter for returning an AdventureObject with a given name
     *
     * @param objectName: Object name to find in the room
     * @return AdventureObject
     */
    public AdventureObject getObject(String objectName){
        for (AdventureObject adventureObject : objectsInRoom)
            if (adventureObject.getName().equals(objectName)) return adventureObject;
        return null;
    }

    /**
     * Getter method for the number attribute.
     *
     * @return number of the room
     */
    public String getRoomNumber() {
        return this.roomNumber;
    }

    /**
     * Getter method for the description attribute.
     *
     * @return description of the room
     */
    public String getRoomDescription() {
        return this.roomDescription.replace("\n", " ");
    }


    /**
     * Getter method for the name attribute.
     *
     * @return name of the room
     */
    public String getRoomName() {
        return this.roomName;
    }


    /**
     * Getter method for the visit attribute.
     *
     * @return visit status of the room
     */
    public boolean getVisited() {
        return this.isVisited;
    }


    /**
     * Getter method for the motionTable attribute.
     *
     * @return motion table of the room
     */
    public PassageTable getMotionTable() {
        return this.motionTable;
    }

    /**
     * Getter method for the secretStringPiece attribute.
     *
     * @return empty string or a piece of the secret string (used to access the secret room)
     */
    public String getSecretStringPiece() {
        return secretStringPiece;
    }

    /**
     * Setter method for the secretStringPiece attribute.
     *
     * @param secretStringPiece A piece of the secret string (used to access the secret room)
     */
    public void setSecretStringPiece(String secretStringPiece) {
        this.secretStringPiece = secretStringPiece;
    }
}
