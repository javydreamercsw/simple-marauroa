package simple.server.extension.d20.dice;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
import java.util.Random;

/**
 * Abstract class describing a die of any kind
 */
public abstract class Die {

    /**
     * Random number seed
     */
    protected static Random rand = new Random();

    /**
     * Holds the rolls of each die
     */
    public int[] rolls;

    /**
     * Die modifier
     */
    public int modifier;

    /**
     * Number of dice
     */
    public int num;

    /**
     * Number of sides
     */
    public int sides;

    /**
     * Number of times rolled
     */
    public int timesRolled;

    /**
     * Total from last die roll
     */
    public int total;

    /**
     * Roll the die, and get back a value
     *
     * @return Result of the die roll
     */
    public abstract int roll();

    /**
     * Writes out the die name (like 2d6+1)
     *
     * @return Die name
     */
    @Override
    public String toString() {
        if (modifier == 0) {
            return num + "d" + sides;
        }
        return num + "d" + sides + (modifier > 0 ? "+" : "")
                + (modifier == 0 ? "" : modifier);
    }

    /**
     * Sets the random Die object. Allows you to put in a seeded random for
     * better randomness.
     *
     * @param rand Random
     */
    public static void setRandom(Random rand) {
        Die.rand = rand;
    }

    /**
     * Returns the last roll.
     *
     * @return The last roll
     */
    public int value() {
        return total;
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive. The
     * difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value. Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }
}
