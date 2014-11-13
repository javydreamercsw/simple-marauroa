package simple.server.extension;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class D20ExtensionTest {

    public D20ExtensionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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
    }

    /**
     * Test of rootRPClassUpdate method, of class D20Extension.
     */
    @Test
    public void testRootRPClassUpdate() {
        System.out.println("rootRPClassUpdate");
        
    }

    /**
     * Test of getName method, of class D20Extension.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        D20Extension instance = new D20Extension();
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
