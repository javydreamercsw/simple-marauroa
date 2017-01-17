package simple.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Class meant to work as a global event multiplexer for world objects and
 * zones. Events shall be reported dependent on succession of game flow and in
 * particular free from events caused by perceptions and sync operations of the
 * lower client layers.
 *
 * Currently works for zone events.
 */
public class WorldObjects {

    private static final List<WorldListener> LISTENERS
            = new ArrayList<WorldListener>();

    private WorldObjects() {
    }

    /**
     *
     */
    public static interface WorldListener {

        /**
         * Called when a world zone has been loaded.
         *
         * @param zoneName
         */
        void zoneEntered(String zoneName);

        /**
         * Called when a world zone has been unloaded.
         *
         * @param zoneName
         */
        void zoneLeft(String zoneName);

        /**
         * Called when the player arrives at a map location.
         */
        void playerMoved();
    }

    /**
     * Adds a WorldListener to this event distributor.
     *
     * @param a
     */
    public static void addWorldListener(WorldListener a) {
        synchronized (LISTENERS) {
            if (!LISTENERS.contains(a)) {
                LISTENERS.add(a);
            }
        }
    }

    /**
     * Removes a WorldListener from this event distributor.
     *
     * @param a
     */
    public static void removeWorldListener(WorldListener a) {
        synchronized (LISTENERS) {
            LISTENERS.remove(a);
        }
    }

    /**
     * Create a zone-entered event.
     *
     * @param zoneName
     */
    public static void fireZoneEntered(String zoneName) {
        synchronized (LISTENERS) {
            LISTENERS.forEach((wl) -> {
                wl.zoneEntered(zoneName);
            });
        }
    } // fireZoneEntered

    /**
     * Create a zone-left event.
     *
     * @param zoneName
     */
    public static void fireZoneLeft(String zoneName) {
        synchronized (LISTENERS) {
            LISTENERS.forEach((wl) -> {
                wl.zoneLeft(zoneName);
            });
        }
    } // fireZoneLeft

    /**
     * Create a player-moved event.
     */
    public static void firePlayerMoved() {

        synchronized (LISTENERS) {
            LISTENERS.forEach((wl) -> {
                wl.playerMoved();
            });
        }
    } // firePlayerMoved
}
