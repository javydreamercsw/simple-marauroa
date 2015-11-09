package simple.client;

import java.util.HashMap;
import java.util.Map;
import marauroa.common.game.RPObject;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class World {

    private Map<RPObject.ID, RPObject> world_objects = 
            new HashMap<RPObject.ID, RPObject>();

    /**
     * @return the world_objects
     */
    public Map<RPObject.ID, RPObject> getWorldObjects() {
        return world_objects;
    }
}
