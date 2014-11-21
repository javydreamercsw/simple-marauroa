package simple.server.extension.skill;

import java.util.HashMap;
import java.util.Map;
import simple.server.extension.attribute.D20Attribute;
import simple.server.extension.attribute.D20Characteristic;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface D20Skill extends D20Characteristic {

    /**
     * Allows to have dice equations as modifier.
     */
    Map<Class<? extends D20Attribute>, String> modifiers = new HashMap<>();

    /**
     * Check if it modifies the attribute.
     * @param attr attribute to check.
     * @return true if it does modify, false otherwise.
     */
    public boolean isModifiesAttribute(Class<? extends D20Attribute> attr);

    /**
     * Get modifier for the attribute.
     *
     * @param attr attribute to modify.
     * @return modifier or 0 if it doesn't modify the attribute.
     */
    public int getModifier(Class<? extends D20Attribute> attr);
}
