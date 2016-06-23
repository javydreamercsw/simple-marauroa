
package simple.server.core.event;

import simple.server.core.entity.RPEntity;

/**
 * Implementing classes will be called back when a player uses them.
 */
public interface UseListener {

    /**
     * Invoked when the object is used.
     * 
     * @param user
     *            the RPEntity who uses the object
     * @return
     */
    boolean onUsed(RPEntity user);
}
