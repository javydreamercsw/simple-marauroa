package simple.server.extension.d20.feat;

import java.util.List;
import java.util.Map;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.rpclass.D20Class;
import simple.server.extension.d20.weapon.D20Weapon;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Feat extends D20Characteristic {

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
    List<Class<? extends D20Feat>> getRequirements();

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

    /**
     * Minimum level for having this feat available.
     *
     * @return Minimum level for having this feat available.
     */
    int levelRequirement();
}
