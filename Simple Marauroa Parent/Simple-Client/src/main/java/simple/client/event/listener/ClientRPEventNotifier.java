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
public final class ClientRPEventNotifier {

    private static final Logger LOG
            = Log4J.getLogger(ClientRPEventNotifier.class);
    /**
     * The Singleton instance.
     */
    private static ClientRPEventNotifier instance;
    /**
     * This HashMap maps each RPEvent to the set of all event listeners waiting
     * for this RPEvent. RPEvents with no listener shouldn't be registered here.
     */
    private HashMap<String, Set<ClientRPEventListener>> register = new HashMap<>();
    /**
     * Used for multi-threading synchronization.
     */
    private final Object sync = new Object();

    private ClientRPEventNotifier() {
        // singleton
    }

    /**
     * @return the instance
     */
    public static ClientRPEventNotifier get() {
        if (instance == null) {
            instance = new ClientRPEventNotifier();
        }
        return instance;
    }

    /**
     * Notifies the <i>eventListener</i> when RPEvent <i>event</i> is received.
     *
     * @param event the RPEvent that triggers the ClientRPEventListener
     * @param eventListener the object to notify
     */
    public void notifyAtEvent(Class<? extends RPEvent> event,
            ClientRPEventListener eventListener) {
        LOG.info("Notify when " + event.getClass().getSimpleName()
                + "(" + event.getName() + ")" + " is detected to " + eventListener);

        synchronized (sync) {
            // Do we have other listeners for this event?
            Set<ClientRPEventListener> set = register.get(event.getName());
            if (set == null) {
                set = new HashSet<>();
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
        HashMap<RPEvent, Boolean> result = new HashMap<>();
        for (RPEvent event : events) {
            Set<ClientRPEventListener> set = register.get(event.getName());

            if (LOG.isDebugEnabled()) {
                StringBuilder os = new StringBuilder();
                os.append("event: ").append(event.getName()).append(", ");
                os.append("event contents: ").append(event).append(", ");
                os.append("registered listeners: ").append(set == null ? 0 : set.size());
                LOG.info(os);
                System.out.println(os.toString());
            }

            if (set != null) {
                result.put(event, true);
                for (ClientRPEventListener currentEvent : set) {
                    ClientRPEventListener eventListener = currentEvent;
                    try {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(eventListener);
                        }
                        try {
                            eventListener.onRPEventReceived(event);
                        } catch (Exception ex) {
                            LOG.fatal(null, ex);
                        }
                    } catch (RuntimeException e) {
                        LOG.error(e, e);
                    }
                }
            } else {
                result.put(event, false);
            }
        }
        return result;
    }
}
