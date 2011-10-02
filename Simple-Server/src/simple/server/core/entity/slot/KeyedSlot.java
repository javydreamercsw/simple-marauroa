
package simple.server.core.entity.slot;

import simple.server.core.entity.Entity;

/**
 * abused slots of players which contain one RPObject used as hashmap
 *
 * @author hendrik
 */
public class KeyedSlot extends EntitySlot {

    /**
     * creates a new keyed slot
     *
     * @param name name of slot
     */
    public KeyedSlot(String name) {
        super(name);
    }

    @Override
    public boolean isItemSlot() {
        return false;
    }

    @Override
    public boolean isReachableForTakingThingsOutOfBy(Entity entity) {
        return false;
    }
}
