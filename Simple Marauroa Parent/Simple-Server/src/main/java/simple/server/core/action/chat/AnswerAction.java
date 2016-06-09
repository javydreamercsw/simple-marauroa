package simple.server.core.action.chat;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import static simple.server.core.action.WellKnownActionConstant.*;

/**
 * handles /tell-action (/msg-action).
 */
@ServiceProvider(service = ActionProvider.class)
public class AnswerAction implements ActionProvider {

    private static final String ANSWER = "answer";

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            if (action.has(TEXT)) {
                if (player.getLastPrivateChatter() != null) {
                    // convert the action to a /tell action
                    action.put(TYPE, "tell");
                    action.put(TARGET, player.getLastPrivateChatter());
                    new TellAction().onAction((RPObject) player, action);
                } else {
                    player.sendPrivateText("Nobody has talked privately to you.");
                }
            }
        }
    }

    @Override
    public void register() {
        CommandCenter.register(ANSWER, new AnswerAction());
    }
}
