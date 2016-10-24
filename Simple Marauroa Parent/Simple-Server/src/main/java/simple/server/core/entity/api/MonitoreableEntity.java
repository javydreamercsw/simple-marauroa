package simple.server.core.entity.api;

import marauroa.common.game.RPEvent;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface MonitoreableEntity extends RPObjectMonitor {

    /**
     * Register for a specific RPEvent for this object.
     *
     * @param eventClassName RPEvent class name
     * @param listener Listener to register.
     */
    public void registerListener(String eventClassName,
            RPEventListener listener);

    /**
     * Unregister for a specific RPEvent for this object.
     *
     * @param eventClassName RPEvent class name
     * @param listener Listener to unregister.
     */
    public void unregisterListener(String eventClassName,
            RPEventListener listener);

    /**
     * Let the class process on it's own.
     *
     * Class is responsible from adding the id to processedEvents if it's not
     * desired to be reprocessed.
     *
     * @param event Event to process.
     */
    public void processEvent(RPEvent event);

    /**
     * Marks an event as processed so it is not processed more than once.
     *
     * @param event event to mark as processed.
     */
    public void markEventAsProcessed(RPEvent event);

    /**
     * Check if this event was already processed.
     *
     * @param event Event to check.
     * @return true if already processed, false otherwise.
     */
    public boolean isAlreadyProcessed(RPEvent event);
}
