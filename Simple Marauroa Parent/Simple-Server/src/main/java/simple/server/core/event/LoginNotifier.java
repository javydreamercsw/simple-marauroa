package simple.server.core.event;

import java.util.ArrayList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.entity.RPEntityInterface;

/**
 * Other classes can register here to be notified when a player logs in.
 *
 * It is the responsibility of the LoginListener to determine which players are
 * of interest for it, and to store this information persistently.
 *
 * @author daniel
 */
@ServiceProvider(service = ILoginNotifier.class)
public final class LoginNotifier implements ILoginNotifier {

    /**
     * Holds a list of all registered listeners.
     */
    private final List<LoginListener> listeners = new ArrayList<>();

    public LoginNotifier() {
    }

    /**
     * Adds a LoginListener.
     *
     * @param listener LoginListener to add
     */
    @Override
    public void addListener(LoginListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a LoginListener.
     *
     * @param listener LoginListener to remove
     */
    @Override
    public void removeListener(LoginListener listener) {
        listeners.remove(listener);
    }

    /**
     * This method is invoked by ClientObjectInterface.create().
     *
     * @param player the player who logged in
     */
    @Override
    public void onPlayerLoggedIn(RPEntityInterface player) {
        listeners.forEach((listener) -> {
            listener.onLoggedIn(player);
        });
    }
}
