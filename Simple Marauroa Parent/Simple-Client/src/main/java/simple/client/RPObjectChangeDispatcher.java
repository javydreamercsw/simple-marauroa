package simple.client;

import java.util.ArrayList;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A dispatcher for RPObjectChangeListeners. This normalizes the tree deltas
 * into individual object deltas.
 *
 * NOTE: The order of dispatch between contained objects and when their
 * container is very specific. Children objects are given a chance to perform
 * creation/updates before their parent is notified it happened to that specific
 * child. For cases of object removal, the parent is notified first, in case the
 * child does destruction/cleanup.
 */
public class RPObjectChangeDispatcher {

    /**
     * The logger.
     */
    private static final Logger logger = Log4J.getLogger(RPObjectChangeDispatcher.class);
    /**
     * The normal listener.
     */
    protected RPObjectChangeListener listener;
    /**
     * The user object listener.
     */
    protected ArrayList<RPObjectChangeListener> userListeners =
            new ArrayList<RPObjectChangeListener>();

    /**
     * Create an RPObjectChange event dispatcher.
     *
     * @param listener
     *            The normal listener.
     * @param userListener
     *            The user object listener.
     */
    public RPObjectChangeDispatcher(final RPObjectChangeListener listener,
            final RPObjectChangeListener userListener) {
        this.listener = listener;
        userListeners.add(userListener);
    }

    //
    // RPObjectChangeDispatcher
    //
    /**
     * Dispatch object added event.
     *
     * @param object
     *            The object.
     * @param user
     *            If this is the private user object.
     */
    public void dispatchAdded(RPObject object, boolean user) {
        try {
            logger.debug("Object(" + object.getID() + ") added to client");
            fixContainers(object);
            fireAdded(object, user);
        } catch (Exception e) {
            logger.error("dispatchAdded failed, object is " + object, e);
        }
    }

    /**
     * Dispatch object removed event.
     *
     * @param object
     *            The object.
     * @param user
     *            If this is the private user object.
     */
    public void dispatchRemoved(RPObject object, boolean user) {
        try {
            logger.debug("Object(" + object.getID() + ") removed from client");
            fixContainers(object);
            fireRemoved(object, user);
        } catch (Exception e) {
            logger.error(
                    "dispatchRemovedonDeleted failed, object is " + object, e);
        }
    }

    /**
     * Dispatch object added/changed attribute(s) event.
     *
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     * @param user
     *            If this is the private user object.
     */
    public void dispatchModifyAdded(final RPObject object,
            final RPObject changes, final boolean user) {
        try {
            logger.debug("Object(" + object.getID() + ") modified in client");
            fixContainers(object);
            fixContainers(changes);
            fireChangedAdded(object, changes, user);
            object.applyDifferences(changes, null);
        } catch (Exception e) {
            logger.debug("dispatchModifyAdded failed, object is " + object
                    + ", changes is " + changes, e);
        }
    }

    /**
     * Dispatch object removed attribute(s) event.
     *
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     * @param user
     *            If this is the private user object.
     */
    public void dispatchModifyRemoved(RPObject object, RPObject changes,
            boolean user) {
        try {
            logger.debug("Object(" + object.getID() + ") modified in client");
            logger.debug("Original(" + object + ") modified in client");

            fixContainers(object);
            fixContainers(changes);
            fireChangedRemoved(object, changes, user);
            object.applyDifferences(null, changes);

            logger.debug("Modified(" + object + ") modified in client");
            logger.debug("Changes(" + changes + ") modified in client");
        } catch (Exception e) {
            logger.error("dispatchModifyRemoved failed, object is " + object
                    + ", changes is " + changes, e);
        }
    }

    /**
     * Dump an object out in an easily readable format. TEMP!! TEST METHOD -
     * USED FOR DEBUGING.
     *
     * Probably should be in a common util class if useful long term.
     *
     * @param object
     *            to be dumped
     */
    public static void dumpObject(RPObject object) {
        StringBuilder sbuf = new StringBuilder();

        sbuf.append(object.getRPClass().getName());
        sbuf.append('[');
        buildIDPath(sbuf, object);
        sbuf.append(']');

        logger.info(sbuf);

        for (String name : object) {
            logger.info("  " + name + ": " + object.get(name));
        }
    }

    /**
     *
     * @param sbuf
     * @param object
     */
    protected static void buildIDPath(final StringBuilder sbuf,
            final RPObject object) {
        RPSlot slot = object.getContainerSlot();

        if (slot != null) {
            buildIDPath(sbuf, object.getContainer());
            sbuf.append(':');
            sbuf.append(slot.getName());
            sbuf.append(':');
        }

        sbuf.append(object.getID().getObjectID());
    }

    /**
     * Fix parent <-> child linkage.
     * on creation.
     * @param object whose slots shall be fixed.
     */
    protected void fixContainers(final RPObject object) {
        for (RPSlot slot : object.slots()) {
            for (RPObject sobject : slot) {
                if (!sobject.isContained()) {
                    logger.debug("Fixing container: " + slot);
                    sobject.setContainer(object, slot);
                }

                fixContainers(sobject);
            }
        }
    }

    /**
     * Notify listeners that an object was added.
     *
     * @param object
     *            The object.
     * @param user
     *            If this is the private user object.
     */
    protected void fireAdded(RPObject object, boolean user) {

        /*
         * Call before children have been notified
         */
        listener.onAdded(object);

        if (user) {
            for (RPObjectChangeListener userListener : userListeners) {
                userListener.onAdded(object);
            }
        }

        /*
         * Walk each slot
         */
        for (RPSlot slot : object.slots()) {
            String slotName = slot.getName();

            for (RPObject sobject : slot) {
                fireAdded(object, slotName, sobject, user);
            }
        }
    }

    /**
     * Notify listeners that a slot object was added.
     *
     * @param object
     *            The parent object.
     * @param slotName
     *            The slot name.
     * @param sobject
     *            The slot object.
     * @param user
     *            If this is the private user object.
     */
    protected void fireAdded(final RPObject object, final String slotName,
            final RPObject sobject, final boolean user) {
        /*
         * Notify child
         */
        fireAdded(sobject, user);

        /*
         * Call after the child has been notified
         */
        listener.onSlotAdded(object, slotName, sobject);

        if (user) {
            for (RPObjectChangeListener userListener : userListeners) {
                userListener.onSlotAdded(object, slotName, sobject);
            }
        }
    }

    /**
     * Notify listeners that an object added/changed attribute(s). This will
     * cascade down slot trees.
     *
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     * @param user
     *            If this is the private user object.
     */
    protected void fireChangedAdded(RPObject object, RPObject changes,
            boolean user) {

        /*
         * Walk each slot
         */
        for (RPSlot cslot : changes.slots()) {
            if (cslot.size() != 0) {
                fireChangedAdded(object, cslot, user);
            }
        }

        /*
         * Call after children have been notified
         */
        listener.onChangedAdded(object, changes);

        if (user) {
            for (RPObjectChangeListener userListener : userListeners) {
                userListener.onChangedAdded(object, changes);
            }
        }
    }

    /**
     * Notify listeners that an object slot added/changed attribute(s). This
     * will cascade down object trees.
     *
     * @param object
     *            The base object.
     * @param cslot
     *            The changes slot.
     * @param user
     *            If this is the private user object.
     */
    protected void fireChangedAdded(RPObject object, RPSlot cslot, boolean user) {
        String slotName = cslot.getName();
        RPSlot slot;

        /*
         * Find the original slot entry (if any)
         */
        if (object.hasSlot(slotName)) {
            slot = object.getSlot(slotName);
        } else {
            slot = null;
        }

        /*
         * Walk the changes
         */
        for (RPObject schanges : cslot) {
            RPObject.ID id = schanges.getID();

            if ((slot != null) && slot.has(id)) {
                RPObject sobject = slot.get(id);

                listener.onSlotChangedAdded(object, slotName, sobject, schanges);

                if (user) {
                    for (RPObjectChangeListener userListener : userListeners) {
                        userListener.onSlotChangedAdded(object, slotName, sobject,
                                schanges);
                    }
                }

                fireChangedAdded(sobject, schanges, user);
            } else {
                if (!schanges.isContained()) {
                    logger.warn("!!! Not contained! - " + schanges);
                }

                fireAdded(object, slotName, schanges, user);
            }
        }
    }

    /**
     * Notify listeners that an object removed attribute(s). This will cascade
     * down slot trees.
     *
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     * @param user
     *            If this is the private user object.
     */
    protected void fireChangedRemoved(RPObject object, RPObject changes,
            boolean user) {

        /*
         * Call before children have been notified
         */
        listener.onChangedRemoved(object, changes);

        if (user) {
            for (RPObjectChangeListener userListener : userListeners) {
                userListener.onChangedRemoved(object, changes);
            }
        }

        /*
         * Walk each slot
         */
        for (RPSlot cslot : changes.slots()) {
            if (cslot.size() != 0) {
                fireChangedRemoved(object, cslot, user);
            }
        }
    }

    /**
     * Notify listeners that an object slot removed attribute(s). This will
     * cascade down object trees.
     *
     * @param object
     *            The base object.
     * @param cslot
     *            The changes slot.
     * @param user
     *            If this is the private user object.
     */
    protected void fireChangedRemoved(RPObject object, RPSlot cslot,
            boolean user) {
        String slotName = cslot.getName();

        /*
         * Find the original slot entry
         */
        RPSlot slot = object.getSlot(slotName);

        /*
         * Walk the changes
         */
        for (RPObject schanges : cslot) {
            RPObject sobject = slot.get(schanges.getID());

            if (sobject == null) {
                logger.debug("Unable to find existing: " + schanges);
                continue;
            }

            /*
             * Remove attrs vs. object [see applyDifferences()]
             */
            if (schanges.size() > 1) {
                listener.onSlotChangedRemoved(object, slotName, sobject,
                        schanges);

                if (user) {
                    for (RPObjectChangeListener userListener : userListeners) {
                        userListener.onSlotChangedRemoved(object, slotName,
                                sobject, schanges);
                    }
                }

                fireChangedRemoved(sobject, schanges, user);
            } else {
                fireRemoved(object, slotName, sobject, user);
            }
        }
    }

    /**
     * Notify listeners that an object was removed.
     *
     * @param object
     *            The object.
     * @param user
     *            If this is the private user object.
     */
    protected void fireRemoved(RPObject object, boolean user) {

        /*
         * Walk each slot
         */
        for (RPSlot slot : object.slots()) {
            String slotName = slot.getName();

            for (RPObject sobject : slot) {
                fireRemoved(object, slotName, sobject, user);
            }
        }

        /*
         * Call after children have been notified
         */
        listener.onRemoved(object);

        if (user) {
            for (RPObjectChangeListener userListener : userListeners) {
                userListener.onRemoved(object);
            }
        }
    }

    /**
     * Notify listeners that a slot object was removed.
     *
     * @param object
     *            The container object.
     * @param slotName
     *            The slot name.
     * @param sobject
     *            The slot object.
     * @param user
     *            If this is the private user object.
     */
    protected void fireRemoved(final RPObject object, final String slotName,
            final RPObject sobject, final boolean user) {
        /*
         * Call before the child is notified
         */
        listener.onSlotRemoved(object, slotName, sobject);

        if (user) {
            for (RPObjectChangeListener userListener : userListeners) {
                userListener.onSlotRemoved(object, slotName, sobject);
            }
        }

        /*
         * Notify child
         */
        fireRemoved(sobject, user);
    }
    
    public boolean addRPObjectChangeListener(RPObjectChangeListener listener){
        return userListeners.add(listener);
    }
    
    public boolean removeRPObjectChangeListener(RPObjectChangeListener listener){
        return userListeners.remove(listener);
    }
}
