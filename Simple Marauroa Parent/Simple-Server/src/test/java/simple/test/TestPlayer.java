package simple.test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;
import static org.junit.Assert.*;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.common.game.ClientObjectInterface;
import simple.server.core.engine.IRPWorld;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.api.MonitoreableEntity;
import simple.server.core.entity.api.RPEventListener;
import simple.server.core.entity.clientobject.ClientObject;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
@ServiceProviders({
    @ServiceProvider(service = ClientObjectInterface.class),
    @ServiceProvider(service = RPEntityInterface.class)})
public class TestPlayer extends ClientObject implements MonitoreableEntity {

    private final Map<String, List<RPEventListener>> listeners;
    private final List<String> processedEvents = new ArrayList<>();
    private static final Logger LOG
            = Logger.getLogger(TestPlayer.class.getName());
    private static final long serialVersionUID = -9018744674003715688L;

    public TestPlayer() {
        this.listeners = new HashMap<>();
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass("test player")) {
            RPClass player = new RPClass("test player");
            player.isA(ClientObject.DEFAULT_RP_CLASSNAME);
        }
    }

    public TestPlayer(RPObject obj) {
        super(obj);
        this.listeners = new HashMap<>();
        try {
            //Create account
            DAORegister.get().get(AccountDAO.class).addPlayer(getName(),
                    "password".getBytes("UTF-8"), "dummy@email.com");
            //Add it to the world so it has an ID
            Lookup.getDefault().lookup(IRPWorld.class)
                    .getDefaultZone().add(TestPlayer.this);
            Lookup.getDefault()
                    .lookup(IRPWorld.class).addPlayer(TestPlayer.this);
            Lookup.getDefault().lookup(IRPWorld.class)
                    .registerMonitor(getName(), TestPlayer.this);
        }
        catch (SQLException | UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Override
    public void registerListener(String eventClassName,
            RPEventListener listener) {
        synchronized (listeners) {
            if (!listeners.containsKey(eventClassName)) {
                listeners.put(eventClassName, new ArrayList<>());
            }
            List<RPEventListener> list = listeners.get(eventClassName);
            if (!list.contains(listener)) {
                list.add(listener);
            }
        }
    }

    @Override
    public void unregisterListener(String eventClassName,
            RPEventListener listener) {
        synchronized (listeners) {
            if (listeners.containsKey(eventClassName)) {
                List<RPEventListener> list = listeners.get(eventClassName);
                if (list.contains(listener)) {
                    list.remove(listener);
                }
            }
        }
    }

    @Override
    public void modify(RPObject obj) {
        for (RPEvent event : obj.events()) {
            if (listeners.containsKey(event.getName())) {
                for (RPEventListener l : listeners.get(event.getName())) {
                    l.onRPEvent(event);
                }
            }
        }
        obj.clearEvents();
    }

    @Override
    public void processEvent(RPEvent event) {

    }

    @Override
    public void markEventAsProcessed(RPEvent event) {
        if (!isAlreadyProcessed(event)) {
            processedEvents.add(event.get("event_id"));
        }
    }

    @Override
    public boolean isAlreadyProcessed(RPEvent event) {
        return processedEvents.contains(event.get("event_id"));
    }

    public void clearListeners() {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    public void updateInDB() {

    }
}