package simple.client;

import java.util.LinkedList;
import java.util.List;
import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

public class PerceptionDispatcher implements IPerceptionListener {

    private final List<IPerceptionListener> listenerList = new LinkedList<>();

    @Override
    public boolean onAdded(final RPObject object) {
        boolean returnValue = false;
        listenerList.forEach((l) -> {
            l.onAdded(object);
        });
        return returnValue;
    }

    @Override
    public boolean onClear() {
        boolean returnValue = false;
        listenerList.forEach((l) -> {
            l.onClear();
        });
        return returnValue;
    }

    @Override
    public boolean onDeleted(final RPObject object) {
        boolean returnValue = false;
        listenerList.forEach((l) -> {
            l.onDeleted(object);
        });
        return returnValue;
    }

    @Override
    public void onException(final Exception exception,
            final MessageS2CPerception perception) {
        listenerList.forEach((l) -> {
            l.onException(exception, perception);
        });
    }

    @Override
    public boolean onModifiedAdded(final RPObject object,
            final RPObject changes) {
        boolean returnValue = false;
        returnValue = listenerList.stream().map((l)
                -> l.onModifiedAdded(object, changes)).reduce(returnValue,
                (accumulator, _item) -> accumulator | _item);
        return returnValue;
    }

    @Override
    public boolean onModifiedDeleted(final RPObject object,
            final RPObject changes) {
        boolean returnValue = false;
        returnValue = listenerList.stream().map((l)
                -> l.onModifiedDeleted(object, changes)).reduce(returnValue,
                (accumulator, _item) -> accumulator | _item);
        return returnValue;
    }

    @Override
    public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
        boolean returnValue = false;
        returnValue = listenerList.stream().map((l)
                -> l.onMyRPObject(added, deleted)).reduce(returnValue,
                (accumulator, _item) -> accumulator | _item);
        return returnValue;
    }

    @Override
    public void onPerceptionBegin(final byte type, final int timestamp) {
        listenerList.forEach((l) -> {
            l.onPerceptionBegin(type, timestamp);
        });
    }

    @Override
    public void onPerceptionEnd(final byte type, final int timestamp) {
        listenerList.forEach((l) -> {
            l.onPerceptionEnd(type, timestamp);
        });
    }

    @Override
    public void onSynced() {
        listenerList.forEach((l) -> {
            l.onSynced();
        });
    }

    @Override
    public void onUnsynced() {
        listenerList.forEach((l) -> {
            l.onUnsynced();
        });
    }

    public void register(final IPerceptionListener listener) {
        listenerList.add(listener);
    }

    public void unregister(final IPerceptionListener listener) {
        listenerList.remove(listener);
    }
}
