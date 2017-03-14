package simple.server.core.entity;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Configuration;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.Grammar;
import simple.common.NotificationType;
import simple.common.SizeLimitedArray;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.ISimpleRPZone;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.entity.api.RPEventListener;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.SimpleRPEvent;
import simple.server.core.event.TextEvent;
import simple.server.extension.MarauroaServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = RPEntityInterface.class, position = 1)
public class Entity extends RPObject implements RPEntityInterface {

    public static final String MY_CLASS = "entity";
    protected String RPCLASS_NAME = MY_CLASS;
    public static final String NAME = "name", DESC = "description",
            DB_ID = "#db_id", ZONE_ID = "zoneid", ID = "id",
            CLIENT_ID = "#clientid";
    /**
     * The ghost mode attribute name.
     */
    protected static final String ATTR_GHOSTMODE = "ghostmode";
    /**
     * The attack invisible attribute name.
     */
    protected static final String ATTR_INVISIBLE = "invisible";
    /**
     * Entity type
     */
    protected static final String TYPE = "type";
    /**
     * The logger.
     */
    private static final Logger LOG
            = Logger.getLogger(Entity.class.getSimpleName());
    private ISimpleRPZone zone = null;
    private boolean disconnected;
    private final IRPWorld world = Lookup.getDefault().lookup(IRPWorld.class);
    private String lastPrivateChatterName;
    private final SizeLimitedArray queue = new SizeLimitedArray();

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Entity(RPObject object) {
        super(object);
        update();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Entity(RPObject object, Map<String, RPEventListener> listeners) {
        super(object);
        update();
        if (listeners != null) {
            listeners.entrySet().forEach((entry) -> {
                world.registerMonitor(getName(), entry.getKey(),
                        entry.getValue());
            });
        }
    }

    public Entity() {
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(MY_CLASS)) {
            RPClass entity = new RPClass(MY_CLASS);
            entity.addAttribute(NAME, Type.LONG_STRING);
            entity.addAttribute(TYPE, Type.STRING);

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
                extension.modifyRootRPClassDefinition(entity);
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
            if (has(TYPE)) {
                ret += " of type " + get(TYPE);
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
    @Override
    public String getName() {
        return has(NAME) ? get(NAME).replace("_", " ") : null;
    }

    /**
     * Set the entity's name
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        put(NAME, name);
    }

    /**
     * Get the nicely formatted entity title/name.
     *
     * @return The title, or <code>null</code> if unknown.
     */
    @Override
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
        } else if (has(TYPE)) {
            result = get(TYPE).replace('_', ' ');
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
    public ISimpleRPZone getZone() {
        // Use onAdded()/onRemoved() to grab a copy
        // of the zone and save as a local variable.
        Lookup.getDefault()
                .lookup(IRPWorld.class).checkZone(this);
        zone = (SimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(get(Entity.ZONE_ID));
        return zone;
    }

    /**
     * Called when this object is added to a zone.
     *
     * @param zone The zone this was added to.
     */
    @Override
    public void onAdded(ISimpleRPZone zone) {
        //Do nothing by default.
        this.zone = zone;
    }

    /**
     * Called when this object is being removed from a zone.
     *
     * @param zone The zone this will be removed from.
     */
    @Override
    public void onRemoved(ISimpleRPZone zone) {
        if (this.zone != zone) {
            LOG.log(Level.SEVERE, "Entity removed from wrong zone: {0}", this);
        }
        this.zone = null;
    }

    /**
     * Notifies the SimpleRPWorld that this entity's attributes have changed.
     *
     */
    @Override
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

    @Override
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

    /**
     * Get a keyed string value on a named slot.
     *
     * @param name The slot name.
     * @param key The value key.
     *
     * @return The keyed value of the slot, or <code>null</code> if not set.
     */
    @Override
    public String getKeyedSlot(String name, String key) {
        RPObject object = getKeyedSlotObject(this, name);
        return object == null ? null : object.has(key) ? object.get(key) : null;
    }

    /**
     * Set a keyed string value on a named slot.
     *
     * @param name The slot name.
     * @param key The value key.
     * @param value The value to assign (or remove if <code>null</code>).
     *
     * @return <code>true</code> if value changed, <code>false</code> if there
     * was a problem.
     */
    @Override
    public boolean setKeyedSlot(String name, String key, String value) {
        RPObject object = getKeyedSlotObject(this, name);
        if (object == null) {
            return false;
        }
        if (value != null) {
            object.put(key, value);
        } else if (object.has(key)) {
            object.remove(key);
        }
        return true;
    }

    /**
     * @return the single object of a "keyed slot".
     *
     * @param name name of key slot
     * @return object or <code>null</code> it does not exist
     */
    static RPObject getKeyedSlotObject(RPObject object, String name) {
        if (!object.hasSlot(name)) {
            LOG.log(Level.SEVERE, "Expected to find {0} slot", name);
            return null;
        }

        RPSlot slot = object.getSlot(name);

        if (slot.size() == 0) {
            return null;
        }

        return slot.iterator().next();
    }

    @Override
    public void destroy() {
        /*
         * Normally a zoneid attribute shouldn't logically exist after an entity
         * is removed from a zone, but we need to keep it for players so that it
         * can be serialized.
         *
         */
        if (getZone() != null) {
            getZone().remove(this);
        }
        setDisconnected(true);
    }

    /**
     * @param disconnected the disconnected to set
     */
    @Override
    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    /**
     * Sends a message that only this player can read.
     *
     * @param text the message.
     */
    @Override
    public void sendPrivateText(String text) {
        sendPrivateText(NotificationType.PRIVMSG, text);
    }

    /**
     * Sends a message that only this player can read.
     *
     * @param type NotificationType
     * @param text the message.
     */
    @Override
    public void sendPrivateText(NotificationType type, String text) {
        addEvent(new PrivateTextEvent(type, text));
        notifyWorldAboutChanges();
    }

    @Override
    public void sendText(String text) {
        try {
            addEvent(new TextEvent(text,
                    Configuration.getConfiguration().get("system_account_name")));
            notifyWorldAboutChanges();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set whether this player is a ghost (invisible/non-interactive).
     *
     * @param ghost <code>true</code> if a ghost.
     */
    @Override
    public void setGhost(final boolean ghost) {
        if (ghost) {
            put(ATTR_GHOSTMODE, "");
        } else if (has(ATTR_GHOSTMODE)) {
            remove(ATTR_GHOSTMODE);
        }
    }

    @Override
    public boolean isGhost() {
        return has(ATTR_GHOSTMODE);
    }

    @Override
    public boolean isInvisibleToCreatures() {
        return has(ATTR_INVISIBLE);
    }

    /**
     * Set whether this player is invisible to creatures.
     *
     * @param invisible <code>true</code> if invisible.
     */
    @Override
    public void setInvisible(final boolean invisible) {
        if (invisible) {
            put(ATTR_INVISIBLE, "");
        } else if (has(ATTR_INVISIBLE)) {
            remove(ATTR_INVISIBLE);
        }
    }

    /**
     * Sets the name of the last player who privately talked to this player
     * using the /tell command. It needs to be stored non-persistently so that
     * /answer can be used.
     *
     * @param lastPrivateChatterName
     */
    @Override
    public void setLastPrivateChatter(String lastPrivateChatterName) {
        this.lastPrivateChatterName = lastPrivateChatterName;
    }

    /**
     * Gets the name of the last player who privately talked to this player
     * using the /tell command, or null if nobody has talked to this player
     * since he logged in.
     *
     * @return
     */
    @Override
    public String getLastPrivateChatter() {
        return lastPrivateChatterName;
    }

    @Override
    public boolean isDisconnected() {
        return disconnected;
    }

    @Override
    public void addEvent(RPEvent event) {
        //Avoid duplicates
        if (!proccessedEvent(event)) {
            if (event.has(SimpleRPEvent.EVENT_ID)) {
                //Add it to the queue
                queue.add(event.get(SimpleRPEvent.EVENT_ID));
            }
            //Add the event
            super.addEvent(event);
        }
    }

    public boolean proccessedEvent(RPEvent event) {
        return event.has(SimpleRPEvent.EVENT_ID) // if it doesn't have an id
                && queue.contains(event.get(SimpleRPEvent.EVENT_ID));//or is not in the queue
    }
}
