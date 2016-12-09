package simple.server.core.event;

import java.util.ArrayList;
import java.util.List;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;
import simple.server.core.entity.RPEntityInterface;
import simple.server.extension.TutorialExtension;

/**
 * Manages the tutorial based on events created all over the game.
 *
 * This class is meant to have generic messages that apply to all games.
 * Additional can be created in the specific games to add more messages.
 *
 * Based on code from hendrik
 *
 * @author Javier Ortiz Bultron javier.ortiz.78@gmail.com
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
    private static void process(RPEntityInterface player, TutorialEventType type) {
        String key = type.name().toLowerCase();
        if (player instanceof ClientObjectInterface) {
            ClientObjectInterface coi = (ClientObjectInterface) player;
            if (coi.getKeyedSlot(TutorialExtension.TUTORIAL, key) == null) {
                coi.setKeyedSlot(TutorialExtension.TUTORIAL, key, "1");
                coi.notifyWorldAboutChanges();
            }
        } else {
            RPObject obj = (RPObject) player;
            if (obj.hasSlot(TutorialExtension.TUTORIAL)
                    && obj.getSlot(TutorialExtension.TUTORIAL).isEmpty()) {
                //TODO: Handle tutorial for player characters.
            }
        }
        // We must delay this for 1 turn for technical reasons (like zone
        // change)
        // but we delay it for 5 seconds so that the player has some time to
        // recognize the event
        TextEvent event = new TextEvent(NotificationType.TUTORIAL,
                type.getMessage(), "System");
        Lookup.getDefault().lookup(ITurnNotifier.class).notifyInTurns(10,
                new DelayedPlayerEventSender(event, (RPObject) player));
    }

    /**
     * Login.
     *
     * @param player ClientObjectInterface
     */
    public static void login(RPEntityInterface player) {
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
    public static void zoneChange(RPEntityInterface player, String sourceZone,
            String destinationZone) {
    }

    @Override
    public void addListener(LoginListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void onPlayerLoggedIn(RPEntityInterface player) {
        login(player);
    }

    @Override
    public void removeListener(LoginListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }
}
