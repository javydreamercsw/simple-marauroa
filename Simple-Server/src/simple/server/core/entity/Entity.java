package simple.server.core.entity;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.RPWorld;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.Grammar;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.SimpleRPZone;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = RPEntityInterface.class, position = 1)
public class Entity extends RPObject implements RPEntityInterface {

    private static final long serialVersionUID = 1L;
    public static String RPCLASS_NAME = "entity";
    /**
     * The logger.
     */
    private static final Logger logger = Log4J.getLogger(Entity.class);
    private int x;
    private int y;
    /**
     * Amount of resistance this has with other entities (0-100).
     */
    private int resistance;
    private SimpleRPZone zone = null;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Entity(RPObject object) {
        super(object);
        if (!has("x")) {
            put("x", 0);
        }
        if (!has("y")) {
            put("y", 0);
        }
        if (!has("width")) {
            put("width", 1);
        }
        if (!has("height")) {
            put("height", 1);
        }
        if (!has("resistance")) {
            put("resistance", 100);
        }
        if (!has("visibility")) {
            put("visibility", 100);
        }
        update();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Entity() {
    }

    @Override
    public void generateRPClass() {
        RPClass entity = new RPClass(RPCLASS_NAME);

        // Some things may have a textual description
        entity.addAttribute("description", Type.LONG_STRING, Definition.HIDDEN);

        entity.addAttribute("type", Type.STRING);
        /**
         * Resistance to other entities (0-100). 0=Phantom, 100=Obstacle.
         */
        entity.addAttribute("resistance", Type.BYTE, Definition.VOLATILE);

        entity.addAttribute("x", Type.SHORT);
        entity.addAttribute("y", Type.SHORT);

        /*
         * The size of the entity (in world units).
         */
        entity.addAttribute("width", Type.SHORT, Definition.VOLATILE);
        entity.addAttribute("height", Type.SHORT, Definition.VOLATILE);

        /*
         * If this is set, the client will discard/ignore entity
         */
        entity.addAttribute("server-only", Type.FLAG, Definition.VOLATILE);

        /*
         * The visibility of the entity drawn on client (0-100). 0=Invisible,
         * 100=Solid. Useful when mixed with effect.
         */
        entity.addAttribute("visibility", Type.INT, Definition.VOLATILE);
    }

    /**
     * describes the entity (if a players looks at it)
     *
     * @return description from the players point of view
     */
    public String describe() {
        if (hasDescription()) {
            return getDescription();
        }

        return "You see " + getDescriptionName(false) + ".";
    }

    /**
     * returns the name or something that can be used to identify the entity for
     * the player
     *
     * @param definite
     *            true for "the" and false for "a/an" in case the entity has no
     *            name
     * @return name
     */
    public String getDescriptionName(boolean definite) {
        String name = getName();
        if (name != null) {
            return name;
        } else if (has("subclass")) {
            return Grammar.article_noun(get("subclass"), definite);
        } else if (has("class")) {
            return Grammar.article_noun(get("class"), definite);
        } else {
            String ret = "something indescribably strange";
            if (has("type")) {
                ret += " of type " + get("type");
            }
            if (has("id")) {
                ret += " with id " + get("id");
            }
            if (has("zone")) {
                ret += " in zone " + get("zone");
            }
            return ret;
        }
    }

    public boolean hasDescription() {
        if (has("description")) {
            return ((getDescription() != null) && (getDescription().length() > 0));
        }
        return (false);
    }

    public void setDescription(String text) {
        if (text == null) {
            text = "";
        }
        put("description", text);
    }

    public String getDescription() {
        String description = "";
        if (has("description")) {
            description = get("description");
        }
        return description;
    }

    /**
     * Get the entity name.
     *
     * @return The entity's name, or <code>null</code> if undefined.
     */
    public String getName() {
        if (has("name")) {
            return get("name").replace("_", " ");
        } else {
            return null;
        }
    }

    /**
     * Get the nicely formatted entity title/name.
     *
     * @return The title, or <code>null</code> if unknown.
     */
    public String getTitle() {
        if (has("title")) {
            return get("title");
        } else if (has("name")) {
            return get("name").replace('_', ' ');
        } else if (has("subclass")) {
            return get("subclass").replace('_', ' ');
        } else if (has("class")) {
            return get("class").replace('_', ' ');
        } else if (has("type")) {
            return get("type").replace('_', ' ');
        } else {
            return null;
        }
    }

    /**
     * Get the entity X coordinate.
     *
     * @return The X coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the entity Y coordinate.
     *
     * @return The Y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Get the zone this entity is in.
     *
     * @return A zone, or <code>null</code> if not in one.
     */
    @Override
    public SimpleRPZone getZone() {
        // Use onAdded()/onRemoved() to grab a copy
        // of the zone and save as a local variable.
        if (zone == null) {
            zone = (SimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class).getRPZone(get("zoneid"));
        }
        return zone;
    }

    /**
     * Get the resistance this has on other entities (0-100).
     *
     * @return The amount of resistance, or 0 if in ghostmode.
     */
    public int getResistance() {
        return (isGhost() ? 0 : resistance);
    }

    /**
     * Get the resistance between this and another entity (0-100).
     *
     * @param entity
     * @return The amount of combined resistance.
     */
    public int getResistance(final Entity entity) {
        return ((getResistance() * entity.getResistance()) / 100);
    }

    /**
     * Checks whether an entity is a ghost (non physically interactive).
     *
     * @return <code>true</code> if in ghost mode.
     */
    public boolean isGhost() {
        return has("ghostmode");
    }

    /**
     * Called when this object is added to a zone.
     *
     * @param zone
     *            The zone this was added to.
     */
    @Override
    public void onAdded(SimpleRPZone zone) {
        if (this.zone != null) {
            //Make sure its not in the old zone
            if (this.zone.has(getID())) {
                logger.error("Entity added while in another zone: " + this + " in zone " + zone.getID());
                this.zone.remove(getID());
            }
        }
        this.zone = zone;
    }

    /**
     * Called when this object is being removed from a zone.
     *
     * @param zone
     *            The zone this will be removed from.
     */
    @Override
    public void onRemoved(SimpleRPZone zone) {
        if (this.zone != zone) {
            logger.error("Entity removed from wrong zone: " + this);
        }
        this.zone = null;
    }

    /**
     * Notifies the SimpleRPWorld that this entity's attributes have changed.
     * 
     */
    public void notifyWorldAboutChanges() {
        logger.debug("Object zone: " + get("zoneid"));
        RPWorld.get().modify(this);
    }

    /**
     * Set the entity class.
     *
     * @param	clazz		The class name.
     */
    public void setEntityClass(final String clazz) {
        put("class", clazz);
    }

    /**
     * Set the entity sub-class.
     *
     * @param	subclazz	The sub-class name.
     */
    public void setEntitySubClass(final String subclazz) {
        put("subclass", subclazz);
    }

    /**
     * Set the entity position.
     *
     * @param x
     *            The x position (in world units).
     * @param y
     *            The y position (in world units).
     */
    public void setPosition(final int x, final int y) {
        if (this.x != x) {
            this.x = x;
            put("x", x);
        }

        if (this.y != y) {
            this.y = y;
            put("y", y);
        }
    }

    /**
     * Set resistance this has with other entities.
     *
     * @param resistance
     *            The amount of resistance (0-100).
     */
    public void setResistance(int resistance) {
        this.resistance = resistance;
        put("resistance", resistance);
    }

    /**
     * Set the entity's visibility.
     *
     * @param visibility
     *            The visibility (0-100).
     */
    public void setVisibility(final int visibility) {
        put("visibility", visibility);
    }

    public void update() {
        if (!has("x")) {
            put("x", 0);
        }
        x = getInt("x");

        if (!has("y")) {
            put("y", 0);
        }
        y = getInt("y");

        if (!has("resistance")) {
            setResistance(100);
        }
        resistance = getInt("resistance");
        if (!has("visibility")) {
            setVisibility(100);
        }
    }

    @Override
    public int getLevel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Outfit getOutfit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLevel(int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setOutfit(Outfit o, boolean defaultValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
