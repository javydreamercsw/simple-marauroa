package simple.client;

import java.util.*;
import java.util.Map.Entry;
import marauroa.client.net.IPerceptionListener;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;
import marauroa.common.net.message.MessageS2CPerception;

/**
 * Translates received perception to object listeners.
 *
 * @author astrid
 *
 */
public class PerceptionToObject implements IPerceptionListener {

    public Map<RPObject.ID, ObjectChangeListener> map = Collections.synchronizedMap(new HashMap<RPObject.ID, ObjectChangeListener>());
    private ObjectFactory of;
    private static final Logger logger = Log4J.getLogger(PerceptionToObject.class);

    /**
     * sets Objectfactory for callback
     * @param of
     */
    public void setObjectFactory(final ObjectFactory of) {
        this.of = of;
    }

    /**
     * issues callback to Objectfactory.onAdded().
     */
    @Override
    public boolean onAdded(final RPObject object) {
        of.onAdded(object, this);
        return false;
    }

    /**
     * call deleted() on every Listener and resets this.
     */
    @Override
    public boolean onClear() {
        for (ObjectChangeListener listener : map.values()) {
            listener.deleted();

        }
        map.clear();
        return false;
    }

    @Override
    public boolean onDeleted(final RPObject object) {
        if (isValid(object)) {
            ObjectChangeListener objectChangeListener = map.get(object.getID());
            if (objectChangeListener == null) {
                logger.error("no listener for: " + object);
            } else {
                objectChangeListener.deleted();
                map.remove(object.getID());
            }
        }
        return false;
    }

    private boolean isValid(final RPObject object) {
        if (object == null) {
            return false;
        } else if (object.has("id")) {
            return !RPObject.INVALID_ID.equals(object.getID());
        } else {
            return false;
        }
    }

    @Override
    public void onException(final Exception exception,
            final MessageS2CPerception perception) {
        onClear();

    }

    @Override
    public boolean onModifiedAdded(final RPObject object, final RPObject changes) {
        if (isValid(object)) {
            ObjectChangeListener objectChangeListener = map.get(object.getID());
            if (objectChangeListener == null) {
                logger.error("no listener for: " + object);
            } else {
                objectChangeListener.modifiedAdded(changes);
            }
        }
        return false;
    }

    @Override
    public boolean onModifiedDeleted(final RPObject object,
            final RPObject changes) {
        if (isValid(object)) {
            ObjectChangeListener objectChangeListener = map.get(object.getID());
            if (objectChangeListener == null) {
                logger.error("no listener for: " + object);
            } else {
                objectChangeListener.modifiedDeleted(changes);
            }
        }
        return false;
    }

    @Override
    public boolean onMyRPObject(final RPObject added, final RPObject deleted) {


        if (isValid(added)) {
            ObjectChangeListener objectChangeListener = map.get(added.getID());
            if (objectChangeListener == null) {
                logger.error("no listener for: " + added);
            } else {
                objectChangeListener.modifiedAdded(added);
            }
        }
        if (isValid(deleted)) {
            ObjectChangeListener objectChangeListener = map.get(deleted.getID());
            if (objectChangeListener == null) {
                logger.error("no listener for: " + added);
            } else {
                objectChangeListener.modifiedDeleted(deleted);
            }
        }
        return false;
    }

    @Override
    public void onPerceptionBegin(final byte type, final int timestamp) {
    }

    @Override
    public void onPerceptionEnd(final byte type, final int timestamp) {
    }

    @Override
    public void onSynced() {
    }

    @Override
    public void onUnsynced() {
    }

    public void register(final RPObject object, final ObjectChangeListener listener) {
        if (isValid(object)) {
            map.put(object.getID(), listener);
        }

    }

    public void unregister(final ObjectChangeListener listener) {
        List<RPObject.ID> idList = new LinkedList<ID>();
        for (Entry<ID, ObjectChangeListener> entry : map.entrySet()) {
            if (entry.getValue() == listener) {
                idList.add(entry.getKey());
            }

        }
        for (RPObject.ID id : idList) {
            map.remove(id);
        }

    }
}
