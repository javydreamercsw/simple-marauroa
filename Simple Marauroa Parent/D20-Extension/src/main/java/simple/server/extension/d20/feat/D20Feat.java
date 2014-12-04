package simple.server.extension.d20.feat;

import java.util.List;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.D20Race;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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
     * List of Races this Feat is exclusive to.
     *
     * @return list of races. Empty means anyone can use it.
     */
    List<Class<? extends D20Race>> getExclusiveRaces();

    /**
     * Requirements for this Feat.
     *
     * @return Map Feats to have this one available.
     */
    List<Class<? extends D20Feat>> getRequirements();
}
