package simple.server.core.entity.api;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface LevelEntity {

    String LEVEL = "Level";

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
}
