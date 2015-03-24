package simple.server.extension.d20;

import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import simple.server.extension.d20.dice.DiceParser;
import simple.server.extension.d20.dice.DieRoll;
import simple.server.extension.d20.dice.RollResult;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DiceTest {

    public DiceTest() {
    }

    /**
     * Test of roll method, of class Dice.
     */
    @Test
    public void testRoll2() {
        System.out.println("roll 2");
        Random r = new Random();
        int num;
        int sides;
        int mod;
        for (int i = 0; i < 10; i++) {
            num = r.nextInt(5) + 1;
            sides = r.nextInt(20) + 1;
            //Random # between -5 and 5
            mod = r.nextInt(10) - 5;
            String eq = num + "d" + sides + (mod > 0 ? "+" : "")
                    + (mod == 0 ? "" : mod);
            List<DieRoll> parseRoll = DiceParser.parseRoll(eq);
            System.out.println("Results for " + eq + ":");
            for (i = 0; i < parseRoll.size(); i++) {
                DieRoll dr = parseRoll.get(i);
                System.out.print(parseRoll.get(i));
                System.out.print(": ");
                RollResult roll = dr.makeRoll();
                System.out.println(roll);
                int result = roll.getTotal();
                assertTrue(result > mod);
                assertTrue(result <= num * sides + mod);
            }
        }
    }
}
