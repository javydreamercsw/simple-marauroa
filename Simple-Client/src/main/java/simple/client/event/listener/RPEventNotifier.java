package simple.client.event.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPEvent;

/**
 * Other classes can register here to be notified when certain RPEvent is
 * received.
 *
 * Based on TurnNotifier by hendrik, daniel
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RPEventNotifier {

    private static final Logger logger = Log4J.getLogger(RPEventNotifier.class);
    /**
     * The Singleton instance.
     */
    private static RPEventNotifier instance;
    /**
     * This HashMap maps each RPEvent to the set of all event listeners waiting
     * for this RPEvent. RPEvents with no listener shouldn't be registered here.
     */
    private HashMap<String, Set<RPEventListener>> register = new HashMap<String, Set<RPEventListener>>();
    /**
     * Used for multi-threading synchronization.
     */
    private final Object sync = new Object();

    private RPEventNotifier() {
        // singleton
    }

    /**
     * @return the instance
     */
    public static RPEventNotifier get() {
        if (instance == null) {
            instance = new RPEventNotifier();
        }
        return instance;
    }

    /**
     * Notifies the <i>eventListener</i> when RPEvent <i>event</i> is received.
     *
     * @param event the RPEvent that triggers the RPEventListener
     * @param eventListener the object to notify
     */
    public void notifyAtEvent(RPEvent event, RPEventListener eventListener) {
        logger.info("Notify when " + event.getClass().getSimpleName()
                + "(" + event.getName() + ")" + " is detected to " + eventListener);

        synchronized (sync) {
            // Do we have other listeners for this event?
            Set<RPEventListener> set = register.get(event.getName());
            if (set == null) {
                set = new HashSet<RPEventListener>();
                register.put(event.getName(), set);
            }
            // add it to the list
            set.add(eventListener);
        }
    }

    /**
     * This method is invoked by SimpleClient.handler.listener.onMyRPObject().
     *
     * @param events list of RPEvents received
     * @return Unprocessed RPEvents. Events without listeners registered.
     */
    public HashMap<RPEvent, Boolean> logic(List<RPEvent> events) {
        HashMap<RPEvent, Boolean> result = new HashMap<RPEvent, Boolean>();
        for (RPEvent event : events) {
            Set<RPEventListener> set = register.get(event.getName());

            if (logger.isDebugEnabled()) {
                StringBuilder os = new StringBuilder();
                os.append("event: ").append(event.getName()).append(", ");
                os.append("event contents: ").append(event).append(", ");
                os.append("registered listeners: ").append(set == null ? 0 : set.size());
                logger.info(os);
                System.out.println(os.toString());
            }

            if (set != null) {
                result.put(event, true);
                for (RPEventListener currentEvent : set) {
                    RPEventListener eventListener = currentEvent;
                    try {
                        if (logger.isDebugEnabled()) {
                            logger.debug(eventListener);
                        }
                        try {
                            eventListener.onRPEventReceived(event);
                        } catch (Exception ex) {
                            logger.fatal(null, ex);
                        }
                    } catch (RuntimeException e) {
                        logger.error(e, e);
                    }
                }
            } else {
                result.put(event, false);
            }
        }
        return result;
    }
}
