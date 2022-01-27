/** Campaign.java
 * A campaign is a set of scenarios that are linked, and can be replayed.
 * The scenarios in a campaign don't necessarily have to be played in
 * order, much like how characters can travel back and forth at their will
 * between rooms in a scenario (assuming the rooms are not locked for some
 * reason). Linear scenarios are certainly an option, but not the only one.
 */
public class Campaign {

    private static int campaignCount = 0;
    private int campaignNum;
    private List<Scenario> scenarios;
    private Map<Room,List<String>> prompts;
    
    /**
     * Initialize a campaign.
     */
    public Campaign(String name) {
        campaignCount++;
        campaignNum = campaignCount;
        prompts = new HashMap<Room,List<String>>();
    }

    public void addScenario(Scenario s) {
        scenarios.add(s);
    }

    public String addPrompt(Room r) {
        // store prompts

    }

    // with prompts, you can have
    // 1 - a sequence of linear events (prompt triggers)
    // 2 - a pool of potential events, from which a random one is chosen
    // 3 - a non-linear sequence of clues, similar to a hidden-object-game
    //     (one item is a key that unlocks something in the next room, etc.)
}
