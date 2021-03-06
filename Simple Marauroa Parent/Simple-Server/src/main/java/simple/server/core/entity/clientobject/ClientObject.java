package simple.server.core.entity.clientobject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.*;
import marauroa.common.game.Definition.Type;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.common.SimpleException;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.ISimpleRPZone;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.engine.rp.SimpleRPAction;
import simple.server.core.entity.Entity;
import simple.server.core.entity.ExtensibleRPClass;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.api.RPEventListener;
import simple.server.extension.MarauroaServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProviders({
    @ServiceProvider(service = ClientObjectInterface.class)
    ,@ServiceProvider(service = RPEntityInterface.class, position = 100)})
public class ClientObject extends RPEntity implements ClientObjectInterface,
        java.io.Serializable {

    /**
     * the LOG instance.
     */
    private static final Logger LOG
            = Logger.getLogger(ClientObject.class.getSimpleName());
    /**
     * The base log for karma use.
     */
    private static final long serialVersionUID = -3451819589645530092L;
    /**
     * A list of away replies sent to players.
     */
    protected HashMap<String, Long> awayReplies;
    private int adminLevel;
    public static final String DEFAULT_RP_CLASSNAME = "client_object";
    public static final String KEY = "#key";

    /**
     * Constructor
     *
     * @param object
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ClientObject(RPObject object) {
        super(object);
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
        setRPClass(RPCLASS_NAME);
        put(WellKnownActionConstant.TYPE, RPCLASS_NAME);
        awayReplies = new HashMap<>();
        addEmptySlots("!visited");
    }

    public ClientObject(RPObject object, Map<String, RPEventListener> listeners) {
        super(object, listeners);
    }

    @Override
    //Warning! Always call super.update() if you are overriding this method.
    public void update() {
        Lookup.getDefault().lookupAll(MarauroaServerExtension.class).stream()
                .map((extension) -> {
                    LOG.log(Level.FINE, "Processing extension to update client object "
                            + "class definition: {0}", extension.getClass()
                                    .getSimpleName());
                    return extension;
                }).forEachOrdered((extension) -> {
            try {
                extension.clientObjectUpdate(this);
            } catch (SimpleException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
        super.update();
    }

    /**
     * Constructor for serialization purposes
     */
    public ClientObject() {
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
    }

    /**
     * Teleports this player to the given destination.
     *
     * @param zone The zone where this player should be teleported to.
     * @param teleporter The player who initiated the teleporting, or null if no
     * player is responsible. This is only to give feedback if something goes
     * wrong. If no feedback is wanted, use null.
     * @return true if teleporting was successful
     */
    public boolean teleport(SimpleRPZone zone,
            ClientObject teleporter) {
        if (SimpleRPAction.placeAt(zone, this)) {
            notifyWorldAboutChanges();
            return true;
        } else {
            String text = "Can't place you there!";
            if (teleporter != null) {
                teleporter.sendPrivateText(text);
            } else {
                this.sendPrivateText(text);
            }
            return false;
        }

    }

    private void addEmptySlots(String slot) {
        if (!hasSlot(slot)) {
            addSlot(new RPSlot(slot));
        }
    }

    @Override
    public ClientObjectInterface create(RPObject object) {

        ClientObject player = new ClientObject(object);
        return player;
    }

    /**
     * Generates the SimpleRPClass and specifies slots and attributes.
     */
    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RPCLASS_NAME)) {
            ExtensibleRPClass player = new ExtensibleRPClass(getRPClassName());
            player.isA(RPEntity.DEFAULT_RPCLASS);
            //This is the assigned key for encryption purposes on the client
            player.addAttribute(KEY, Type.LONG_STRING, Definition.PRIVATE);

            player.addAttribute("away", Type.LONG_STRING, Definition.VOLATILE);
            player.addAttribute("grumpy", Type.LONG_STRING,
                    Definition.VOLATILE);

            player.addAttribute("invisible", Type.FLAG, Definition.HIDDEN);

            //TODO: move to an extension
            player.addAttribute("ghostmode", Type.FLAG);

            player.addAttribute("release", Type.STRING, Definition.PRIVATE);

            player.addRPSlot("!visited", 1, Definition.HIDDEN);

            //TODO: move to an extension
            // The guild name
            player.addAttribute("guild", Type.STRING);

            //TODO: move to an extension
            // Last time this player attacked another player
            player.addAttribute("last_pvp_action_time", Type.FLOAT,
                    Definition.HIDDEN);
            extendClass(player);
        }
    }

    protected static void extendClass(RPClass player) {
        Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                .stream().map((extension) -> {
                    LOG.log(Level.FINE, "Processing extension to modify "
                            + "client definition: {0}",
                            extension.getClass().getSimpleName());
                    return extension;
                }).forEach((extension) -> {
            extension.modifyClientObjectDefinition(player);
            if (player.subclassOf(Entity.MY_CLASS)) {
                extension.modifyRootEntityRPClassDefinition(player);
            }
        });
        LOG.fine("ClientObject attributes:");
        player.getDefinitions().stream().forEach((def) -> {
            LOG.log(Level.FINE, "{0}: {1}",
                    new Object[]{def.getName(), def.getType()});
        });
        LOG.fine("-------------------------------");
    }

    public static ClientObject createEmptyZeroLevelPlayer(String characterName) {
        ClientObject object = new ClientObject(new RPObject());
        object.setID(RPObject.INVALID_ID);
        object.put(WellKnownActionConstant.TYPE, DEFAULT_RP_CLASSNAME);
        object.put(Entity.NAME, characterName);
        object.put(Entity.ZONE_ID,
                Lookup.getDefault().lookup(IRPWorld.class).getDefaultZone().getID().getID());
        object.update();
        return object;
    }

    public static ClientObject createEmptyZeroLevelPlayer(RPObject template) {
        ClientObject object = new ClientObject(template);
        object.setID(RPObject.INVALID_ID);
        object.put(WellKnownActionConstant.TYPE, DEFAULT_RP_CLASSNAME);
        object.put(Entity.ZONE_ID,
                Lookup.getDefault().lookup(IRPWorld.class).getDefaultZone().getID().getID());
        object.update();
        return object;
    }

    /**
     * Called when this object is added to a zone.
     *
     * @param zone The zone this was added to.
     */
    @Override
    public void onAdded(ISimpleRPZone zone) {
        String zoneName = zone.getName();
        /*
         * Remember zones we've been in
         */
        put(Entity.ZONE_ID, zone.getName());
        setKeyedSlot("!visited", zoneName,
                Long.toString(System.currentTimeMillis()));
        super.onAdded((SimpleRPZone) zone);
    }

    @Override
    public ClientObjectInterface createDefaultClientObject(String name) {
        return createEmptyZeroLevelPlayer(name);
    }

    @Override
    public ClientObjectInterface createDefaultClientObject(RPObject object) {
        return createEmptyZeroLevelPlayer(object);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) o;
            return getName().compareTo(player.getName());
        } else {
            throw new ClassCastException("Unable to cast " + o + " as a "
                    + ClientObjectInterface.class.getSimpleName());
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && obj instanceof ClientObject) {
            final ClientObject other = (ClientObject) obj;
            result = this.hashCode() == other.hashCode();
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + this.adminLevel;
        hash = 31 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
        hash = 31 * hash + (this.has(Entity.ID) ? this.getID().hashCode() : 0);
        return hash;
    }
}
