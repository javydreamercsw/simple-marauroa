package simple.server.core.action.chat;

import simple.server.core.engine.SimpleRPWorld;
import simple.server.core.engine.SimpleSingletonRepository;
import marauroa.common.Logger;
import marauroa.common.Log4J;
import simple.server.core.entity.clientobject.GagManager;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionListener;
import simple.server.core.event.PrivateTextEvent;
import static simple.server.core.action.WellKnownActionConstant.*;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class PrivateChatAction implements ActionListener {

    /** the logger instance. */
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
                logger.debug("Processing private text event: " + text);
                SimpleSingletonRepository.get().get(SimpleRPWorld.class).applyPrivateEvent(target,
                        new PrivateTextEvent(NotificationType.PRIVMSG, text));
            } else {
            }
        }
    }
}
