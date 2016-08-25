package simple.server.extension.d20.level;

/**
 * Interface for entities that have levels.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Level {

    String LEVEL = "Level";
    String MAX = "max";

    /**
     * Get Level.
     *
     * @return level
     */
    public int getLevel();

    /**
     * Set level.
     *
     * @param level
     */
    public void setLevel(int level);

    /**
     * Get max level for this object.
     *
     * @return max level for this object, -1 if no max level.
     */
    public int getMaxLevel();

    /**
     * Set max level.
     *
     * @param max max level to set.
     */
    public void setMaxLevel(int max);
}
