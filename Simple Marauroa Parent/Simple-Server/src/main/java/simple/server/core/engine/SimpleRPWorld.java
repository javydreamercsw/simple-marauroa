package simple.server.core.engine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.crypto.Hash;
import marauroa.common.game.IRPZone;
import marauroa.common.game.IRPZone.ID;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.game.rp.RPWorld;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.clientobject.ClientObject;
import simple.server.core.event.DelayedPlayerEventSender;
import simple.server.core.event.ITurnNotifier;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.api.IRPEvent;
import simple.server.extension.MarauroaServerExtension;

@ServiceProvider(service = IRPWorld.class)
public class SimpleRPWorld extends RPWorld implements IRPWorld {

    /**
     * the logger instance.
     */
    private static final Logger logger = Log4J.getLogger(SimpleRPWorld.class);
    /**
     * A common place for milliseconds per turn.
     */
    public static final int MILLISECONDS_PER_TURN = 300;
    /*
     * Holds the initialization state of the world
     */
    private static boolean initialized = false;

    @SuppressWarnings({"OverridableMethodCallInConstructor",
        "LeakingThisInConstructor"})
    public SimpleRPWorld() {
        super();
        if (!initialized) {
            initialize();
            initialized = true;
        }
    }

    @Override
    public void setDefaultZone(IRPZone defaultZone) {
        super.setDefaultZone(defaultZone);
        if (!hasRPZone(defaultZone.getID())) {
            addRPZone(defaultZone);
        }
    }

    public static SimpleRPWorld get() {
        return (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
    }

    @Override
    public void deleteIfEmpty(String zone) {
        SimpleRPZone sZone = (SimpleRPZone) getRPZone(zone);
        if (sZone != null && sZone.isDeleteWhenEmpty()
                && sZone.getPlayers().isEmpty()) {
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

    /**
     * World initialization
     */
    @Override
    public void onInit() {
        try {
            logger.info("Loading extensions...");
            Collection<? extends MarauroaServerExtension> ext =
                    Lookup.getDefault().lookupAll(MarauroaServerExtension.class);
            logger.debug("Found " + ext.size() + " extensions to register!");
            for (Iterator<? extends MarauroaServerExtension> it =
                    ext.iterator(); it.hasNext();) {
                MarauroaServerExtension extension = it.next();
                logger.debug("Loading extension: " + extension.getClass()
                        .getSimpleName());
                extension.updateDatabase();
            }
            logger.info("Done!");
            logger.info("Loading events...");
            Collection<? extends IRPEvent> events =
                    Lookup.getDefault().lookupAll(IRPEvent.class);
            logger.debug("Found " + events.size() + " events to register!");
            for (Iterator<? extends IRPEvent> it = events.iterator(); it.hasNext();) {
                IRPEvent event = it.next();
                logger.debug("Registering event: " + event.getClass()
                        .getSimpleName()
                        + ": " + event.getRPClassName());
                event.generateRPClass();
            }
            logger.info("Done!");
            logger.info("Creating RPClasses...");
            Collection<? extends RPEntityInterface> classes =
                    Lookup.getDefault().lookupAll(RPEntityInterface.class);
            logger.debug("Found " + classes.size() + " Entities to register!");
            for (Iterator<? extends RPEntityInterface> it =
                    classes.iterator(); it.hasNext();) {
                RPEntityInterface entity = it.next();
                logger.debug(entity.getClass().getSimpleName());
                entity.generateRPClass();
            }
            logger.info("Done!");

            //Make sure the system account exists. This will be the owner of NPC's
            if (!DAORegister.get().get(AccountDAO.class).hasPlayer(
                    Configuration.getConfiguration()
                    .get("system_account_name"))) {
                logger.info("Creating system account...");
                //Must be the one with id 1
                DAORegister.get().get(AccountDAO.class).addPlayer(
                        Configuration.getConfiguration()
                        .get("system_account_name"),
                        Hash.hash(Configuration.getConfiguration()
                        .get("system_password")),
                        Configuration.getConfiguration().get("system_email"));
                logger.info("Done!");
            } else {
                logger.info("Updating system account...");
                //Account exists, make sure the password is up to date
                DAORegister.get().get(AccountDAO.class).changePassword(
                        Configuration.getConfiguration()
                        .get("system_account_name"),
                        Configuration.getConfiguration()
                        .get("system_password"));
                logger.info("Done!");
            }
            //Empty right now but just in case
            super.onInit();
            boolean needDefault = true;
            for (Iterator<? extends IDefaultZoneProvider> it =
                    Lookup.getDefault().lookupAll(IDefaultZoneProvider.class)
                    .iterator(); it.hasNext();) {
                IDefaultZoneProvider provider = it.next();
                for (Iterator<IRPZone> it2 = provider.getDefaultZones()
                        .iterator(); it2.hasNext();) {
                    IRPZone zone = it2.next();
                    if (needDefault) {
                        setDefaultZone(zone);
                        needDefault = false;
                    } else {
                        //setDefaultZone adds the zone
                        addRPZone(zone);
                    }
                }
            }
            for (Iterator<? extends MarauroaServerExtension> it2 =
                    Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                    .iterator(); it2.hasNext();) {
                MarauroaServerExtension extension = it2.next();
                extension.afterWorldInit();
            }
        } catch (IOException e) {
            logger.error("Error initializing the server!", e);
        } catch (SQLException e) {
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
        //Make sure all zones are SimpleRPZone instances
        SimpleRPZone simpleZone;
        if (!(zone instanceof SimpleRPZone)) {
            simpleZone = new SimpleRPZone(zone.getID().getID());
        } else {
            simpleZone = (SimpleRPZone) zone;
        }
        super.addRPZone(simpleZone);
        for (Iterator<? extends MarauroaServerExtension> it2 =
                Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                .iterator(); it2.hasNext();) {
            MarauroaServerExtension extension = it2.next();
            extension.onAddRPZone(simpleZone);
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
     * @return The matching zone, or <code>null</code> if not found.
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
        boolean result = false;
        for (SimpleRPZone z : getZones()) {
            //Only if zone is not empty
            if (!z.getPlayers().isEmpty() && z.getPlayer(target) != null) {
                logger.debug("Sending private event:" + event
                        + " to: " + target + " currently in zone: " + z);
                ClientObject targetCO = ((ClientObject) z.getPlayer(target));
                targetCO.addEvent(event);
                targetCO.notifyWorldAboutChanges();
                result = true;
                break;
            }
        }
        if (!result) {
            logger.debug("Unable to find player:" + target + "!");
        }
        return result;
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
                logger.debug("Zone:" + z.getName()
                        + " ignored because is empty (no players)");
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
        boolean result = false;
        if (object instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) object;
            for (Iterator<IRPZone> it = this.iterator(); it.hasNext();) {
                IRPZone zone = it.next();
                if (zone.getID().getID().equals(object.get("zoneid"))) {
                    add(object);
                    logger.debug("Object added");
                    showWorld();
                    //Add it to the RuleProcessor as well
                    if (SimpleRPRuleProcessor.get().getOnlinePlayers()
                            .getOnlinePlayer(player.getName()) == null) {
                        SimpleRPRuleProcessor.get().getOnlinePlayers()
                                .add(player);
                    }
                    result = true;
                    break;
                }
            }
        } else if (object != null) {
            logger.debug("addPlayer Zone " + object.get("zoneid")
                    + "not found for Player " + object.get("name"));
        }
        return result;
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
        IRPZone result;
        try {
            result = removeRPZone(zone.getID());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SimpleRPWorld.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        }
        return result;
    }
}
