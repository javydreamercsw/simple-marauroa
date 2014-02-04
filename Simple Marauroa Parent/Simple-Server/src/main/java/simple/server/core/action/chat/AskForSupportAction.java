package simple.server.core.action.chat;

import java.util.HashMap;
import java.util.Map;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import static simple.server.core.action.WellKnownActionConstant.TEXT;
import simple.server.core.engine.SimpleRPRuleProcessor;

/**
 * Handles asking for /support.
 */
@ServiceProvider(service = ActionProvider.class)
public class AskForSupportAction implements ActionProvider {

    protected Map<String, Long> lastMsg = new HashMap<String, Long>();
    private static final String _SUPPORT = "support";

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            if (action.has(TEXT)) {

                if ("".equals(action.get(TEXT).trim())) {
                    player.sendPrivateText("Usage /support <your message here>");
                    return;
                }

                String message = action.get(TEXT) + "\r\nPlease use #/supportanswer #" + player.getTitle() + " to answer.";
                ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class)).addGameEvent(player.getName(),
                        "support", action.get(TEXT));
                SimpleRPRuleProcessor.sendMessageToSupporters(player.getTitle(), message);
                player.sendPrivateText("You ask for support: " + action.get(TEXT) + "\nIt may take a little time until your question is answered.");
                player.notifyWorldAboutChanges();
            }
        }
    }

    public void register() {
        CommandCenter.register(_SUPPORT, new AskForSupportAction());
    }
}
