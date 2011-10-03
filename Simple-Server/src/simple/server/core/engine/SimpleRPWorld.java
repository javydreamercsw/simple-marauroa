package simple.server.core.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.crypto.Hash;
import marauroa.common.game.IRPZone;
import marauroa.common.game.IRPZone.ID;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.extension.MarauroaServerExtension;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.game.rp.RPWorld;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.clientobject.ClientObject;
import simple.server.core.entity.item.Item;
import simple.server.core.event.DelayedPlayerEventSender;
import simple.server.core.event.ITurnNotifier;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.api.IRPEvent;

@ServiceProvider(service = IRPWorld.class)
public class SimpleRPWorld extends RPWorld implements IRPWorld {

    /**
     * the logger instance.
     */
    private static final Logger logger = Log4J.getLogger(SimpleRPWorld.class);
    private static String DEFAULT_ROOM = "Default Room";
    /**
     * A common place for milliseconds per turn.
     */
    public static final int MILLISECONDS_PER_TURN = 300;

    /**
     * @return the DEFAULT_ROOM
     */
    public static String getDefaultRoom() {
        return DEFAULT_ROOM;
    }

    /**
     * @param aDEFAULT_ROOM the DEFAULT_ROOM to set
     */
    public static void setDefaultRoom(String aDEFAULT_ROOM) {
        DEFAULT_ROOM = aDEFAULT_ROOM;
    }

    @SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
    public SimpleRPWorld() {
        super();
        initialize();
    }

    @SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
    protected SimpleRPWorld(String defaultRoom) {
        super();
        setDefaultRoom(defaultRoom);
    }

    @SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor"})
    protected SimpleRPWorld(String defaultRoom, String jail) {
        super();
        setDefaultRoom(defaultRoom);
    }

    public static SimpleRPWorld get() {
        return (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
    }

    @Override
    public void deleteIfEmpty(String zone) {
        SimpleRPZone sZone = (SimpleRPZone) getRPZone(zone);
        if (sZone != null && sZone.isDeleteWhenEmpty() && sZone.getPlayers().isEmpty()) {
            try {
                logger.debug("Removing empty zone: " + sZone.getName());
                removeRPZone(sZone.getID());
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
    }

    /**
     * Gives the number of turns that will take place during a given number of
     * seconds.
     *
     * @param seconds The number of seconds.
     *
     * @return The number of turns.
     */
    @Override
    public int getTurnsInSeconds(int seconds) {
        return seconds * 1000 / MILLISECONDS_PER_TURN;
    }

    protected void createRPClasses() {
        if (!RPClass.hasRPClass("entity")) {
            Entity.generateRPClass();
        }

        if (!RPClass.hasRPClass("item")) {
            // Entity sub-classes
            Item.generateRPClass();
        }
        if (!RPClass.hasRPClass("rpentity")) {
            // ActiveEntity sub-classes
            RPEntity.generateRPClass();
        }

        // RPEntity sub-classes
        Lookup.getDefault().lookup(IRPObjectFactory.class).generateClientObjectRPClass();

        // NPC sub-classes

        // Creature sub-classes

        // PassiveEntityRespawnPoint sub-class

        // zone storage

        // rpevents
        for (Iterator<? extends IRPEvent> it = Lookup.getDefault().lookupAll(IRPEvent.class).iterator(); it.hasNext();) {
            IRPEvent event = it.next();
            logger.info("Registering event: " + event.getClass());
            event.generateRPClass();
        }
        //guilds

        //Client events
    }

    /**
     * World initialization
     */
    @Override
    public void onInit() {
        try {
            for (Iterator<? extends MarauroaServerExtension> it = Lookup.getDefault().lookupAll(MarauroaServerExtension.class).iterator(); it.hasNext();) {
                MarauroaServerExtension extension = it.next();
                logger.info("Loading extension: " + extension.getClass());
                extension.init();
            }
            //Create classes after plugins are initialized to allow them to plugin into the class creation.
            logger.info("Creating RPClasses.");
            createRPClasses();
            Iterator<RPClass> it = RPClass.iterator();
            if (logger.isDebugEnabled()) {
                logger.debug("Defined RPClasses:");
                while (it.hasNext()) {
                    logger.debug(it.next().getName());
                }
            }
            logger.info("Done!");
            //Make sure the system account exists. This will be the owner of NPC's
            if (!DAORegister.get().get(AccountDAO.class).hasPlayer(
                    Configuration.getConfiguration().get("system_account_name"))) {
                logger.info("Creating system account...");
                //Must be the one with id 1
                DAORegister.get().get(AccountDAO.class).addPlayer(
                        Configuration.getConfiguration().get("system_account_name"),
                        Hash.hash(Configuration.getConfiguration().get("system_password")),
                        Configuration.getConfiguration().get("system_email"));
                logger.info("Done!");
            } else {
                //Account exists, make sure the password is up to date
                DAORegister.get().get(AccountDAO.class).changePassword(
                        Configuration.getConfiguration().get("system_account_name"),
                        Configuration.getConfiguration().get("system_password"));
            }
            super.onInit();
            addZone(getDefaultRoom(), "");
            for (Iterator<? extends MarauroaServerExtension> it2 = Lookup.getDefault().lookupAll(MarauroaServerExtension.class).iterator(); it2.hasNext();) {
                MarauroaServerExtension extension = it2.next();
                extension.afterWorldInit();
            }
        } catch (Exception e) {
            logger.error("Error initializing the server!", e);
        }
    }

    /**
     * Gets all zones in this world.
     *
     * @param separator Character to separate the names in the list.
     * @return zones in this world in a list separated with the separator
     * character.
     */
    @Override
    public StringBuilder listZones(String separator) {
        StringBuilder rooms = new StringBuilder();
        Iterator i = iterator();
        while (i.hasNext()) {
            SimpleRPZone sZone = (SimpleRPZone) i.next();
            rooms.append(sZone.getName()).append(
                    sZone.getDescription().isEmpty() ? "" : ": "
                    + sZone.getDescription());
            if (i.hasNext()) {
                rooms.append(separator);
            }
        }
        return rooms;
    }

    @Override
    public void addRPZone(IRPZone zone) {
        super.addRPZone(zone);
        for (Iterator<? extends MarauroaServerExtension> it2 = Lookup.getDefault().lookupAll(MarauroaServerExtension.class).iterator(); it2.hasNext();) {
            MarauroaServerExtension extension = it2.next();
            extension.onAddRPZone(zone);
        }
    }

    @Override
    public void addZone(String name) {
        addZone(name, "");
    }

    @Override
    public void addZone(String name, String description) {
        if (getRPZone(name) == null) {
            SimpleRPZone zone = new SimpleRPZone(name);
            if (!description.isEmpty()) {
                zone.setDescription(description);
            }
            addRPZone(zone);
        } else {
            logger.warn("Request to add an already existing zone: " + name);
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(
                IRPRuleProcessor.class)).addGameEvent("server system", "shutdown");
    }

    @Override
    public IRPZone getRPZone(String zone) {
        return getRPZone(new IRPZone.ID(zone));
    }

    /**
     * Finds a zone by its id.
     *
     * @param id The zone's id
     *
     * @return The matching zone, or
     * <code>null</code> if not found.
     */
    @Override
    public SimpleRPZone getZone(final String id) {
        return (SimpleRPZone) getRPZone(new IRPZone.ID(id));
    }

    @Override
    public List<SimpleRPZone> getZones() {
        ArrayList<SimpleRPZone> availableZones = new ArrayList<SimpleRPZone>();

        Iterator zoneList = iterator();
        while (zoneList.hasNext()) {
            availableZones.add((SimpleRPZone) zoneList.next());
        }
        return availableZones;
    }

    @Override
    public boolean applyPrivateEvent(String target, RPEvent event) {
        return applyPrivateEvent(target, event, 0);
    }

    @Override
    public boolean applyPrivateEvent(String target, RPEvent event, int delay) {
        for (SimpleRPZone z : getZones()) {
            //Only if zone is not empty
            if (!z.getPlayers().isEmpty() && z.getPlayer(target) != null) {
                logger.debug("Sending private event:" + event
                        + " to: " + target + " currently in zone: " + z);
                ClientObject targetCO = ((ClientObject) z.getPlayer(target));
                targetCO.addEvent(event);
                targetCO.notifyWorldAboutChanges();
                return true;
            }
        }
        logger.debug("Unable to find player:" + target + "!");
        return false;
    }

    @Override
    public boolean applyPublicEvent(RPEvent event) {
        return applyPublicEvent(null, event);
    }

    @Override
    public boolean applyPublicEvent(SimpleRPZone zone, RPEvent event) {
        return applyPublicEvent(zone, event, 0);
    }

    @Override
    public boolean applyPublicEvent(SimpleRPZone zone, RPEvent event, int delay) {
        ArrayList<SimpleRPZone> availableZones = new ArrayList<SimpleRPZone>();
        if (zone != null) {
            availableZones.add(zone);
        } else {
            availableZones.addAll(getZones());
        }
        for (SimpleRPZone z : availableZones) {
            //Only if zone is not empty
            if (!z.getPlayers().isEmpty()) {
                logger.debug("Applying public event:" + event + " to: " + z);
                z.applyPublicEvent(event, delay);
            } else {
                logger.debug("Zone:" + z.getName() + " ignored because is empty (no players)");
            }
        }
        return true;
    }

    @Override
    public SimpleRPZone updateRPZoneDescription(String zone, String desc) {
        logger.debug("Updating room: " + zone + " with desc: " + desc);
        SimpleRPZone sZone = null;
        if (hasRPZone(new ID(zone))) {
            sZone = (SimpleRPZone) getRPZone(zone);
        }
        if (sZone != null) {
            sZone.setDescription(desc);
            logger.debug("Updated: " + sZone.toString());
        } else {
            logger.debug("Couldn't find zone: " + zone);
        }
        return sZone;
    }

    @Override
    public boolean addPlayer(RPObject object) {
        for (IRPZone RPzone : this) {
            SimpleRPZone zone = (SimpleRPZone) RPzone;
            if (zone.getName().equals(object.get("zoneid"))) {
                add(object);
                object.put("type", "player");
                logger.debug("Object added");
                showWorld();
                return true;
            }
        }
        logger.debug("addPlayer Zone " + object.get("zoneid")
                + "not found for Player " + object.get("name"));
        return false;
    }

    @Override
    public void changeZone(String newzoneid, RPObject object) {
        logger.debug("World before changing zone:");
        showWorld();
        if (object instanceof ClientObjectInterface) {
            SimpleRPZone zone = getZone(newzoneid);
            if (zone != null) {
                //ChangeZone takes care of removing from current zone
                super.changeZone(zone.getID(), object);
                Lookup.getDefault().lookup(ITurnNotifier.class).notifyInTurns(5,
                        new DelayedPlayerEventSender(new PrivateTextEvent(
                        NotificationType.INFORMATION, "Changed to zone: " + newzoneid),
                        (ClientObjectInterface) object));
            } else {
                ((ClientObjectInterface) object).sendPrivateText("Zone " + newzoneid + " doesn't exist!");
            }
        }
        logger.debug("World after changing zone:");
        showWorld();
    }

    @Override
    public void showWorld() {
        if (logger.isDebugEnabled()) {
            Iterator it = iterator();
            while (it.hasNext()) {
                ((SimpleRPZone) it.next()).showZone();
            }
        }
    }

    @Override
    public IRPZone removeRPZone(IRPZone zone) {
        try {
            return removeRPZone(zone.getID());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SimpleRPWorld.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
