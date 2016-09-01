package simple.server.extension;

import java.util.logging.Logger;
import marauroa.common.game.RPObject;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import simple.server.core.entity.Entity;
import simple.test.AbstractSystemTest;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DimensionExtensionTest extends AbstractSystemTest {

    private static final Logger LOG
            = Logger.getLogger(DimensionExtensionTest.class.getName());

    public DimensionExtensionTest() {
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
