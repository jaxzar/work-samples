public class CharacterSheet {

    private String name;
    private CharacterStat strength;
    private CharacterStat dexterity;
    private CharacterStat speed;
    private CharacterStat armor;
    private CharacterStat intelligence;
    private CharacterStat constitution;
    private int strDexPoints;
    private int spdArmPoints;
    private int intConPoints;
    private Race race;
    private List<Moves> moves; // class
    private List<Skill> skills;
    private List<Item> items;
    private Weapon weapon[];
    private int gold;

    public CharacterSheet(String name) {
        this.name = name;
        weapon = new Weapon[4];
        strength = new CharacterStat(dexterity);
        speed = new CharacterStat(armor);
        intelligence = new CharacterStat(constitution);
    }

    /**
     * Save the character info to a file.
     */
    public void save() throws IOException {
        String filename = name + ".dat";
        ObjectOutputStream os = new ObjectOutputStream(filename);
        os.writeObject(this);
        os.flush();
    }

    public static CharacterSheet loadCharacter(String name) throws IOException {
        ObjectInputStream is = new ObjectInputStream(name + ".dat");
        CharacterSheet cs = is.readObject();
        is.close();
        return cs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Character: ");
        sb.append(name).append("\n");
        sb.append("---------------------------");
        sb.append("Str: ").append(strength).append("  Dex: ").append(dexterity);
        sb.append("  [").append(strDexPoints.append("] \n");
        sb.append("Spd: ".append(speed).append("  Arm: ").append(armor);
        sb.append("  [").append(spdArmPoints).append("] \n");
        sb.append("Int: ").append(intelligence).append("  Con: ").append(constitution);
        sb.append("  [").append(intConPoints).append("] \n");
        sb.append("---------------------------");
        // print out items and other stuff
        sb.append("---------------------------");
        return sb.toString();
    }
}
