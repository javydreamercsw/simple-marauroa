package simple.server.extension;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Log4J;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Lookup;
import simple.common.SimpleException;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.clientobject.ClientObject;
import simple.server.extension.attribute.iD20Attribute;
import simple.server.extension.attribute.iD20Stat;
import simple.server.mock.MockSimpleRPWorld;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class D20ExtensionTest {

    private static final Logger LOG
            = Logger.getLogger(D20ExtensionTest.class.getName());

    public D20ExtensionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        Log4J.init();

        MockSimpleRPWorld.get();

        Lookup.getDefault().lookupAll(RPEntityInterface.class)
                .stream().forEach((entity) -> {
            entity.generateRPClass();
        });
    }

    @AfterClass
    public static void tearDownClass() {
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
    public void testModifyRootRPClassDefinition() {
        System.out.println("modifyRootRPClassDefinition");
        RPClass entity = new RPClass("Test");
        D20Extension instance = new D20Extension();
        instance.modifyRootRPClassDefinition(entity);
        //Make sure that the Attributes are added
        assertTrue(entity.hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                new DummyAttr().getName()));
        assertTrue(entity.hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                new DummyAttr2().getName()));
        //Make sure that the attribute lists are added
        assertTrue(entity.hasDefinition(Definition.DefinitionClass.RPSLOT,
                new DummyList().getName()));
        assertTrue(entity.hasDefinition(Definition.DefinitionClass.RPSLOT,
                new DummyList2().getName()));
        //Make sure stats are added
        assertTrue(entity.hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                new DummyStat().getName()));
    }

    /**
     * Test of rootRPClassUpdate method, of class D20Extension.
     */
    @Test
    public void testRootRPClassUpdate() {
        System.out.println("rootRPClassUpdate");
        ClientObject entity = new ClientObject(new RPObject("Test"));
        D20Extension instance = new D20Extension();
        try {
            instance.clientObjectUpdate(entity);
            int count = 0;
            for (iD20Attribute attr : 
                    Lookup.getDefault().lookupAll(iD20Attribute.class)) {
                count++;
                LOG.log(Level.INFO, "Checking default value for {0}", 
                        attr.getName());
                assertEquals(attr.getDefaultValue(), 
                        entity.getInt(attr.getName()));
            }
            if (count == 0) {
                fail("Found no Attributes");
            }
            count=0;
            for (iD20Stat stat : 
                    Lookup.getDefault().lookupAll(iD20Stat.class)) {
                count++;
                LOG.log(Level.INFO, "Checking default value for {0}", 
                        stat.getName());
                assertEquals(stat.getDefaultValue(), 
                        entity.getInt(stat.getName()));
            }
            if (count == 0) {
                fail("Found no Stats");
            }
        } catch (SimpleException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
