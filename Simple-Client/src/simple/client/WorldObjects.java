/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
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

    private static List<WorldListener> worldListeners = new ArrayList<WorldListener>();

    private WorldObjects() {
    }

    /**
     *
     */
    public static interface WorldListener {

        /** Called when a world zone has been loaded.
         * @param zoneName
         */
        void zoneEntered(String zoneName);

        /** Called when a world zone has been unloaded.
         * @param zoneName
         */
        void zoneLeft(String zoneName);

        /** Called when the player arrives at a map location. */
        void playerMoved();
    }

    /** Adds a WorldListener to this event distributor.
     * @param a
     */
    public static void addWorldListener(WorldListener a) {
        synchronized (worldListeners) {
            if (!worldListeners.contains(a)) {
                worldListeners.add(a);
            }
        }
    }

    /** Removes a WorldListener from this event distributor.
     * @param a
     */
    public static void removeWorldListener(WorldListener a) {
        synchronized (worldListeners) {
            worldListeners.remove(a);
        }
    }

    /** Create a zone-entered event.
     * @param zoneName
     */
    public static void fireZoneEntered(String zoneName) {
        synchronized (worldListeners) {
            for (WorldListener wl : worldListeners) {
                wl.zoneEntered(zoneName);
            }
        }
    } // fireZoneEntered

    /** Create a zone-left event.
     * @param zoneName
     */
    public static void fireZoneLeft(String zoneName) {
        synchronized (worldListeners) {
            for (WorldListener wl : worldListeners) {
                wl.zoneLeft(zoneName);
            }
        }
    } // fireZoneLeft

    /** Create a player-moved event. */
    public static void firePlayerMoved() {

        synchronized (worldListeners) {
            for (WorldListener wl : worldListeners) {
                wl.playerMoved();
            }
        }
    } // firePlayerMoved
}
