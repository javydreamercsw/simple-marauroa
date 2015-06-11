package simple.server.core.action;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import simple.server.core.event.TurnListener;

/**
 * Delays performing actions until later.
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class DelayedAction implements TurnListener {

    private Action action;
    private static final Logger logger =
            Logger.getLogger(DelayedAction.class.getSimpleName());

    public DelayedAction(Action action) {
        this.action = action;
    }

    @Override
    public void onTurnReached(int currentTurn) {
        if(action !=null){
            //Perform the action
            logger.log(Level.FINE, "Executing action");
            action.actionPerformed(null);
        }
    }
}
