package simple.server.extension.d20.requirement;

import java.util.List;
import java.util.Map;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.rpclass.D20Class;

/**
 * Interface to expose requirements.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Requirement {

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
    Map<Class<? extends D20Characteristic>, Integer> getRequirements();

    /**
     * Minimum level for having this feat available.
     *
     * @return Minimum level for having this feat available.
     */
    int levelRequirement();
}
