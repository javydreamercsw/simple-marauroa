package simple.server.extension.d20.dice;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * This class does the rolling of the dice.
 */
public class DieEx extends Die {

    /**
     * Drop high roll
     */
    private boolean highDrop;

    /**
     * Dice roll that is dropped
     */
    private int drops;

    /**
     * Creates an instance of this class to vet values as a die roll.
     *
     * @param roll Roll that needs to be made
     */
    public DieEx(String roll) {
        StringTokenizer strTok = new StringTokenizer(roll, "d-+ ");
        String hold = roll.contains("-") ? "-" : "";
        num = Integer.parseInt(strTok.nextToken());
        sides = Integer.parseInt(strTok.nextToken());
        rolls = new int[num];

        if (strTok.hasMoreTokens()) {
            try {
                hold += strTok.nextToken();
            } catch (NoSuchElementException e) {
                drops = 0;
            }

            try {
                drops = Integer.parseInt(hold);
                modifier=drops;
            } catch (NoSuchElementException e) {
                drops = 0;
            }

            try {
                hold = strTok.nextToken();
            } catch (NoSuchElementException e) {
                hold = "";
            }

            highDrop = !(hold.equals("lowest") || hold.equals(""));
        }
    }

    /**
     * Creates an instance of this class using the default roll
     */
    public DieEx() {
        this("1d6");
    }

    /**
     * Method used for testing and running on it's own
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        DieEx DieRoller;
        StringBuilder temp = new StringBuilder();

        for (String arg : args) {
            temp.append(arg).append(" ");
        }

        DieRoller = new DieEx(temp.toString());
        System.out.println("you rolled " + DieRoller.roll());
    }

    /**
     * Rolls the die using the paramaters set
     *
     * @return Value of the die rolls
     */
    @Override
    public int roll() {
        total = 0;

        for (int x = 0; x < num; x++) {
            rolls[x] = rand.nextInt(sides) + 1;
            total += rolls[x];
        }

        if (drops != 0) {
            // sort rolls first or this doesn't work.
            Arrays.sort(rolls);

            if (!highDrop) {
                for (int x = 0; ((x < drops) && (x < rolls.length)); x++) {
                    total -= rolls[x];
                }
            } else {
                for (int x = rolls.length - 1; x > (rolls.length - drops - 1); x--) {
                    total -= rolls[x];
                }
            }
        }

        timesRolled++;
        return total;
    }
}
