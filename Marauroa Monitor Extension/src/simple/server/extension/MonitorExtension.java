package simple.server.extension;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.*;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.JDBCSQLHelper;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.RPObjectDAO;
import marauroa.server.game.extension.MarauroaServerExtension;
import marauroa.server.game.rp.RPWorld;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionListener;
import simple.server.core.action.CommandCenter;
import simple.server.core.engine.IRPWorld;
import simple.server.core.entity.clientobject.ClientObject;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service=MarauroaServerExtension.class)
public class MonitorExtension extends SimpleServerExtension implements ActionListener {

    public static final String _MONITOR = "monitor";
    private static final Logger logger = Log4J.getLogger(MonitorExtension.class);
    public static final int LISTZONES = 1, LISTPLAYERS = 2, LISTCONTENTS = 3;

    @Override
    public void init() {
        CommandCenter.register(_MONITOR, this);
    }

    private void getZoneInfo(ClientObjectInterface monitor, RPAction action) {
        if (isMonitor(monitor)) {
            list(monitor, LISTPLAYERS, action);
        }
    }

    private void getZones(ClientObjectInterface monitor, RPAction action) {
        if (isMonitor(monitor)) {
            list(monitor, LISTZONES, action);
        }
    }

    private boolean isMonitor(ClientObjectInterface monitor) {
        return DAORegister.get().get(MonitorDAO.class).isMonitor(monitor.getName());
    }

    private void list(ClientObjectInterface monitor, int option, RPAction action) {
        try {
            if (option == LISTZONES) {
                logger.debug("Sending zone list...");
                String zones = Lookup.getDefault().lookup(IRPWorld.class).listZones("#").toString();
                logger.debug("Sending: '" + zones + "'");
                monitor.sendText(zones);
            }
            if (option == LISTPLAYERS) {
                monitor.sendText(
                        Lookup.getDefault().lookup(IRPWorld.class).getZone(action.get(MonitorEvent.STRING)).getPlayersInString("#"));
            }
            if (option == LISTCONTENTS) {
//                monitor.sendPrivateText(
//                        Lookup.getDefault().lookup(IRPWorld.class)
//                        .getZone(action.get(MonitorEvent.STRING)).getNonPlayers());
            }
            monitor.notifyWorldAboutChanges();
            logger.debug("Done!");
        } catch (Exception ex) {
            logger.error(null, ex);
        }
    }

    @Override
    public RPObject onRPObjectAddToZone(RPObject object) {
        logger.debug("Processing adding object from " + getClass().getSimpleName());
        logger.debug(object);
        return object;
    }

    @Override
    public RPObject onRPObjectRemoveFromZone(RPObject object) {
        logger.debug("Processing removal of object from " + getClass().getSimpleName());
        if (object instanceof ClientObjectInterface
                && isMonitor((ClientObjectInterface) object)) {
            ClientObjectInterface monitor = (ClientObjectInterface) object;
            setEnabled(monitor, false);
        }
        return object;
    }

    @Override
    public boolean updateMonitor(RPObject object, Perception perception) {
        try {
            if (object instanceof ClientObjectInterface) {
                ClientObjectInterface monitor = (ClientObjectInterface) object;
                if (DAORegister.get().get(MonitorDAO.class).isMonitor(monitor.getName())) {
                    logger.debug("Updating monitor: " + monitor.getName());
                    for (IRPZone zone : getZones()) {
                        //Only add stuff not currently in the perception
                        if (zone.getID() != monitor.getZone().getID()) {
                            Perception p = zone.getPerception(object, Perception.DELTA);
                            logger.debug("Adding contents of zone: " + p.zoneid);
                            for (RPObject o : p.addedList) {
                                perception.addedList.add(o);
                            }
                            for (RPObject o : p.deletedList) {
                                perception.deletedList.add(o);
                            }
                            for (RPObject o : p.modifiedAddedList) {
                                perception.modifiedAddedList.add(o);
                            }
                            for (RPObject o : p.modifiedDeletedList) {
                                perception.modifiedDeletedList.add(o);
                            }
                        }
                    }
                    logger.debug(perception.toString());
                }
            }
        } catch (Exception e) {
            logger.error(null, e);
        }
        return true;
    }

    public List<IRPZone> getZones() {
        ArrayList<IRPZone> zones = new ArrayList<IRPZone>();
        try {
            Configuration conf = Configuration.getConfiguration();
            RPWorld world = null;
            if (conf.get("world") != null && !conf.get("world").isEmpty()) {
                Class<?> clientObjectClass = Class.forName(conf.get("world"));
                java.lang.reflect.Method localSingleton = clientObjectClass.getDeclaredMethod("get");
                world = (RPWorld) localSingleton.invoke(null);
            }
            for (IRPZone z : world) {
                zones.add(z);
            }
            return zones;
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
        return zones;
    }

    @Override
    public void modifyClientObjectDefinition(RPClass client) {
        client.addRPEvent(MonitorEvent.RPCLASS_NAME, Definition.VOLATILE);
    }

    @Override
    public void afterWorldInit() {
        try {
            if (!DAORegister.get().get(CharacterDAO.class).hasCharacter(_MONITOR)) {
                logger.debug("Adding the monitor character: " + _MONITOR + " to the System account.");
                //Create it
                ClientObject monitor = new ClientObject(new RPObject());
                //Save it.
                DAORegister.get().get(RPObjectDAO.class).storeRPObject(monitor);
                //Link with System account
                DAORegister.get().get(CharacterDAO.class).addCharacter(
                        Configuration.getConfiguration().get("system_account_name"),
                        _MONITOR, monitor);
            }
        } catch (SQLException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void register(ClientObjectInterface monitor) throws SQLException {
        DBTransaction transaction = TransactionPool.get().beginWork();
        try {
            if (!DAORegister.get().get(MonitorDAO.class).isMonitor(monitor.getName())) {
                DAORegister.get().get(MonitorDAO.class).addPlayer(
                        transaction, monitor.getName());
            }
            DAORegister.get().get(MonitorDAO.class).setEnabled(monitor, true);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            TransactionPool.get().commit(transaction);
        }
    }

    private void unregister(ClientObjectInterface monitor) throws SQLException {
        DBTransaction transaction = TransactionPool.get().beginWork();
        try {
            if (DAORegister.get().get(MonitorDAO.class).isMonitor(monitor.getName())) {
                DAORegister.get().get(MonitorDAO.class).removePlayer(
                        transaction, monitor.getName());
            }
            DAORegister.get().get(MonitorDAO.class).setEnabled(monitor, false);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            TransactionPool.get().commit(transaction);
        }
    }

    private void setEnabled(ClientObjectInterface monitor, boolean enabled) {
        try {
            DAORegister.get().get(MonitorDAO.class).setEnabled(monitor, enabled);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateDatabase() {
        DAORegister.get().register(MonitorDAO.class, new MonitorDAO());
        final DBTransaction transaction = TransactionPool.get().beginWork();
        try {
            createTablesUnlessTheyAlreadyExist(transaction);
            TransactionPool.get().commit(transaction);
        } catch (SQLException e) {
            logger.error(e, e);
            TransactionPool.get().rollback(transaction);
        }
    }

    private void createTablesUnlessTheyAlreadyExist(final DBTransaction transaction) {
        logger.debug("Creating Monitor extension Tables...");
        logger.debug("Creation successful? " + new JDBCSQLHelper(transaction).runDBScript("simple/server/extension/monitor_init.sql"));
        logger.debug("Done!");
    }

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface monitor = (ClientObjectInterface) rpo;
            logger.debug("Got action request from: " + monitor + ": " + action);
            switch (action.getInt(MonitorEvent.ACTION)) {
                case MonitorEvent.GET_ZONES:
                    getZones(monitor, action);
                    break;
                case MonitorEvent.GET_ZONE_INFO:
                    getZoneInfo(monitor, action);
                    break;
                case MonitorEvent.REGISTER:
                    try {
                        register(monitor);
                    } catch (SQLException ex) {
                        java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case MonitorEvent.UNREGISTER:
                    try {
                        unregister(monitor);
                    } catch (SQLException ex) {
                        java.util.logging.Logger.getLogger(MonitorExtension.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
