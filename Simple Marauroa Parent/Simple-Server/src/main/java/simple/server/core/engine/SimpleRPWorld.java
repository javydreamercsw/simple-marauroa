package simple.server.core.engine;

import java.io.IOException;
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
import marauroa.common.game.Definition;
import marauroa.common.game.IRPZone;
import marauroa.common.game.IRPZone.ID;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.game.rp.RPWorld;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.action.ActionProvider;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.api.RPEventListener;
import simple.server.core.event.api.IRPEvent;
import simple.server.core.tool.Tool;
import simple.server.extension.MarauroaServerExtension;

@ServiceProvider(service = IRPWorld.class)
public class SimpleRPWorld extends RPWorld implements IRPWorld {

    public static final int MILLISECONDS_PER_TURN = 300;
    /**
     * Flag to mark if we already initialized.
     */
    private boolean initialized = false;

    /**
     * The logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(SimpleRPWorld.class.getSimpleName());

    //Monitors
    private static final Map<String, Map<String, List<RPEventListener>>> MONITORS
            = new HashMap<>();

    //Used by reflection from Marauroa's code. Do not remove!
    public static SimpleRPWorld get() {
        return (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
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
        super.add(object);
    }

    @Override
    public IRPZone removeRPZone(String zone) throws Exception {
        return removeRPZone(new IRPZone.ID(zone));
    }

    @Override
    public IRPZone removeRPZone(IRPZone zone) {
        IRPZone result;
        try {
            result = removeRPZone(zone.getID());
        } catch (Exception ex) {
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
                    toMove.add((RPObject) i.next());
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
    public void addRPZone(String name) {
        addRPZone(name, "");
    }

    @Override
    public void addRPZone(String name, String description) {
        if (getZone(name) == null) {
            SimpleRPZone zone = new SimpleRPZone(name);
            if (!description.isEmpty()) {
                zone.setDescription(description);
            }
            addRPZone(zone);
        } else {
            LOG.log(Level.WARNING,
                    "Request to add an already existing zone: {0}", name);
        }
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
                Iterator<RPObject> iterator = z.iterator();
                while (iterator.hasNext()) {
                    RPObject obj = iterator.next();
                    if (obj.has(Entity.NAME)
                            && Tool.extractName(obj).equals(target)) {
                        LOG.log(Level.FINE, "Adding event to: {0}, {1}, {2}",
                                new Object[]{obj, obj.getID(),
                                    obj.get(Entity.ZONE_ID)});
                        obj.addEvent(event);
                        Lookup.getDefault().lookup(IRPWorld.class).modify(obj);
                        result = true;
                        break;
                    }
                }
            }
        }
        //Update the players
        PlayerEntryContainer container
                = PlayerEntryContainer.getContainer();
        Iterator<PlayerEntry> it = container.iterator();
        while (it.hasNext()) {
            PlayerEntry entry = it.next();
            if (entry.object.has(Entity.NAME)
                    && Tool.extractName(entry.object).equals(target)) {
                LOG.log(Level.FINE, "Applying event {0} to:\n {1}",
                        new Object[]{event, entry.object});
                entry.object.addEvent(event);
                Lookup.getDefault().lookup(IRPWorld.class).modify(entry.object);
                result = true;
                break;
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
        ArrayList<String> availableZones = new ArrayList<>();
        if (zone != null) {
            availableZones.add(zone.getID().getID());
        } else {
            getZones().forEach((z) -> {
                availableZones.add(z.getID().getID());
            });
        }
        getZones().forEach((z) -> {
            if (availableZones.contains(z.getID())) {
                Iterator<RPObject> iterator = z.iterator();
                while (iterator.hasNext()) {
                    RPObject obj = iterator.next();
                    if (obj.getRPClass().hasDefinition(
                            Definition.DefinitionClass.RPEVENT, event.getName())) {
                        obj.addEvent(event);
                        modify(obj);
                    } else {
                        LOG.log(Level.WARNING, "Skipping event: {0} for {1}",
                                new Object[]{event, obj});
                    }
                }
            }
        });
        //Update the players
        PlayerEntryContainer container
                = PlayerEntryContainer.getContainer();
        Iterator<PlayerEntry> it = container.iterator();
        while (it.hasNext()) {
            PlayerEntry entry = it.next();
            if (availableZones.contains(entry.object.get(Entity.ZONE_ID))) {
                LOG.log(Level.FINE, "Applying event {0} to:\n {1}",
                        new Object[]{event, entry.object});
                entry.object.addEvent(event);
                Lookup.getDefault().lookup(IRPWorld.class).modify(entry.object);
            }
        }
        return true;
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
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        } else {
            LOG.log(Level.WARNING, "Not deleting zone: {0}", zone);
        }
    }

    @Override
    public int getTurnsInSeconds(int seconds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IRPZone getZone(String id) {
        return getZone(new IRPZone.ID(id));
    }

    @Override
    public IRPZone getZone(IRPZone.ID id) {
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
    public StringBuilder listZones(String separator) {
        StringBuilder zones = new StringBuilder();
        Iterator i = iterator();
        while (i.hasNext()) {
            SimpleRPZone sZone = (SimpleRPZone) i.next();
            zones.append(sZone.getName()).append(
                    sZone.getDescription().isEmpty() ? "" : ": "
                    + sZone.getDescription());
            if (i.hasNext()) {
                zones.append(separator);
            }
        }
        return zones;
    }

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
                        addRPZone(zone);
                        if (needDefault) {
                            setDefaultZone(zone);
                            needDefault = false;
                        }
                    }
                }
                if (needDefault) {
                    //If none defined, this will be a MarauroaRPZone, we need to replace it.
                    IRPZone defaultZone = super.getDefaultZone();
                    if (defaultZone != null
                            && !(defaultZone instanceof ISimpleRPZone)) {
                        try {
                            String id = defaultZone.getID().getID();
                            removeRPZone(defaultZone.getID());
                            //Recreate it in our system
                            addRPZone(id);
                            IRPZone zone = getZone(id);
                            setDefaultZone(zone);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                    if (getDefaultZone() == null) {
                        //Something is wrong, no default zone!
                        LOG.log(Level.SEVERE, "No default zone found!");
                        System.exit(0);
                    }
                } else {
                    Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                            .stream().forEach((extension) -> {
                                extension.afterWorldInit();
                            });
                    initialized = true;
                }
            } catch (SQLException | IOException e) {
                LOG.log(Level.SEVERE, "Error initializing the server!", e);
            }
        }
    }

    @Override
    public void showWorld() {
        Iterator it = iterator();
        while (it.hasNext()) {
            ((ISimpleRPZone) it.next()).showZone();
        }
    }

    @Override
    public ISimpleRPZone updateRPZoneDescription(String zone, String desc) {
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
            LOG.log(Level.FINE, "Couldn't find zone: {0}", zone);
        }
        return sZone;
    }

    @Override
    public boolean hasRPZone(String zone) {
        return hasRPZone(new IRPZone.ID(zone));
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
    public void registerMonitor(RPObject target, String eventClassName,
            RPEventListener listener) {
        registerMonitor(Tool.extractName(target), eventClassName, listener);
    }

    @Override
    public void registerMonitor(String target, String eventClassName,
            RPEventListener listener) {
        if (target != null && listener != null) {
            synchronized (MONITORS) {
                if (!MONITORS.containsKey(target)) {
                    MONITORS.put(target, new HashMap<>());
                }
                if (!MONITORS.get(target).containsKey(eventClassName)) {
                    MONITORS.get(target).put(eventClassName, new ArrayList<>());
                }
                MONITORS.get(target).get(eventClassName).add(listener);
            }
        } else {
            if (target == null) {
                LOG.warning("Ignoring request due to null target!");
            } else {
                LOG.warning("Ignoring request due to null listener!");
            }
            Tool.printStackTrace(Thread.currentThread().getStackTrace());
        }
    }

    @Override
    public void unregisterMonitor(RPObject target, String eventClassName,
            RPEventListener listener) {
        unregisterMonitor(Tool.extractName(target), eventClassName, listener);
    }

    @Override
    public void unregisterMonitor(String target, String eventClassName,
            RPEventListener listener) {
        synchronized (MONITORS) {
            if (MONITORS.containsKey(target)) {
                if (MONITORS.get(target).containsKey(eventClassName)) {
                    MONITORS.get(target).remove(eventClassName);
                }
            }
        }
    }

    @Override
    public RPObject.ID getID(String name) {
        Iterator<IRPZone> iterator = iterator();
        while (iterator.hasNext()) {
            IRPZone zone = iterator.next();
            Iterator<RPObject> iterator1 = zone.iterator();
            while (iterator1.hasNext()) {
                RPObject next = iterator1.next();
                if (Tool.extractName(next).equals(name)) {
                    return next.getID();
                }
            }
        }
        return null;
    }

    @Override
    public void checkZone(RPObject object) {
        try {
            IRPZone zone = Lookup.getDefault().lookup(IRPWorld.class)
                    .getZone(object.get(Entity.ZONE_ID));
            if (zone == null) {
                //The zone we were no longer exists use default.
                zone = Lookup.getDefault()
                        .lookup(IRPWorld.class).getDefaultZone();
                object.put(Entity.ZONE_ID, zone.getID().getID());
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void emptyZone(IRPZone zone) {
        emptyZone(zone.getID());
    }

    @Override
    public void emptyZone(IRPZone.ID zoneid) {
        IRPZone zone = Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(zoneid);
        Iterator<RPObject> iterator = zone.iterator();
        List<RPObject.ID> toRemove = new ArrayList<>();
        while (iterator.hasNext()) {
            toRemove.add(iterator.next().getID());
        }
        toRemove.forEach((id) -> {
            zone.remove(id);
        });
    }

    @Override
    public RPObject getPlayer(String name) {
        return (RPObject) ((SimpleRPRuleProcessor) Lookup.getDefault()
                .lookup(IRPRuleProcessor.class))
                .getPlayer(name);
    }

    @Override
    public void modify(RPObject object) {
        checkZone(object);
        super.modify(object);
        synchronized (MONITORS) {
            if (MONITORS.size() > 0
                    && MONITORS.containsKey(Tool.extractName(object))) {
                List<RPEvent> events = object.events();
                List<RPEvent> copy = new ArrayList<>();
                events.forEach((e) -> {
                    copy.add((RPEvent) e.clone());
                });
                copy.stream().filter((event)
                        -> (MONITORS.get(Tool.extractName(object))
                                .containsKey(event.getName())))
                        .forEachOrdered((event) -> {
                            MONITORS.get(Tool.extractName(object))
                                    .get(event.getName()).forEach((listener) -> {
                                listener.onRPEvent(event);
                            });
                        });
            }
        }
    }

    @Override
    public RPObject getNPC(String name) {
        RPObject result = null;
        for (IRPZone zone : getZones()) {
            SimpleRPZone z = (SimpleRPZone) zone;
            RPObject npc = z.getNPC(name);
            if (npc != null) {
                result = npc;
                break;
            }
        }
        return result;
    }
}
