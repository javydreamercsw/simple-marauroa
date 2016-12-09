package simple.client.api;

import marauroa.common.game.RPObject;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface SelfChangeListener {

    /**
     * Detect changes on myself.
     *
     * @param added new things
     * @param deleted removed things
     * @return True if successful.
     */
    public boolean onMyRPObject(RPObject added, RPObject deleted);

    /**
     * Get my RPObject.
     *
     * @return my RPObject
     */
    public RPObject getMyObject();
}
