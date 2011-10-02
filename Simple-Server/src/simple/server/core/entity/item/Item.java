
package simple.server.core.entity.item;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import simple.common.Grammar;
import simple.server.core.engine.SimpleRPWorld;
import simple.server.core.entity.RPEntity;
import simple.server.core.event.EquipListener;
import simple.server.core.event.TurnListener;

/**
 * This is an item.
 */
public class Item extends RPEntity implements TurnListener, EquipListener {

    private static final long serialVersionUID = 1L;
    /** list of possible slots for this item */
    private List<String> possibleSlots;
    public static final int DEGRADATION_TIMEOUT = 10 * 60; // 10 minutes
    public static final String RPCLASS_NAME = "item";

    public static void generateRPClass() {
        RPClass entity = new RPClass(RPCLASS_NAME);
        entity.isA("entity");

        // class, sword/armor/...
        entity.addAttribute("class", Type.STRING);

        // subclass, long sword/leather/armor/...
        entity.addAttribute("subclass", Type.STRING);

        // name of item (ie 'Kings Sword')
        entity.addAttribute("name", Type.STRING);

        // Some items have attack values
        entity.addAttribute("atk", Type.SHORT);

        // Some items indicate how often you can attack.
        entity.addAttribute("rate", Type.SHORT);

        // Some items have defense values
        entity.addAttribute("def", Type.SHORT);

        // Some items(food) have amount of something
        // (a bottle, a piece of meat).
        entity.addAttribute("amount", Type.INT);

        // Some items (range weapons, ammunition, missiles)
        // have a range.
        entity.addAttribute("range", Type.SHORT);

        // Some items(food) have regeneration
        entity.addAttribute("regen", Type.INT);

        // Some items(food) have regeneration speed
        entity.addAttribute("frequency", Type.INT);

        // Some items(Stackable) have quantity
        entity.addAttribute("quantity", Type.INT);

        // Some items (Stackable) have maximum quantity
        entity.addAttribute("max_quantity", Type.INT);

        // Some items have minimum level to prevent spoiling
        // the fun for new players
        entity.addAttribute("min_level", Type.INT);

        // To store addAttributeitional info with an item
        entity.addAttribute("infostring", Type.STRING);

        // Some items have individual values
        entity.addAttribute("persistent", Type.FLAG);

        // Some items have lifesteal values
        entity.addAttribute("lifesteal", Type.FLOAT);

        // Some items are quest rewards that other players
        // don't deserve.
        entity.addAttribute("bound", Type.STRING);

        // Some items should not be dropped on death
        entity.addAttribute("undroppableondeath", Type.SHORT);
    }

    /**
     *
     * Creates a new Item.
     *
     * @param name
     *            name of item
     * @param clazz
     *            class (or type) of item
     * @param subclass
     *            subclass of this item
     * @param attributes
     *            attributes (like attack). may be empty or <code>null</code>
     */
    public Item(String name, String clazz, String subclass,
            Map<String, String> attributes) {
        this();

        setEntityClass(clazz);
        setEntitySubClass(subclass);

        put("name", name);

        if (attributes != null) {
            // store all attributes
            for (Entry<String, String> entry : attributes.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }

        update();
    }

    /** no public 'default' item */
    private Item() {
        setRPClass(RPCLASS_NAME);
        put("type", "item");
        possibleSlots = new LinkedList<String>();
        update();
    }

    /**
     * copy constructor
     *
     * @param item
     *            item to copy
     */
    public Item(Item item) {
        super(item);
        setRPClass("item");
        possibleSlots = new ArrayList<String>(item.possibleSlots);
    }

    /**
     * on which slots may this item be equipped
     *
     * @param slots
     *            list of allowed slots
     */
    public void setEquipableSlots(List<String> slots) {
        // save slots
        possibleSlots = slots;
    }

    /**
     * Return the attack points of this item. Positive and negative values are
     * allowed. If this item doesn't modify the attack it should return '0'.
     *
     * @return attack points
     */
    public int getAttack() {
        if (has("atk")) {
            return getInt("atk");
        }

        return 0;
    }

    /**
     * Return the defense points of this item. Positive and negative values are
     * allowed. If this item doesn't modify the defense it should return '0'.
     *
     * @return defense points
     */
    public int getDefense() {
        if (has("def")) {
            return getInt("def");
        }

        return 0;
    }

    /**
     * Return each how many turns this item can attack.
     *
     * @return each how many turns this item can attack.
     */
    public int getAttackRate() {
        if (has("rate")) {
            return getInt("rate");
        }

        /* Default attack rate is 5. */
        return 5;
    }

    /**
     * Return if the item is persistent. Persistent items do not update their
     * stats from the item database and thus can have individual stats
     *
     * @return true if item is persistent
     */
    public boolean isPersistent() {
        if (has("persistent")) {
            return (getInt("persistent") == 1);
        }

        return false;
    }

    /**
     * Set the item's persistence.
     *
     * @param persistent
     *            If the item's stats are persistent.
     */
    public void setPersistent(boolean persistent) {
        if (persistent) {
            put("persistent", 1);
        } else if (has("persistent")) {
            remove("persistent");
        }
    }

    /**
     * Checks if the item is of type <i>type</i>
     *
     * @param clazz
     *            the class to check
     * @return true if the type matches, else false
     */
    public boolean isOfClass(String clazz) {
        return getItemClass().equals(clazz);
    }

    /** @return the type of the item */
    public String getItemClass() {
        if (has("class")) {
            return get("class");
        }

        throw new IllegalStateException("the item does not have a class: " + this);
    }

    /** @return the type of the item */
    public String getItemSubclass() {
        if (has("subclass")) {
            return get("subclass");
        }

        throw new IllegalStateException("the item does not have a subclass: " + this);
    }

    /**
     * Get the name of the item.
     *
     * @return	The programatic item name.
     */
    @Override
    public String getName() {
        return get("name");
    }

    /**
     * Get item count.
     *
     * @return 1.
     */
    public int getQuantity() {
        return 1;
    }

    /** @return the list of possible slots for this item */
    public List<String> getPossibleSlots() {
        return possibleSlots;
    }

    /**
     * Get the player this is bound to. A bound item can only be used by that
     * player.
     *
     * @return The player name, or <code>null</code>.
     */
    public String getBoundTo() {
        if (has("bound")) {
            return get("bound");
        } else {
            return null;
        }
    }

    /**
     * Get the item's infostring. The infostring contains context specific
     * information that is used by the implementation.
     *
     * @return The infostring.
     */
    public String getInfoString() {
        if (has("infostring")) {
            return get("infostring");
        } else {
            return null;
        }
    }

    /**
     * Bind this item to a player. A bound item can only be used by that player.
     *
     * @param name
     *            The player name, or <code>null</code>.
     */
    public void setBoundTo(String name) {
        if (name != null) {
            put("bound", name);
        } else if (has("bound")) {
            remove("bound");
        }
    }

    /**
     * Is the item undroppable. Undroppable items will never be dropped if the player dies.
     *
     * @return true if item is undroppable.
     */
    public boolean isUndroppableOnDeath() {
        if (has("undroppableondeath")) {
            return (getInt("undroppableondeath") == 1);
        }

        return false;
    }

    /**
     * Set is the item undroppable when player dies.
     *
     * @param unDroppableOnDeath If true, the item won't be dropped if the player dies.
     */
    public void setUndroppableOnDeath(boolean unDroppableOnDeath) {
        if (unDroppableOnDeath) {
            put("undroppableondeath", 1);
        } else if (has("undroppableondeath")) {
            remove("undroppableondeath");
        }
    }

    /**
     * Set the item's infostring. The infostring contains context specific
     * information that is used by the implementation.
     *
     * @param infostring
     *            The item's infostring.
     */
    public void setInfoString(String infostring) {
        if (infostring != null) {
            put("infostring", infostring);
        } else if (has("infostring")) {
            remove("infostring");
        }
    }

    @Override
    public String toString() {
        return "Item, " + super.toString();
    }

    @Override
    public void onTurnReached(int currentTurn) {
        // remove this object from the zone where it's lying on
        // the ground
        if (getZone() != null) {
            getZone().remove(getID());
        }
    }

    @Override
    public String describe() {
        String text = "You see " + Grammar.A_noun(getTitle()) + ".";
        String s = "";
        if (hasDescription()) {
            text = getDescription();
        }

        String boundTo = getBoundTo();

        if (boundTo != null) {
            text = text + " It is a special quest reward for " + boundTo + ", and cannot be used by others.";
        }

        if (has("atk")) {
            s += " ATK: " + get("atk");
        }
        if (has("def")) {
            s += " DEF: " + get("def");
        }
        if (has("rate")) {
            s += " RATE: " + get("rate");
        }
        if (has("amount")) {
            s += " HP: " + get("amount");
        }
        if (has("range")) {
            s += " RANGE: " + get("range");
        }
        if (s.length() > 0) {
            s = " Stats are (" + s.trim() + ").";
        }
        return (text + s);
    }

    /**
     * Removes the item. I case of StackableItems only one is removed.
     */
    public void removeOne() {
        removeFromWorld();
    }

    @Override
    public boolean canBeEquippedIn(String slot) {
        if (slot == null) {
            return true; // ground

        }

        // when the slot is called "content", it's a personal chest.
        return possibleSlots.contains(slot) || slot.equals("content");
    }

    public void removeFromWorld() {
        if (isContained()) {
            // We modify the base container if the object change.
            RPObject base = getContainer();

            while (base.isContained()) {
                base = base.getContainer();
            }

            RPSlot slot = getContainerSlot();
            slot.remove(getID());

            SimpleRPWorld.get().modify(base);
        } else {
            SimpleRPWorld.get().remove(getID());
        }
    }

    //
    // Entity
    //
    /**
     * Return the name or something that can be used to identify the
     * entity for the player
     *
     * @param definite
     *	<code>true</code> for "the", and <code>false</code> for "a/an"
     *	in case the entity has no name.
     *
     * @return	The description name.
     */
    @Override
    public String getDescriptionName(final boolean definite) {
        String name = getName();

        if (name != null) {
            return name.replace('_', ' ');
        } else {
            return super.getDescriptionName(definite);
        }
    }

    /**
     * Get the nicely formatted entity title/name.
     *
     * @return The title, or <code>null</code> if unknown.
     */
    @Override
    public String getTitle() {
        String name = getName();

        if (name != null) {
            return name.replace('_', ' ');
        } else {
            return super.getTitle();
        }
    }
}
