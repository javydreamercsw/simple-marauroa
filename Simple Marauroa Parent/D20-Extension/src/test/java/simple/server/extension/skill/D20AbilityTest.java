package simple.server.extension.skill;

import marauroa.common.game.Definition;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.DummySkill;
import simple.server.extension.DummyAttr;
import simple.server.extension.DummyAttr2;
import simple.server.extension.attribute.D20Attribute;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class D20AbilityTest {

    public D20AbilityTest() {
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
     * Test of isModifiesAttribute method, of class iD20Ability.
     */
    @Test
    public void testIsModifiesAttribute() {
        System.out.println("isModifiesAttribute");
        DummySkill dq = new DummySkill();
        assertTrue(dq.isModifiesAttribute(DummyAttr.class));
        assertTrue(dq.isModifiesAttribute(DummyAttr2.class));
        assertFalse(dq.isModifiesAttribute(iD20AttributeImpl.class));
    }

    /**
     * Test of getModifier method, of class iD20Ability.
     */
    @Test
    public void testGetModifier() {
        System.out.println("getModifier");
        DummySkill dq = new DummySkill();
        assertEquals(dq.getModifier(iD20AttributeImpl.class), 0);
        assertEquals(dq.getModifier(DummyAttr.class), 1);
        assertTrue(dq.getModifier(DummyAttr2.class) > 1);
    }

    @ServiceProvider(service = D20Attribute.class)
    public static class iD20AttributeImpl implements D20Attribute {

        @Override
        public String getName() {
            return "Invalid";
        }

        @Override
        public String getShortName() {
            return "IV";
        }

        @Override
        public int getAttributeMod() {
            return 0;
        }

        @Override
        public String getDefaultValue() {
            return "0";
        }

        @Override
        public Definition.Type getDefinitionType() {
            return Definition.Type.INT;
        }
    }

}
