package simple.client;

import java.util.LinkedList;
import java.util.List;
import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

public class PerceptionDispatcher implements IPerceptionListener {

    private final List<IPerceptionListener> listenerList = new LinkedList<IPerceptionListener>();

    @Override
    public boolean onAdded(final RPObject object) {
        boolean returnValue = false;
        for (final IPerceptionListener l : listenerList) {
            l.onAdded(object);
        }
        return returnValue;
    }

    @Override
    public boolean onClear() {
        boolean returnValue = false;
        for (final IPerceptionListener l : listenerList) {
            l.onClear();
        }
        return returnValue;
    }

    @Override
    public boolean onDeleted(final RPObject object) {
        boolean returnValue = false;
        for (final IPerceptionListener l : listenerList) {
            l.onDeleted(object);
        }
        return returnValue;
    }

    @Override
    public void onException(final Exception exception,
            final MessageS2CPerception perception) {
        for (final IPerceptionListener l : listenerList) {
            l.onException(exception, perception);
        }
    }

    @Override
    public boolean onModifiedAdded(final RPObject object, final RPObject changes) {
        boolean returnValue = false;
        for (final IPerceptionListener l : listenerList) {
            returnValue |= l.onModifiedAdded(object, changes);
        }
        return returnValue;
    }

    @Override
    public boolean onModifiedDeleted(final RPObject object,
            final RPObject changes) {
        boolean returnValue = false;
        for (final IPerceptionListener l : listenerList) {
            returnValue |= l.onModifiedDeleted(object, changes);
        }
        return returnValue;
    }

    @Override
    public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
        boolean returnValue = false;
        for (final IPerceptionListener l : listenerList) {
            returnValue |= l.onMyRPObject(added, deleted);
        }
        return returnValue;
    }

    @Override
    public void onPerceptionBegin(final byte type, final int timestamp) {
        for (final IPerceptionListener l : listenerList) {
            l.onPerceptionBegin(type, timestamp);
        }
    }

    @Override
    public void onPerceptionEnd(final byte type, final int timestamp) {
        for (final IPerceptionListener l : listenerList) {
            l.onPerceptionEnd(type, timestamp);
        }
    }

    @Override
    public void onSynced() {
        for (final IPerceptionListener l : listenerList) {
            l.onSynced();
        }
    }

    @Override
    public void onUnsynced() {
        for (final IPerceptionListener l : listenerList) {
            l.onUnsynced();
        }
    }

    public void register(final IPerceptionListener listener) {
        listenerList.add(listener);
    }

    public void unregister(final IPerceptionListener listener) {
        listenerList.remove(listener);
    }
}