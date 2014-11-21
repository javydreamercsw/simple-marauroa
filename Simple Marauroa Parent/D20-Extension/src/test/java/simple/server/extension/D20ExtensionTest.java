package simple.server.extension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Log4J;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.extension.attribute.D20Attribute;
import simple.server.extension.attribute.D20List;
import simple.server.extension.attribute.D20Race;
import simple.server.extension.attribute.D20Stat;
import simple.server.mock.MockSimpleRPWorld;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class D20ExtensionTest {

    private static final Logger LOG
            = Logger.getLogger(D20ExtensionTest.class.getName());

    public D20ExtensionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        Log4J.init();

        MockSimpleRPWorld.get();

        Lookup.getDefault().lookupAll(RPEntityInterface.class)
                .stream().forEach((entity) -> {
                    LOG.log(Level.FINE, "Registering RPEntity: {0}",
                            entity.getClass().getSimpleName());
                    entity.generateRPClass();
                });
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of modifyRootRPClassDefinition method, of class D20Extension.
     */
    @Test
    public void testRPClassDefinition() {
        try {
            System.out.println("RPClass Definition Test");
            RPClass entity = new RPClass("Test");
            entity.isA(DummyRace.class.newInstance().getRPClassName());
            //Check all classes are defined properly
            int count = 0;
            Collection<? extends D20Race> races
                    = Lookup.getDefault().lookupAll(D20Race.class);
            for (D20Race r : races) {
                try {
                    System.out.println("Checking class: " + r.getClass().getSimpleName());
                    assertTrue(RPClass.hasRPClass(((Entity) r).getRPClassName()));
                    //Check class
                    //Attributes
                    Constructor<?> cons = r.getClass().getConstructor(RPObject.class);
                    RPObject test = (RPObject) cons.newInstance(new RPObject());
                    test.setRPClass(RPClass.getRPClass(((Entity) r).getRPClassName()));
                    assertTrue(test.instanceOf(RPClass.getRPClass(((Entity) r).getRPClassName())));
                    Lookup.getDefault().lookupAll(D20Attribute.class).stream().map((attr) -> {
                        System.out.println(attr.getName() + ": " + test.get(attr.getName()));
                        return attr;
                    }).forEach((attr) -> {
                        assertTrue(test.has(attr.getName()));
                    });
                    //Stats
                    Lookup.getDefault().lookupAll(D20Stat.class).stream().map((stat) -> {
                        assertTrue(test.has(stat.getName()));
                        return stat;
                    }).forEach((stat) -> {
                        System.out.println(stat.getName() + ": " + test.get(stat.getName()));
                    });
                    //Other attributes
                    Lookup.getDefault().lookupAll(D20List.class).stream().map((attr) -> {
                        assertTrue(test.hasSlot(attr.getName()));
                        return attr;
                    }).forEach((attr) -> {
                        System.out.println(attr.getName() + ": " + test.get(attr.getName()));
                    });
                    System.out.println(r.toString());
                    count++;
                } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    fail();
                }
            }
            if (count == 0) {
                fail("Found no wrestler classes!");
            }
            assertEquals(races.size(), count);
        } catch (InstantiationException | IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
