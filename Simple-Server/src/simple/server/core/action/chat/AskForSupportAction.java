/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.action.chat;

import simple.server.core.action.ActionListener;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.engine.SimpleSingletonRepository;
import simple.common.game.ClientObjectInterface;
import static simple.server.core.action.WellKnownActionConstant.TEXT;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * Handles asking for /support.
 */
public class AskForSupportAction implements ActionListener {

    protected Map<String, Long> lastMsg = new HashMap<String, Long>();

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
                SimpleSingletonRepository.get().get(SimpleRPRuleProcessor.class).addGameEvent(player.getName(),
                        "support", action.get(TEXT));
                SimpleRPRuleProcessor.sendMessageToSupporters(player.getTitle(), message);
                player.sendPrivateText("You ask for support: " + action.get(TEXT) + "\nIt may take a little time until your question is answered.");
                player.notifyWorldAboutChanges();
            }
        }
    }
}
