package simple.server.extension.d20.rpclass;

import marauroa.common.game.RPSlot;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.level.D20Level;

/**
 * This represents a Class in a D20 based game.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Class extends D20Characteristic, D20Level {

    /**
     * A list of race bonuses in the format: Attribute name, Bonus
     *
     * @return race bonuses
     */
    public RPSlot getAttributeBonuses();

    /**
     * A list of the preferred feats on a D20 game. (Easier to gain levels)
     *
     * @return list of the preferred feats
     */
    public RPSlot getPrefferedFeats();

    /**
     * Feats awarded as a bonus.
     *
     * @return map of bonus feats.
     */
    public RPSlot getBonusFeats();

    /**
     * Skills awarded as a bonus.
     *
     * @return map of bonus skills.
     */
    public RPSlot getBonusSkills();

    /**
     * A list of the preferred skills on a D20 game. (Easier to gain levels)
     *
     * @return list of the preferred skills
     */
    public RPSlot getPrefferedSkills();

    /**
     * Equation for getting HP for each level.
     *
     * @return Equation for getting HP for each level
     */
    public String getHPDice();

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

    /**
     * Do the initial rolls for this class abilities and other stats.
     */
    public void initialRolls();
}
