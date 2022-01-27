public class CharacterStat {
    CharacterStat pair;
    Random rand;
    int stat = 0;

    public CharacterStat() {
        rand = new Random();
        reroll();
    }

    public CharacterStat(CharacterStat pairedStat) {
        pairedStat = new CharacterStat();
        pairedStat.setPair(this);
        pair = pairedStat;
        reroll();
    }

    public void setPair(CharacterStat cs) {
        pair = cs;
    }

    public void reroll() {
        int a = rand.nextInt(6)+1;
        int b = rand.nextInt(6)+1;
        int c = rand.nextInt(6)+1;
        stat = a + b + c;
    }

    @Override
    public String toString() {
        return (String)stat;
    }
}
