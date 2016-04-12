package simple.client;

import java.util.HashMap;
import java.util.Map;
import marauroa.common.game.RPObject;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class World {

    private final Map<RPObject.ID, RPObject> world_objects = 
            new HashMap<>();

    /**
     * @return the world_objects
     */
    public Map<RPObject.ID, RPObject> getWorldObjects() {
        return world_objects;
    }
}
