package simple.server.core.entity.item;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.extension.MarauroaServerExtension;

/**
 * This is an item.
 */
@ServiceProvider(service = RPEntityInterface.class, position = 5)
public class Item extends RPEntity {

    public static final int DEGRADATION_TIMEOUT = 10 * 60; // 10 minutes
    public static final String DEFAULT_RPCLASS_NAME = "item";

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(DEFAULT_RPCLASS_NAME)) {
            try {
                RPClass entity = new RPClass(DEFAULT_RPCLASS_NAME);
                entity.isA(RPEntity.class.newInstance().getRPClassName());
                for (MarauroaServerExtension ext
                        : Lookup.getDefault().lookupAll(MarauroaServerExtension.class)) {
                    ext.modifyItemRPClassDefinition(entity);
                }
            }
            catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (!RPCLASS_NAME.isEmpty() && !RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass clazz = new RPClass(RPCLASS_NAME);
            clazz.isA(DEFAULT_RPCLASS_NAME);
        }
    }

    /**
     *
     * Creates a new Item.
     *
     * @param name name of item
     * @param clazz class (or type) of item
     * @param subclass subclass of this item
     * @param attributes attributes (like attack). may be empty or
     * <code>null</code>
     */
    public Item(String name, String clazz, String subclass,
            Map<String, String> attributes) {
        setRPClass(DEFAULT_RPCLASS_NAME);
        put(WellKnownActionConstant.TYPE, DEFAULT_RPCLASS_NAME);

        setEntityClass(clazz);
        setEntitySubClass(subclass);

        put(Entity.NAME, name);

        if (attributes != null) {
            // store all attributes
            attributes.entrySet().stream().forEach((entry) -> {
                put(entry.getKey(), entry.getValue());
            });
        }

        update();
    }

    public Item() {
        RPCLASS_NAME = DEFAULT_RPCLASS_NAME;
    }

    /**
     * copy constructor
     *
     * @param item item to copy
     */
    public Item(Item item) {
        super(item);
        setRPClass(DEFAULT_RPCLASS_NAME);
    }

    public Item(RPObject item) {
        super(item);
        setRPClass(DEFAULT_RPCLASS_NAME);
    }

    @Override
    public void update() {
        super.update();
        for (MarauroaServerExtension ext
                : Lookup.getDefault().lookupAll(MarauroaServerExtension.class)) {
            ext.itemRPClassUpdate(this);
        }
    }
}
