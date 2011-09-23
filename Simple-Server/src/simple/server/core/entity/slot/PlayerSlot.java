/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.entity.slot;

import simple.server.core.entity.Entity;

/**
 * Slots of players which contain items
 *
 * @author hendrik 
 */
public class PlayerSlot extends EntitySlot {

    /**
     * Creates a new PlayerSlot
     *
     * @param name name of slot
     */
    public PlayerSlot(String name) {
        super(name);
    }

    @Override
    public boolean isReachableForTakingThingsOutOfBy(Entity entity) {
        return super.hasAsAncestor(entity);
    }
}
