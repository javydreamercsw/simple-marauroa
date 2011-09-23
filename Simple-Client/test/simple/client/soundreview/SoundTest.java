package simple.client.soundreview;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import marauroa.common.Log4J;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import simple.client.entity.User;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class SoundTest {

    /**
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Log4J.init();
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        User.setNull();
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     *
     */
    @Test
    public void testSoundStringIntInt() {
        new Sound("bla", 0, 0);
    }

    /**
     *
     */
    @Test
    public void testSoundStringIntIntBoolean() {
        new Sound("bla", 0, 0, true);

    }

    /**
     *
     */
    @Test
    public void testPlay() {
        final SoundMaster sm = new SoundMaster();
        sm.init();
        Sound valid = new Sound("chicken-mix", 0, 0);
        assertNull(valid.play());
        valid = new Sound("crowd-mix", 1, 1);
        assertNotNull("this sound exists", valid);
        new User();
        assertNotNull(valid.play());
        final Sound invalid = new Sound("bla", 1, 1);
        assertNull(invalid.play());
    }
}
