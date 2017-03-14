package simple.client.event;

import marauroa.common.game.RPEvent;
import simple.client.ChatScreenInterface;
import simple.client.SimpleClient;
import simple.client.event.listener.ClientRPEventListener;
import simple.common.NotificationType;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TextEvent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ChatListener implements ClientRPEventListener {

    /**
     *
     * @param event
     */
    @Override
    public void onRPEventReceived(RPEvent event) {
        String from = event.get("from"), text = event.get("text");
        ChatScreenInterface screen = null;
        switch (SimpleClient.get().getCurrentState().ordinal()) {
            case 0:
                screen = SimpleClient.get().getInterface();
                break;
            default:
        }
        if (screen != null) {
            if (event.getName().equals(PrivateTextEvent.RPCLASS_NAME)) {
                screen.addLine(from == null ? "" : from, text == null ? ""
                        : text, NotificationType.PRIVMSG);
            } else if (event.getName().equals(TextEvent.RPCLASS_NAME)) {
                screen.addLine(from == null ? "" : from, text == null ? ""
                        : text, NotificationType.NORMAL);
            }
        }
    }
}
