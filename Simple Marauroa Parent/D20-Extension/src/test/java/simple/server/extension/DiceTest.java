package simple.server.extension;

import simple.server.extension.DieEx;
import simple.server.extension.Dice;
import simple.server.extension.Die;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DiceTest {

    public DiceTest() {
    }

    /**
     * Test of roll method, of class Dice.
     */
    @Test
    public void testRoll() {
        System.out.println("roll");
        Random r = new Random();
        int num;
        int sides;
        int mod;
        for (int i = 0; i < 100; i++) {
            num = r.nextInt(5) + 1;
            sides = r.nextInt(20) + 1;
            mod = Die.randInt(-5, 5);
            Dice instance = new Dice(num, sides, mod);
            int result = instance.roll();
            assertTrue(result > mod);
            assertTrue(result <= num * sides + mod);
            String eq = num + "d" + sides + (mod > 0 ? "+" : "") 
                    + (mod == 0 ? "" : mod);
            System.out.println(eq + " = " + result);
            assertEquals(eq, instance.toString());
            //Reverse parse
            DieEx dieEx = new DieEx(eq);
            assertEquals(eq, dieEx.toString());
        }
    }
}
