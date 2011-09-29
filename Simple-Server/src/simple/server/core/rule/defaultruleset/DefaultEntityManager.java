/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.rule.defaultruleset;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import simple.server.core.config.ItemGroupsXMLLoader;
import simple.server.core.entity.Entity;
import simple.server.core.entity.item.Item;
import simple.server.core.rule.EntityManager;

/**
 * entity manager for the default ruleset.
 * 
 * @author Matthias Totz
 */
public class DefaultEntityManager implements EntityManager {

    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(DefaultEntityManager.class);
    /** maps the tile ids to the classes. */
    private Map<String, String> idToClass;
    /** maps the item names to the actual item enums. */
    private Map<String, DefaultItem> classToItem;
    /** lists all items that are being used at least once .*/
    private Map<String, Item> createdItem;

    /** no public constructor. */
    public DefaultEntityManager() {
        idToClass = new HashMap<String, String>();

        // Build the items tables
        classToItem = new HashMap<String, DefaultItem>();
        createdItem = new HashMap<String, Item>();

        try {
            if (new File("/data/conf/items.xml").exists()) {
                ItemGroupsXMLLoader loader = new ItemGroupsXMLLoader(new URI(
                        "/data/conf/items.xml"));
                List<DefaultItem> items = loader.load();

                for (DefaultItem item : items) {
                    String clazz = item.getItemName();

                    if (classToItem.containsKey(clazz)) {
                        logger.warn("Repeated item name: " + clazz);
                    }

                    classToItem.put(clazz, item);
                }
            }
        } catch (Exception e) {
            logger.error("items.xml could not be loaded", e);
        }
    }

    @Override
    public boolean addItem(DefaultItem item) {
        String clazz = item.getItemName();

        if (classToItem.containsKey(clazz)) {
            logger.warn("Repeated item name: " + clazz);
            return false;
        }

        classToItem.put(clazz, item);

        return true;
    }

    /**
     * @return a list of all Items that are instantiated.
     */
    @Override
    public Collection<Item> getItems() {
        return createdItem.values();
    }

    /**
     * @return the entity or <code>null</code> if the id is unknown.
     *
     * @throws NullPointerException
     *             if clazz is <code>null</code>
     */
    @Override
    public Entity getEntity(String clazz) {
        if (clazz == null) {
            throw new NullPointerException("entity class is null");
        }

        Entity entity;

        // Lookup the id in the item table
        entity = getItem(clazz);
        if (entity != null) {
            return entity;
        }

        return null;
    }

    /** return true if the Entity is a creature. */
    @Override
    public boolean isItem(String clazz) {
        if (clazz == null) {
            throw new NullPointerException("entity class is null");
        }

        return classToItem.containsKey(clazz);
    }

    /**
     * @return the item or <code>null</code> if the clazz is unknown.
     *
     * @throws NullPointerException
     *             if clazz is <code>null</code>
     */
    @Override
    public Item getItem(String clazz) {
        if (clazz == null) {
            throw new NullPointerException("entity class is null");
        }

        // Lookup the clazz in the item table
        DefaultItem item = classToItem.get(clazz);
        if (item != null) {
            if (createdItem.get(clazz) == null) {
                createdItem.put(clazz, item.getItem());
            }
            return item.getItem();
        }

        return null;
    }
}
