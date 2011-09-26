package simple.server.core.action.chat;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionListener;
import static simple.server.core.action.WellKnownActionConstant.TARGET;
import static simple.server.core.action.WellKnownActionConstant.TEXT;
import simple.server.core.engine.SimpleRPWorld;
import simple.server.core.engine.SimpleSingletonRepository;
import simple.server.core.entity.clientobject.GagManager;
import simple.server.core.event.PrivateTextEvent;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class PrivateChatAction implements ActionListener {

    /**
     * the logger instance.
     */
    private static final Logger logger = Log4J.getLogger(PrivateChatAction.class);

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            if (GagManager.checkIsGaggedAndInformPlayer(player)) {
                return;
            }
            if (action.has(TEXT) && action.has(TARGET)) {
                String text = action.get(TEXT);
                String target = action.get(TARGET);
                String from = rpo.get("name");
                logger.info("Processing private text action: " + action);
                SimpleSingletonRepository.get().get(SimpleRPWorld.class).applyPrivateEvent(target,
                        new PrivateTextEvent(NotificationType.PRIVMSG, text, target, from));
            } else {
                StringBuilder mess = new StringBuilder("Action is missing key components:\n");
                if (!action.has(TEXT)) {
                    mess.append(TEXT).append("\n");
                }
                if (!action.has(TARGET)) {
                    mess.append(TARGET).append("\n");
                }
                logger.warn(mess.toString());
            }
        }
    }
}
