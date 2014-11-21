package simple.server.extension.attribute;

import java.util.List;
import java.util.Map;

/**
 * This represents a Race in a D20 game.
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface D20Race extends D20Characteristic{
   
    /**
     * A list of race bonuses in the format: Attribute name, Bonus
     * @return race bonuses
     */
    Map<String, Integer> getAttributeBonuses();
    
    /**
     * A list of the preferred classes on a D20 game. (Easier to gain levels)
     * @return list of the preferred
     */
    List<D20Class> getPrefferedClasses();
}
