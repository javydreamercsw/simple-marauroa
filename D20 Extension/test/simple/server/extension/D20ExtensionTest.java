package simple.server.extension;

import marauroa.common.game.RPObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.*;
import org.openide.util.Lookup;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.clientobject.ClientObject;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class D20ExtensionTest {

    public D20ExtensionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
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
    public void testCustomDefinition() {
        System.out.println("modifyRootRPClassDefinition");
        for (RPEntityInterface entity : Lookup.getDefault().lookupAll(RPEntityInterface.class)) {
            entity.generateRPClass();
        }
        //Lower level
        RPEntity entity = new RPEntity(new RPObject());
        assertTrue(entity.has(D20Extension.AC));
        assertEquals(entity.getInt(D20Extension.AC),0);
        //Client level
        ClientObject co = new ClientObject(new RPObject());
        assertTrue(co.has(D20Extension.AC));
        assertEquals(co.getInt(D20Extension.AC),0);
    }
}