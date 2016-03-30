package simple.server.core.event;

import java.util.ArrayList;
import java.util.List;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;

/**
 * Manages the tutorial based on events created all over the game.
 * 
 * This class is meant to have generic messages that apply to all games. 
 * Additional can be created in the specific games to add more messages.
 * 
 * Based on code from hendrik
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = ILoginNotifier.class)
public class TutorialNotifier implements ILoginNotifier {

    private final List<LoginListener> listeners = new ArrayList<>();

    public TutorialNotifier() {
    }

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
        }
        // we must delay this for 1 turn for technical reasons (like zone
        // change)
        // but we delay it for 5 seconds so that the player has some time to
        // recognize the event
        TextEvent event = new TextEvent(NotificationType.TUTORIAL,
                type.getMessage(), "System");
        Lookup.getDefault().lookup(ITurnNotifier.class).notifyInTurns(5,
                new DelayedPlayerEventSender(event, player));
    }

    /**
     * Login.
     *
     * @param player ClientObjectInterface
     */
    public static void login(ClientObjectInterface player) {
        //Here add the messages to be sent to the user on login.
        process(player, TutorialEventType.LOGIN);
        process(player, TutorialEventType.TIMED_PASSWORD);
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

    @Override
    public void addListener(LoginListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void onPlayerLoggedIn(ClientObjectInterface player) {
        login(player);
    }

    @Override
    public void removeListener(LoginListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }
}
