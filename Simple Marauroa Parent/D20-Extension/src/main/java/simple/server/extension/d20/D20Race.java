package simple.server.extension.d20;

import java.util.List;
import java.util.Map;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.feat.D20Feat;
import simple.server.extension.d20.skill.D20Skill;

/**
 * This represents a Race in a D20 game.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface D20Race extends D20Characteristic {

    /**
     * A list of race bonuses in the format: Attribute name, Bonus
     *
     * @return race bonuses
     */
    Map<Class<? extends D20Ability>, Integer> getAttributeBonuses();

    /**
     * A list of the preferred feats on a D20 game. (Easier to gain levels)
     *
     * @return list of the preferred feats
     */
    List<Class<? extends D20Feat>> getPrefferedFeats();
    
    /**
     * Feats awarded as a bonus.
     * @return 
     */
    public Map<Class<? extends D20Feat>, Integer> getBonusFeats();

    /**
     * A list of the preferred skills on a D20 game. (Easier to gain levels)
     *
     * @return list of the preferred skills
     */
    public List<Class<? extends D20Skill>> getPrefferedSkills();

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
}
