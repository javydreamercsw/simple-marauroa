package simple.server.core.action.chat;

import marauroa.common.game.RPAction;
import org.openide.util.Lookup;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.CommandCenter;
import simple.server.core.entity.clientobject.GagManager;
import simple.server.core.event.LoginListener;
import simple.server.util.TimeUtil;

/**
 * Processes /chat, /tell (/msg) and /support.
 */
public class ChatAction {

    private static final String _SUPPORT = "support";
    public static final String _TELL = "tell";
    public static final String _CHAT = "chat";
    public static final String _PRIVATE_CHAT = "private-chat";
    private static final String _ANSWER = "answer";

    /**
     * Registers AnswerAction ChatAction TellAction and SupportAction.
     */
    public static void register() {
        CommandCenter.register(_ANSWER, new AnswerAction());
        CommandCenter.register(_CHAT, new PublicChatAction());
        CommandCenter.register(_TELL, new TellAction());
        CommandCenter.register(_SUPPORT, new AskForSupportAction());
        CommandCenter.register(_PRIVATE_CHAT, new PrivateChatAction());
    }

    public void onAction(final ClientObjectInterface player, final RPAction action) {
        if (GagManager.isGagged(player)) {
            long timeRemaining = Lookup.getDefault().lookup(LoginListener.class).getTimeRemaining(player);
            player.sendPrivateText("You are gagged, it will expire in " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)));
        }
    }
}
