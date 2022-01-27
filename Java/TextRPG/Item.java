public interface Item {
    public String getDescription();
    public String getName();
    public List<Action> getActions();
    public void executeAction(Action a);
}
