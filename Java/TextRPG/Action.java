public abstract class Action {

    protected List<Item> requiredItems;
    protected Item target;
    protected String command;
    protected String pDescription; // prompt name

    public Action(String command, String descPrompt) {
        this.command = command;
        this.pDescription = descPrompt;
    }

    public abstract void execute(List<Item> playerInventory);

    public String getCommand() {
        return command;
    }

    // maybe make PromptReader package private to enable inner project access?
    // so we don't have to pass it around as a parameter
    public String getDescription(PromptReader pr) {
        return pr.getPrompt(pDescription);
    }

    public List<Item> getRequiredItems() {
        return requiredItems;
    }
    public void setTarget(Item target) {
        this.target = target;
    }
    public Item getTarget() {
        return this.target;
    }
    public void requireItem(Item item) {
        requiredItems.add();
    }
}
