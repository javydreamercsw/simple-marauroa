package simple.server.core.event.api;

import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IRPEvent extends IAttribute{

    /**
     * Copy constructor
     *
     * @param event
     *            the object that is going to be copied.
     */
    public void fill(RPEvent event);

    /**
     * Set the owner of this RPEvent.
     *
     * @param owner
     */
    public void setOwner(RPObject owner);

    /**
     * Return the name of the event
     *
     * @return name of the event
     */
    public String getName();
}
