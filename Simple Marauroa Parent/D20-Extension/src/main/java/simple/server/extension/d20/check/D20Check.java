package simple.server.extension.d20.check;

import java.util.List;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.dice.RollResult;
import simple.server.extension.d20.rpclass.D20Class;

/**
 * A check is done against one or a series of Abilities (@see D20Ability).
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface D20Check extends D20Characteristic {

    /**
     * Get a list of abilities related to this check.
     *
     * @return List of abilities related to this check
     */
    List<Class<? extends D20Ability>> getAbilities();

    /**
     * Get the check roll result.
     *
     * @param clazz class to get the roll against.
     * @return Result of the roll.
     */
    RollResult getCheckRoll(D20Class clazz);

    /**
     * The amount of faces for the die to be used. D20 used by default.
     *
     * @return amount of faces for the die to be used
     */
    int dieType();
}
