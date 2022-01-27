import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

public class Mastermind {
    public static Random rand;
    public static final int E_HUMAN = 0;
    public static final int E_COMPUTER = 1;
    public static final int E_COLOR_BLANK = 0;
    public static final int E_COLOR_A = 1;
    public static final int E_COLOR_B = 2;
    public static final int E_COLOR_C = 3;
    public static final int E_COLOR_D = 4;
    public static final int E_COLOR_E = 5;
    public static final int E_COLOR_F = 6;
    public static final int E_COLOR_G = 7;
    public static final int E_COLOR_H = 8;
    public static final int E_COLOR_I = 9;
    public static final int E_COLOR_J = 10;
    public static final int E_COLOR_K = 11;
    public static final int E_COLOR_L = 12;
    public static final int E_STANDARD = 0; // no repeated colors or blanks allowed
    public static final int E_HARD = 1; // allow repeated colors
    public static final int E_MASTER = 2; // allow blanks
    public static final int E_RANDOM = 0; // random guessing strategy
    public static final int E_AMATEUR = 1; // amateur guessing strategy
    public static final int E_SOLVER = 2; // intelligent guessing strategy
    public static int[] code;
    public static boolean done = false;
    public static boolean debug = false;
    public static boolean validGuess = false;
    public static boolean hintsOff = false;
    public static boolean progMsg1 = false;
    public static boolean progMsg2 = false;
    public static boolean progMsg3 = false;
    public static int setter = E_COMPUTER;
    public static int solver = E_HUMAN;
    public static int pegs;
    public static int colors;
    public static int difficulty;
    public static int white;
    public static int black;
    public static int guesses;
    public static int strategy = -1;
    public static int offset;
    public static String stratStr;
    public static BufferedReader in;
    public static InputStreamReader isr;
    public static String cmd;
    public static String codeStr;
    public static String guess;
    public static Set<String> guessHistory;

    public static String promptSolver(int ePlayer) throws IOException {
        String guess = "";
        switch (ePlayer) {
            case E_HUMAN:
                System.out.print("Please enter your next guess: ");
                guess = in.readLine();
                break;
            case E_COMPUTER:
                if (strategy == -1) {
                    System.out.println("random(R), amateur(A), solver(S)");
                    System.out.print("Please choose a guessing strategy for the computer: ");
                    stratStr = in.readLine();
                    strategy = E_RANDOM;
                    if (stratStr.equalsIgnoreCase("a")) {
                        strategy = E_AMATEUR;
                    } else if (stratStr.equalsIgnoreCase("s")) {
                        strategy = E_SOLVER;
                    }
                }
                switch (strategy) {
                    case E_RANDOM:
                        // just pick a random guess
                        guess = "";
                        offset = (difficulty == E_MASTER ? 0 : 1);
                        for (int i = 0; i < pegs; i++) {
                            guess += display(rand.nextInt(colors - offset) + offset);
                        }
                        while (guessHistory.contains(guess)) {
                            guess = "";
                            // reset and pick again
                            for (int i = 0; i < pegs; i++) {
                                guess += display(rand.nextInt(colors - offset) + offset);
                            }
                        }
                        break;
                    case E_AMATEUR:
                        // semi-random; choose groupings until we get good information
                        // slowly narrow down colors one by one by process of elimination
                        // then narrow down their positions by looking at the black pegs
                        break;
                    case E_SOLVER:
                        // compare each guess to each other guess (cartesian product)
                        // 1. compare number of white pegs to blank pegs
                        //    a. blank-white colors cannot be part of the solution
                        //    b. always-white colors MUST be part of the solution
                        //    c. slots that received white for a given color can have that color possibility removed
                        // 2. compare number of black pegs to white pegs
                        //    a. black-white indicates that a particular color is not the black peg
                        //    b. always-black colors are likely correct
                        // 3. look at colors not in the guess
                        //    a. if the number of colors not in the guess = number of blanks, all those
                        //       colors are part of the solution
                        // 4. if no clues, none of the guessed colors are in the solution
                        break;
                    default:
                        System.out.println("Whoops! This level of guessing strategy has not yet been implemented.");
                        System.exit(0);
                        break;
                }
                break;
            default:
                break;
        }
        return guess;
    }
    public static void promptSetter(int ePlayer) throws IOException {
        boolean isValid = false;
        switch (ePlayer) {
            case E_HUMAN:
                System.out.println("(only use letters A thru " + display(colors) + ")");
                System.out.print("Please enter your secret code: ");
                codeStr = in.readLine();
                while (!isValid) {
                    if (!validateCode(codeStr)) {
                        System.out.println("Invalid code. Only letters A thru " + display(colors) + " are available.");
                    } else {
                        isValid = true;
                    }
                    if (isValid) {
                        if (!validateRepeats(codeStr)) {
                            isValid = false;
                            System.out.println("Invalid code. Repeated colors only allowed for HARD difficulty and above.");
                        }
                    }
                    if (isValid) {
                        if (!validateBlanks(codeStr)) {
                            isValid = false;
                            System.out.println("Invalid code. Blanks only allowed for MASTER difficulty.");
                        }
                    }
                    if (!isValid) {
                        System.out.print("Please enter your secret code: ");
                        codeStr = in.readLine();
                    }
                }
                code = setCode(codeStr);
                break;
            case E_COMPUTER:
                code = chooseRandomCode();
                System.out.println("The computer has set up a secret code.");
                if (debug) {
                    System.out.print("Your lucky numbers for the week are: ");
                    for (int i = 0; i < pegs; i++) {
                        System.out.print(code[i] + " ");
                    }
                    System.out.println();
                }
                break;
            default:
                break;
        }
    }
    public static void validateGuess(String g) {
        validGuess = guessHistory.add(g);
        if (!validGuess) {
            System.out.println("You have already guessed that sequence! Each guess must be unique.");
            System.out.println("The same guess will always receive the same clues for one secret code.");
        }

        if (validGuess) {
            if (g.length() != pegs) {
                if (g.equalsIgnoreCase("q")) {
                    System.exit(0);
                }
                System.out.println("Your guess is not the right length! You asked for a code of length " + pegs + ".");
                validGuess = false;
            }
        }

        if (validGuess) {
            for (int i = 0; i < g.length(); i++) {
                if (translate(g.charAt(i)) < E_COLOR_BLANK || translate(g.charAt(i)) > colors) {
                    validGuess = false;
                    System.out.println("Your guess contains invalid letters! You asked for a game with only " + colors + " colors.");
                }
            }
        }
    }
    public static boolean validateCode(String c) {
        for (int i = 0; i < c.length(); i++) {
            if (translate(c.charAt(i)) < E_COLOR_BLANK || translate(c.charAt(i)) > E_COLOR_L) {
                return false;
            }
        }
        return true;
    }
    public static boolean validateRepeats(String c) {
        Set<Character> charSet;
        if (difficulty == E_STANDARD) {
            charSet = new HashSet<Character>();
            for (int i = 0; i < c.length(); i++) {
                if (!charSet.add(c.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean validateBlanks(String c) {
        if (difficulty < E_MASTER) {
            for (int i = 0; i < c.length(); i++) {
                if (translate(c.charAt(i)) == E_COLOR_BLANK) {
                    return false;
                }
            }
        }
        return true;
    }
    public static int[] chooseRandomCode() {
        int newCode[] = new int[pegs];
        Set<Integer> charSet = null;
        if (difficulty == E_STANDARD) {
            charSet = new HashSet<Integer>();
        }
        offset = (difficulty == E_MASTER ? 0 : 1);
        for (int i = 0; i < pegs; i++) {
            newCode[i] = rand.nextInt(colors - offset) + offset;
            if (charSet != null) {
                // try to add to set
                while (!charSet.add(newCode[i])) {
                    // re-choose until it adds successfully to avoid duplicates
                    newCode[i] = rand.nextInt(colors - offset) + offset;
                }
            }
        }
        return newCode;
    }
    public static int[] setCode(String sCode) {
        int newCode[] = new int[pegs];
        for (int i = 0; i < sCode.length(); i++) {
            newCode[i] = translate(sCode.charAt(i));
        }
        return newCode;
    }
    public static boolean evaluateGuess(String guess) {
        int[] colorCount = new int[13];
        white = 0;
        black = 0;

        validateGuess(guess);
        if (validGuess) {
            for (int i = 0; i < colors; i++) {
                colorCount[i] = 0;
            }
            for (int i = 0; i < pegs; i++) {
                colorCount[code[i]]++; // count up occurrences of each color
            }

            for (int i = 0; i < pegs; i++) {
                int gt = translate(guess.charAt(i));
                if (gt == code[i]) {
                    black++;
                } else if (colorCount[gt] > 0) {
                    colorCount[gt]--;
                    white++;
                }
            }
        }
        return (black == pegs);
    }
    public static int translate(char c) {
        int e = E_COLOR_BLANK; // blank space
        switch (c) {
            case 'A': e = E_COLOR_A; break;
            case 'B': e = E_COLOR_B; break;
            case 'C': e = E_COLOR_C; break;
            case 'D': e = E_COLOR_D; break;
            case 'E': e = E_COLOR_E; break;
            case 'F': e = E_COLOR_F; break;
            case 'G': e = E_COLOR_G; break;
            case 'H': e = E_COLOR_H; break;
            case 'I': e = E_COLOR_I; break;
            case 'J': e = E_COLOR_J; break;
            case 'K': e = E_COLOR_K; break;
            case 'L': e = E_COLOR_L; break;
            default: break;
        }
        return e;
    }
    public static char display(int c) {
        char r = '_'; // blank space
        switch (c) {
            case E_COLOR_A: r = 'A'; break;
            case E_COLOR_B: r = 'B'; break;
            case E_COLOR_C: r = 'C'; break;
            case E_COLOR_D: r = 'D'; break;
            case E_COLOR_E: r = 'E'; break;
            case E_COLOR_F: r = 'F'; break;
            case E_COLOR_G: r = 'G'; break;
            case E_COLOR_H: r = 'H'; break;
            case E_COLOR_I: r = 'I'; break;
            case E_COLOR_J: r = 'J'; break;
            case E_COLOR_K: r = 'K'; break;
            case E_COLOR_L: r = 'L'; break;
            default: break;
        }
        return r;
    }
    public static void main(String[] args) {
        System.out.println(" /--------------\\");
        System.out.println("< Mastermind 1.0 >");
        System.out.println(" \\--------------/");
        System.out.println("   By Tim Baker");
        System.out.println("   ------------");
        rand = new Random();
        guessHistory = new HashSet<String>();

        try {
            isr = new InputStreamReader(System.in);
            in = new BufferedReader(isr);
            System.out.print("New game? (y/n) ");
            cmd = in.readLine();
            if (cmd.equalsIgnoreCase("n")) {
                done = true;
            } else {
                if (cmd.equalsIgnoreCase("debug")) {
                    debug = true;
                }
                if (cmd.equalsIgnoreCase("notips") || cmd.equalsIgnoreCase("nohints")) {
                    hintsOff = true;
                }
                // set up new game parameters
                pegs = 5;
                do {
                    System.out.print("How many pegs/slots? (4-7) ");
                    cmd = in.readLine();
                    if (cmd.length() < 1) {
                        System.out.println("(using default: 5)");
                    } else {
                        try {
                            pegs = Integer.parseInt(cmd);
                        } catch (Exception e) {
                            System.out.println("Please enter a valid number.");
                        }
                    }
                } while (pegs < 4 || pegs > 7);
                colors = 8;
                do {
                    System.out.print("How many colors? (6-12) ");
                    cmd = in.readLine();
                    if (cmd.length() < 1) {
                        System.out.println("(using default: 8)");
                    } else {
                        try {
                            colors = Integer.parseInt(cmd);
                        } catch (Exception e) {
                            System.out.println("Please enter a valid number.");
                        }
                    }
                } while (colors < 6 || colors > 12);
                difficulty = E_STANDARD;
                System.out.println("Hard = Repeated Colors Allowed, Master = Blank Slots Allowed");
                System.out.print("Select a difficulty: standard(S), hard(H), master(M) ");
                cmd = in.readLine();
                if (cmd.equalsIgnoreCase("h")) {
                    difficulty = E_HARD;
                } else if (cmd.equalsIgnoreCase("m")) {
                    difficulty = E_MASTER;
                } else if (cmd.length() < 1) {
                    System.out.println("(using default: STANDARD)");
                }
                System.out.print("Play as setter(T), solver(V), or 2P hotseat(H)? ");
                cmd = in.readLine();
                if (cmd.equalsIgnoreCase("t")) {
                    solver = E_COMPUTER;
                    setter = E_HUMAN;
                } else if (cmd.equalsIgnoreCase("h")) {
                    setter = E_HUMAN;
                } else if (cmd.length() < 1) {
                    System.out.println("(using default: SOLVER)");
                }

                promptSetter(setter); // set up the code
            }
            guesses = 0;
            while (!done) {
                guess = promptSolver(solver);
                if (evaluateGuess(guess)) {
                    done = true;
                    System.out.println("--- --- --- --- --- *** *** *** *** *** *** *** *** --- --- --- --- ---");
                    System.out.print("                    {{ = ");
                    for (int i = 0; i < pegs; i++) {
                        System.out.print(" [" + display(code[i]) + "]");
                    }
                    System.out.println("  = }}");
                    System.out.println("--- --- --- --- --- *** *** *** *** *** *** *** *** --- --- --- --- ---");
                    System.out.println("Congratulations! You cracked the code in " + guesses + " guesses!");
                } else if (validGuess) {
                    guesses++;
                    displayClues();
                    if (difficulty == E_STANDARD && !hintsOff) {
                        if (!progMsg3 && (pegs - black) == 1) {
                            progMsg3 = true;
                            progMsg2 = true;
                            progMsg1 = true;
                            System.out.println("Great work! You're getting very close to cracking the code.");
                        } else if (!progMsg2 && (pegs - black) == 2) {
                            progMsg2 = true;
                            progMsg1 = true;
                            System.out.println("Nice! You're getting closer to cracking the code.");
                        } else if (!progMsg1 && black > 1) {
                            progMsg1 = true;
                            System.out.println("Good work. You're making some solid progress towards solving the code.");
                        }
                        if (black == 0 && white == 0) {
                            System.out.println("Hmmm! No clues for you this time.");
                            System.out.println("(no pegs in your guess are the right color)");
                        } else if (black == 0 && white > 0) {
                            System.out.println("Not bad - you get " + white + " white pegs(o).");
                            System.out.println("(" + white + " pegs in your guess are the right color, but in the wrong location)");
                        } else if (black > 0 && white == 0) {
                            System.out.println("Very nice guess! You get " + black + " black pegs(x).");
                            System.out.println("(" + black + " pegs in your guess are the right color, and in the right location)");
                        } else {
                            System.out.println("Good try - you get " + white + " white pegs(o) and " + black + " black ones(x).");
                            System.out.println("(" + white + " pegs in your guess are the right color, but in the wrong location)");
                            System.out.println("(" + black + " pegs in your guess are the right color, and in the right location)");
                        }
                    }
                    System.out.println("--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---");
                }
            }
        } catch (IOException io) {
            System.out.println("Rogue IOException encountered! Roll for initiative...");
        }
    }
    public static void displayClues() {
        if (difficulty == E_STANDARD && !hintsOff) {
            System.out.println("Note: The location of the clues does not correspond to the location of the pegs in either your guess or in the secret code.");
            System.out.println("Clues are always given black first, then white, and always contiguously.");
        }
        System.out.println("--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---");
        System.out.print("         GUESS #" + guesses + ": < " + guess + " > - {{ = ");
        for (int i = 0; i < black; i++) {
            System.out.print(" [x]");
        }
        for (int i = 0; i < white; i++) {
            System.out.print(" [o]");
        }
        for (int i = 0; i < (pegs - (black + white)); i++) {
            System.out.print(" [ ]");
        }
        System.out.println("  = }}");
    }
}
