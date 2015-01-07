package simple.server.extension.d20.skill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.rpclass.D20Class;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Skill extends D20Characteristic {

    /**
     * Allows to have dice equations as modifier.
     */
    Map<Class<? extends D20Ability>, String> modifiers = new HashMap<>();

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
     * @return
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
    
    /**
     * List of Classes this Feat is exclusive to.
     *
     * @return list of classes. Empty means anyone can use it.
     */
    List<Class<? extends D20Class>> getExclusiveClasses();

    /**
     * Requirements for this Feat.
     *
     * @return Map Feats to have this one available.
     */
    List<Class<? extends D20Skill>> getRequirements();
}
