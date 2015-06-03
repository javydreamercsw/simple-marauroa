package simple.client;

import java.util.HashMap;
import java.util.Map;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import simple.client.api.IWorldManager;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IWorldManager.class)
public class WorldManager implements IWorldManager {

    private final Map<RPObject.ID, RPObject> world_objects = new HashMap<>();

    @Override
    public RPObject get(RPObject.ID id) {
        if (world_objects != null) {
            return world_objects.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Map<RPObject.ID, RPObject> getWorld() {
        return world_objects;
    }
}
