package simple.client.event.listener;

import marauroa.common.game.RPEvent;

/**
 * Implementing classes can be notified that a certain event has been
 * received.
 *
 * After registering at the RPEventNotifier, the RPEventNotifier will wait until the
 * specified RPEvent has been received, and notify the RPEventListener.
 *
 * Based on TurnListener by hendrik
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RPEventListener {

    /**
     * This method is called when the RPEvent is received.
     *
     * @param event
     *            event received
     */
    void onRPEventReceived(RPEvent event) throws Exception;
}
