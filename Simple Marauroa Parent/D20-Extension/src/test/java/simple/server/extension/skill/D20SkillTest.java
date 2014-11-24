package simple.server.extension.skill;

import marauroa.common.game.Definition;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.DummySkill;
import simple.server.extension.DummyAbility;
import simple.server.extension.DummyAbility2;
import simple.server.extension.ability.AbstractAbility;
import simple.server.extension.ability.D20Ability;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class D20SkillTest {

    public D20SkillTest() {
    }

    /**
     * Test of isModifiesAttribute method, of class iD20Ability.
     */
    @Test
    public void testIsModifiesAttribute() {
        System.out.println("isModifiesAttribute");
        DummySkill dq = new DummySkill();
        assertTrue(dq.isModifiesAttribute(DummyAbility.class));
        assertTrue(dq.isModifiesAttribute(DummyAbility2.class));
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
        assertEquals(dq.getModifier(DummyAbility.class), 1);
        assertTrue(dq.getModifier(DummyAbility2.class) > 1);
    }

    /**
     * Test of getAbility method, of class D20Skill.
     */
    @Test
    public void testGetAbility() {
        System.out.println("getAbility");
        DummySkill ds = new DummySkill();
        assertEquals(DummyAbility.class, ds.getAbility());
    }

    /**
     * Test of Rank methods, of class D20Skill.
     */
    @Test
    public void testRank() {
        System.out.println("Rank");
        DummySkill ds = new DummySkill();
        assertEquals(0, ds.getRank());
        ds.setRank(1);
        assertEquals(1, ds.getRank());
    }

    @ServiceProvider(service = D20Ability.class)
    public static class iD20AttributeImpl extends AbstractAbility {

        @Override
        public String getName() {
            return "Invalid";
        }

        @Override
        public String getShortName() {
            return "IV";
        }

        @Override
        public String getDescription() {
            return "Dummy";
        }

        @Override
        public Definition.Type getDefinitionType() {
            return Definition.Type.INT;
        }
    }
}
