package net.sourceforge.javydreamercsw.buddy;

import marauroa.common.game.Definition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import simple.test.AbstractSystemTest;
import static simple.test.AbstractSystemTest.getTestPlayer;
import simple.test.TestPlayer;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class BuddyExtensionTest extends AbstractSystemTest {

    /**
     * Test of modifyClientObjectDefinition method, of class AdminExtension.
     */
    @Test
    public void testModifyClientObjectDefinition() {
        System.out.println("modifyClientObjectDefinition");
        TestPlayer client = getTestPlayer("Test");
        assertTrue(client.getRPClass()
                .hasDefinition(Definition.DefinitionClass.RPSLOT,
                        BuddyExtension.BUDDY));
        assertTrue(client.getRPClass()
                .hasDefinition(Definition.DefinitionClass.RPSLOT,
                        BuddyExtension.IGNORE));
        assertTrue(client.has(BuddyExtension.OFFLINE));
        assertTrue(client.has(BuddyExtension.ONLINE));
        client.update();
        assertEquals("", client.get(BuddyExtension.OFFLINE));
        assertEquals("", client.get(BuddyExtension.ONLINE));
    }
}
