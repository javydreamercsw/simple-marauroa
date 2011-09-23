package simple.server.extension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Log4J;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;
import simple.common.game.ClientObjectInterface;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class MonitorDAO {

    private static final marauroa.common.Logger logger = Log4J.getLogger(MonitorDAO.class);
    private ArrayList<ClientObjectInterface> monitors = new ArrayList<ClientObjectInterface>();

    public MonitorDAO() {
    }

    public void addPlayer(DBTransaction transaction, String username) throws SQLException {
        try {
            String query = "insert into monitor(account_id, enabled) values('[account_id]', '[enabled]')";
            int playerId = DAORegister.get().get(AccountDAO.class).getDatabasePlayerId(username);
            if (playerId > 0) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("account_id", playerId);
                params.put("enabled", 0); //disabled by default
                logger.debug("addPlayer is using query: " + query);
                transaction.execute(query, params);
            } else {
                throw new SQLException("Player: " + username + " doesn't exist on the database.");
            }
        } catch (SQLException e) {
            logger.error("Can't add player \"" + username + "\" to database", e);
            throw e;
        }
    }

    public void removePlayer(DBTransaction transaction, String username) throws SQLException {
        try {
            String query = "delete from monitor where account_id = '[account_id]')";
            int playerId = DAORegister.get().get(AccountDAO.class).getDatabasePlayerId(username);
            if (playerId > 0) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("account_id", playerId);
                logger.debug("removePlayer is using query: " + query);
                transaction.execute(query, params);
            } else {
                throw new SQLException("Player: " + username + " doesn't exist on the database.");
            }
        } catch (SQLException e) {
            logger.error("Can't remove player \"" + username + "\" from database", e);
            throw e;
        }
    }

    public boolean isMonitor(String username) {
        final DBTransaction transaction = TransactionPool.get().beginWork();
        boolean valid=false;
        try {
            String query = "select id from monitor where account_id= '[account_id]'";
            int playerId = DAORegister.get().get(AccountDAO.class).getDatabasePlayerId(username);
            if (playerId > 0) {

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("account_id", playerId);
                ResultSet result = transaction.query(query, params);
                valid = result.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MonitorDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                TransactionPool.get().commit(transaction);
            } catch (SQLException ex) {
                Logger.getLogger(MonitorDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return valid;
    }

    public void setEnabled(ClientObjectInterface monitor, boolean enabled) throws SQLException {
        try {
            String query = "update monitor set enabled = '[enabled]' "
                    + "where account_id = '[account_id]'";
            int playerId = DAORegister.get().get(AccountDAO.class).getDatabasePlayerId(monitor.getName());
            if (playerId > 0) {
                DBTransaction transaction = TransactionPool.get().beginWork();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("account_id", playerId);
                params.put("enabled", enabled ? 1 : 0);
                logger.debug("setEnabled is using query: " + query);
                transaction.execute(query, params);
                logger.debug(monitor.getName() + " got " + (enabled ? "enabled" : "disabled") + "!");
                if (enabled && !monitors.contains(monitor)) {
                    getMonitors().add(monitor);
                } else {
                    if (getMonitors().contains(monitor)) {
                        getMonitors().remove(monitor);
                    }
                }
            } else {
                throw new SQLException("Player: " + monitor.getName() + " doesn't exist on the database.");
            }
        } catch (SQLException e) {
            logger.error("Can't enable/disable player \"" + monitor.getName() + "\" to database", e);
            throw e;
        }
    }

    /**
     * @return the monitors
     */
    public ArrayList<ClientObjectInterface> getMonitors() {
        return monitors;
    }
}
