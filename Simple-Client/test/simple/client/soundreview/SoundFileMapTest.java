package simple.client.soundreview;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class SoundFileMapTest {

    /**
     *
     */
    @Test
    public void testIsNull() {
        final SoundFileMap sfm = new SoundFileMap();
        assertTrue(sfm.isNull());
        sfm.put("test", new byte[0]);
        assertFalse(sfm.isNull());
        assertNotNull(sfm.get("test"));
    }
}
