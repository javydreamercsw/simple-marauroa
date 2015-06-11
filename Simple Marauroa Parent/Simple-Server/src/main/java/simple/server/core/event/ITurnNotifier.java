/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package simple.server.core.event;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ITurnNotifier {

    /**
     * Forgets all registered notification entries for the given TurnListener
     * where the entry's message equals the given one.
     *
     * @param turnListener
     */
    void dontNotify(TurnListener turnListener);

    /**
     * Returns the current turn. Note this is only for debugging TurnNotifier
     *
     * @return current turn
     */
    int getCurrentTurnForDebugging();

    /**
     * Returns the list of events. Note this is only for debugging the
     * TurnNotifier
     *
     * @return eventList
     */
    Map<Integer, Set<TurnListener>> getEventListForDebugging();

    /**
     * Return the number of the next turn.
     *
     * @return number of the next turn
     */
    int getNumberOfNextTurn();

    /**
     * Finds out how many seconds will pass until the given TurnListener will be
     * notified with the given message.
     *
     * @param turnListener
     * @return the number of remaining seconds, or -1 if the given TurnListener
     * will not be notified with the given message.
     */
    int getRemainingSeconds(TurnListener turnListener);

    /**
     * Finds out how many turns will pass until the given TurnListener will be
     * notified with the given message.
     *
     * @param turnListener
     * @return the number of remaining turns, or -1 if the given TurnListener
     * will not be notified with the given message.
     */
    int getRemainingTurns(TurnListener turnListener);

    /**
     * This method is invoked by SimpleRPRuleProcessor.endTurn().
     *
     * @param currentTurn
     * currentTurn
     */
    void logic(int currentTurn);

    /**
     * Notifies the <i>turnListener</i> at turn number <i>turn</i>.
     *
     * @param turn
     * the number of the turn
     * @param turnListener
     * the object to notify
     */
    void notifyAtTurn(int turn, TurnListener turnListener);

    /**
     * Notifies the <i>turnListener</i> in <i>sec</i> seconds.
     *
     * @param sec
     * the number of seconds to wait before notifying
     * @param turnListener
     * the object to notify
     */
    void notifyInSeconds(int sec, TurnListener turnListener);

    /**
     * Notifies the <i>turnListener</i> in <i>diff</i> turns.
     *
     * @param diff
     * the number of turns to wait before notifying
     * @param turnListener
     * the object to notify
     */
    void notifyInTurns(int diff, TurnListener turnListener);
    
}
