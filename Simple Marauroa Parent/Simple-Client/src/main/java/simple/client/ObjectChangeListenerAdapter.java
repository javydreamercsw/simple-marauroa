package simple.client;

import marauroa.common.game.RPObject;

/**
 * Convenience class with empty implementation of ObjectChangeListener.
 */
final class ObjectChangeListenerAdapter implements ObjectChangeListener {

    /**
     * is called when object is deleted.
     *
     * In addition to real deletion this happens on every zone change.
     */
    @Override
    public void deleted() {
        // do nothing
    }

    /**
     * is called when object got additional attributes (or values got changed
     * ?).
     *
     */
    @Override
    public void modifiedAdded(final RPObject changes) {
        // do nothing
    }

    /**
     * is called when attributes got deleted.
     */
    @Override
    public void modifiedDeleted(final RPObject changes) {
        // do nothing
    }
}
