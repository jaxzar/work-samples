public class GameTemplate extends GameEngine {

    // Name this template class whatever you want. This is where you set up and create your game.
    // Change the options below to configure your game. Then set this as your main class.
    // To run your game, in a command line window, type the following:
    //
    //     javac *.java
    //
    // then type
    //
    //     java GameTemplate
    //
    // (if you renamed it, type the new name instead)
    public GameTemplate(String[] args) {
        super(
            option1,
            option2,
            args
        );
        Campaign c = new Campaign("My Campaign");
        Scenario s1 = new Scenario("The Tavern");
        Scenario s2 = new Scenario("The Valley");
        c.addScenario(s1);
        c.addScenario(s2);
        c.setPrereq(s2, s1); // make s1 a prerequisite of completion for s2
        Room r1 = new Room("The Inn");
        Room r2 = new Room("The Bar");
        s1.addRoom(r1);
        s1.addRoom(r2);
        //           n,    e     s     w
        r1.linkRooms(r2,   null, null, null);
        r2.linkRooms(null, null, r1,   null);
    }

    // This is part of the template and shouldn't require any changes.
    // It ensures your game begins automatically when this is run as the main class.
    public static void main(String[] args) {
        (new Thread(new GameTemplate(args))).run();
    }
}
