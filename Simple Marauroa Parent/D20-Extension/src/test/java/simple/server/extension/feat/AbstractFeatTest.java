package simple.server.extension.feat;

import org.junit.Test;
import static org.junit.Assert.*;
import simple.server.extension.DummySavingThrow;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AbstractFeatTest {

    public AbstractFeatTest() {
    }

    /**
     * Test of getBonus method, of class AbstractFeat.
     */
    @Test
    public void testGetBonus() {
        System.out.println("getBonus");
        DummyFeat instance = new DummyFeat();
        instance.bonus.put(DummySavingThrow.class, "1");
        assertEquals(1, instance.getBonus(DummySavingThrow.class));
        instance.bonus.clear();
        instance.bonus.put(DummySavingThrow.class, "2d+1");
        assertTrue(instance.getBonus(DummySavingThrow.class) > 1);
        instance.bonus.clear();
        assertEquals(0, instance.getBonus(DummySavingThrow.class));
    }
}
