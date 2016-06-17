package simple.common;

import java.util.logging.Logger;

/**
 * Utility class for getting the creature-level for some given exp. points.
 */
public class Level {

    /**
     * the logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(Level.class.getSimpleName());
    // Max Level is LEVELS - 1.
    // xp formula overflows for level = 599.
    private static final int LEVELS = 598;
    private static final int[] XP;
    private static double[] wisdom;

    static {
        /*
         * Calculate eXPerience
         */
        XP = new int[LEVELS + 1];

        XP[0] = 0;
        XP[1] = 50;
        XP[2] = 100;
        XP[3] = 200;
        XP[4] = 400;
        XP[5] = 800;

        for (int i = 5; i < LEVELS; i++) {
            final int exp = ((i * 16 + i * i * 5 + i * i * i * 10 + 300) / 100) * 100;
            XP[i + 1] = exp;
        }

        if (LOG.isLoggable(java.util.logging.Level.FINE)) {
            for (int i = 0; i < LEVELS; i++) {
                LOG.log(java.util.logging.Level.FINE, "Level {0}: {1} xp",
                        new Object[]{i, XP[i]});
            }
        }

        /*
         * Calculate Wisdom
         */
        wisdom = new double[LEVELS];

        for (int i = 0; i < LEVELS; i++) {
            wisdom[i] = 1.0 - (1 / Math.pow(1.01, i));
        }

        if (LOG.isLoggable(java.util.logging.Level.FINE)) {
            for (int i = 0; i < LEVELS; i++) {
                LOG.log(java.util.logging.Level.FINE,
                        "Level {0}: {1} wisdom",
                        new Object[]{i, (int) ((wisdom[i] * 100.0) + 0.5)});
            }
        }
    }

    public static void main(final String[] args) {
        for (int i = 0; i < LEVELS; i++) {
            System.out.println("<tr><td>" + i + "</td><td>"
                    + XP[i] + "</td></tr>");
        }
    }

    public static int maxLevel() {
        return LEVELS - 1;
    }

    /**
     * calculates the level according to the experience.
     *
     * @param exp experience needed
     * @return level
     */
    public static int getLevel(final int exp) {

        int first = 0;
        int last = LEVELS - 1;
        if (exp <= XP[first]) {
            return first;
        }
        if (exp >= XP[last]) {
            return last;
        }
        while (last - first > 1) {
            final int current = first + ((last - first) / 2);
            if (exp < XP[current]) {
                last = current;
            } else {
                first = current;
            }
        }
        return first;
    }

    /**
     * Calculates the experienced needed for a level.
     *
     * @param level
     * @return experience needed
     */
    public static int getXP(final int level) {
        if ((level >= 0) && (level < XP.length)) {
            return XP[level];
        }
        return -1;
    }

    /**
     * Calculates how many levels to add when a certain amount of experience is
     * added.
     *
     * @param exp the current Experience
     * @param added the added Experience
     * @return difference of levels
     */
    public static int changeLevel(final int exp, final int added) {
        int i;
        for (i = 0; i < LEVELS; i++) {
            if (exp < XP[i]) {
                break;
            }
        }

        for (int j = i; j <= LEVELS; j++) {
            if (exp + added < XP[j]) {
                return j - i;
            }
        }

        return 0;
    }

    /**
     * Get an entity's wisdom factor based on their level. As no one really has
     * 100% (i.e. 1.0) wisdom, it should be scaled as needed.
     *
     * @param level A player level.
     *
     * @return A value between <code>0.0</code> (inclusive) and <code>1.0</code>
     * (exclusive).
     */
    public static double getWisdom(final int level) {
        if (level > LEVELS) {
            return wisdom[LEVELS];
        }

        return wisdom[level];
    }
}
