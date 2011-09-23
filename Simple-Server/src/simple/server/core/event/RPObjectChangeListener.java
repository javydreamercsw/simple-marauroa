/*
 * $Rev: 328 $
 * $LastChangedDate: 2010-06-23 07:17:22 -0500 (Wed, 23 Jun 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.server.core.event;

import marauroa.common.game.RPObject;

/**
 * A listener of RPObject changes.
 */
public interface RPObjectChangeListener {

    /**
     * An object was added.
     * 
     * @param object
     *            The object.
     */
    void onAdded(RPObject object);

    /**
     * The object added/changed attribute(s).
     * 
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     */
    void onChangedAdded(RPObject object, RPObject changes);

    /**
     * The object removed attribute(s).
     * 
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     */
    void onChangedRemoved(RPObject object, RPObject changes);

    /**
     * An object received RPevents.
     *
     * @param object
     *            The object.
     * @return The object.
     */
    RPObject onRPEvent(RPObject object);

    /**
     * An object was removed.
     * 
     * @param object
     *            The object.
     */
    void onRemoved(RPObject object);

    /**
     * A slot object was added.
     * 
     * @param object
     *            The container object.
     * @param slotName
     *            The slot name.
     * @param sobject
     *            The slot object.
     */
    void onSlotAdded(RPObject object, String slotName, RPObject sobject);

    /**
     * A slot object added/changed attribute(s).
     * 
     * @param object
     *            The base container object.
     * @param slotName
     *            The container's slot name.
     * @param sobject
     *            The slot object.
     * @param schanges
     *            The slot object changes.
     */
    void onSlotChangedAdded(RPObject object, String slotName, RPObject sobject,
            RPObject schanges);

    /**
     * A slot object removed attribute(s).
     * 
     * @param object
     *            The base container object.
     * @param slotName
     *            The container's slot name.
     * @param sobject
     *            The slot object.
     * @param schanges
     *            The slot object changes.
     */
    void onSlotChangedRemoved(RPObject object, String slotName,
            RPObject sobject, RPObject schanges);

    /**
     * A slot object was removed.
     * 
     * @param object
     *            The container object.
     * @param slotName
     *            The slot name.
     * @param sobject
     *            The slot object.
     */
    void onSlotRemoved(RPObject object, String slotName, RPObject sobject);
}
