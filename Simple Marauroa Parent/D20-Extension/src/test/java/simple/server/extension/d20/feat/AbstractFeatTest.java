package simple.server.extension.d20.feat;

import static org.junit.Assert.*;
import org.junit.Test;
import simple.server.extension.d20.DummySavingThrow;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class AbstractFeatTest {

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
        instance.bonus.put(DummySavingThrow.class, "2d1+1");
        assertTrue(instance.getBonus(DummySavingThrow.class) > 1);
        instance.bonus.clear();
        assertEquals(0, instance.getBonus(DummySavingThrow.class));
    }
}
