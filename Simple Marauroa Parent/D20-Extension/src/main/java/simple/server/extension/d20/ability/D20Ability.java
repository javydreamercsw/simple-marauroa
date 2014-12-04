package simple.server.extension.d20.ability;

import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.iD20Definition;

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
}
