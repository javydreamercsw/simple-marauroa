package simple.server.extension.d20.feat;

import simple.server.extension.d20.D20Characteristic;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface D20Feat extends D20Characteristic{
  
    /**
     * Get bonus for this throw.
     * @param st throw to get bonus for.
     * @return bonus or 0 if not provided.
     */
    int getBonus(Class<? extends D20Characteristic> st);
}
