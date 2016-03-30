package simple.server.core.event;

import simple.common.game.ClientObjectInterface;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ILoginNotifier {

    /**
     * Adds a LoginListener.
     *
     * @param listener
     * LoginListener to add
     */
    void addListener(LoginListener listener);

    /**
     * This method is invoked by ClientObjectInterface.create().
     *
     * @param player
     * the player who logged in
     */
    void onPlayerLoggedIn(ClientObjectInterface player);

    /**
     * Removes a LoginListener.
     *
     * @param listener
     * LoginListener to remove
     */
    void removeListener(LoginListener listener);
    
}
