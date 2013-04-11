package simple.server.core.entity.clientobject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.*;
import marauroa.common.game.Definition.Type;
import marauroa.common.io.UnicodeSupportingInputStreamReader;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.common.FeatureList;
import simple.common.NotificationType;
import simple.common.SimpleException;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.admin.AdministrationAction;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.engine.SimpleSingletonRepository;
import simple.server.core.engine.rp.SimpleRPAction;
import simple.server.core.entity.ExtensibleRPClass;
import simple.server.core.entity.Outfit;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.item.Item;
import simple.server.core.entity.item.StackableItem;
import simple.server.core.entity.slot.PlayerSlot;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TextEvent;
import simple.server.extension.MarauroaServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProviders({
    @ServiceProvider(service = ClientObjectInterface.class),
    @ServiceProvider(service = RPEntityInterface.class, position = 100)})
public class ClientObject extends RPEntity implements ClientObjectInterface,
        java.io.Serializable {

    /**
     * The administration level attribute name.
     */
    protected static final String ATTR_ADMINLEVEL = "adminlevel";
    /**
     * The away message attribute name.
     */
    protected static final String ATTR_AWAY = "away";
    /**
     * The ghost mode attribute name.
     */
    protected static final String ATTR_GHOSTMODE = "ghostmode";
    /**
     * The attack invisible attribute name.
     */
    protected static final String ATTR_INVISIBLE = "invisible";
    /**
     * The grumpy attribute name.
     */
    protected static final String ATTR_GRUMPY = "grumpy";
    /**
     * the logger instance.
     */
    private static final Logger logger = Log4J.getLogger(ClientObject.class);
    /**
     * The base log for karma use.
     */
    private static final long serialVersionUID = -3451819589645530092L;
    private String lastPrivateChatterName;
    /**
     * A list of enabled client features.
     */
    protected FeatureList features;
    /**
     * A list of away replies sent to players.
     */
    protected HashMap<String, Long> awayReplies;
    /**
     * list of super admins read from admins.list.
     */
    private static List<String> adminNames = new LinkedList<String>();
    private int adminLevel;
    private boolean disconnected;
    private PlayerQuests quests = new PlayerQuests(this);
    public static final String CLASS_NAME = "client_object";

    /**
     * Constructor
     *
     * @param object
     */
    public ClientObject(RPObject object) {
        super(object);
        //Is empty here?
        setRPClass(CLASS_NAME);
        put("type", CLASS_NAME);
        awayReplies = new HashMap<String, Long>();
        update();
        addEmptySlots("!tutorial");
        addEmptySlots("!visited");
    }

    @Override
    public final void update() {
        for (Iterator<? extends MarauroaServerExtension> it =
                Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                .iterator(); it.hasNext();) {
            MarauroaServerExtension extension = it.next();
            logger.debug("Processing extension to update client object "
                    + "class definition: " + extension.getClass()
                    .getSimpleName());
            try {
                extension.clientObjectUpdate(this);
            } catch (SimpleException ex) {
                java.util.logging.Logger.getLogger(ClientObject.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
        super.update();
    }

    /**
     * Constructor for serialization purposes
     */
    public ClientObject() {
    }

    /**
     * Get the away message.
     *
     * @return The away message, or <code>null</code> if unset.
     */
    @Override
    public String getAwayMessage() {
        return has(ATTR_AWAY) ? get(ATTR_AWAY) : null;
    }

    /**
     * Get the grumpy message.
     *
     * @return The grumpy message, or <code>null</code> if unset.
     */
    @Override
    public String getGrumpyMessage() {
        return has(ATTR_GRUMPY) ? get(ATTR_GRUMPY) : null;
    }

    /**
     * Set the away message.
     *
     * @param message An away message, or <code>null</code>.
     */
    @Override
    public void setAwayMessage(final String message) {
        if (message != null) {
            put(ATTR_AWAY, message);
        } else if (has(ATTR_AWAY)) {
            remove(ATTR_AWAY);
        }

        resetAwayReplies();
    }

    /**
     * Check if another player should be notified that this player is away. This
     * assumes the player has already been checked for away. Players will be
     * reminded once an hour.
     *
     * @param name The name of the other player.
     *
     * @return <code>true</code> if the player should be notified.
     */
    @Override
    public boolean isAwayNotifyNeeded(String name) {
        long now = System.currentTimeMillis();
        Long lObj = awayReplies.get(name);

        if (lObj != null) {
            /*
             * Only notify once an hour
             */
            if ((now - lObj.longValue()) < (1000L * 60L * 60L)) {
                return false;
            }
        }

        awayReplies.put(name, now);
        return true;
    }

    /**
     * Clear out all recorded away responses.
     */
    @Override
    public void resetAwayReplies() {
        awayReplies.clear();
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
     * Determine if a player is on the ignore list and return their reply
     * message.
     *
     * @param name The player name.
     *
     * @return The custom reply message (including an empty string), or
     * <code>null</code> if not ignoring.
     */
    @Override
    public String getIgnore(String name) {
        String info = getKeyedSlot("!ignore", "_" + name);
        int i;
        long expiration;

        if (info == null) {
            /*
             * Special "catch all" fallback
             */
            info = getKeyedSlot("!ignore", "_*");
            if (info == null) {
                return null;
            }
        }
        i = info.indexOf(';');
        if (i == -1) {
            /*
             * Do default
             */
            return "";
        }

        /*
         * Has expiration?
         */
        if (i != 0) {
            expiration = Long.parseLong(info.substring(0, i));

            if (System.currentTimeMillis() >= expiration) {
                setKeyedSlot("!ignore", "_" + name, null);
                return null;
            }
        }

        return info.substring(i + 1);
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

    /**
     * Notifies this player that the given player has logged in.
     *
     * @param who The name of the player who has logged in.
     */
    @Override
    public void notifyOnline(String who) {
        String playerOnline = "_" + who;

        boolean found = false;
        RPSlot slot = getSlot("!buddy");
        if (slot != null && slot.size() > 0) {
            RPObject buddies = slot.iterator().next();
            for (String name : buddies) {
                if (playerOnline.equals(name)) {
                    buddies.put(playerOnline, 1);
                    notifyWorldAboutChanges();
                    found = true;
                    break;
                }
            }
        }
        if (found) {
            if (has("online")) {
                put("online", get("online") + "," + who);
            } else {
                put("online", who);
            }
        }
    }

    /**
     * Notifies this player that the given player has logged out.
     *
     * @param who The name of the player who has logged out.
     */
    @Override
    public void notifyOffline(String who) {
        String playerOffline = "_" + who;

        boolean found = false;
        RPSlot slot = getSlot("!buddy");
        if (slot != null && slot.size() > 0) {
            RPObject buddies = slot.iterator().next();
            for (String name : buddies) {
                if (playerOffline.equals(name)) {
                    buddies.put(playerOffline, 0);
                    notifyWorldAboutChanges();
                    found = true;
                    break;
                }
            }
        }
        if (found) {
            if (has("offline")) {
                put("offline", get("offline") + "," + who);
            } else {
                put("offline", who);
            }
        }
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
            java.util.logging.Logger.getLogger(ClientObject.class.getName()).log(Level.SEVERE, null, ex);
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
     * Set the grumpy message.
     *
     * @param message A grumpy message, or <code>null</code>.
     */
    @Override
    public void setGrumpyMessage(final String message) {
        if (message != null) {
            put(ATTR_GRUMPY, message);
        } else if (has(ATTR_GRUMPY)) {
            remove(ATTR_GRUMPY);
        }

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
     * Teleports this player to the given destination.
     *
     * @param zone The zone where this player should be teleported to.
     * @param teleporter The player who initiated the teleporting, or null if no
     * player is responsible. This is only to give feedback if something goes
     * wrong. If no feedback is wanted, use null.
     * @return true iff teleporting was successful
     */
    public boolean teleport(SimpleRPZone zone,
            ClientObject teleporter) {
        if (SimpleRPAction.placeat(zone, this)) {
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

    /**
     * Checks whether the player has completed the given quest or not.
     *
     * @param name The quest's name
     * @return true iff the quest has been completed by the player
     */
    @Override
    public boolean isQuestCompleted(String name) {
        return quests.isQuestCompleted(name);
    }

    /**
     * Checks whether the player has made any progress in the given quest or
     * not. For many quests, this is true right after the quest has been
     * started.
     *
     * @param name The quest's name
     * @return true iff the player has made any progress in the quest
     */
    @Override
    public boolean hasQuest(String name) {
        return quests.hasQuest(name);
    }

    /**
     * Gets the player's current status in the given quest.
     *
     * @param name The quest's name
     * @return the player's status in the quest
     */
    @Override
    public String getQuest(String name) {
        return quests.getQuest(name);
    }

    /**
     * Allows to store the player's current status in a quest in a string. This
     * string may, for instance, be "started", "done", a semicolon- separated
     * list of items that need to be brought/NPCs that need to be met, or the
     * number of items that still need to be brought. Note that the string
     * "done" has a special meaning: see isQuestComplete().
     *
     * @param name The quest's name
     * @param status the player's status in the quest. Set it to null to
     * completely reset the player's status for the quest.
     */
    @Override
    public void setQuest(String name, String status) {
        quests.setQuest(name, status);
    }

    @Override
    public List<String> getQuests() {
        return quests.getQuests();
    }

    @Override
    public void removeQuest(String name) {
        quests.removeQuest(name);
    }

    /**
     * Is the named quest in one of the listed states?
     *
     * @param name quest
     * @param states valid states
     * @return true, if the quest is in one of theses states, false otherwise
     */
    @Override
    public boolean isQuestInState(String name, String... states) {
        return quests.isQuestInState(name, states);
    }

    private void addEmptySlots(String slot) {
        if (!hasSlot(slot)) {
            addSlot(new RPSlot(slot));
        }
    }

    @Override
    public ClientObjectInterface create(RPObject object) {

        ClientObject player = new ClientObject(object);

        //TODO: Move this to an extension
//        if (player.has(ATTR_AWAY)) {
//            player.remove(ATTR_AWAY);
//        }
//        // remove grumpy on login to give postman a chance to deliver messages
//        // (and in the hope that player is receptive now)
//        if (player.has(ATTR_GRUMPY)) {
//            player.remove(ATTR_GRUMPY);
//        }

        readAdminsFromFile(player);
        //TODO: Move this to an extension
//        loadItemsIntoSlots(player);
        //TODO: Move this to an extension
//
//        if (player.getSlot("!buddy").size() > 0) {
//            RPObject buddies = player.getSlot("!buddy").iterator().next();
//            for (String buddyName : buddies) {
//                if (buddyName.charAt(0) == '_') {
//                    ClientObject buddy =
//                            (ClientObject) ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class)).getPlayer(
//                            buddyName.substring(1));
//                    if ((buddy != null) && !buddy.isGhost()) {
//                        buddies.put(buddyName, 1);
//                    } else {
//                        buddies.put(buddyName, 0);
//                    }
//                }
//            }
//        }
        return player;
    }

    /**
     * reads the admins from admins.list.
     *
     * @param player ClientObject to check for super admin status.
     */
    protected static void readAdminsFromFile(ClientObject player) {
        if (adminNames.isEmpty()) {

            String adminFilename = "data/conf/admins.list";

            try {
                InputStream is = player.getClass().getClassLoader()
                        .getResourceAsStream(
                        adminFilename);

                if (is == null) {
                    logger.info("data/conf/admins.list does not exist.");
                } else {
                    BufferedReader in = new BufferedReader(
                            new UnicodeSupportingInputStreamReader(is));
                    try {
                        String line;
                        while ((line = in.readLine()) != null) {
                            adminNames.add(line);
                        }
                    } catch (Exception e) {
                        logger.error("Error loading admin names from: " + adminFilename, e);
                    } finally {
                        in.close();
                    }
                }
            } catch (Exception e) {
                logger.error(
                        "Error loading admin names from: " + adminFilename, e);
            }
        }

        if (adminNames.contains(player.getName())) {
            player.setAdminLevel(AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPER);
        }
    }

    /**
     * Generates the SimpleRPClass and specifies slots and attributes.
     */
    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass("client_object")) {
            ExtensibleRPClass player = new ExtensibleRPClass("client_object");
            player.isA("rpentity");
            //This is the assigned key for encryption purposes on the client
            player.addAttribute(KEY, Type.LONG_STRING, Definition.PRIVATE);
            player.addRPEvent(TextEvent.RPCLASS_NAME, Definition.VOLATILE);
            /*
             * Add event player.addRPEvent("<Event RPClassName>",
             * Definition.VOLATILE);
             */
            player.addRPEvent(PrivateTextEvent.RPCLASS_NAME, Definition.PRIVATE);

            player.addAttribute("outfit", Type.INT);
            player.addAttribute("outfit_org", Type.INT);

            player.addAttribute("away", Type.LONG_STRING, Definition.VOLATILE);
            player.addAttribute("grumpy", Type.LONG_STRING, Definition.VOLATILE);

            // Use this for admin menus and usage.
            player.addAttribute("admin", Type.FLAG);
            player.addAttribute("adminlevel", Type.INT);
            player.addAttribute("invisible", Type.FLAG, Definition.HIDDEN);
            //User with Monitor permissions
            player.addAttribute("monitor", Type.FLAG);

            player.addAttribute("ghostmode", Type.FLAG);

            player.addAttribute("release", Type.STRING, Definition.PRIVATE);

            player.addAttribute("age", Type.INT);

            // We use this for the buddy system
            player.addRPSlot("!buddy", 1, Definition.PRIVATE);
            player.addRPSlot("!ignore", 1, Definition.HIDDEN);
            player.addAttribute("online", Type.LONG_STRING,
                    (byte) (Definition.PRIVATE | Definition.VOLATILE));
            player.addAttribute("offline", Type.LONG_STRING,
                    (byte) (Definition.PRIVATE | Definition.VOLATILE));

            //TODO: move to an extension
            player.addRPSlot("!quests", 1, Definition.HIDDEN);
            player.addRPSlot("!tutorial", 1, Definition.HIDDEN);

            player.addAttribute("karma", Type.FLOAT, Definition.PRIVATE);
            player.addAttribute("sentence", Type.STRING, Definition.HIDDEN);

            player.addRPSlot("skills", 1, Definition.HIDDEN);

            // Non-removable while stored ones have values
            player.addRPSlot("!skills", 1,
                    (byte) (Definition.HIDDEN | Definition.VOLATILE));

            player.addRPSlot("!visited", 1, Definition.HIDDEN);

            //TODO: move to an extension
            // The guild name
            player.addAttribute("guild", Type.STRING);

            // ClientObject features
            player.addRPSlot("!features", 1, Definition.PRIVATE);

            //TODO: move to an extension
            // Last time this player attacked another player
            player.addAttribute("last_pvp_action_time", Type.FLOAT,
                    Definition.HIDDEN);
            extendClass(player);
        }
    }

    protected static void extendClass(RPClass player) {
        for (Iterator<? extends MarauroaServerExtension> it =
                Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                .iterator(); it.hasNext();) {
            MarauroaServerExtension extension = it.next();
            logger.debug("Processing extension to modify client definition: "
                    + extension.getClass().getSimpleName());
            extension.modifyClientObjectDefinition(player);
        }
        logger.debug("ClientObject attributes:");
        for (Definition def : player.getDefinitions()) {
            logger.debug(def.getName() + ": " + def.getType());
        }
        logger.debug("-------------------------------");
    }

    /**
     * Loads the items into the slots of the player on login.
     *
     * @param player ClientObject
     */
    protected static void loadItemsIntoSlots(ClientObject player) {

        // load items
        String[] slotsItems = {"bag", "rhand", "lhand", "head", "armor",
            "legs", "feet", "finger", "cloak", "keyring"
        };

        try {
            for (String slotName : slotsItems) {
                RPSlot slot = player.getSlot(slotName);
                RPSlot newSlot = new PlayerSlot(slotName);
                loadSlotContent(player, slot, newSlot);
            }
        } catch (RuntimeException e) {
            logger.error("cannot create player", e);
        }
    }

    /**
     * Loads the items into the slots of the player on login.
     *
     * @param player ClientObject
     * @param slot original slot
     * @param newSlot new Simple specific slot
     */
    private static void loadSlotContent(ClientObject player, RPSlot slot,
            RPSlot newSlot) {
        if (slot == null) {
            return;
        }
        List<RPObject> objects = new LinkedList<RPObject>();
        for (RPObject objectInSlot : slot) {
            objects.add(objectInSlot);
        }
        slot.clear();
        player.removeSlot(slot.getName());
        player.addSlot(newSlot);

        for (RPObject item : objects) {
            try {
                // We simply ignore corpses...
                if (item.get("type").equals("item")) {

                    String name = item.get("name");
                    Item entity = SimpleSingletonRepository
                            .getEntityManager().getItem(name);

                    // log removed items
                    if (entity == null) {
                        int quantity = 1;
                        if (item.has("quantity")) {
                            quantity = item.getInt("quantity");
                        }
                        logger.warn("Cannot restore " + quantity + " "
                                + name + " on login of " + player.getName()
                                + " because this item"
                                + " was removed from items.xml");
                        continue;
                    }

                    entity.setID(item.getID());

                    if (item.has("persistent")
                            && (item.getInt("persistent") == 1)) {
                        /*
                         * Keep [new] rpclass
                         */
                        RPClass rpclass = entity.getRPClass();
                        entity.fill(item);
                        entity.setRPClass(rpclass);

                        // If we've updated the item name we don't 
                        //want persistent reverting it
                        entity.put("name", name);
                    }

                    if (entity instanceof StackableItem) {
                        int quantity = 1;
                        if (item.has("quantity")) {
                            quantity = item.getInt("quantity");
                        } else {
                            logger.warn("Adding quantity=1 to "
                                    + item + ". Most likely cause is that "
                                    + "this item was not stackable in the past");
                        }
                        ((StackableItem) entity).setQuantity(quantity);

                        if (quantity <= 0) {
                            logger.warn("Ignoring item " + name
                                    + " on login of player " + player.getName()
                                    + " because this item has an invalid "
                                    + "quantity: " + quantity);
                            continue;
                        }
                    }

                    // make sure saved individual information is
                    // restored
                    String[] individualAttributes = {"infostring",
                        "description", "bound", "undroppableondeath"
                    };
                    for (String attribute : individualAttributes) {
                        if (item.has(attribute)) {
                            entity.put(attribute, item.get(attribute));
                        }
                    }

                    if (item.has("logid")) {
                        entity.put("logid", item.get("logid"));
                    }
                    newSlot.add(entity);
                } else {
                    logger.warn("Non-item object found in " + player.getName()
                            + "[" + slot.getName() + "]: " + item);
                }
            } catch (Exception e) {
                logger.error("Error adding " + item + " to player slot" + slot,
                        e);
            }
        }
    }

    /**
     * @return the adminLevel
     */
    @Override
    public int getAdminLevel() {
        return adminLevel;
    }

    /**
     * @param adminLevel the adminLevel to set
     */
    @Override
    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }

    public void setOutfit(Outfit outfit) {
        setOutfit(outfit, false);
    }

    /**
     * Makes this player wear the given outfit. If the given outfit contains
     * null parts, the current outfit will be kept for these parts.
     *
     * @param outfit The new outfit.
     * @param temporary If true, the original outfit will be stored so that it
     * can be restored later.
     */
    @Override
    public void setOutfit(Outfit outfit, boolean temporary) {
        // if the new outfit is temporary and the player is not wearing
        // a temporary outfit already, store the current outfit in a
        // second slot so that we can return to it later.
        if (temporary && !has("outfit_org")) {
            put("outfit_org", get("outfit"));
        }

        // if the new outfit is not temporary, remove the backup
        if (!temporary && has("outfit_org")) {
            remove("outfit_org");
        }

        // combine the old outfit with the new one, as the new one might
        // contain null parts.
        Outfit newOutfit = outfit.putOver(getOutfit());
        put("outfit", newOutfit.getCode());
        notifyWorldAboutChanges();
    }

    public Outfit getOriginalOutfit() {
        return has("outfit_org") ? new Outfit(getInt("outfit_org")) : null;
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
            logger.error("Expected to find " + name + " slot");
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

    public static ClientObject createEmptyZeroLevelPlayer(String characterName) {
        ClientObject object = new ClientObject(new RPObject());
        object.setID(RPObject.INVALID_ID);
        object.put("type", "client_object");
        object.put("name", characterName);
        object.update();
        return object;
    }

    public static ClientObject createEmptyZeroLevelPlayer(RPObject template) {
        ClientObject object = new ClientObject(template);
        object.setID(RPObject.INVALID_ID);
        object.put("type", "client_object");

        object.update();
        return object;
    }

    /**
     * Add a player ignore entry.
     *
     * @param name The player name.
     * @param duration The ignore duration (in minutes), or <code>0</code> for
     * infinite.
     * @param reply The reply.
     *
     * @return <code>true</code> if value changed, <code>false</code> if there
     * was a problem.
     */
    @Override
    public boolean addIgnore(String name, int duration, String reply) {
        StringBuilder sbuf = new StringBuilder();

        if (duration != 0) {
            sbuf.append(System.currentTimeMillis() + (duration * 60000L));
        }

        sbuf.append(';');

        if (reply != null) {
            sbuf.append(reply);
        }

        return setKeyedSlot("!ignore", "_" + name, sbuf.toString());
    }

    /**
     * Called when this object is added to a zone.
     *
     * @param zone The zone this was added to.
     */
    @Override
    public void onAdded(IRPZone zone) {
        String zoneName = ((SimpleRPZone) zone).getName();
        /*
         * Remember zones we've been in
         */
        put("zoneid", ((SimpleRPZone) zone).getName());
        setKeyedSlot("!visited", zoneName,
                Long.toString(System.currentTimeMillis()));
        super.onAdded((SimpleRPZone) zone);
    }

    @Override
    public void onRemoved(IRPZone zone) {
        super.onRemoved((SimpleRPZone) zone);
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
        if (obj != null && getClass() != obj.getClass()) {
            final ClientObject other = (ClientObject) obj;
            result = this.hashCode() == other.hashCode();
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.features != null ? this.features.hashCode() : 0);
        hash = 31 * hash + this.adminLevel;
        hash = 31 * hash + (this.quests != null ? this.quests.hashCode() : 0);
        hash = 31 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
        hash = 31 * hash + (this.getID() != null ? this.getID().hashCode() : 0);
        return hash;
    }
}
