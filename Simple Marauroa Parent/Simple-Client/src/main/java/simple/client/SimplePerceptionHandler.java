package simple.client;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.client.net.IPerceptionListener;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;
import org.openide.util.Lookup;
import simple.client.api.AddListener;
import simple.client.api.ClearListener;
import simple.client.api.DeleteListener;
import simple.client.api.ExceptionListener;
import simple.client.api.IWorldManager;
import simple.client.api.ModificationListener;
import simple.client.api.PerceptionListener;
import simple.client.api.SelfChangeListener;
import simple.client.api.SyncListener;
import simple.client.entity.IUserContext;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class SimplePerceptionHandler extends PerceptionHandler
        implements IPerceptionListener {

    private final PerceptionDispatcher dispatch;
    private final RPObjectChangeDispatcher rpobjDispatcher;
    private final SimpleClient client;
    private static final Logger LOG
            = Logger.getLogger(SimplePerceptionHandler.class.getSimpleName());
    private final IWorldManager worldManager
            = Lookup.getDefault().lookup(IWorldManager.class);

    public SimplePerceptionHandler(PerceptionDispatcher dispatch,
            RPObjectChangeDispatcher rpobjDispatcher, SimpleClient client) {
        super(dispatch);
        this.dispatch = dispatch;
        this.rpobjDispatcher = rpobjDispatcher;
        this.client = client;
        //Register itself so the methods below are executed.
        this.dispatch.register(SimplePerceptionHandler.this);
    }

    @Override
    public boolean onAdded(RPObject object) {
        LOG.log(Level.FINE, "onAdded: {0}", object);
        rpobjDispatcher.dispatchAdded(object, isUser(object));
        client.onAdded(object);
        boolean result = false;

        LOG.log(Level.FINE, "onAdded: {0}", object);
        //Check if ID is different from last time (zone change, etc)
        Set<Map.Entry<RPObject.ID, RPObject>> values
                = worldManager.getWorld().entrySet();
        for (AddListener listener
                : Lookup.getDefault().lookupAll(AddListener.class)) {
            if (!listener.onAdded(object)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean onClear() {
        client.onClear();
        boolean result = false;
        for (ClearListener listener
                : Lookup.getDefault().lookupAll(ClearListener.class)) {
            if (!listener.onClear()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean onDeleted(RPObject object) {
        LOG.log(Level.FINE, "onDeleted: {0}", object);
        rpobjDispatcher.dispatchRemoved(object, isUser(object));
        client.onDeleted(object);
        boolean result = false;
        for (DeleteListener listener
                : Lookup.getDefault().lookupAll(DeleteListener.class)) {
            if (!listener.onDeleted(object)) {
                result = true;
            }
        }
        worldManager.getWorld().remove(object.getID());
        return result;
    }

    @Override
    public void onException(Exception exception,
            MessageS2CPerception perception) {
        LOG.log(Level.SEVERE, null, exception);
        client.onException(exception, perception);
        Lookup.getDefault().lookupAll(ExceptionListener.class)
                .stream().forEach((listener) -> {
                    listener.onException(exception, perception);
                });
    }

    @Override
    public boolean onModifiedAdded(RPObject object, RPObject changes) {
        LOG.log(Level.FINE, "onModifiedAdded: {0}:{1}",
                new Object[]{object, changes});
        rpobjDispatcher.dispatchModifyAdded(object, changes, false);
        client.onModifiedAdded(object, changes);
        //Process the events
        if (changes != null) {
            //Process Events
            Lookup.getDefault().lookup(IUserContext.class).onRPEvent(changes);
        }
        boolean result = false;
        for (ModificationListener listener
                : Lookup.getDefault().lookupAll(ModificationListener.class)) {
            if (!listener.onModifiedAdded(object, changes)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean onModifiedDeleted(RPObject object, RPObject changes) {
        LOG.log(Level.FINE, "onModifiedDeleted: {0}:{1}",
                new Object[]{object, changes});
        rpobjDispatcher.dispatchModifyRemoved(object, changes, false);
        client.onModifiedDeleted(object, changes);
        boolean result = false;
        for (ModificationListener listener
                : Lookup.getDefault().lookupAll(ModificationListener.class)) {
            if (!listener.onModifiedDeleted(object, changes)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean onMyRPObject(RPObject added, RPObject deleted) {
        LOG.fine("onMyRPObject");
        //Process the events
        if (added != null) {
            //Process Events
            Lookup.getDefault().lookup(IUserContext.class).onRPEvent(added);
        }
        boolean result = true;
        RPObject.ID id = null;
        if (added != null) {
            id = added.getID();
        }
        if (deleted != null) {
            id = deleted.getID();
        }
        if (id != null) {
            RPObject object
                    = Lookup.getDefault().lookup(IWorldManager.class)
                            .get(id);
            if (object != null) {
                object.applyDifferences(added, deleted);
                SelfChangeListener listener
                        = Lookup.getDefault()
                                .lookup(SelfChangeListener.class);
                if (listener != null) {
                    if (!listener.onMyRPObject(added, deleted)) {
                        result = false;
                    }
                }
            }
        } else {
            // Unchanged.
            // Do nothing.
        }
        return result && client.onMyRPObject(added, deleted);
    }

    @Override
    public void onPerceptionBegin(byte type, int timestamp) {
        LOG.log(Level.FINE, "onPerceptionBegin: {0}, {1}",
                new Object[]{type, timestamp});
        client.onPerceptionBegin(type, timestamp);
        Lookup.getDefault().lookupAll(PerceptionListener.class)
                .stream().forEach((listener) -> {
                    listener.onPerceptionBegin(type, timestamp);
                });
    }

    @Override
    public void onPerceptionEnd(byte type, int timestamp) {
        LOG.log(Level.FINE, "onPerceptionEnd: {0}, {1}",
                new Object[]{type, timestamp});
        client.onPerceptionEnd(type, timestamp);
        Lookup.getDefault().lookupAll(PerceptionListener.class)
                .stream().forEach((listener) -> {
                    listener.onPerceptionEnd(type, timestamp);
                });
    }

    @Override
    public void onSynced() {
        LOG.fine("onSynced");
        client.onSynced();
        Lookup.getDefault().lookupAll(SyncListener.class).stream()
                .forEach((listener) -> {
                    listener.onSynced();
                });
    }

    @Override
    public void onUnsynced() {
        LOG.fine("onUnsynced");
        client.onUnsynced();
        Lookup.getDefault().lookupAll(SyncListener.class).stream()
                .forEach((listener) -> {
                    listener.onUnsynced();
                });
    }

    /**
     * Check to see if the object is the connected user. This is an ugly hack
     * needed because the perception protocol distinguishes between normal and
     * private (my) object changes, but not full add/removes.
     *
     * @param object An object.
     *
     * @return <code>true</code> if it is the user object.
     */
    public boolean isUser(final RPObject object) {
        if (object.getRPClass().subclassOf("client_object")) {
            return client.getAccountUsername() != null
                    && client.getAccountUsername()
                            .equalsIgnoreCase(object.get("name"));
        } else {
            return false;
        }
    }
}
