package simple.server.application.db;

import java.sql.SQLException;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.server.game.db.DatabaseFactory;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IDatabase.class)
public class SimpleDatabase implements IDatabase {

    private static final Logger LOG = Log4J.getLogger(SimpleDatabase.class);
    private boolean initialized = false;

    @Override
    public void initialize() throws SQLException {
        if (!isInitialized()) {
            new DatabaseFactory().initializeDatabase();
            registerDAOs();
            initialized = true;
        }
    }

    protected void registerDAOs() {
        LOG.debug("Loading DAOs from: " + getClass().getSimpleName());
        //Override DAO's here
        for (DAO dao : Lookup.getDefault().lookupAll(DAO.class)) {
            LOG.debug("Registerig DAO: " + dao.getClass().getSimpleName());
            dao.register();
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
}
