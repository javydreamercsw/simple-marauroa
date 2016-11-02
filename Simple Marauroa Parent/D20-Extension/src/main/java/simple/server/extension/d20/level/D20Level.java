package simple.server.extension.d20.level;

import simple.server.core.entity.api.LevelEntity;

/**
 * Interface for entities that have levels.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Level extends LevelEntity {

    String MAX = "max";

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
