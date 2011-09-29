/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.entity.slot;

import marauroa.common.game.RPSlot;
import simple.server.core.entity.Entity;

/**
 * Simple specific information about this slot
 *
 * @author hendrik 
 */
public class EntitySlot extends RPSlot implements Slot {

    /**
     * creates an uninitialized EntitySlot
     *
     */
    public EntitySlot() {
        super();
    }

    /**
     * Creates a new EntitySlot
     *
     * @param name name of slot
     */
    public EntitySlot(String name) {
        super(name);
    }

    public boolean isReachableForTakingThingsOutOfBy(@SuppressWarnings("unused") Entity entity) {
        return false;
    }

    public boolean isReachableForThrowingThingsIntoBy(Entity entity) {
        return isReachableForTakingThingsOutOfBy(entity);
    }

    public boolean isItemSlot() {
        return true;
    }

    public RPSlot getWriteableSlot() {
        return this;
    }
}
