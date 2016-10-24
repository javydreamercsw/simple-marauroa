package simple.server.core.entity.api;

import marauroa.common.game.RPEvent;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 * @param <T> RPEvent being listened to.
 */
public interface RPEventListener<T extends RPEvent> {

    /**
     * Notification of an event occurring that we are monitoring.
     *
     * @param event Event that occurred.
     */
    public void onRPEvent(T event);
}
