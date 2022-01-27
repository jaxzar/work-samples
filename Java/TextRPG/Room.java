public class Room {

    private static int roomCount = 0;
    private int roomNum;
    private String name;
    private Scenario parent;
    private List<String> prompts;
    private List<Item> items;
    private String roomPrompt;
    private Room north;
    private Room south;
    private Room east;
    private Room west;

    public Room(Scenario s, String name) {
        roomCount++;
        roomNum = roomCount;
        this.name = name;
        parent = s;
        prompts = parent.addRoom(this);
    }

    public void linkRooms(Room n, Room e, Room s, Room w) {
        north = n;
        east = e;
        south = s;
        west = w;
    }

    public void setMainPrompt(String rp) {
        roomPrompt = rp;
    }

    public String getFilename() {
        return parent.getFilename() + name + ".dat";
    }

    public String linkedRooms() {
        StringBuilder sb = new StringBuilder(roomPrompt);
        sb.append("Room Exits: ");
        if (north != null) {
            sb.append("   [North - ").append(north).append(" ]\n");
        }
        if (east != null) {
            sb.append("   [East - ").append(east).append(" ]\n");
        }
        if (south != null) {
            sb.append("   [South - ").append(south).append(" ]\n");
        }
        if (west != null) {
            sb.append("   [West - ").append(west).append(" ]\n");
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
