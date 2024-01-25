package glsoft.tmg;

import java.util.Map;
import java.util.Set;

public class Verifier {
    public static Map<Integer,Set<Integer>> verifiers;

    int vClass;
    int index;
    Criterion[] criteria;
    public Verifier(int i, Criterion[] c, int v) {
        this.index = i;
        this.vClass = v;
        this.criteria = c;
        for (Criterion cx: c) {
            cx.setVerifier(index);
        }
        if (verifiers != null) {
            verifiers.get(v).add(i);
        }
    }

    public String getDescription(String code, int nType) {
        // prints the matching criterion for a given code
        StringBuilder sb = new StringBuilder("Verifier [");
        for (Criterion c: criteria) {
            if (c.evaluate(code)) {
                sb.append(index).append("]: ").append(c.getCode(nType));
                break;
            }
        }
        return sb.toString();
    }

    public static void setVerifiers(Map<Integer,Set<Integer>> vSet) {
        verifiers = vSet;
    }
}
