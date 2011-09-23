/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.event;

import simple.common.game.ClientObjectInterface;
import java.util.ArrayList;
import java.util.List;

/**
 * Other classes can register here to be notified when a player logs in.
 * 
 * It is the responsibility of the LoginListener to determine which players are
 * of interest for it, and to store this information persistently.
 * 
 * @author daniel
 */
public final class LoginNotifier {

    /** The Singleton instance. */
    private static LoginNotifier instance;
    /**
     * Holds a list of all registered listeners.
     */
    private List<LoginListener> listeners;

    // singleton
    private LoginNotifier() {
        listeners = new ArrayList<LoginListener>();
    }

    /**
     * Return the LoginNotifier instance.
     *
     * @return LoginNotifier the Singleton instance
     */
    public static LoginNotifier get() {
        if (instance == null) {
            instance = new LoginNotifier();
        }
        return instance;
    }

    /**
     * Adds a LoginListener.
     *
     * @param listener
     *            LoginListener to add
     */
    public void addListener(LoginListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a LoginListener.
     *
     * @param listener
     *            LoginListener to remove
     */
    public void removeListener(LoginListener listener) {
        listeners.remove(listener);
    }

    /**
     * This method is invoked by ClientObjectInterface.create().
     *
     * @param player
     *            the player who logged in
     */
    public void onPlayerLoggedIn(ClientObjectInterface player) {
        for (LoginListener listener : listeners) {
            listener.onLoggedIn(player);
        }
    }
}
