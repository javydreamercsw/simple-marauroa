package simple.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.client.net.IPerceptionListener;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class SimplePerceptionHandler extends PerceptionHandler implements IPerceptionListener {

    private final PerceptionDispatcher dispatch;
    private RPObjectChangeDispatcher rpobjDispatcher;
    private SimpleClient client;
    private static final Logger logger =
            Logger.getLogger(SimplePerceptionHandler.class.getSimpleName());

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
        logger.log(Level.FINE, "onAdded: {0}", object);
        rpobjDispatcher.dispatchAdded(object, isUser(object));
        client.onAdded(object);
        return false;
    }

    @Override
    public boolean onClear() {
        client.onClear();
        return false;
    }

    @Override
    public boolean onDeleted(RPObject object) {
        logger.log(Level.FINE, "onDeleted: {0}", object);
        rpobjDispatcher.dispatchRemoved(object, isUser(object));
        client.onDeleted(object);
        return false;
    }

    @Override
    public void onException(Exception exception,
            MessageS2CPerception perception) {
        logger.log(Level.SEVERE, null, exception);
        client.onException(exception, perception);
    }

    @Override
    public boolean onModifiedAdded(RPObject object, RPObject changes) {
        logger.log(Level.FINE, "onModifiedAdded: {0}:{1}", new Object[]{object, changes});
        rpobjDispatcher.dispatchModifyAdded(object, changes, false);
        client.onModifiedAdded(object, changes);
        return false;
    }

    @Override
    public boolean onModifiedDeleted(RPObject object, RPObject changes) {
        logger.log(Level.FINE, "onModifiedDeleted: {0}:{1}", new Object[]{object, changes});
        rpobjDispatcher.dispatchModifyRemoved(object, changes, false);
        client.onModifiedDeleted(object, changes);
        return false;
    }

    @Override
    public boolean onMyRPObject(RPObject added, RPObject deleted) {
        logger.fine("onMyRPObject");
        return client.onMyRPObject(added, deleted);
    }

    @Override
    public void onPerceptionBegin(byte type, int timestamp) {
        logger.log(Level.FINE, "onPerceptionBegin: {0}, {1}",
                new Object[]{type, timestamp});
        client.onPerceptionBegin(type, timestamp);
    }

    @Override
    public void onPerceptionEnd(byte type, int timestamp) {
        logger.log(Level.FINE, "onPerceptionEnd: {0}, {1}",
                new Object[]{type, timestamp});
        client.onPerceptionEnd(type, timestamp);
    }

    @Override
    public void onSynced() {
        logger.fine("onSynced");
        client.onSynced();
    }

    @Override
    public void onUnsynced() {
        logger.fine("onUnsynced");
        client.onUnsynced();
    }

    /**
     * Check to see if the object is the connected user. This is an ugly hack
     * needed because the perception protocol distinguishes between normal and
     * private (my) object changes, but not full add/removes.
     *
     * @param object
     *            An object.
     * 
     * @return <code>true</code> if it is the user object.
     */
    public boolean isUser(final RPObject object) {
        if (object.getRPClass().subclassOf("client_object")) {
            return client.getAccountUsername().equalsIgnoreCase(object.get("name"));
        } else {
            return false;
        }
    }
}
