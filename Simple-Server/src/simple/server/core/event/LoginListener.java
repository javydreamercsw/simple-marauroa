/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.event;

import simple.common.game.ClientObjectInterface;

/**
 * Implementing classes can be notified that a player has logged in.
 * 
 * After registering at the LoginNotifier, the LoginNotifier will notify it
 * about each player who logs in.
 * 
 * It is the responsibility of the LoginListener to determine which players are
 * of interest for it, and to store this information persistently.
 * 
 * @author daniel
 */
public interface LoginListener {

    /**
     * Is called when the turn number is reached.
     *
     * @param player
     *            the player who has logged in
     */
    void onLoggedIn(ClientObjectInterface player);
}
