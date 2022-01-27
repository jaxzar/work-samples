/** TextRPG (TRPG) Game Engine
 * TextRPG combines interactive fiction (IF) with RPG elements, sort of like a single-player MUD.
 * This game engine allows you create your own such game from a simple template, and write the
 * prompts and game dialogue in text files, where each file name is the name of the prompt that
 * you set up when creating the game.
 *
 * For example, in some room, you might have a lengthy passage of dialogue to set the scene.
 * Rather than having to type this laboriously into some GUI editor or string literal buried
 * in game engine code, simply name it something like dialogue01.rm and the game engine will
 * pick it up automatically when you register the opening dialog sequence as "dialogue01".
 * When it comes time in the game to display this text, the file will be read at that point,
 * saving valuable loading time. These days, hard drives are fast enough that this shouldn't
 * be an issue, but if it is, you can simply set the option to have all prompts and game text
 * pre-loaded, which will make game initialization take longer, but will result in quicker
 * responses during gameplay.
 */
public class GameEngine implements Runnable {

    // templatized game configuration
    private boolean quit = false; // player decides to quit

    public GameEngine(int option1, int option2, int etc) {
        CharacterSheet cs;

        // more initialization
    }

    public void run() {
        // start up game CLI and display welcome/opening prompt
        Scanner in = new Scanner(System.in);

        // input player name
        System.out.println("Welcome to TextRPG Engine. Please enter your adventurer name: ");
        String name = in.read();

        cs = new CharacterSheet(name);

        // if the file already exists, load saved game
        System.out.println("Welcome back, " + name);
        Campaign c = loadSavedCampaign(name);

        // if not, start a new game (player chooses campaign here)

        Campaign c = new Campaign( playerChoice );
        Scenario startingScenario = c.getStartingScenario();
        Room currentRoom = startingScenario.getFirstRoom();
        String userCommand = "";

        while (!quit) {
            currentRoom.prompt();
            // read user input
            userCommand = in.read();
            currentRoom = interpret(userCommand);
        }

        // prompt user to save game
    }

    public Campaign loadSavedCampaign(String playerName) {

    }

    public void interpret(String command) {
        command.toLowerCase();
        String[] words = command.split(" ");
        for (String word: words) {
            if (word.equals("inventory")) word = "inv";
            switch (word) {
                case "quit": quit = true; break;
                case "save": saveGame(); break;
                case "north": currentRoom = currentRoom.travel(Room.NORTH); break;
                case "east": currentRoom = currentRoom.travel(Room.NORTH); break;
                case "south": currentRoom = currentRoom.travel(Room.NORTH); break;
                case "west": currentRoom = currentRoom.travel(Room.NORTH); break;
                case "inv": displayInventory(); break;
                case "stats": displayStats(); break;
                case "look": currentRoom.lookPrompt(); break;
                case "use": useItem = true; break;
                case "fight": executeBattle(); break;
                default: if (useItem) applyItem(word); break;
            }
        }
    }

    public void executeBattle() {
    }

    public void applyItem(String name) {
        // check inventory for item
        // if it does not exist, tell user "no such item"
        // otherwise, call item.apply
    }

    public void displayInventory() {
    }

    public void displayStats() {
    }
}
