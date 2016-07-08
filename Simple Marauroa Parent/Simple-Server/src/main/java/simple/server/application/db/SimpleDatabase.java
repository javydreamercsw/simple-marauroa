package simple.server.application.db;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IDatabase.class)
public class SimpleDatabase implements IDatabase {

    private static final Logger LOG
            = Logger.getLogger(SimpleDatabase.class.getSimpleName());
    private boolean initialized = false;

    @Override
    public void initialize() throws SQLException {
        if (!isInitialized()) {
            registerDAOs();
            initialized = true;
        }
    }

    protected void registerDAOs() {
        LOG.log(Level.FINE, "Loading DAOs from: {0}",
                getClass().getSimpleName());
        //Override DAO's here
        for (DAO dao : Lookup.getDefault().lookupAll(DAO.class)) {
            LOG.log(Level.FINE, "Registerig DAO: {0}",
                    dao.getClass().getSimpleName());
            dao.register();
            dao.init();
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
}
