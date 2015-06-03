package simple.server.extension.d20.feat;

import simple.server.extension.d20.requirement.D20Requirement;
import java.util.Map;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.weapon.D20Weapon;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Feat extends D20Characteristic, D20Requirement {

    /**
     * Get bonus for this throw.
     *
     * @param st throw to get bonus for.
     * @return bonus or 0 if not provided.
     */
    int getBonus(Class<? extends D20Characteristic> st);

    /**
     * Get the bonuses for this feat.
     *
     * @return
     */
    Map<Class<? extends D20Characteristic>, String> getBonuses();


    /**
     * Can the player have multiple instances of this Feat.
     *
     * @return true if allowed, false otherwise.
     */
    boolean isMultiple();

    /**
     * A characteristic this feat focuses on.
     *
     * @return focused characteristic or null if none;
     */
    D20Characteristic getFocusCharacteristic();

    /**
     * A weapon this feat focuses on.
     *
     * @return focused weapon or null if none;
     */
    D20Weapon getFocusWeapon();
}
