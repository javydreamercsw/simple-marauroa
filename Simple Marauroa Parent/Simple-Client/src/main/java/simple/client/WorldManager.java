package simple.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import simple.client.api.IWorldManager;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IWorldManager.class)
public class WorldManager implements IWorldManager {

    private static final Logger LOG
            = Logger.getLogger(WorldManager.class.getSimpleName());

    private final Map<RPObject.ID, RPObject> world_objects = new HashMap<>();

    @Override
    public RPObject get(RPObject.ID id) {
        return world_objects.get(id);
    }

    @Override
    public Map<RPObject.ID, RPObject> getWorld() {
        return world_objects;
    }

    @Override
    public void showWorld() {
        LOG.log(Level.INFO, "<World contents ------------------------------------->");
        int j = 0;
        for (RPObject object : world_objects.values()) {
            j++;
            LOG.log(Level.INFO, "{0}. {1}",
                    new Object[]{j, object});
        }
        LOG.log(Level.INFO, "</World contents ------------------------------------->");
    }
}
