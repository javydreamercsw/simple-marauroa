package simple.client.api;

import java.util.Map;
import marauroa.common.game.RPObject;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface IWorldManager {

    /**
     * Get the RPObject for the provided id.
     *
     * @param id
     * @return null if no object with that ID exists.
     */
    RPObject get(RPObject.ID id);

    /**
     * Get the world.
     *
     * @return
     */
    public Map<RPObject.ID, RPObject> getWorld();
    
    /**
     * Display the world in a text format.
     */
    public void showWorld();
}
