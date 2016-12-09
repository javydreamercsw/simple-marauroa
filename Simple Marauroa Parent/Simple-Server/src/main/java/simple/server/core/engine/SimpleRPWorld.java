package simple.server.core.engine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Configuration;
import marauroa.common.crypto.Hash;
import marauroa.common.game.Definition;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.rp.RPWorld;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.action.ActionProvider;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.api.RPObjectMonitor;
import simple.server.core.event.api.IRPEvent;
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

    public static SimpleRPWorld get() {
        return (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
    }

    @Override
    public void addZone(IRPZone zone) {
        super.addRPZone(zone);
    }

    @Override
    public IRPZone removeRPZone(IRPZone zone) throws Exception {
        return super.removeRPZone(zone.getID());
    }

    @Override
    public IRPZone removeRPZone(String zone) throws Exception {
        return removeRPZone(new IRPZone.ID(zone));
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
    public boolean applyPrivateEvent(String target, RPEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean applyPrivateEvent(String target, RPEvent event, int delay) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        getZones().forEach((z) -> {
            if (availableZones.contains(z.getID())) {
                while (z.iterator().hasNext()) {
                    RPObject obj = z.iterator().next();
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
        return true;
    }

    @Override
    public void deleteIfEmpty(String zone) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            } catch (SQLException | IOException e) {
                LOG.log(Level.SEVERE, "Error initializing the server!", e);
            }
        }
    }

    @Override
    public void showWorld() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ISimpleRPZone updateRPZoneDescription(String zone, String desc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasRPZone(String zone) {
        return hasRPZone(new IRPZone.ID(zone));
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
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return defaultZone;
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
    public void registerMonitor(String target, RPObjectMonitor monitor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unregisterMonitor(String target, RPObjectMonitor monitor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RPObject.ID getID(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void checkZone(RPObject object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void emptyZone(IRPZone zone) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void emptyZone(IRPZone.ID zoneid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RPEntityInterface getPlayer(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
