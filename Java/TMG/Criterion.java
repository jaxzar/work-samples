package glsoft.tmg;

import java.util.function.Predicate;

public class Criterion {
    public static final int NTYPE_YELLOW = 0;
    public static final int NTYPE_GREEN = 1;
    public static final int NTYPE_PURPLE = 2;
    public static final int NTYPE_BLUE = 3;
    public Predicate<String> evaluator;
    int verifier;
    int yellow;
    int green;
    int purple;
    int blue;

    public Criterion(int y, int g, int p, int b, Predicate<String> eval) {
        this.yellow = y;
        this.green = g;
        this.purple = p;
        this.blue = b;
        this.evaluator = eval;
    }
    public void setVerifier(int v) {
        this.verifier = v;
    }
    public boolean evaluate(String code) {
        return evaluator.test(code);
    }
    public String getCode(int nType) {
        String result = "";
        switch (nType) {
            case NTYPE_YELLOW:
                result = new String("Yellow Code [" + yellow + "]");
                break;

            case NTYPE_GREEN:
                result = new String("Green Code [" + green + "]");
                break;

            case NTYPE_PURPLE:
                result = new String("Purple Code [" + purple + "]");
                break;

            case NTYPE_BLUE:
                result = new String("Blue Code [" + blue + "]");
                break;
        }
        return result;
    }
}
