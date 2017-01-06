package simple.common.game;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.net.Serializable;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ClientObjectInterface extends RPEntityInterface,
        Serializable, Comparable {

    /**
     * Create a ClientObject from an RPObject
     *
     * @param object RPObject to create from
     * @return Created object
     */
    public ClientObjectInterface create(RPObject object);

    /**
     * Create default ClientObject
     *
     * @param name name of the object
     * @return Created object
     */
    public ClientObjectInterface createDefaultClientObject(String name);

    /**
     * Create default ClientObject
     *
     * @param object Object to create from
     * @return Created object
     */
    public ClientObjectInterface createDefaultClientObject(RPObject object);

    /**
     * Get slot.
     *
     * @param name Slot name
     * @return Slot or null if not found.
     */
    public RPSlot getSlot(String name);
}
