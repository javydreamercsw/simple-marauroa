package simple.server.extension;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface ChallengeListener {

    /**
     * Challenge was rejected.
     *
     * @param rpo RPObject triggering the action.
     * @param action Event with more details.
     */
    public void challengeRejected(RPObject rpo, RPAction action);

    /**
     * Challenge was accepted.
     *
     * @param rpo RPObject triggering the action.
     * @param action Event with more details.
     */
    public void challengeAccepted(RPObject rpo, RPAction action);

    /**
     * Challenge was canceled.
     *
     * @param rpo RPObject triggering the action.
     * @param action Event with more details.
     */
    public void challengeCanceled(RPObject rpo, RPAction action);

    /**
     * Challenge was made.
     *
     * @param rpo RPObject triggering the action.
     * @param action Event with more details.
     */
    public void challengeMade(RPObject rpo, RPAction action);
}
