/** Scenario.java
 * TextRPG (TRPG) Game Engine
 * A scenario is a set of rooms that are linked, and can be traveled between.
 * Think of a scenario like a large building or a multi-stage movie set. Each
 * room is a single set, and you can go between the rooms as needed. You can
 * also create linear rooms where the next room is locked, and progress can't
 * be made until one or more tasks are completed in the current room.
 * @author Tim Baker
 */
public class Scenario {

    private static int scenarioCount = 0;
    private boolean isLocked;
    private int scenarioNum;
    private Scenario prereq;
    private Campaign parent;
    private List<String> prompts;
    private List<Room> rooms;
    private Room currentRoom;
    private boolean started;
    private boolean completed;

    public Scenario(Campaign c, String name) {
        scenarioCount++;
        scenarioNum = scenarioCount;
        isLocked = false;
        this.name = name;
        parent = c;
        prompts = parent.getPrompts(this);
    }

    public void setPrereq(Scenario s) {
        isLocked = true;
        prereq = s;
    }

    public void addRoom(Room r) {
        rooms.add(r);
    }

    public List<String> getPrompts() {
        return parent.getRoomPrompts(r);
    }

    public String getFilename() {
        return parent.getFilename() + "s" + scenarioCount;
    }

    public void start() {
        if (isLocked) {
            if (!prereq.isCompleted()) {
                System.out.println("You must first complete " + prereq);
            } else {
                isLocked = false;
            }
        }
        if (!isLocked) {
            // begin scenario (initiate prompt sequence)
        }
    }
}
