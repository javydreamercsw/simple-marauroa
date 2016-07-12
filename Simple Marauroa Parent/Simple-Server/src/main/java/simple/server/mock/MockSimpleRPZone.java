package simple.server.mock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.TransferContent;
import org.openide.util.Lookup;
import simple.common.game.ClientObjectInterface;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MockSimpleRPZone extends SimpleRPZone {

    private List<TransferContent> contents;
    private HashMap<String, ClientObjectInterface> players;

    public MockSimpleRPZone(String name) {
        super(name);
        contents = new LinkedList<TransferContent>();
        players = new HashMap<String, ClientObjectInterface>();
    }

    @Override
    public synchronized void add(RPObject object, ClientObjectInterface player) {
        add(object, player, true);
    }

    @Override
    public void add(RPObject object) {
        add(object, null, true);
    }

    private synchronized void add(final RPObject object,
            final ClientObjectInterface player, final boolean expire) {
        /*
         * Assign [zone relative] ID info.
         */
        assignRPObjectID(object);

        if (object instanceof ClientObjectInterface) {
            final ClientObjectInterface p = ((ClientObjectInterface) object);
            players.put(p.getName(), p);
            objects.put(object.getID(), (RPObject) p);
        }
        if (object instanceof Entity) {
            ((RPEntityInterface) object).onAdded(this);
        }
        Lookup.getDefault().lookup(IRPWorld.class).requestSync(object);
    }

    @Override
    public boolean has(RPObject.ID id) {
        System.out.println("Zone: " + getID() + " has " + objects.size()
                + " objects in it.");
        for (RPObject o : objects.values()) {
            System.out.println((o instanceof ClientObjectInterface
                    ? "Player: " : "Object: ") + o);
        }
        return super.has(id);
    }
}
