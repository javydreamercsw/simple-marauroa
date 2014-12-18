package simple.server.extension.d20.race;

import java.util.List;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.rpclass.D20Class;

/**
 * This represents a Race in a D20 game.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Race extends D20Characteristic {

    /**
     * Get favored classes.
     *
     * @return favored classes
     */
    List<Class<? extends D20Class>> getFavoredClasses();

    /**
     * Bonus points per level.
     *
     * @param level
     * @return Bonus points for level
     */
    public int getBonusSkillPoints(int level);

    /**
     * Bonus points per level.
     *
     * @param level
     * @return Bonus points for level
     */
    public int getBonusFeatPoints(int level);
}
