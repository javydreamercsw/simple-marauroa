package simple.server.extension.ability;

import simple.server.extension.D20Characteristic;
import simple.server.extension.iD20Definition;

/**
 * An attribute is something for a character like Wisdom, Fortitude, etc.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface D20Ability extends D20Characteristic, iD20Definition {

    /**
     * Get attribute default value.
     *
     * @return default value
     */
    public int getDefaultValue();

    /**
     * Current ability score.
     *
     * @return ability score
     */
    public int getAbilityScore();

    /**
     *Set ability score.
     * @param score new ability score
     */
    public void setAbilityScore(int score);

    /**
     * Get ability modifier.
     *
     * @return ability modifier
     */
    public int getAbilityModifier();
}