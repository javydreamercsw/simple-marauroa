package simple.client.entity;

import marauroa.common.game.RPEvent;
import simple.client.RPObjectChangeListener;
import simple.client.event.listener.RPEventListener;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IUserContext extends RPObjectChangeListener {

    /**
     * Get the administrator level.
     *
     * @return The administrator level.
     */
    int getAdminLevel();

    /**
     * Get the player character name.
     *
     * @return The player character name.
     */
    String getName();

    /**
     * Determine if the user is an admin.
     *
     * @return <code>true</code> is the user is an admin.
     */
    boolean isAdmin();

    /**
     * Register an RPEvent listener
     * @param event     event to listen for
     * @param listener  listener
     */
    void registerRPEventListener(Class<? extends RPEvent> event, RPEventListener listener);
}
