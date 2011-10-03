package simple.server.core.event;

import org.openide.util.Lookup;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;

/**
 * manages the tutorial based on events created all over the game.
 *
 * @author hendrik
 */
public class TutorialNotifier {

    /**
     * If the specified event is unknown, add it to the list and send the text
     * to the player.
     *
     * @param player ClientObjectInterface
     * @param type EventType
     */
    private static void process(ClientObjectInterface player, TutorialEventType type) {
        String key = type.name().toLowerCase();
        if (player.getKeyedSlot("!tutorial", key) == null) {
            player.setKeyedSlot("!tutorial", key, "1");
            player.notifyWorldAboutChanges();
            // we must delay this for 1 turn for technical reasons (like zone
            // change)
            // but we delay it for 5 seconds so that the player has some time to
            // recognize the event
            DelayedPlayerTextSender dpts = new DelayedPlayerTextSender(player,
                    "Tutorial: " + type.getMessage(), NotificationType.TUTORIAL);
            Lookup.getDefault().lookup(ITurnNotifier.class).notifyInSeconds(5, dpts);
        }
    }

    /**
     * Login.
     *
     * @param player ClientObjectInterface
     */
    public static void login(ClientObjectInterface player) {
        process(player, TutorialEventType.FIRST_LOGIN);
    }

    /**
     * Zone changes.
     *
     * @param player ClientObjectInterface
     * @param sourceZone source zone
     * @param destinationZone destination zone
     */
    public static void zoneChange(ClientObjectInterface player, String sourceZone,
            String destinationZone) {
    }

    /**
     * player got attacked.
     *
     * @param player ClientObjectInterface
     */
    public static void attacked(ClientObjectInterface player) {
    }

    /**
     * player killed something.
     *
     * @param player ClientObjectInterface
     */
    public static void killedSomething(ClientObjectInterface player) {
    }

    /**
     * player got poisoned.
     *
     * @param player ClientObjectInterface
     */
    public static void poisoned(ClientObjectInterface player) {
    }

    /**
     * a player who stayed another minute in game.
     *
     * @param player ClientObjectInterface
     * @param age playing time
     */
    public static void aged(ClientObjectInterface player, int age) {
    }

    /**
     * player > level 2 logged in for new release.
     *
     * @param player ClientObjectInterface
     */
    public static void newrelease(ClientObjectInterface player) {
    }
}
