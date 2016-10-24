package simple.server.core.entity.api;

import marauroa.common.game.RPObject;

/**
 * This allows to listen to changes in a particular RPObject.
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface RPObjectMonitor {

    /**
     * Get notified when your object has changed.
     *
     * @param obj
     */
    public void modify(RPObject obj);
}
