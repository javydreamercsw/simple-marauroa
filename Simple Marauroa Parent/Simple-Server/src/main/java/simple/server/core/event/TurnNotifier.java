package simple.server.core.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.SimpleRPWorld;

/**
 * Other classes can register here to be notified at some time in the future.
 *
 * @author hendrik, daniel
 */
@ServiceProvider(service = ITurnNotifier.class)
public final class TurnNotifier implements ITurnNotifier {

    private static final Logger LOG
            = Logger.getLogger(TurnNotifier.class.getSimpleName());
    private int currentTurn = -1;
    /**
     * This Map maps each turn to the set of all events that will take place at
     * this turn. Turns at which no event should take place needn't be
     * registered here.
     */
    private final Map<Integer, Set<TurnListener>> register = new HashMap<>();
    /**
     * Used for multi-threading synchronization. *
     */
    private final Object sync = new Object();

    public TurnNotifier() {
        // singleton
    }

    /**
     * This method is invoked by SimpleRPRuleProcessor.endTurn().
     *
     * @param currentTurn currentTurn
     */
    @Override
    public void logic(int currentTurn) {
        // Note: It is OK to only synchronise the remove part
        // because notifyAtTurn will not allow registrations
        // for the current turn. So it is important to
        // adjust currentTurn before the loop.

        this.currentTurn = currentTurn;

        // get and remove the set for this turn
        Set<TurnListener> set;
        synchronized (sync) {
            set = register.remove(currentTurn);
        }

        if (set != null) {
            set.stream().map((event) -> event)
                    .forEachOrdered((turnListener) -> {
                        try {
                            LOG.log(Level.FINE,
                                    "Processing turn listener: {0}",
                                    turnListener.getClass().getName());
                            turnListener.onTurnReached(currentTurn);
                        } catch (RuntimeException e) {
                            LOG.log(Level.SEVERE, null, e);
                        }
                    });
        }
    }

    /**
     * Return the number of the next turn.
     *
     * @return number of the next turn
     */
    @Override
    public int getNumberOfNextTurn() {
        return this.currentTurn + 1;
    }

    /**
     * Notifies the <i>turnListener</i> in <i>diff</i> turns.
     *
     * @param diff the number of turns to wait before notifying
     * @param turnListener the object to notify
     */
    @Override
    public void notifyInTurns(int diff, TurnListener turnListener) {
        notifyAtTurn(currentTurn + diff + 1, turnListener);
    }

    /**
     * Notifies the <i>turnListener</i> in <i>sec</i> seconds.
     *
     * @param sec the number of seconds to wait before notifying
     * @param turnListener the object to notify
     */
    @Override
    public void notifyInSeconds(int sec, TurnListener turnListener) {
        notifyInTurns(Lookup.getDefault().lookup(IRPWorld.class).getTurnsInSeconds(sec),
                turnListener);
    }

    /**
     * Notifies the <i>turnListener</i> at turn number <i>turn</i>.
     *
     * @param turn the number of the turn
     * @param turnListener the object to notify
     */
    @Override
    public void notifyAtTurn(int turn, TurnListener turnListener) {
        LOG.log(Level.FINE, "Notify at {0} by {1}",
                new Object[]{turn, turnListener.getClass().getName()});

        if (turn <= currentTurn) {
            LOG.log(Level.SEVERE, "requested turn " + turn
                    + " is in the past. Current turn is " + currentTurn,
                    new IllegalArgumentException("turn"));
            return;
        }

        synchronized (sync) {
            // do we have other events for this turn?
            Integer turnInt = turn;
            Set<TurnListener> set = register.get(turnInt);
            if (set == null) {
                set = new HashSet<>();
                register.put(turnInt, set);
            }
            // add it to the list
            set.add(turnListener);
        }
    }

    /**
     * Forgets all registered notification entries for the given TurnListener
     * where the entry's message equals the given one.
     *
     * @param turnListener
     */
    @Override
    public void dontNotify(TurnListener turnListener) {
        // all events that are equal to this one should be forgotten.
        // TurnEvent turnEvent = new TurnEvent(turnListener);
        register.entrySet().stream().map((mapEntry)
                -> mapEntry.getValue()).forEachOrdered((set) -> {
            // We don't remove directly, but first store in this
            // set. This is to avoid ConcurrentModificationExceptions.
            Set<TurnListener> toBeRemoved = new HashSet<>();
            if (set.contains(turnListener)) {
                toBeRemoved.add(turnListener);
            }
            toBeRemoved.forEach((event) -> {
                set.remove(event);
            });
        });
    }

    /**
     * Finds out how many turns will pass until the given TurnListener will be
     * notified with the given message.
     *
     * @param turnListener
     * @return the number of remaining turns, or -1 if the given TurnListener
     * will not be notified with the given message.
     */
    @Override
    public int getRemainingTurns(TurnListener turnListener) {
        // all events match that are equal to this.
        // TurnEvent turnEvent = new TurnEvent(turnListener);
        // the HashMap is unsorted, so we need to run through
        // all of it.
        List<Integer> matchingTurns = new ArrayList<>();
        register.entrySet().forEach((mapEntry) -> {
            Set<TurnListener> set = mapEntry.getValue();
            for (TurnListener currentEvent : set) {
                if (currentEvent.equals(turnListener)) {
                    matchingTurns.add(mapEntry.getKey());
                }
            }
        });
        if (matchingTurns.size() > 0) {
            Collections.sort(matchingTurns);
            return matchingTurns.get(0) - currentTurn;
        } else {
            return -1;
        }
    }

    /**
     * Finds out how many seconds will pass until the given TurnListener will be
     * notified with the given message.
     *
     * @param turnListener
     * @return the number of remaining seconds, or -1 if the given TurnListener
     * will not be notified with the given message.
     */
    @Override
    public int getRemainingSeconds(TurnListener turnListener) {
        return (getRemainingTurns(turnListener)
                * SimpleRPWorld.MILLISECONDS_PER_TURN) / 1000;
    }

    /**
     * Returns the list of events. Note this is only for debugging the
     * TurnNotifier
     *
     * @return eventList
     */
    @Override
    public Map<Integer, Set<TurnListener>> getEventListForDebugging() {
        return register;
    }

    /**
     * Returns the current turn. Note this is only for debugging TurnNotifier
     *
     * @return current turn
     */
    @Override
    public int getCurrentTurnForDebugging() {
        return currentTurn;
    }
}
