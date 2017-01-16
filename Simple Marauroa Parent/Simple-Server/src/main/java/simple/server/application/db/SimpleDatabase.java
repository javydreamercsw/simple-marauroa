package simple.server.application.db;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.server.db.DatabaseConnectionException;
import marauroa.server.game.db.DatabaseFactory;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IDatabase.class)
public class SimpleDatabase extends AbstractDatabase implements IDatabase {

    private static final Logger LOG
            = Logger.getLogger(SimpleDatabase.class.getSimpleName());
    private boolean initialized = false;

    @Override
    public void initialize() throws DatabaseConnectionException {
        if (!isInitialized()) {
            try {
                //Connect to database
                if (getPersistenceUnitName() != null) {
                    getEntityManagerFactory();
                } else {
                    //Do it the old way.
                    new DatabaseFactory().initializeDatabase();
                }
                registerDAOs();
                initialized = true;
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    protected void registerDAOs() {
        LOG.log(Level.FINE, "Loading DAOs from: {0}",
                getClass().getSimpleName());
        //Override DAO's here
        Lookup.getDefault().lookupAll(DAO.class).stream().map((dao) -> {
            LOG.log(Level.FINE, "Registerig DAO: {0}",
                    dao.getClass().getSimpleName());
            return dao;
        }).map((dao) -> {
            dao.register();
            return dao;
        }).forEachOrdered((dao) -> {
            dao.init();
        });
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
}
