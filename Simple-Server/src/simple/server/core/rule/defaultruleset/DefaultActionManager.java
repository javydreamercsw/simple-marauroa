
package simple.server.core.rule.defaultruleset;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import simple.server.core.engine.ItemLogger;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.item.Item;
import simple.server.core.entity.item.Stackable;
import simple.server.core.entity.item.StackableItem;
import simple.server.core.rule.ActionManager;

/**
 * 
 * @author Matthias Totz
 */
public class DefaultActionManager implements ActionManager {

    /** the singleton instance, lazy initialisation. */
    private static DefaultActionManager manager;

    /** no public constructor. */
    private DefaultActionManager() {
        // hide constructor, this is a Singleton
    }

    /**
     * @return the instance of this manager. Note: This method is synchonized.
     */
    public static synchronized DefaultActionManager getInstance() {
        if (manager == null) {
            manager = new DefaultActionManager();
        }
        return manager;
    }

    /**
     * @param entity
     * @param item 
     * @return the slot name for the item or null if there is no matching slot
     *         in the entity
     */
    public String getSlotNameToEquip(RPEntity entity, Item item) {
        // get all possible slots for this item
        List<String> slotNames = item.getPossibleSlots();

        if (item instanceof Stackable) {
            // first try to put the item on an existing stack
            Stackable stackEntity = (Stackable) item;
            for (String slotName : slotNames) {
                if (entity.hasSlot(slotName)) {
                    RPSlot rpslot = entity.getSlot(slotName);
                    for (RPObject object : rpslot) {
                        if (object instanceof Stackable) {
                            // found another stackable
                            Stackable other = (Stackable) object;
                            if (other.isStackable(stackEntity)) {
                                return slotName;
                            }
                        }
                    }
                }
            }
        }

        // We can't stack it on another item. Check if we can simply
        // add it to an empty cell.
        for (String slot : slotNames) {
            if (entity.hasSlot(slot)) {
                RPSlot rpslot = entity.getSlot(slot);
                if (!rpslot.isFull()) {
                    return slot;
                }
            }
        }
        return null;
    }

    /** Equips the item in the specified slot.
     * @param entity
     * @param slotName
     * @param item
     * @return 
     */
    public boolean onEquip(RPEntity entity, String slotName, Item item) {
        if (!entity.hasSlot(slotName)) {
            return false;
        }

        RPSlot rpslot = entity.getSlot(slotName);

        if (item instanceof StackableItem) {
            StackableItem stackEntity = (StackableItem) item;
            // find a stackable item of the same type
            for (RPObject object : rpslot) {
                if (object instanceof StackableItem) {
                    // found another stackable
                    StackableItem other = (StackableItem) object;
                    if (other.isStackable(stackEntity)) {
                        try {
                            // other is the same type...merge them
                            ItemLogger.merge(entity, stackEntity, other);
                        } catch (Exception ex) {
                            Logger.getLogger(DefaultActionManager.class.getSimpleName()).log(Level.SEVERE, null, ex);
                        }
                        other.add(stackEntity);
                        return true;
                    }
                }
            }
        }

        // We can't stack it on another item. Check if we can simply
        // add it to an empty cell.
        if (rpslot.isFull()) {
            return false;
        } else {
            rpslot.add(item);
            return true;
        }
    }
}
