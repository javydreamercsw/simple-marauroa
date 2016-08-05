package simple.server.extension.d20.check;

import marauroa.common.game.RPObject;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.dice.RollResult;
import simple.server.extension.d20.rpclass.AbstractClass;
import simple.test.AbstractSystemTest;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class D20CheckTest extends AbstractSystemTest {

    /**
     * Test of getAbilities method, of class D20Check.
     */
    @Test
    public void testGetAbilities() {
        System.out.println("getAbilities");
        for (D20Check check : Lookup.getDefault().lookupAll(D20Check.class)) {
            //All must have at least one related ability.
            assertTrue(check.getAbilities().size() > 0);
        }
    }

    /**
     * Test of getCheckRoll method, of class D20Check.
     */
    @Test
    public void testGetCheckRoll() {
        System.out.println("getCheckRoll");
        DummyClass dummy;
        for (int i = 0; i < 100; i++) {
            dummy = new DummyClass();
            dummy.initialRolls();
            System.out.println(dummy);
            for (D20Check check : Lookup.getDefault().lookupAll(D20Check.class)) {
                int bonus = 0;
                for (Class<? extends D20Ability> a : check.getAbilities()) {
                    try {
                        D20Ability ability = a.newInstance();
                        System.out.println("Ability stat modifier: "
                                + dummy.getAbilityModifier(a)
                                + "(" + ability.getCharacteristicName() + ")");
                        bonus += dummy.getAbilityModifier(a);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        System.err.println(ex);
                        fail();
                    }
                }
                RollResult result = check.getCheckRoll(dummy);
                assertNotNull(result);
                assertTrue(result.getTotal() > bonus);
                System.out.println(check.getCharacteristicName() + " check: "
                        + result);
            }
        }
    }

    private class DummyClass extends AbstractClass {

        public DummyClass() {
            super(new RPObject());
        }

        @Override
        public String getHPDice() {
            return "d10";
        }

        @Override
        public String getCharacteristicName() {
            return "Test";
        }

        @Override
        public String getShortName() {
            return "Test";
        }
    }

    /**
     * Test of dieType method, of class D20Check.
     */
    @Test
    public void testDieType() {
        System.out.println("dieType");
        for (D20Check check : Lookup.getDefault().lookupAll(D20Check.class)) {
            assertTrue(check.dieType() > 0);
        }
    }
}
