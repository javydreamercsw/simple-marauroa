package simple.server.extension.d20.saving_throw;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import simple.server.extension.d20.DummyAbility;
import simple.server.extension.d20.ability.D20Ability;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AbstractSavingThrowTest {

    public AbstractSavingThrowTest() {
    }

    /**
     * Test of getBaseScore method, of class AbstractSavingThrow.
     */
    @Test
    public void testGetBaseScore() {
        System.out.println("getBaseScore");
        AbstractSavingThrow instance = new AbstractSavingThrowImpl();
        int expResult = 10;
        int result = instance.getBaseScore();
        assertEquals(expResult, result);
    }

    /**
     * Test of setBaseScore method, of class AbstractSavingThrow.
     */
    @Test
    public void testSetBaseScore() {
        System.out.println("setBaseScore");
        int score = 20;
        AbstractSavingThrow instance = new AbstractSavingThrowImpl();
        instance.setBaseScore(score);
        assertEquals(score, instance.getBaseScore());
    }

    /**
     * Test of getMiscMod method, of class AbstractSavingThrow.
     */
    @Test
    public void testGetMiscMod() {
        System.out.println("getMiscMod");
        AbstractSavingThrow instance = new AbstractSavingThrowImpl();
        int expResult = 0;
        int result = instance.getMiscMod();
        assertEquals(expResult, result);
    }

    /**
     * Test of setMiscMod method, of class AbstractSavingThrow.
     */
    @Test
    public void testSetMiscMod() {
        System.out.println("setMiscMod");
        int score = 10;
        AbstractSavingThrow instance = new AbstractSavingThrowImpl();
        instance.setMiscMod(score);
        assertEquals(score, instance.getMiscMod());
    }

    /**
     * Test of getScore method, of class AbstractSavingThrow.
     */
    @Test
    public void testGetScore() {
        System.out.println("getScore");
        AbstractSavingThrow instance = new AbstractSavingThrowImpl();
        Random r = new Random();
        instance.setBaseScore(r.nextInt(10) + 1);
        instance.setMiscMod(r.nextInt(10) + 1);
        int result = instance.getScore();
        int calculated =  instance.getBaseScore()
                + instance.getMiscMod();
        assertEquals(calculated, result);
    }

    public class AbstractSavingThrowImpl extends AbstractSavingThrow {

        @Override
        public Class<? extends D20Ability> getAbility() {
            return DummyAbility.class;
        }

        @Override
        public String getName() {
            return "Test Save";
        }

        @Override
        public String getShortName() {
            return "Test Save";
        }

        @Override
        public String getDescription() {
            return "Dummy";
        }
    }
}
