package simple.server.extension.d20.skill;

import java.util.HashMap;
import java.util.Map;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.requirement.D20Requirement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Skill extends D20Characteristic, D20Requirement {

    /**
     * Allows to have dice equations as modifier.
     */
    Map<Class<? extends D20Ability>, String> MODS = new HashMap<>();

    /**
     * Check if it modifies the attribute.
     *
     * @param attr attribute to check.
     * @return true if it does modify, false otherwise.
     */
    public boolean isModifiesAttribute(Class<? extends D20Ability> attr);

    /**
     * Get modifier for the attribute.
     *
     * @param attr attribute to modify.
     * @return modifier or 0 if it doesn't modify the attribute.
     */
    public int getModifier(Class<? extends D20Ability> attr);

    /**
     * Get the ability tied to this skill.
     *
     * @return ability for this skill.
     */
    public Class<? extends D20Ability> getAbility();

    /**
     * Get the skill rank.
     *
     * @return skill rank
     */
    public Double getRank();

    /**
     * Set the skill rank.
     *
     * @param rank
     */
    public void setRank(Double rank);
}
