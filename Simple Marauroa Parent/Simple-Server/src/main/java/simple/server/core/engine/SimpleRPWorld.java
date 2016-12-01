package simple.server.core.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Configuration;
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
import simple.server.core.action.ActionProvider;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.api.RPObjectMonitor;
import simple.server.core.event.DelayedPlayerEventSender;
import simple.server.core.event.ITurnNotifier;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TurnNotifier;
import simple.server.core.event.api.IRPEvent;
import simple.server.core.tool.Tool;
import simple.server.extension.MarauroaServerExtension;

@ServiceProvider(service = IRPWorld.class)
public class SimpleRPWorld extends RPWorld implements IRPWorld {

    /**
     * the logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(SimpleRPWorld.class.getSimpleName());
    /**
     * A common place for milliseconds per turn.
     */
    public static final int MILLISECONDS_PER_TURN = 300;
    /*
     * Holds the initialization state of the world
     */
    private static boolean initialized = false;

    private static final Map<String, List<RPObjectMonitor>> MONITORS
            = new HashMap<>();

    @SuppressWarnings({"OverridableMethodCallInConstructor",
        "LeakingThisInConstructor"})

    @Override
    public void setDefaultZone(IRPZone defaultZone) {
        super.setDefaultZone(defaultZone);
        if (defaultZone != null && !hasRPZone(defaultZone.getID())) {
            addZone(defaultZone);
        }
    }

    public static SimpleRPWorld get() {
        return (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
    }

    @Override
    public void deleteIfEmpty(String zone) {
        IRPZone sZone = getZone(zone);
        if (sZone instanceof ISimpleRPZone) {
            ISimpleRPZone z = (ISimpleRPZone) sZone;
            if (z.isDeleteWhenEmpty()
                    && z.getPlayers().isEmpty()
                    && !getDefaultZone().getID().equals(sZone.getID())) {
                try {
                    LOG.log(Level.INFO, "Removing empty zone: {0}",
                            z.getName());
                    removeRPZone(sZone.getID());
                }
                catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        } else {
            LOG.log(Level.WARNING, "Not deleting zone: {0}", zone);
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
        if (!initialized) {
            initialize();
            try {
                LOG.info("Loading events...");
                Collection<? extends IRPEvent> events
                        = Lookup.getDefault().lookupAll(IRPEvent.class);
                LOG.log(Level.INFO, "Found {0} events to register!",
                        events.size());
                events.stream().map((event) -> {
                    LOG.log(Level.FINE, "Registering event: {0}: {1}",
                            new Object[]{event.getClass()
                                        .getSimpleName(), event.getRPClassName()});
                    return event;
                }).forEachOrdered((event) -> {
                    event.generateRPClass();
                });
                LOG.info("Done!");
                LOG.info("Loading extensions...");
                Collection<? extends MarauroaServerExtension> ext
                        = Lookup.getDefault().lookupAll(MarauroaServerExtension.class);
                LOG.log(Level.INFO, "Found {0} extensions to register!", ext.size());
                ext.stream().map((extension) -> {
                    LOG.log(Level.FINE, "Loading extension: {0}",
                            extension.getClass()
                                    .getSimpleName());
                    return extension;
                }).forEachOrdered((extension) -> {
                    extension.updateDatabase();
                });
                LOG.info("Done!");
                LOG.info("Loading actions...");
                Collection<? extends ActionProvider> actions
                        = Lookup.getDefault().lookupAll(ActionProvider.class);
                LOG.log(Level.INFO, "Found {0} Actions to register!",
                        actions.size());
                actions.stream().map((action) -> {
                    LOG.log(Level.FINE, "Registering action: {0}",
                            action.getClass().getSimpleName());
                    return action;
                }).forEachOrdered((action) -> {
                    action.register();
                });
                LOG.info("Done!");
                LOG.info("Creating RPClasses...");
                Collection<? extends RPEntityInterface> classes
                        = Lookup.getDefault().lookupAll(RPEntityInterface.class);
                LOG.log(Level.INFO, "Found {0} Entities to register!",
                        classes.size());
                classes.stream().map((entity) -> {
                    LOG.log(Level.FINE, "Registering entity: {0}",
                            entity.getClass().getSimpleName());
                    return entity;
                }).forEachOrdered((entity) -> {
                    entity.generateRPClass();
                });
                LOG.info("Done!");

                createSystemAccount();
                //Empty right now but just in case
                super.onInit();
                boolean needDefault = true;
                for (IDefaultZoneProvider provider
                        : Lookup.getDefault().lookupAll(IDefaultZoneProvider.class)) {
                    for (IRPZone zone : provider.getDefaultZones()) {
                        if (needDefault) {
                            setDefaultZone(zone);
                            needDefault = false;
                        } else {
                            //setDefaultZone adds the zone
                            addZone(zone);
                        }
                    }
                }
                Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                        .stream().forEach((extension) -> {
                            extension.afterWorldInit();
                        });
                initialized = true;
            }
            catch (SQLException | IOException e) {
                LOG.log(Level.SEVERE, "Error initializing the server!", e);
            }
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
    public void addZone(IRPZone zone) {
        //Make sure all zones are SimpleRPZone instances
        SimpleRPZone simpleZone;
        if (!(zone instanceof SimpleRPZone)) {
            simpleZone = new SimpleRPZone(zone.getID().getID());
        } else {
            simpleZone = (SimpleRPZone) zone;
        }
        super.addRPZone(simpleZone);
        Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                .forEach((extension) -> {
                    extension.onAddRPZone(simpleZone);
                });
    }

    @Override
    public void addZone(String name) {
        addZone(name, "");
    }

    @Override
    public void addZone(String name, String description) {
        if (getZone(name) == null) {
            SimpleRPZone zone = new SimpleRPZone(name);
            if (!description.isEmpty()) {
                zone.setDescription(description);
            }
            addZone(zone);
        } else {
            LOG.log(Level.WARNING,
                    "Request to add an already existing zone: {0}", name);
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(
                IRPRuleProcessor.class)).addGameEvent("server system", "shutdown");
    }

    @Override
    public IRPZone getZone(final String id) {
        return getZone(new IRPZone.ID(id));
    }

    @Override
    public IRPZone getZone(final IRPZone.ID id) {
        if (id != null) {
            for (IRPZone z : getZones()) {
                if (z.getID() != null) {
                    if (z.getID().equals(id)) {
                        return z;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<IRPZone> getZones() {
        ArrayList<IRPZone> availableZones = new ArrayList<>();

        Iterator zoneList = iterator();
        while (zoneList.hasNext()) {
            availableZones.add((IRPZone) zoneList.next());
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
        for (IRPZone zone : getZones()) {
            if (zone instanceof ISimpleRPZone) {
                ISimpleRPZone z = (ISimpleRPZone) zone;
                if (!z.getPlayers().isEmpty() && z.getPlayer(target) != null) {
                    LOG.log(Level.FINE,
                            "Sending private event:{0} to: {1} currently "
                            + "in zone: {2}", new Object[]{event, target, z});
                    RPEntityInterface targetCO = z.getPlayer(target);
                    targetCO.addEvent(event);
                    targetCO.notifyWorldAboutChanges();
                    result = true;
                    break;
                }
                z.getNPCS().stream().filter((npc) -> (((RPObject) npc).has(Entity.NAME)
                        && Tool.extractName(((RPObject) npc)).equals(target)))
                        .map((npc) -> {
                            LOG.log(Level.FINE, "Adding event to: {0}, {1}, {2}",
                                    new Object[]{npc, ((RPObject) npc).getID(),
                                        npc.get(Entity.ZONE_ID)});
                            return npc;
                        }).map((npc) -> {
                    ((RPObject) npc).addEvent(event);
                    return npc;
                }).forEachOrdered((npc) -> {
                    Lookup.getDefault().lookup(IRPWorld.class).modify((RPObject) npc);
                });
            }
        }
        if (!result) {
            LOG.log(Level.FINE, "Unable to find player:{0}!", target);
        }
        return result;
    }

    @Override
    public boolean applyPublicEvent(RPEvent event) {
        return applyPublicEvent(null, event);
    }

    @Override
    public boolean applyPublicEvent(ISimpleRPZone zone, RPEvent event) {
        return applyPublicEvent(zone, event, 0);
    }

    @Override
    public boolean applyPublicEvent(ISimpleRPZone zone, RPEvent event, int delay) {
        ArrayList<IRPZone.ID> availableZones = new ArrayList<>();
        if (zone != null) {
            availableZones.add(zone.getID());
        } else {
            getZones().forEach((z) -> {
                availableZones.add(z.getID());
            });
        }
        ((SimpleRPRuleProcessor) Lookup.getDefault()
                .lookup(IRPRuleProcessor.class)).getOnlinePlayers()
                .forAllPlayersExecute((RPEntityInterface p) -> {
                    if (availableZones.contains(p.getZone().getID())) {
                        if (delay == 0) {
                            p.addEvent(event);
                            p.notifyWorldAboutChanges();
                        } else {
                            Lookup.getDefault().lookup(TurnNotifier.class)
                                    .notifyInTurns(delay,
                                            new DelayedPlayerEventSender(event,
                                                    (RPObject) p));
                        }
                    }
                });
        return true;
    }

    @Override
    public SimpleRPZone updateRPZoneDescription(String zone, String desc) {
        LOG.log(Level.FINE, "Updating room: {0} with desc: {1}",
                new Object[]{zone, desc});
        SimpleRPZone sZone = null;
        if (hasRPZone(new ID(zone))) {
            sZone = (SimpleRPZone) getZone(zone);
        }
        if (sZone != null) {
            sZone.setDescription(desc);
            LOG.log(Level.FINE, "Updated: {0}", sZone.toString());
        } else {
            LOG.log(Level.FINE, "Couldn''t find zone: {0}", zone);
        }
        return sZone;
    }

    protected boolean addPlayer(RPEntityInterface object) {
        boolean result = false;
        for (IRPZone zone : this) {
            if (zone.getID().getID().equals(object.getZone().getID().getID())) {
                LOG.fine("Object added");
                showWorld();
                //Add it to the RuleProcessor as well
                RPEntityInterface player = SimpleRPRuleProcessor.get()
                        .getOnlinePlayers()
                        .getOnlinePlayer(object.getName());
                if (player == null) {
                    //Just joined us, greet the player!
                    welcome(object);
                } else {
                    //Replace
                    SimpleRPRuleProcessor.get().onExit((RPObject) object);
                }
                SimpleRPRuleProcessor.get().onInit((RPObject) object);
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Send a welcome message to the player which can be configured in
     * server.ini file as "server_welcome". If the value is an http:// address,
     * the first line of that address is read and used as the message
     *
     * @param player RPEntityInterface
     */
    protected static void welcome(final RPEntityInterface player) {
        String msg = "";
        try {
            Configuration config = Configuration.getConfiguration();
            if (config.has("server_welcome")) {
                msg = config.get("server_welcome");
                if (msg.startsWith("http://")) {
                    URL url = new URL(msg);
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection connection
                            = (HttpURLConnection) url.openConnection();
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        msg = br.readLine();
                    }
                    connection.disconnect();
                }
            }
        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        TurnNotifier notifier = Lookup.getDefault().lookup(TurnNotifier.class);
        if (msg != null && !msg.isEmpty()) {
            if (notifier != null) {
                notifier.notifyInTurns(10,
                        new DelayedPlayerEventSender(new PrivateTextEvent(
                                NotificationType.TUTORIAL, msg),
                                (RPObject) player));
            } else {
                LOG.log(Level.WARNING,
                        "Unable to send message: ''{0}'' to player: {1}",
                        new Object[]{msg, player.getName()});
            }
        }
    }

    @Override
    public void changeZone(IRPZone.ID newzoneid, RPObject object) {
        changeZone(newzoneid.getID(), object);
    }

    @Override
    public void changeZone(String newzoneid, RPObject object) {
        LOG.fine("World before changing zone:");
        showWorld();
        IRPZone zone = getZone(newzoneid);
        if (zone != null) {
            //ChangeZone takes care of removing from current zone
            super.changeZone(zone.getID(), object);
            if (LOG.isLoggable(Level.FINE)) {
                if (object instanceof RPEntityInterface) {
                    Lookup.getDefault().lookup(ITurnNotifier.class).notifyInTurns(10,
                            new DelayedPlayerEventSender(new PrivateTextEvent(
                                    NotificationType.INFORMATION,
                                    "Changed to zone: " + newzoneid), object));
                }
            }
        } else {
            LOG.log(Level.SEVERE, "Zone {0} doesn't exist!", newzoneid);
        }
        LOG.fine("World after changing zone:");
        showWorld();
    }

    @Override
    public void showWorld() {
        Iterator it = iterator();
        while (it.hasNext()) {
            ((ISimpleRPZone) it.next()).showZone();
        }
    }

    @Override
    public IRPZone removeRPZone(IRPZone zone) {
        IRPZone result;
        try {
            result = removeRPZone(zone.getID());
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            result = null;
        }
        return result;
    }

    @Override
    public IRPZone removeRPZone(ID zoneid) throws Exception {
        if (hasRPZone(zoneid)) {
            if (getZone(zoneid) instanceof ISimpleRPZone) {
                ISimpleRPZone zone = (ISimpleRPZone) getZone(zoneid);
                /**
                 * Kick everyone to the default zone or they'll end in the
                 * limbo!
                 */
                Iterator i = zone.getPlayers().iterator();
                List<RPObject> toMove = new ArrayList<>();
                while (i.hasNext()) {
                    RPObject next = (RPObject) i.next();
                    if (next instanceof RPEntityInterface) {
                        toMove.add(next);
                    }
                }
                toMove.forEach((co) -> {
                    Lookup.getDefault().lookup(IRPWorld.class).changeZone(
                            Lookup.getDefault().lookup(IRPWorld.class).getDefaultZone()
                                    .getID().getID(), co);
                });
                //Handle NPC's
                i = zone.getNPCS().iterator();
                while (i.hasNext()) {
                    Lookup.getDefault().lookup(IRPWorld.class)
                            .remove(((RPObject) i.next()).getID());
                }
            }
        } else {
            return null;
        }
        return super.removeRPZone(zoneid);
    }

    @Override
    public void emptyZone(IRPZone zone) {
        emptyZone(zone.getID());
    }

    @Override
    public void emptyZone(ID zoneid) {
        IRPZone zone = Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(zoneid);
        if (zone instanceof ISimpleRPZone) {
            ISimpleRPZone z = (ISimpleRPZone) zone;
            Iterator<RPEntityInterface> i = z.getPlayers().iterator();
            List<RPEntityInterface> toRemove = new ArrayList<>();
            while (i.hasNext()) {
                toRemove.add(i.next());
            }
            toRemove.forEach((co) -> {
                zone.remove(((RPObject) co).getID());
            });
            //Handle NPC's
            Iterator<RPObject> i2 = z.getNPCS().iterator();
            List<RPObject> toRemove2 = new ArrayList<>();
            while (i2.hasNext()) {
                toRemove2.add(i2.next());
            }
            toRemove2.forEach((co) -> {
                zone.remove(((RPObject) co).getID());
            });
        }
    }

    @Override
    public void createSystemAccount() throws SQLException, IOException {
        //Make sure the system account exists. This will be the owner of NPC's
        if (!DAORegister.get().get(AccountDAO.class).hasPlayer(
                Configuration.getConfiguration()
                        .get("system_account_name"))) {
            LOG.info("Creating system account...");
            //Must be the one with id 1
            DAORegister.get().get(AccountDAO.class).addPlayer(
                    Configuration.getConfiguration()
                            .get("system_account_name"),
                    Hash.hash(Configuration.getConfiguration()
                            .get("system_password")),
                    Configuration.getConfiguration().get("system_email"));
            LOG.info("Done!");
        } else {
            LOG.info("Updating system account...");
            //Account exists, make sure the password is up to date
            DAORegister.get().get(AccountDAO.class).changePassword(
                    Configuration.getConfiguration()
                            .get("system_account_name"),
                    Configuration.getConfiguration()
                            .get("system_password"));
            LOG.info("Done!");
        }
    }

    @Override
    public void checkZone(RPObject object) {
        SimpleRPZone zone = (SimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(object.get(Entity.ZONE_ID));
        if (zone == null) {
            //The zone we were no longer exists use default.
            zone = (SimpleRPZone) Lookup.getDefault()
                    .lookup(IRPWorld.class).getDefaultZone();
            object.put(Entity.ZONE_ID, zone.getName());
        }
    }

    @Override
    public void modify(RPObject object) {
        checkZone(object);
        super.modify(object);
        synchronized (MONITORS) {
            if (MONITORS.containsKey(Tool.extractName(object))) {
                Iterator<RPObjectMonitor> iter
                        = MONITORS.get(Tool.extractName(object)).iterator();
                while (iter.hasNext()) {
                    RPObjectMonitor m = iter.next();
                    m.modify(object);
                }
            }
        }
    }

    @Override
    public void registerMonitor(String target, RPObjectMonitor monitor) {
        synchronized (MONITORS) {
            if (!MONITORS.containsKey(target)) {
                MONITORS.put(target, new ArrayList<>());
            }
            MONITORS.get(target).add(monitor);
        }
    }

    @Override
    public void unregisterMonitor(String target, RPObjectMonitor monitor) {
        synchronized (MONITORS) {
            if (MONITORS.containsKey(target)) {
                MONITORS.get(target).remove(monitor);
            }
        }
    }

    @Override
    public RPObject.ID getID(String name) {
        RPObject.ID result = null;
        for (IRPZone z : getZones()) {
            Iterator<RPObject> zoneIterator = z.iterator();
            while (zoneIterator.hasNext()) {
                RPObject next = zoneIterator.next();
                if (Tool.extractName(next).equals(name)) {
                    result = next.getID();
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public IRPZone getDefaultZone() {
        //If none defined, this will be a MarauroaRPZone, we need to replace it.
        IRPZone defaultZone = super.getDefaultZone();
        if (defaultZone != null
                && !(defaultZone instanceof ISimpleRPZone)) {
            try {
                String id = defaultZone.getID().getID();
                removeRPZone(defaultZone.getID());
                //Recreate it in our system
                addZone(id);
                IRPZone zone = getZone(id);
                setDefaultZone(zone);
                return zone;
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return defaultZone;
    }

    @Override
    public boolean hasRPZone(String zone) {
        return hasRPZone(new IRPZone.ID(zone));
    }

    @Override
    public IRPZone removeRPZone(String zone) throws Exception {
        return removeRPZone(new IRPZone.ID(zone));
    }

    @Override
    public RPEntityInterface getPlayer(String name) {
        return ((SimpleRPRuleProcessor) Lookup.getDefault()
                .lookup(IRPRuleProcessor.class))
                .getPlayer(name);
    }

    @Override
    public void add(RPObject object) {
        if (!object.has(Entity.ZONE_ID) //No zone assigned
                || !hasRPZone(object.get(Entity.ZONE_ID))) { //Assigned zone doesn't exist
            //Assign the current default zone
            object.put(Entity.ZONE_ID, getDefaultZone().getID().getID());
            LOG.log(Level.WARNING, "Assigning {0} to default zone: {1}",
                    new Object[]{Tool.extractName(object),
                        getDefaultZone().getID().getID()});
        }
        if (object.getRPClass().subclassOf(RPEntity.DEFAULT_RPCLASS)) {
            addPlayer(new RPEntity(object));
        }
        super.add(object);
    }
}
