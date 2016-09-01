package simple.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
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
    private static File ini = new File("server.ini");

    @BeforeClass
    public static void setup() throws Exception {
        try {
            if (!DB.isInitialized()) {
                LOG.log(Level.INFO, "Initializing test database environment...");
                checkINIFile();
                DB.initialize();
            }
            WORLD.onInit();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    private static void checkINIFile() {
        if (!ini.exists()) {
            try {
                LOG.warning("INI file not found, generating default for "
                        + "testing purposes!");
                ini.deleteOnExit();
                List<String> lines = Arrays.asList(
                        "database_implementation=simple.server.application.db.SimpleDatabase",
                        "factory_implementation=simple.server.core.engine.SimpleRPObjectFactory",
                        "world=simple.server.core.engine.SimpleRPWorld",
                        "ruleprocessor=simple.server.core.engine.SimpleRPRuleProcessor",
                        "client_object=simple.server.core.entity.clientobject.ClientObject",
                        "log4j_url=simple/server/log4j.properties",
                        "jdbc_url=jdbc\\:h2\\:./target/simple;CREATE=TRUE;AUTO_SERVER=TRUE;LOCK_TIMEOUT=10000;MVCC=true;DB_CLOSE_ON_EXIT=FALSE",
                        "jdbc_class=org.h2.Driver",
                        "database_adapter=marauroa.server.db.adapter.H2DatabaseAdapter",
                        "jdbc_user=simple_user",
                        "jdbc_pwd=password",
                        "tcp_port=32180",
                        "turn_length=100",
                        "server_typeGame=Simple",
                        "server_name=Simple",
                        "server_version = 0.02.07",
                        "statistics_filename=./simple.server_stats.xml",
                        "system_account_name=System",
                        "system_password=system",
                        "system_email=email@email.com",
                        "n = 24083767696329668268912537536174127468626867947407"
                        + "231757744234300439278504980856392206847956297"
                        + "47326949838501777926669337171495421818563824"
                        + "539329224927899179237",
                        "e = 15",
                        "d = 2247818318324102371765170170042918563738507675091"
                        + "3416307227952013743326604648798383322370040"
                        + "7625284965452796321477265264173527901632535"
                        + "4691167883850414929419335");
                Path file = Paths.get(ini.getAbsolutePath());
                Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    @Before
    public void prepare() throws Exception {
        cleanup();
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
        LOG.log(Level.INFO, "Cleaning test database environment...");
        //Remove players from zones
        for (SimpleRPZone zone : WORLD.getZones()) {
            WORLD.emptyZone(zone);
        }
        try {   //It's deleted on the initialization of the environemnt
            WORLD.createSystemAccount();
            //Reset database. This only works with H2
            Properties prop = new Properties();
            InputStream input = null;
            try {
                input = new FileInputStream(ini);
                // load a properties file
                prop.load(input);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        LOG.log(Level.SEVERE, null, e);
                    }
                }
            }
            Class.forName(prop.getProperty("jdbc_class"));
            try (Connection conn = DriverManager.
                    getConnection(prop.getProperty("jdbc_url"),
                            prop.getProperty("jdbc_user"),
                            prop.getProperty("jdbc_pwd"))) {
                Statement stat = conn.createStatement();
                switch (prop.getProperty("jdbc_class")) {
                    case "org.h2.Driver":
                        stat.execute("SET REFERENTIAL_INTEGRITY FALSE");
                        ResultSet rs = stat.executeQuery("SHOW TABLES");
                        List<String> tables = new ArrayList<>();
                        while (rs.next()) {
                            tables.add(rs.getString(1));
                        }
                        for (String table : tables) {
                            LOG.log(Level.INFO, "Truncating table: {0}", table);
                            stat.execute("TRUNCATE TABLE " + table);
                        }
                        stat.execute("SET REFERENTIAL_INTEGRITY TRUE");
                        break;
                    default:
                        LOG.log(Level.WARNING, "Unhandled data base type: {0}. "
                                + "Database was not cleared and might cause "
                                + "errors between tests.",
                                prop.getProperty("jdbc_class"));
                        break;
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException | SQLException | IOException ex) {
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
