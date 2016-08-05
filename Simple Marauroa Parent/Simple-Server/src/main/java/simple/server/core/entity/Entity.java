package simple.server.core.entity;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.Grammar;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.SimpleRPZone;
import simple.server.extension.MarauroaServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = RPEntityInterface.class, position = 1)
public class Entity extends RPObject implements RPEntityInterface {

    private final String MY_CLASS = "entity";
    protected String RPCLASS_NAME = MY_CLASS;
    public static final String NAME = "name", DESC = "description",
            DB_ID = "#db_id", ZONE_ID = "zoneid", ID = "id";
    /**
     * The logger.
     */
    private static final Logger LOG
            = Logger.getLogger(Entity.class.getSimpleName());
    private SimpleRPZone zone = null;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Entity(RPObject object) {
        super(object);
        update();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Entity() {
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(MY_CLASS)) {
            RPClass entity = new RPClass(MY_CLASS);
            entity.addAttribute(NAME, Type.LONG_STRING);

            // Some things may have a textual description
            entity.addAttribute(DESC, Type.LONG_STRING,
                    Definition.HIDDEN);
            /*
             * If this is set, the client will discard/ignore entity
             */
            entity.addAttribute("server-only", Type.FLAG, Definition.VOLATILE);

            Lookup.getDefault().lookupAll(MarauroaServerExtension.class).stream()
                    .map((extension) -> {
                        LOG.log(Level.FINE, "Processing extension to modify root class "
                                + "definition: {0}",
                                extension.getClass().getSimpleName());
                        return extension;
                    }).map((extension) -> {
                extension.modifyRootEntityRPClassDefinition(entity);
                return extension;
            }).filter((_item) -> (LOG.isLoggable(Level.FINE))).forEach((_item) -> {
                entity.getDefinitions().stream().forEach((def) -> {
                    LOG.log(Level.INFO, "{0}: {1}",
                            new Object[]{def.getName(), def.getType()});
                });
            });
        } else if (!RPClass.hasRPClass(getRPClassName())) {
            RPClass entity = new RPClass(getRPClassName());
            entity.isA(MY_CLASS);
        }
    }

    /**
     * describes the entity (if a players looks at it)
     *
     * @return description from the players point of view
     */
    public String describe() {
        return hasDescription() ? getDescription()
                : "You see " + getDescriptionName(false) + ".";
    }

    /**
     * returns the name or something that can be used to identify the entity for
     * the player
     *
     * @param definite true for "the" and false for "a/an" in case the entity
     * has no name
     * @return name
     */
    public String getDescriptionName(boolean definite) {
        String name = getName();
        String result;
        if (name != null) {
            result = name;
        } else if (has("subclass")) {
            result = Grammar.article_noun(get("subclass"), definite);
        } else if (has("class")) {
            result = Grammar.article_noun(get("class"), definite);
        } else {
            String ret = "something indescribably strange";
            if (has(WellKnownActionConstant.TYPE)) {
                ret += " of type " + get(WellKnownActionConstant.TYPE);
            }
            if (has("id")) {
                ret += " with id " + get("id");
            }
            if (has("zone")) {
                ret += " in zone " + get("zone");
            }
            result = ret;
        }
        return result;
    }

    public boolean hasDescription() {
        return has(DESC) ? ((getDescription() != null)
                && (getDescription().length() > 0)) : false;
    }

    public void setDescription(String text) {
        if (text == null) {
            put(DESC, "");
        } else {
            put(DESC, text);
        }
    }

    public String getDescription() {
        String description = "";
        if (has(DESC)) {
            description = get(DESC);
        }
        return description;
    }

    /**
     * Get the entity name.
     *
     * @return The entity's name, or <code>null</code> if undefined.
     */
    public String getName() {
        return has(NAME) ? get(NAME).replace("_", " ") : null;
    }

    /**
     * Set the entity's name
     *
     * @param name
     */
    public void setName(String name) {
        put(NAME, name);
    }

    /**
     * Get the nicely formatted entity title/name.
     *
     * @return The title, or <code>null</code> if unknown.
     */
    public String getTitle() {
        String result;
        if (has("title")) {
            result = get("title");
        } else if (has(NAME)) {
            result = get(NAME).replace('_', ' ');
        } else if (has("subclass")) {
            result = get("subclass").replace('_', ' ');
        } else if (has("class")) {
            result = get("class").replace('_', ' ');
        } else if (has(WellKnownActionConstant.TYPE)) {
            result = get(WellKnownActionConstant.TYPE).replace('_', ' ');
        } else {
            result = null;
        }
        return result;
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
        Lookup.getDefault()
                .lookup(IRPWorld.class).checkZone(this);
        zone = (SimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getRPZone(get(Entity.ZONE_ID));
        return zone;
    }

    /**
     * Called when this object is added to a zone.
     *
     * @param zone The zone this was added to.
     */
    @Override
    public void onAdded(SimpleRPZone zone) {
        if (this.zone != null) {
            //Make sure its not in the old zone
            if (this.zone.has(getID())) {
                LOG.log(Level.SEVERE,
                        "Entity added while in another zone: {0} in zone {1}",
                        new Object[]{this, zone.getID()});
                this.zone.remove(getID());
            }
        }
        this.zone = zone;
    }

    /**
     * Called when this object is being removed from a zone.
     *
     * @param zone The zone this will be removed from.
     */
    @Override
    public void onRemoved(SimpleRPZone zone) {
        if (this.zone != zone) {
            LOG.log(Level.SEVERE, "Entity removed from wrong zone: {0}", this);
        }
        this.zone = null;
    }

    /**
     * Notifies the SimpleRPWorld that this entity's attributes have changed.
     *
     */
    public void notifyWorldAboutChanges() {
        LOG.log(Level.FINE, "Object zone: {0}", get(Entity.ZONE_ID));
        Lookup.getDefault().lookup(IRPWorld.class).modify(this);
    }

    /**
     * Set the entity class.
     *
     * @param	clazz	The class name.
     */
    public void setEntityClass(final String clazz) {
        put("class", clazz);
    }

    /**
     * Set the entity sub-class.
     *
     * @param	subclazz The sub-class name.
     */
    public void setEntitySubClass(final String subclazz) {
        put("subclass", subclazz);
    }

    public void update() {
        Lookup.getDefault().lookupAll(MarauroaServerExtension.class).stream()
                .map((extension) -> {
                    LOG.log(Level.FINE, "Processing extension to update root class "
                            + "definition: {0}", extension.getClass().getSimpleName());
                    return extension;
                }).forEach((extension) -> {
            extension.rootRPClassUpdate(this);
            extension.entityRPClassUpdate(this);
        });
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

    /**
     * @return the RPCLASS_NAME
     */
    public String getRPClassName() {
        return RPCLASS_NAME;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.RPCLASS_NAME);
        hash = 79 * hash + Objects.hashCode(this.zone);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        if (!Objects.equals(this.RPCLASS_NAME, other.RPCLASS_NAME)) {
            return false;
        }
        return Objects.equals(this.zone, other.zone);
    }
}
