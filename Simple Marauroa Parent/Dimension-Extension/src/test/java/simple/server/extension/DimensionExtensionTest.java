package simple.server.extension;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.mock.MockSimpleRPWorld;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DimensionExtensionTest {

    private static final Logger LOG
            = Logger.getLogger(DimensionExtensionTest.class.getName());

    public DimensionExtensionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        Log4J.init();

        MockSimpleRPWorld.get();

        for (RPEntityInterface entity
                : Lookup.getDefault().lookupAll(RPEntityInterface.class)) {
            LOG.log(Level.FINE, "Registering RPEntity: {0}",
                    entity.getClass().getSimpleName());
            entity.generateRPClass();
        }
    }

    /**
     * Test of modifyRootRPClassDefinition method, of class DimensionExtension.
     */
    @Test
    public void testDimensionExtension() {
        System.out.println("RPClass Definition Test");
        Entity e = new Entity(new RPObject("Test"));
        assertTrue(e.has(DimensionExtension.X));
        assertTrue(e.has(DimensionExtension.Y));
        assertTrue(e.has(DimensionExtension.Z));
        assertTrue(e.has(DimensionExtension.HEIGHT));
        assertTrue(e.has(DimensionExtension.RESISTANCE));
        assertTrue(e.has(DimensionExtension.VISIBILITY));
        assertTrue(e.has(DimensionExtension.WIDTH));
    }
}
