package simple.extension.test;

import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import simple.SimpleServerT;
import static simple.SimpleServerT.init;
import static org.junit.Assert.*;
import simple.extension.DummyExtension;
import simple.server.core.engine.IRPObjectFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ExtensionTest extends SimpleServerT {

    @org.junit.Test
    public void testExtensionModifications() throws Exception {
        System.out.println("Test Extension Modifications");
        init();
        RPObject object =
                (RPObject) Lookup.getDefault()
                .lookup(IRPObjectFactory.class)
                .createDefaultClientObject("test");
        assertTrue(object.has(DummyExtension.testAttr1));
        assertEquals(1, object.getInt(DummyExtension.testAttr1));
        assertTrue(object.has(DummyExtension.testAttr2));
        assertEquals(1, object.getInt(DummyExtension.testAttr2));
        assertTrue(object.has(DummyExtension.testAttr3));
        assertEquals(1, object.getInt(DummyExtension.testAttr3));
    }
}
