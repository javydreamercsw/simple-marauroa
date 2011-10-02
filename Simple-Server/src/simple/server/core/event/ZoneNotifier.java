
package simple.server.core.event;

import simple.common.game.ClientObjectInterface;
import simple.server.core.engine.SimpleSingletonRepository;

/**
 * Manages messages based on entering a new zone.
 * 
 * @author kymara (based on Tutorial Notifer by hendrik)
 */
public class ZoneNotifier {

    /**
     * If the specified event is unknown, add it to the list and send the text
     * to the player.
     *
     * @param player
     *            ClientObject
     * @param type
     *            EventType
     */
    private static void process(ClientObjectInterface player, ZoneEventType type) {
        String key = type.name().toLowerCase();
        // Use tutorial slot
        if (player.getKeyedSlot("!tutorial", key) == null) {
            player.setKeyedSlot("!tutorial", key, "1");

            // we must delay this for 1 turn for technical reasons (like zone
            // change)
            // but we delay it for 2 seconds so that the player has some time to
            // recognize the event
            DelayedPlayerTextSender dpts = new DelayedPlayerTextSender(player,
                    type.getMessage());
            SimpleSingletonRepository.get().get(TurnNotifier.class).notifyInSeconds(2, dpts);
        }
    }

    /**
     * Zone changes.
     *
     * @param player
     *            ClientObject
     * @param sourceZone
     *            source zone
     * @param destinationZone
     *            destination zone
     */
    public static void zoneChange(ClientObjectInterface player, String sourceZone,
            String destinationZone) {
    }
}
