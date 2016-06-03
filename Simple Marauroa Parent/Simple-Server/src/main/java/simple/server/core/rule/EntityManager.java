
package simple.server.core.rule;

import java.util.Collection;
import simple.server.core.entity.Entity;
import simple.server.core.entity.item.Item;
import simple.server.core.rule.defaultruleset.DefaultItem;

/**
 * Ruleset Interface for resolving Entities.
 * 
 * @author Matthias Totz
 */
public interface EntityManager {

    boolean addItem(DefaultItem item);

    /**
     * Return a list of all Items that are being used at least once.
     *
     * @return
     */
    Collection<Item> getItems();

    /**
     * Return the entity or <code>null</code> if the class is unknown.
     *
     * @param clazz
     *            the creature class, must not be <code>null</code>
     * @return the entity or <code>null</code>
     *
     */
    Entity getEntity(String clazz);

    /**
     * Return true if the Entity is a Item.
     *
     * @param clazz
     *            the Item class, must not be <code>null</code>
     * @return true if it is a Item, false otherwise
     *
     */
    boolean isItem(String clazz);

    /**
     * Return the item or <code>null</code> if the clazz is unknown.
     *
     * @param clazz
     *            the item class, must not be <code>null</code>
     * @return the item or <code>null</code>
     *
     */
    Item getItem(String clazz);
}