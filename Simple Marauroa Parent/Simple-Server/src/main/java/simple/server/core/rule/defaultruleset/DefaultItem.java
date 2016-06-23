package simple.server.core.rule.defaultruleset;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import simple.server.core.entity.item.Item;

/**
 * All default items which can be reduced to stuff that increase the attack
 * point and stuff that increase the defense points.
 *
 * @author Matthias Totz, chad3f
 */
public class DefaultItem {

    private static final Logger LOG
            = Logger.getLogger(DefaultItem.class.getSimpleName());
    /**
     * items class.
     */
    private String clazz;
    /**
     * items sub class.
     */
    private String subclazz;
    /**
     * items type.
     */
    private String name;
    /**
     * optional item description.
     */
    private String description;
    // weight system is not yet implemented.
    @SuppressWarnings("unused")
    private double weight;
    /**
     * slots where this item can be equipped.
     */
    private List<String> slots;
    /**
     * Map Tile Id.
     */
    private int tileid;
    /**
     * Attributes of the item .
     */
    private Map<String, String> attributes;
    /**
     * Implementation creator.
     */
    protected Creator creator;
    private Class<?> implementation;
    private int value;

    public DefaultItem(String clazz, String subclazz, String name, int tileid) {
        this.clazz = clazz;
        this.subclazz = subclazz;
        this.name = name;
        this.tileid = tileid;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void setEquipableSlots(List<String> slots) {
        this.slots = slots;
    }

    public List<String> getEquipableSlots() {
        return slots;
    }

    public void setDescription(String text) {
        this.description = text;
    }

    public String getDescription() {
        return description;
    }

    public void setImplementation(Class<?> implementation) {
        this.implementation = implementation;
        creator = buildCreator(implementation);
    }

    public Class<?> getImplementation() {
        return implementation;
    }

    /**
     * Build a creator for the class. It uses the following constructor search
     * order:<br>
     *
     * <ul>
     * <li><em>Class</em>(<em>name</em>, <em>clazz</em>,
     * <em>subclazz</em>, <em>attributes</em>)
     * <li><em>Class</em>(<em>attributes</em>)
     * <li><em>Class</em>()
     * </ul>
     *
     * @param implementation The implementation class.
     *
     * @return A creator, or <code>null</code> if none found.
     */
    protected Creator buildCreator(Class<?> implementation) {
        Constructor<?> construct;

        /*
         * <Class>(name, clazz, subclazz, attributes)
         */
        try {
            construct = implementation.getConstructor(new Class[]{
                String.class, String.class, String.class, Map.class});

            return new FullCreator(construct);
        } catch (NoSuchMethodException ex) {
            // ignore and continue
        }

        /*
         * <Class>(attributes)
         */
        try {
            construct = implementation.getConstructor(new Class[]{Map.class});

            return new AttributesCreator(construct);
        } catch (NoSuchMethodException ex) {
            // ignore and continue
        }

        /*
         * <Class>()
         */
        try {
            construct = implementation.getConstructor(new Class[]{});

            return new DefaultCreator(construct);
        } catch (NoSuchMethodException ex) {
            // ignore and continue
        }

        return null;
    }

    /**
     * Return an item-instance.
     *
     * @return An item, or <code>null</code> on error.
     */
    public Item getItem() {

        /*
         * Just in case - Really should generate fatal error up front (in
         * ItemXMLLoader).
         */
        if (creator == null) {
            return null;
        }
        Item item = creator.createItem();
        if (item != null) {
            item.setEquipableSlots(slots);
            item.setDescription(description);
        }

        return item;
    }

    /**
     * Return the tile id .
     *
     * @return
     */
    public int getTileId() {
        return tileid;
    }

    public void setTileId(int val) {
        tileid = val;
    }

    public void setValue(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    /**
     * Return the class.
     *
     * @return
     */
    public String getItemClass() {
        return clazz;
    }

    public void setItemClass(String val) {
        clazz = val;
    }

    /**
     * Return the subclass.
     *
     * @return
     */
    public String getItemSubClass() {
        return subclazz;
    }

    public void setItemSubClass(String val) {
        subclazz = val;
    }

    public String getItemName() {
        return name;
    }

    public void setItemName(String val) {
        name = val;
    }

    public String toXML() {
        StringBuilder os = new StringBuilder();
        os.append("  <item name=\"").append(name).append("\">\n");
        os.append("    <type class=\"").append(clazz).append("\" subclass=\"")
                .append(subclazz).append("\" tileid=\"").append(tileid).append("\"/>\n");
        if (description != null) {
            os.append("    <description>").append(description).append("</description>\n");
        }
        os.append("    <implementation class-name=\"")
                .append(implementation.getCanonicalName()).append("\"/>");
        os.append("    <attributes>\n");
        attributes.entrySet().stream().forEach((entry) -> {
            os.append("      <").append(entry.getKey()).append(" value=\"")
                    .append(entry.getValue()).append("\"/>\n");
        });

        os.append("    </attributes>\n");
        os.append("    <weight value=\"").append(weight).append("\"/>\n");
        os.append("    <value value=\"").append(value).append("\"/>\n");
        os.append("    <equipable>\n");
        for (String slot : slots) {
            os.append("      <slot name=\"").append(slot).append("\"/>\n");
        }
        os.append("    </equipable>\n");
        os.append("  </item>\n");
        return os.toString();
    }

    /**
     * Base item creator (using a constructor).
     */
    protected abstract class Creator {

        protected Constructor<?> construct;

        public Creator(Constructor<?> construct) {
            this.construct = construct;
        }

        protected abstract Object create() throws IllegalAccessException,
                InstantiationException, InvocationTargetException;

        public Item createItem() {
            try {
                return (Item) create();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                LOG.log(Level.SEVERE, "Error creating item: " + name, ex);
            } catch (ClassCastException ex) {
                /*
                 * Wrong type (i.e. not [subclass of] Item)
                 */
                LOG.log(Level.SEVERE,
                        "Implementation for {0} is not an Item class", name);
            }

            return null;
        }
    }

    /**
     * Create an item class via the <em>attributes</em> constructor.
     */
    protected class AttributesCreator extends Creator {

        public AttributesCreator(Constructor<?> construct) {
            super(construct);
        }

        @Override
        protected Object create() throws IllegalAccessException,
                InstantiationException, InvocationTargetException {
            return construct.newInstance(new Object[]{attributes});
        }
    }

    /**
     * Create an item class via the default constructor.
     */
    protected class DefaultCreator extends Creator {

        public DefaultCreator(Constructor<?> construct) {
            super(construct);
        }

        @Override
        protected Object create() throws IllegalAccessException,
                InstantiationException, InvocationTargetException {
            return construct.newInstance(new Object[]{});
        }
    }

    /**
     * Create an item class via the full arguments (<em>name, clazz, subclazz,
     * attributes</em>) constructor.
     */
    protected class FullCreator extends Creator {

        public FullCreator(Constructor<?> construct) {
            super(construct);
        }

        @Override
        protected Object create() throws IllegalAccessException,
                InstantiationException, InvocationTargetException {
            return construct.newInstance(new Object[]{name, clazz, subclazz,
                attributes});
        }
    }
}
