package simple.server.application.db;

import java.sql.SQLException;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.server.game.db.DatabaseFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SimpleDatabase {

    private static final Logger logger = Log4J.getLogger(SimpleDatabase.class);
    private static SimpleDatabase instance = null;

    public SimpleDatabase() {
        new DatabaseFactory().initializeDatabase();
    }

    public static SimpleDatabase get() {
        if (instance == null) {
            instance = new SimpleDatabase();
        }
        return instance;
    }

    public void initialize() throws SQLException{
        //Initialization made in JPADatabaseAdapter
        registerDAOs();
    }

    protected void registerDAOs() {
        logger.debug("Loading DAOs from: " + getClass().getSimpleName());
        //Override DAO's here
    }
}
