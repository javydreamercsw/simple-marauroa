package simple.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openide.util.Lookup;
import simple.server.application.db.DAO;
import simple.server.application.db.IDatabase;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.clientobject.ClientObject;
import simple.server.extension.MarauroaServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public abstract class AbstractSystemTest {

    private static final Logger LOG
            = Logger.getLogger(AbstractSystemTest.class.getSimpleName());
    private static final IRPWorld WORLD = Lookup.getDefault().lookup(IRPWorld.class);
    private static final IDatabase DB = Lookup.getDefault().lookup(IDatabase.class);

    @BeforeClass
    public static void setup() throws Exception {
        try {
            if (!DB.isInitialized()) {
                LOG.log(Level.INFO, "Initializing test database environment...");
                DB.initialize();
            }
            WORLD.onInit();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Before
    public void prepare() throws Exception {
        //Run all after init actions on extensions in case they create something.
        for (MarauroaServerExtension ext : Lookup.getDefault()
                .lookupAll(MarauroaServerExtension.class)) {
            ext.afterWorldInit();
        }
        //Load all DAO's for the instances there are some relationships between them.
        for (DAO dao : Lookup.getDefault().lookupAll(DAO.class)) {
            dao.register();
            dao.init();
        }
        LOG.log(Level.INFO, "Done!");
    }

    @After
    public void cleanup() {
        try {
            LOG.log(Level.INFO, "Cleaning test database environment...");
            //Remove players from zones
            for (SimpleRPZone zone : WORLD.getZones()) {
                WORLD.emptyZone(zone);
            }
            //It's deleted on the initialization of the environemnt
            WORLD.createSystemAccount();
        }
        catch (SQLException | IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public RPObject createObject(String name) {
        LOG.log(Level.INFO, "Creating object: {0}", name);
        RPObject object = new RPObject(name);
        object.setRPClass(ClientObject.DEFAULT_RP_CLASSNAME);
        object.put(Entity.NAME, name);
        object.put("zoneid",
                Lookup.getDefault()
                .lookup(IRPWorld.class).getDefaultZone().getID().getID());
        return object;
    }

    public IRPZone createZone(String test) {
        WORLD.addZone(test);
        return WORLD.getRPZone(test);
    }

    public static TestPlayer getTestPlayer(String name) {
        System.out.println("Setting up test player " + name);
        RPObject obj = new RPObject();
        obj.setRPClass("test player");
        obj.put(ClientObject.KEY, "AbCdEfG");
        obj.put(RPEntity.NAME, name);
        TestPlayer player = new TestPlayer(obj);
        System.out.println("Done!");
        return player;
    }
}
