package simple.server.core.action.chat;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionListener;
import static simple.server.core.action.WellKnownActionConstant.TEXT;
import simple.server.core.engine.IRPWorld;
import simple.server.core.event.LoginListener;
import simple.server.core.event.TextEvent;

/**
 * Handles public said text
 */
public class PublicChatAction implements ActionListener {

    /**
     * the logger instance.
     */
    private static final Logger logger = Log4J.getLogger(PublicChatAction.class);

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            if (Lookup.getDefault().lookup(LoginListener.class).checkIsGaggedAndInformPlayer(player)) {
                return;
            }
            if (action.has(TEXT)) {
                String text = action.get(TEXT);
                logger.debug("Processing text event: " + text);
                Lookup.getDefault().lookup(IRPWorld.class).applyPublicEvent(
                        Lookup.getDefault().lookup(IRPWorld.class).getZone(((RPObject) player).get("zoneid")),
                        new TextEvent(text, player.getName()));
            }
        }
    }
}
