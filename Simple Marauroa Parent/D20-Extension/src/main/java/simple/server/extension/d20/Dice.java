package simple.server.extension.d20;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Dice extends Die {

    /**
     * Constructor for the Dice object
     *
     * @param num Number of dice
     * @param sides Number of sides
     * @param modifier Modifier to the die roll
     */
    public Dice(int num, int sides, int modifier) {
        this.num = num;
        this.sides = sides;
        this.modifier = modifier;
        rolls = new int[num];
        roll();
    }

    /**
     * Constructor for the Dice object
     *
     * @param num Number of dice
     * @param sides Number of sides per die
     */
    public Dice(int num, int sides) {
        this(num, sides, 0);
    }

    /**
     * Rolls the die, and returns the result. I made it final as it's called
     * from the constructor.
     *
     * @return Result of the die roll
     */
    @Override
    public final int roll() {
        int value = 0;
        int i;
        total = 0;

        for (i = 0; i < num; i++) {
            rolls[i] = rand.nextInt(sides) + 1;
            value = rolls[i] + value;
        }

        total = value + modifier;
        timesRolled++;

        return total;
    }
}
