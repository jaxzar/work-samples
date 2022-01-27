public abstract class IdentifiableItem implements Item {
    public abstract boolean isIdentified();
    public abstract String getDescription();
    public abstract String getName();
    public abstract List<Action> getActions();
    public abstract void executeAction(Action a);
}
