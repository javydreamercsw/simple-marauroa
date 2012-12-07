package simple.server.extension;

import gmgen.plugin.DieEx;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;

/**
 * Simulates a dice roll
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IDiceRoller.class)
public class DiceRoller implements IDiceRoller {

    private static final Logger logger = Logger.getLogger(DiceRoller.class.getSimpleName());
    private static final Level level = Level.FINE;

    public DiceRoller() {
    }

    @Override
    public int roll(String diceExp) {
        //Make sure there's an space between operators
        diceExp = diceExp.replaceAll("\\+", " + ").replaceAll("-", " - ");
        logger.log(level, "Expression: {0}", diceExp);
        int result = 0;
        String[] tokens = diceExp.split("\\s");
        String operator = null;
        for (String token : tokens) {
            String nextToken = token.trim().toLowerCase();
            if (nextToken.contains("d")) {
                //Is a dice expresion (i.e. 3d12)
                result += new DieEx(nextToken).roll();
            } else if (nextToken.equals("+") || nextToken.equals("-")) {
                operator = nextToken;
            } else {
                //It should be an integer
                float toAdd = Float.valueOf(nextToken);
                if (operator != null && operator.equals("+")) {
                    logger.log(level, "Adding {0} to the result...", toAdd);
                    result += toAdd;
                } else if (operator != null && operator.equals("-")) {
                    logger.log(level, "Substracting {0} to the result...", toAdd);
                    result -= toAdd;
                }
            }
        }
        logger.log(level, "Final Result: {0}", result);
        return result;
    }
}
