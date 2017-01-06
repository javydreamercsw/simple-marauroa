package net.sourceforge.javydreamercsw.admin;

import static org.junit.Assert.*;
import org.junit.Test;
import simple.test.AbstractSystemTest;
import simple.test.TestPlayer;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class AdminExtensionTest extends AbstractSystemTest {

    /**
     * Test of modifyClientObjectDefinition method, of class AdminExtension.
     */
    @Test
    public void testModifyClientObjectDefinition() {
        System.out.println("modifyClientObjectDefinition");
        TestPlayer client = getTestPlayer("Test");
        assertTrue(client.has(AdminExtension.ADMIN));
        assertTrue(client.has(AdminExtension.ADMIN_LEVEL));
        client.update();
        assertEquals(false, client.getBool(AdminExtension.ADMIN));
        assertEquals(-1, client.getInt(AdminExtension.ADMIN_LEVEL));
    }
}
