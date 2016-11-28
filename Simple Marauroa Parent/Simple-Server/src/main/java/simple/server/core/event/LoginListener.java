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
     * @param player the player who has logged in
     */
    void onLoggedIn(ClientObjectInterface player);

    /**
     * Like isGagged(player) but informs the player in case it is gagged.
     *
     * @param player player to check
     * @return true, if it is gagged, false otherwise.
     */
    boolean checkIsGaggedAndInformPlayer(ClientObjectInterface player);

    /**
     * @param criminalName The name of the player who should be gagged
     * @param policeman The name of the admin who wants to gag the criminal
     * @param minutes The duration of the sentence
     * @param reason why criminal was gagged
     */
    void gag(final String criminalName, ClientObjectInterface policeman,
            int minutes, String reason);

    /**
     * Gets time remaining in milliseconds.
     *
     * @param criminal player to check
     * @return time remaining in milliseconds
     */
    long getTimeRemaining(ClientObjectInterface criminal);
}
