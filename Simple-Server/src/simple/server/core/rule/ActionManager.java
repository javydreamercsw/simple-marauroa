
package simple.server.core.rule;

import simple.server.core.entity.RPEntity;
import simple.server.core.entity.item.Item;

/**
 * Ruleset Interface for processing actions in Simple.
 * 
 * @author Matthias Totz
 */
public interface ActionManager {

    String getSlotNameToEquip(RPEntity entity, Item item);

    boolean onEquip(RPEntity entity, String slotName, Item item);
}
